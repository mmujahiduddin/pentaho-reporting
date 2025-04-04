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


package org.pentaho.reporting.designer.core.actions.elements.layout;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilter;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilterFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

@SuppressWarnings( "HardCodedStringLiteral" )
public abstract class LayoutAction extends AbstractElementSelectionAction implements ToggleStateAction {
  private ReportModelEventFilter eventFilter;
  private String prefix;
  private String layoutMode;

  public LayoutAction( final String prefix, final String layoutMode ) {
    if ( prefix == null ) {
      throw new NullPointerException();
    }
    if ( layoutMode == null ) {
      throw new NullPointerException();
    }

    this.prefix = prefix;
    this.layoutMode = layoutMode;
    this.eventFilter = new ReportModelEventFilterFactory().createStyleFilter( BandStyleKeys.LAYOUT );

    putValue( Action.NAME, ActionMessages.getString( prefix + ".Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( prefix + ".Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( prefix + ".Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( prefix + ".Accelerator" ) );
    putValue( Action.SELECTED_KEY, Boolean.FALSE );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
    if ( eventFilter.isFilteredEvent( event ) ) {
      updateSelection();
    }
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  protected void updateSelection() {
    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Band> visualElements = model.getSelectedElementsOfType( Band.class );
    if ( visualElements.isEmpty() ) {
      setEnabled( false );
      return;
    }


    final Element first = visualElements.get( 0 );
    setSelected( layoutMode.equals
      ( first.getStyle().getStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_CANVAS ) ) );
    setEnabled( true );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel model = getSelectionModel();
    if ( model == null ) {
      return;
    }
    final List<Band> visualElements = model.getSelectedElementsOfType( Band.class );

    final MassElementStyleUndoEntryBuilder builder = new MassElementStyleUndoEntryBuilder( visualElements );

    for ( Band object : visualElements ) {
      object.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, layoutMode );
    }

    final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
    getActiveContext().getUndo()
      .addChange( ActionMessages.getString( prefix + ".UndoName" ), massElementStyleUndoEntry );
  }
}
