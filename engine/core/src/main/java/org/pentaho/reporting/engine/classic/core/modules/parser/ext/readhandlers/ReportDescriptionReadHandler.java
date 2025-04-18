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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.GroupList;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class ReportDescriptionReadHandler extends AbstractPropertyXmlReadHandler {
  private GroupList groupList;

  public ReportDescriptionReadHandler() {
    this.groupList = new GroupList();
  }

  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    final ReportDefinition report =
        (ReportDefinition) getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );

    final int groupCount = report.getGroupCount();
    for ( int i = 0; i < groupCount; i++ ) {
      final Group g = report.getGroup( i );
      if ( g instanceof RelationalGroup ) {
        groupList.add( (RelationalGroup) g );
      } else {
        throw new ParseException( "The existing report contains non-default groups. "
            + "This parser cannot handle such a construct." );
      }
    }

  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    final ReportDefinition report =
        (ReportDefinition) getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );

    if ( "report-header".equals( tagName ) ) {
      return new RootBandReadHandler( report.getReportHeader() );
    } else if ( "report-footer".equals( tagName ) ) {
      return new RootBandReadHandler( report.getReportFooter() );
    } else if ( "page-header".equals( tagName ) ) {
      return new BandReadHandler( report.getPageHeader() );
    } else if ( "page-footer".equals( tagName ) ) {
      return new BandReadHandler( report.getPageFooter() );
    } else if ( "watermark".equals( tagName ) ) {
      return new BandReadHandler( report.getWatermark() );
    } else if ( "no-data-band".equals( tagName ) ) {
      final NoDataBand noDataBand = (NoDataBand) report.getChildElementByType( NoDataBandType.INSTANCE );
      if ( noDataBand == null ) {
        throw new ParseException( "Not a relational report" );
      }
      return new RootBandReadHandler( noDataBand );
    } else if ( "groups".equals( tagName ) ) {
      return new GroupsReadHandler( groupList );
    } else if ( "itemband".equals( tagName ) ) {
      final ItemBand itemBand = (ItemBand) report.getChildElementByType( ItemBandType.INSTANCE );
      if ( itemBand == null ) {
        throw new ParseException( "Not a relational report" );
      }
      return new RootBandReadHandler( itemBand );
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final AbstractReportDefinition report =
        (AbstractReportDefinition) getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    try {
      final GroupList clone = (GroupList) groupList.clone();
      clone.installIntoReport( report );
    } catch ( CloneNotSupportedException e ) {
      throw new ParseException( "Failed to add group-list to report", getLocator() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
