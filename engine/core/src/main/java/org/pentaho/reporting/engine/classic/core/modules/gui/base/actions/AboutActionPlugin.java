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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.SwingPreviewModule;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.about.AboutDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Creation-Date: 16.11.2006, 16:34:55
 *
 * @author Thomas Morgner
 */
public class AboutActionPlugin extends AbstractActionPlugin implements ControlActionPlugin {
  private ResourceBundleSupport resources;
  private AboutDialog aboutFrame;

  public AboutActionPlugin() {
  }

  public boolean initialize( final SwingGuiContext context ) {
    super.initialize( context );
    resources =
        new ResourceBundleSupport( context.getLocale(), SwingPreviewModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingPreviewModule.class ) );
    return true;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.base.about."; //$NON-NLS-1$
  }

  /**
   * Returns the display name for the export action.
   *
   * @return The display name.
   */
  public String getDisplayName() {
    return resources.getString( "action.about.name" ); //$NON-NLS-1$
  }

  /**
   * Returns the short description for the export action.
   *
   * @return The short description.
   */
  public String getShortDescription() {
    return resources.getString( "action.about.description" ); //$NON-NLS-1$
  }

  /**
   * Returns the small icon for the export action.
   *
   * @return The icon.
   */
  public Icon getSmallIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getSmallIcon( locale, "action.about.small-icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the large icon for the export action.
   *
   * @return The icon.
   */
  public Icon getLargeIcon() {
    final Locale locale = getContext().getLocale();
    return getIconTheme().getLargeIcon( locale, "action.about.icon" ); //$NON-NLS-1$
  }

  /**
   * Returns the accelerator key for the export action.
   *
   * @return The accelerator key.
   */
  public KeyStroke getAcceleratorKey() {
    return null;
  }

  /**
   * Returns the mnemonic key code.
   *
   * @return The code.
   */
  public Integer getMnemonicKey() {
    return resources.getOptionalMnemonic( "action.about.mnemonic" ); //$NON-NLS-1$
  }

  public boolean configure( final PreviewPane reportPane ) {
    if ( aboutFrame == null ) {
      final String title = getDisplayName();
      // look where we have been added ...
      final Window w = LibSwingUtil.getWindowAncestor( reportPane );
      if ( w instanceof Frame ) {
        aboutFrame = new AboutDialog( (Frame) w, title, ClassicEngineInfo.getInstance() );
      } else if ( w instanceof Dialog ) {
        aboutFrame = new AboutDialog( (Dialog) w, title, ClassicEngineInfo.getInstance() );
      } else {
        aboutFrame = new AboutDialog( title, ClassicEngineInfo.getInstance() );
      }
      aboutFrame.pack();
      LibSwingUtil.centerFrameOnScreen( aboutFrame );
    }

    aboutFrame.setVisible( true );
    aboutFrame.requestFocus();
    return true;
  }

}
