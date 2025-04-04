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


package org.pentaho.reporting.ui.datasources.external;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ExternalDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;

public class ExternalDataSourcePlugin implements DataSourcePlugin
{
  public ExternalDataSourcePlugin()
  {
  }

  public DataFactory performEdit(final DesignTimeContext context,
                                 final DataFactory input,
                                 final String queryName,
                                 final DataFactoryChangeRecorder changeRecorder)
  {
    if (input == null)
    {
      return new ExternalDataFactory();
    }
    return input;
  }

  public boolean canHandle(final DataFactory dataFactory)
  {
    return false;
  }

  public DataFactoryMetaData getMetaData()
  {
    return DataFactoryRegistry.getInstance().getMetaData(ExternalDataFactory.class.getName());
  }
}
