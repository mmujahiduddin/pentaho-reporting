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


package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceMessageType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.wizard.WizardProcessorUtil;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationGoldTest extends TestCase {
  public SerializationGoldTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    //  if (true) return originalReport;
    final byte[] bytes = serializeReportObject( originalReport );
    final MasterReport report = deserializeReportObject( bytes );
    return report;
  }

  private byte[] serializeReportObject( final MasterReport report ) throws IOException {
    // we don't test whether our demo models are serializable :)
    // clear all report properties, which may cause trouble ...
    final MemoryByteArrayOutputStream bo = new MemoryByteArrayOutputStream();
    final ObjectOutputStream oout = new ObjectOutputStream( bo );
    oout.writeObject( report );
    oout.close();
    return bo.toByteArray();
  }

  private MasterReport deserializeReportObject( final byte[] data ) throws IOException, ClassNotFoundException {
    final ByteArrayInputStream bin = new ByteArrayInputStream( data );
    final ObjectInputStream oin = new ObjectInputStream( bin );
    final MasterReport report2 = (MasterReport) oin.readObject();
    assertNotNull( report2 );
    return report2;
  }

  public void testWizardDefinitionIsAvailable() throws Exception {
    final File url = GoldTestBase.locateGoldenSampleReport( "prd-2887.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport org = (MasterReport) directly.getResource();

    assertNotNull( WizardProcessorUtil.loadWizardSpecification( org, resourceManager ) );
    final MasterReport report = postProcess( org );
    assertNotNull( WizardProcessorUtil.loadWizardSpecification( report, report.getResourceManager() ) );
    DetailsHeader detailsHeader = report.getDetailsHeader();
    detailsHeader.getElement( 0 ).setName( "MagicChange" );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 1 );
    ModelPrinter.INSTANCE.print( logicalPageBox );
  }

  public void testResourceLabelAfterSerialization() throws Exception {
    final File url = GoldTestBase.locateGoldenSampleReport( "Prd-3514.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport org = (MasterReport) directly.getResource();

    final MasterReport report = postProcess( org );

    RelationalGroup relationalGroup = report.getRelationalGroup( 0 );
    GroupHeader header = relationalGroup.getHeader();
    Band band = (Band) header.getElement( 0 );
    Element element = band.getElement( 1 );
    assertTrue( element.getElementType() instanceof ResourceMessageType );
    element.setName( "DateTitleField" );
    //    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 1);
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, header, false, false );
    RenderNode dateTitleField = MatchFactory.findElementByName( logicalPageBox, "DateTitleField" );
    assertNotNull( dateTitleField );
    //    ModelPrinter.INSTANCE.print(logicalPageBox);
  }
}
