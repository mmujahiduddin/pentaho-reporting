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


package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleStyleRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterHandlerRegistry;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml.BarSparklineElementReadHandler;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml.LineSparklineElementReadHandler;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml.PieSparklineElementReadHandler;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml.SparklineStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml.SparklineStylesReadHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This module adds support for Sparkline graphs. Sparkline graphs are tiny word sized graphs allowing a rather fast
 * reading of data evolution over the time (usual case).<br/>
 * Such graphs can be seen as tiny linecharts or barcharts.
 * <p/>
 * For additional reading check out <a href="http://www.edwardtufte.com/bboard/q-and-a-fetch-msg?msg_id=0001OR">
 * Sparklines: theory and practice</a> thread.
 * <p/>
 * This module adds BarSparkline and LineSparkline graph element types to the reporting engine.
 *
 * @author Thomas Morgner
 */
public class SparklineModule extends AbstractModule {
  // Extensions share the same namespace for the XML-Element itself and the Attributes for the sake of simplicity.
  public static final String NAMESPACE = SparklineAttributeNames.NAMESPACE;

  public SparklineModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error occurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( getClass() );
      Class.forName( "org.pentaho.reporting.libraries.libsparklines.BarGraphDrawable", false, loader );
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Unable to load the Sparkline library class." );
    }

    ElementTypeRegistry.getInstance().registerNamespacePrefix( NAMESPACE, "sparkline" );
    ElementMetaDataParser
        .initializeOptionalElementMetaData( "org/pentaho/reporting/engine/classic/extensions/modules/sparklines/meta-elements.xml" );
    ElementMetaDataParser
        .initializeOptionalExpressionsMetaData( "org/pentaho/reporting/engine/classic/extensions/modules/sparklines/meta-expressions.xml" );

    BundleElementRegistry.getInstance().registerGenericElement( LineSparklineType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( BarSparklineType.INSTANCE );
    BundleElementRegistry.getInstance().registerGenericElement( PieSparklineType.INSTANCE );
    // legacy element handling
    BundleElementRegistry.getInstance().register( NAMESPACE, "line-spark", LineSparklineElementReadHandler.class );
    BundleElementRegistry.getInstance().register( NAMESPACE, "pie-spark", PieSparklineElementReadHandler.class );
    BundleElementRegistry.getInstance().register( NAMESPACE, "bar-spark", BarSparklineElementReadHandler.class );
    BundleWriterHandlerRegistry.getInstance().setNamespaceHasCData( NAMESPACE, false );

    BundleStyleRegistry.getInstance().register( SparklineStyleSetWriteHandler.class );
    BundleStyleRegistry.getInstance().register( NAMESPACE, "spark-styles", SparklineStylesReadHandler.class );

  }
}
