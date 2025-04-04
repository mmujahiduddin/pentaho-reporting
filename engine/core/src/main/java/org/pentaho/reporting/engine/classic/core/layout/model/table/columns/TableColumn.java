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


package org.pentaho.reporting.engine.classic.core.layout.model.table.columns;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.util.LongList;

/**
 * A column definition. A column has an effective definedWidth, which corresponds with the computed definedWidth of the
 * content. If that definedWidth gets greater than the initial definedWidth (the definedWidth that has been computed by
 * the table at the beginning of the rendering), we entered the auto-mode.
 * <p/>
 * Once a column has been explicitly marked as validated, any attempt to redefine the computed sizes must fail. (This
 * makes sure that the table stays in sync and does not get disordered if its rendered incrementally.)
 * <p/>
 * A column is constrained by three metrics:
 * <p/>
 * The Minimum ChunkSize defines the smallest non-breakable content item in the column. A column will always consume at
 * least this space. (This is zero, if the column has overflow enabled.)
 * <p/>
 * The Maximum Box-Width is the size the content would consume, if there is infinite space available. Manual linebreaks
 * are taken into account, but not automatic ones.
 * <p/>
 * If the column explicitly defines a width, the preferred size indicates that. If no preferred width is defined, the
 * preferred size will be zero.
 *
 * @author Thomas Morgner
 */
public class TableColumn {
  private Border border;
  private RenderLength definedWidth;

  private long effectiveSize;
  private LongList cachedSize;

  private boolean autoGenerated;
  private boolean validated;

  public TableColumn( final Border border, final RenderLength definedWidth, final boolean autoGenerated ) {
    if ( border == null ) {
      throw new NullPointerException();
    }
    if ( definedWidth == null ) {
      throw new NullPointerException();
    }

    this.definedWidth = definedWidth;
    this.border = border;
    this.autoGenerated = autoGenerated;
    this.cachedSize = new LongList( 10 );
  }

  public RenderLength getDefinedWidth() {
    return definedWidth;
  }

  public Border getBorder() {
    return border;
  }

  public long getEffectiveSize() {
    return effectiveSize;
  }

  public void setEffectiveSize( final long effectiveSize ) {
    this.effectiveSize = effectiveSize;
  }

  public boolean isValidated() {
    return validated;
  }

  public void setValidated( final boolean validated ) {
    this.validated = validated;
  }

  public boolean isAutoGenerated() {
    return autoGenerated;
  }

  public void setCachedSize( final int colSpan, final long size ) {
    final int idx = colSpan - 1;
    if ( getCachedSize( colSpan ) < size ) {
      cachedSize.set( idx, size );
    }
  }

  public long getCachedSize( final int colspan ) {
    final int idx = colspan - 1;
    if ( idx < cachedSize.size() ) {
      return cachedSize.get( idx );
    }
    return 0;
  }

  public int getMaxColspan() {
    return cachedSize.size();
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "TableColumn" );
    sb.append( "{validated=" ).append( validated );
    sb.append( ", autoGenerated=" ).append( autoGenerated );
    sb.append( ", effectiveSize=" ).append( effectiveSize );
    sb.append( ", definedWidth=" ).append( definedWidth );
    sb.append( ", border=" ).append( border );
    sb.append( ", cachedSize=" ).append( cachedSize );
    sb.append( '}' );
    return sb.toString();
  }
}
