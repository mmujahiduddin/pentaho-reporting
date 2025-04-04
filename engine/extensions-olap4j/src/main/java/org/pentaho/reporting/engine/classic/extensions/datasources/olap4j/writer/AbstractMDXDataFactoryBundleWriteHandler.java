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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.writer;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.Olap4JDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public abstract class AbstractMDXDataFactoryBundleWriteHandler {
  public AbstractMDXDataFactoryBundleWriteHandler() {
  }

  protected void writeBody( final AbstractMDXDataFactory dataFactory, final XmlWriter xmlWriter )
    throws IOException, BundleWriterException {
    writeConnectionInfo( xmlWriter, dataFactory.getConnectionProvider() );
    final String roleField = dataFactory.getRoleField();
    if ( roleField != null ) {
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "role-field", XmlWriter.OPEN );
      xmlWriter.writeTextNormalized( roleField, false );
      xmlWriter.writeCloseTag();
    }

    final String jdbcUserField = dataFactory.getJdbcUserField();
    if ( jdbcUserField != null ) {
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "jdbc-user-field", XmlWriter.OPEN );
      xmlWriter.writeTextNormalized( jdbcUserField, false );
      xmlWriter.writeCloseTag();
    }

    final String jdbcPasswordField = dataFactory.getJdbcPasswordField();
    if ( jdbcPasswordField != null ) {
      xmlWriter.writeTag( Olap4JDataFactoryModule.NAMESPACE, "jdbc-password-field", XmlWriter.OPEN );
      xmlWriter.writeTextNormalized( jdbcPasswordField, false );
      xmlWriter.writeCloseTag();
    }
  }

  private void writeConnectionInfo( final XmlWriter xmlWriter,
                                    final OlapConnectionProvider connectionProvider )
    throws IOException, BundleWriterException {
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    final String configKey = Olap4JDataFactoryModule.CONNECTION_WRITER_PREFIX + connectionProvider.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty( configKey );
    if ( value == null ) {
      throw new BundleWriterException
        ( "Unable to find writer for connection info of type " + connectionProvider.getClass() );
    }

    final OlapConnectionProviderWriteHandler handler = ObjectUtilities.loadAndInstantiate
      ( value, AbstractMDXDataFactoryBundleWriteHandler.class, OlapConnectionProviderWriteHandler.class );
    if ( handler != null ) {
      handler.writeReport( xmlWriter, connectionProvider );
    }
  }

  protected DefaultTagDescription createTagDescription() {
    final DefaultTagDescription tagDescription = new DefaultTagDescription();
    tagDescription.setNamespaceHasCData( Olap4JDataFactoryModule.NAMESPACE, false );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "driver", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "path", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "property", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "url", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "jdbc-user-field", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "jdbc-password-field", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "role-field", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "global-script", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "static-query", true );
    tagDescription.setElementHasCData( Olap4JDataFactoryModule.NAMESPACE, "script", true );
    return tagDescription;
  }

}
