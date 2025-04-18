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


package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * Drop-Initials are left out for now.
 *
 * @author Thomas Morgner
 * @see http://www.w3.org/TR/css3-linebox/
 */
public class LineStyleKeys {
  /**
   * The 'text-height' property determine the block-progression dimension of the text content area of an inline box.
   */
  public static final StyleKey TEXT_HEIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "text-height", false, true, StyleKey.DOM_ELEMENTS );

  /**
   * Either one of the constants 'normal' or 'none' or a length, percentage or number. The computed value is a result of
   * a computation based on the current line-box and therefore is only valid for that particluar linebox.
   */
  public static final StyleKey LINE_HEIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "line-height", false, true, StyleKey.All_ELEMENTS );

  /**
   * This property determines the line stacking strategy for stacked line boxes within a containing block element. The
   * term 'stack-height' is used in the context of this property description to indicate the block-progression advance
   * for the line boxes.
   */
  public static final StyleKey LINE_STACKING_STRATEGY =
    StyleKeyRegistry.getRegistry().createKey
      ( "line-stacking-strategy", false, true, StyleKey.All_ELEMENTS );

  /**
   * Ruby is not implemented.
   */
  public static final StyleKey LINE_STACKING_RUBY =
    StyleKeyRegistry.getRegistry().createKey
      ( "line-stacking-ruby", false, true, StyleKey.All_ELEMENTS );

  /**
   * This is a character level computation, we ignore that for now.
   */
  public static final StyleKey LINE_STACKING_SHIFT =
    StyleKeyRegistry.getRegistry().createKey
      ( "line-stacking-shift", false, true, StyleKey.All_ELEMENTS );

  public static final StyleKey BASELINE_SHIFT =
    StyleKeyRegistry.getRegistry().createKey
      ( "baseline-shift", false, true, StyleKey.All_ELEMENTS );


  /**
   * This is a shorthand property for the 'dominant-baseline', 'alignment-baseline' and 'alignment-adjust' properties.
   * It has a different meaning in the context of table cells.
   */
  public static final StyleKey VERTICAL_ALIGN =
    StyleKeyRegistry.getRegistry().createKey
      ( "vertical-align", false, true, StyleKey.All_ELEMENTS );

  public static final StyleKey INLINE_BOX_ALIGN =
    StyleKeyRegistry.getRegistry().createKey
      ( "inline-box-align", false, false, StyleKey.All_ELEMENTS );

  /**
   * DominantBaseLine is not implemented.
   */
  public static final StyleKey DOMINANT_BASELINE =
    StyleKeyRegistry.getRegistry().createKey
      ( "dominant-baseline", false, true, StyleKey.All_ELEMENTS );
  public static final StyleKey ALIGNMENT_BASELINE =
    StyleKeyRegistry.getRegistry().createKey
      ( "alignment-baseline", false, true, StyleKey.All_ELEMENTS );
  public static final StyleKey ALIGNMENT_ADJUST =
    StyleKeyRegistry.getRegistry().createKey
      ( "alignment-adjust", false, true, StyleKey.All_ELEMENTS );

  public static final StyleKey DROP_INITIAL_AFTER_ADJUST =
    StyleKeyRegistry.getRegistry().createKey
      ( "drop-initial-after-adjust", false, false, StyleKey.PSEUDO_FIRST_LETTER );
  public static final StyleKey DROP_INITIAL_AFTER_ALIGN =
    StyleKeyRegistry.getRegistry().createKey
      ( "drop-initial-after-align", false, false, StyleKey.PSEUDO_FIRST_LETTER );

  public static final StyleKey DROP_INITIAL_BEFORE_ADJUST =
    StyleKeyRegistry.getRegistry().createKey
      ( "drop-initial-before-adjust", false, false, StyleKey.PSEUDO_FIRST_LETTER );
  public static final StyleKey DROP_INITIAL_BEFORE_ALIGN =
    StyleKeyRegistry.getRegistry().createKey
      ( "drop-initial-before-align", false, false, StyleKey.PSEUDO_FIRST_LETTER );

  public static final StyleKey DROP_INITIAL_SIZE =
    StyleKeyRegistry.getRegistry().createKey
      ( "drop-initial-size", false, false, StyleKey.PSEUDO_FIRST_LETTER );
  public static final StyleKey DROP_INITIAL_VALUE =
    StyleKeyRegistry.getRegistry().createKey
      ( "drop-initial-value", false, false, StyleKey.PSEUDO_FIRST_LETTER );

  private LineStyleKeys() {
  }
}
