/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.repository.zipwriter;

import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.LibRepositoryBoot;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.Deflater;

/**
 * Creation-Date: 01.12.2006, 21:23:25
 *
 * @author Thomas Morgner
 */
public class ZipContentItem implements ContentItem {
  private boolean newItem;
  private String name;
  private String contentId;
  private ZipRepository repository;
  private ZipContentLocation parent;
  private String comment;
  private long time;
  private Integer method;
  private int compression;

  public ZipContentItem( final String name,
                         final ZipRepository repository,
                         final ZipContentLocation parent ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( repository == null ) {
      throw new NullPointerException();
    }
    if ( parent == null ) {
      throw new NullPointerException();
    }

    this.time = System.currentTimeMillis();
    this.name = name;
    this.repository = repository;
    this.parent = parent;
    this.contentId = RepositoryUtilities.buildName( this, "/" );
    this.newItem = true;
    this.method = LibRepositoryBoot.ZIP_METHOD_DEFLATED;
    this.compression = Deflater.DEFAULT_COMPRESSION;
  }

  public String getMimeType() throws ContentIOException {
    return getRepository().getMimeRegistry().getMimeType( this );
  }

  public OutputStream getOutputStream() throws ContentIOException, IOException {
    if ( newItem == false ) {
      throw new ContentIOException( "This item is no longer writeable." );
    }
    newItem = false;
    return new ZipEntryOutputStream( this );
  }

  public InputStream getInputStream() throws ContentIOException, IOException {
    throw new ContentIOException( "This item is not readable." );
  }

  public boolean isReadable() {
    return false;
  }

  public boolean isWriteable() {
    return newItem;
  }

  public String getName() {
    return name;
  }

  public Object getContentId() {
    return contentId;
  }

  public Object getAttribute( final String domain, final String key ) {
    if ( LibRepositoryBoot.REPOSITORY_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.VERSION_ATTRIBUTE.equals( key ) ) {
        return new Date( time );
      }
    } else if ( LibRepositoryBoot.ZIP_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE.equals( key ) ) {
        return comment;
      } else if ( LibRepositoryBoot.ZIP_METHOD_ATTRIBUTE.equals( key ) ) {
        return method;
      } else if ( LibRepositoryBoot.ZIP_COMPRESSION_ATTRIBUTE.equals( key ) ) {
        return new Integer( compression );
      }
    }
    return null;
  }

  public boolean setAttribute( final String domain, final String key, final Object value ) {
    if ( LibRepositoryBoot.REPOSITORY_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.VERSION_ATTRIBUTE.equals( key ) ) {
        if ( value instanceof Date ) {
          final Date n = (Date) value;
          time = n.getTime();
          return true;
        } else if ( value instanceof Number ) {
          final Number n = (Number) value;
          time = n.longValue();
          return true;
        }
      }
    } else if ( LibRepositoryBoot.ZIP_DOMAIN.equals( domain ) ) {
      if ( LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE.equals( key ) ) {
        if ( value != null ) {
          comment = String.valueOf( value );
          return true;
        } else {
          comment = null;
          return true;
        }
      }

      if ( LibRepositoryBoot.ZIP_METHOD_ATTRIBUTE.equals( key ) ) {
        if ( LibRepositoryBoot.ZIP_METHOD_STORED.equals( value ) ) {
          method = LibRepositoryBoot.ZIP_METHOD_STORED;
          return true;
        } else if ( LibRepositoryBoot.ZIP_METHOD_DEFLATED.equals( value ) ) {
          method = LibRepositoryBoot.ZIP_METHOD_DEFLATED;
          return true;
        }
      }
      if ( LibRepositoryBoot.ZIP_COMPRESSION_ATTRIBUTE.equals( key ) ) {
        if ( value instanceof Integer ) {
          final Integer valueInt = (Integer) value;
          final int compression = valueInt.intValue();
          if ( compression >= 0 && compression <= 9 ) {
            this.compression = compression;
            return true;
          }
        }
      }
    }
    return false;
  }

  public Repository getRepository() {
    return repository;
  }

  public ContentLocation getParent() {
    return parent;
  }

  public boolean delete() {
    return false;
  }
}
