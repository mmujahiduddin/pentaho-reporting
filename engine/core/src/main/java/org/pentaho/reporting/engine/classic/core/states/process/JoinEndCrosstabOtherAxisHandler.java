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


package org.pentaho.reporting.engine.classic.core.states.process;

import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

public class JoinEndCrosstabOtherAxisHandler implements AdvanceHandler {
  public static final AdvanceHandler HANDLER = new JoinEndCrosstabOtherAxisHandler();

  public JoinEndCrosstabOtherAxisHandler() {
  }

  public ProcessState advance( final ProcessState state ) throws ReportProcessingException {
    return state.deriveForAdvance();
  }

  /**
   * Checks whether there are more groups active.
   *
   * @return true if this is the last (outer-most) group.
   */
  private boolean isRootGroup( final ProcessState state ) {
    return state.getCurrentGroupIndex() == ReportState.BEFORE_FIRST_GROUP;
  }

  public ProcessState commit( final ProcessState next ) throws ReportProcessingException {
    next.leaveGroup();
    final DefaultFlowController fc = next.getFlowController();
    final boolean advanceRequested = fc.isAdvanceRequested();
    final boolean advanceable = fc.getMasterRow().isAdvanceable();
    if ( isRootGroup( next ) ) {
      throw new ReportProcessingException( "This report is invalid. A CR-Other-Group cannot be a root group." );
    }

    final Group parentGroup = next.getReport().getGroup( next.getCurrentGroupIndex() );
    if ( advanceRequested == false || advanceable == false ) {
      // This happens for empty - reports. Empty-Reports are never advanceable, therefore we can
      // reach an non-advance state where inner group-footers are printed.
      if ( parentGroup instanceof CrosstabGroup ) {
        next.setAdvanceHandler( EndCrosstabBodyHandler.HANDLER );
      } else if ( parentGroup instanceof CrosstabOtherGroup ) {
        next.setAdvanceHandler( EndCrosstabOtherBodyHandler.HANDLER );
      } else {
        throw new ReportProcessingException( "This report is invalid." );
      }
      return next;
    }

    // This group is not the outer-most group ..
    final DefaultFlowController cfc = fc.performCommit();
    if ( ProcessState.isLastItemInGroup( parentGroup, fc.getMasterRow(), cfc.getMasterRow() ) ) {
      // continue with an other EndGroup-State ...
      if ( parentGroup instanceof CrosstabGroup ) {
        next.setAdvanceHandler( EndCrosstabBodyHandler.HANDLER );
      } else if ( parentGroup instanceof CrosstabOtherGroup ) {
        next.setAdvanceHandler( EndCrosstabOtherBodyHandler.HANDLER );
      } else {
        throw new ReportProcessingException( "This report is invalid." );
      }
      return next;
    } else {
      // The parent group is not finished, so finalize the createRollbackInformation.
      // more data in parent group, print the next header
      final DefaultFlowController rfc = cfc.resetRowCursor();
      next.setFlowController( rfc );
      next.setAdvanceHandler( BeginCrosstabOtherAxisHandler.HANDLER );
      return next;
    }
  }

  public boolean isFinish() {
    return false;
  }

  public int getEventCode() {
    return ReportEvent.GROUP_FINISHED | ProcessState.ARTIFICIAL_EVENT_CODE | ReportEvent.CROSSTABBING;
  }

  public boolean isRestoreHandler() {
    return false;
  }
}
