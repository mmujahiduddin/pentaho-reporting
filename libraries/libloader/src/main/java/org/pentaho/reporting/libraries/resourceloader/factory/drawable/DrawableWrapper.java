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


package org.pentaho.reporting.libraries.resourceloader.factory.drawable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DrawableWrapper {
  private static final Log logger = LogFactory.getLog( DrawableWrapper.class );
  private static final Map<String, Boolean> drawables = Collections.synchronizedMap( new HashMap<String, Boolean>() );

  private Object backend;
  private Method drawMethod;
  private Method getPreferredSizeMethod;
  private Method isKeepAspectRatioMethod;
  private static final Object[] EMPTY_ARGS = new Object[ 0 ];
  private static final Class[] EMPTY_PARAMS = new Class[ 0 ];
  private static final Class[] PARAMETER_TYPES = new Class[] { Graphics2D.class, Rectangle2D.class };

  public DrawableWrapper( final Object maybeDrawable ) {
    if ( maybeDrawable == null ) {
      throw new NullPointerException( "Drawable must not be null" );
    }
    if ( maybeDrawable instanceof DrawableWrapper ) {
      throw new IllegalArgumentException( "Cannot wrap around a drawable-wrapper" );
    }
    final Class<?> aClass = maybeDrawable.getClass();
    try {
      drawMethod = aClass.getMethod( "draw", PARAMETER_TYPES );
      final int modifiers = drawMethod.getModifiers();
      if ( Modifier.isPublic( modifiers ) == false ||
        Modifier.isAbstract( modifiers ) ||
        Modifier.isStatic( modifiers ) ) {
        if ( logger.isWarnEnabled() ) {
          logger.warn( String.format( "DrawMethod is not valid: %s#%s", aClass, drawMethod ) ); // NON-NLS
        }
        drawMethod = null;
      }
    } catch ( NoSuchMethodException e ) {
      // ignore exception
      if ( logger.isWarnEnabled() ) {
        logger.warn( String.format( "The object is not a drawable: %s", aClass ) ); // NON-NLS
      }
      drawMethod = null;
    }

    if ( drawMethod != null ) {
      try {
        isKeepAspectRatioMethod = aClass.getMethod( "isPreserveAspectRatio", EMPTY_PARAMS );
        final int modifiers = isKeepAspectRatioMethod.getModifiers();
        if ( Modifier.isPublic( modifiers ) == false ||
          Modifier.isAbstract( modifiers ) ||
          Modifier.isStatic( modifiers ) ||
          Boolean.TYPE.equals( isKeepAspectRatioMethod.getReturnType() ) == false ) {
          isKeepAspectRatioMethod = null;
        }
      } catch ( NoSuchMethodException e ) {
        // ignored ..
      }

      try {
        getPreferredSizeMethod = aClass.getMethod( "getPreferredSize", EMPTY_PARAMS );
        final int modifiers = getPreferredSizeMethod.getModifiers();
        if ( Modifier.isPublic( modifiers ) == false ||
          Modifier.isAbstract( modifiers ) ||
          Modifier.isStatic( modifiers ) ||
          Dimension.class.isAssignableFrom( getPreferredSizeMethod.getReturnType() ) == false ) {
          getPreferredSizeMethod = null;
        }
      } catch ( NoSuchMethodException e ) {
        // ignored ..
      }
    }
    backend = maybeDrawable;
  }

  public Object getBackend() {
    return backend;
  }

  public void draw( final Graphics2D g2, final Rectangle2D bounds ) {
    if ( drawMethod == null ) {
      return;
    }

    try {
      drawMethod.invoke( backend, g2, bounds );
    } catch ( Throwable e ) {
      if ( logger.isDebugEnabled() ) {
        logger.warn( "Invoking draw failed:", e ); // NON-NLS
      }
    }
  }

  /**
   * Returns the preferred size of the drawable. If the drawable is aspect ratio aware, these bounds should be used to
   * compute the preferred aspect ratio for this drawable.
   *
   * @return the preferred size.
   */
  public Dimension getPreferredSize() {
    if ( getPreferredSizeMethod == null ) {
      return null;
    }

    try {
      return (Dimension) getPreferredSizeMethod.invoke( backend, EMPTY_ARGS );
    } catch ( Throwable e ) {
      if ( logger.isWarnEnabled() ) {
        logger.warn( "Invoking getPreferredSize failed:", e ); // NON-NLS
      }
      return null;
    }
  }

  /**
   * Returns true, if this drawable will preserve an aspect ratio during the drawing.
   *
   * @return true, if an aspect ratio is preserved, false otherwise.
   */
  public boolean isPreserveAspectRatio() {
    if ( isKeepAspectRatioMethod == null ) {
      return false;
    }

    try {
      return Boolean.TRUE.equals( isKeepAspectRatioMethod.invoke( backend, EMPTY_ARGS ) );
    } catch ( Throwable e ) {
      if ( logger.isWarnEnabled() ) {
        logger.warn( "Invoking isKeepAspectRatio failed:", e ); // NON-NLS
      }
      return false;
    }
  }

  public static boolean isDrawable( final Object maybeDrawable ) {
    if ( maybeDrawable == null ) {
      throw new NullPointerException( "A <null> value can never be a drawable." );
    }
    final String key = maybeDrawable.getClass().getName();
    final Boolean result = drawables.get( key );
    if ( result != null ) {
      return result.booleanValue();
    }
    final boolean b = computeIsDrawable( maybeDrawable );
    if ( b == true ) {
      drawables.put( key, Boolean.TRUE );
    } else {
      drawables.put( key, Boolean.FALSE );
    }
    return b;
  }

  private static boolean computeIsDrawable( final Object maybeDrawable ) {
    final Class<?> aClass = maybeDrawable.getClass();
    try {
      final Method drawMethod = aClass.getMethod( "draw", PARAMETER_TYPES );
      final int modifiers = drawMethod.getModifiers();
      return ( Modifier.isPublic( modifiers ) &&
        Modifier.isAbstract( modifiers ) == false &&
        Modifier.isStatic( modifiers ) == false );
    } catch ( NoSuchMethodException e ) {
      // ignore exception
      return false;
    }
  }
}
