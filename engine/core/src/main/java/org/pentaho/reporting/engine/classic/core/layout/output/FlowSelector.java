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


package org.pentaho.reporting.engine.classic.core.layout.output;

/**
 * Creation-Date: 09.04.2007, 11:01:40
 *
 * @author Thomas Morgner
 */
public interface FlowSelector {
  public boolean isLogicalPageAccepted( LogicalPageKey key );
}
