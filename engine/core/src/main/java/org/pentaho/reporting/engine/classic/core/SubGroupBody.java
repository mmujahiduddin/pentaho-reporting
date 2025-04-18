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


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubGroupBodyType;

/**
 * A group body that contains a sub-group. The body itself can contain styles and attributes.
 *
 * @author Thomas Morgner
 */
public class SubGroupBody extends GroupBody {
  private Group group;

  public SubGroupBody() {
    setElementType( new SubGroupBodyType() );
    group = new RelationalGroup();
    registerAsChild( group );
  }

  public SubGroupBody( final Group group ) {
    this();
    setGroup( group );
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup( final Group group ) {
    if ( group == null ) {
      throw new NullPointerException( "The group must not be null" );
    }
    if ( group instanceof RelationalGroup == false && group instanceof CrosstabGroup == false ) {
      throw new NullPointerException( "The group must be one of relational-group or crosstab." );
    }
    validateLooping( group );
    if ( unregisterParent( group ) ) {
      return;
    }
    final Group oldGroup = this.group;
    this.group.setParent( null );
    this.group = group;
    this.group.setParent( this );

    notifyNodeChildRemoved( oldGroup );
    notifyNodeChildAdded( this.group );
  }

  protected void removeElement( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException();
    }

    if ( element == group ) {
      this.group.setParent( null );
      this.group = new RelationalGroup();
      this.group.setParent( this );

      notifyNodeChildRemoved( element );
      notifyNodeChildAdded( this.group );
    }
  }

  public void setElementAt( final int position, final Element element ) {
    if ( position != 0 ) {
      throw new IndexOutOfBoundsException();
    }
    setGroup( (Group) element );
  }

  public int getElementCount() {
    return 1;
  }

  public Element getElement( final int index ) {
    if ( index == 0 ) {
      return group;
    }
    throw new IndexOutOfBoundsException();
  }

  /**
   * Clones this Element, the datasource and the private stylesheet of this Element. The clone does no longer have a
   * parent, as the old parent would not recognize that new object anymore.
   *
   * @return a clone of this Element.
   */
  public SubGroupBody clone() {
    final SubGroupBody o = (SubGroupBody) super.clone();
    o.group = (Group) group.clone();
    o.registerAsChild( o.group );
    return o;
  }

  /**
   * Clones the report.
   *
   * @return the clone.
   */
  public SubGroupBody derive( final boolean preserveElementInstanceIds ) {
    final SubGroupBody o = (SubGroupBody) super.derive( preserveElementInstanceIds );
    o.group = (Group) group.derive( preserveElementInstanceIds );
    o.registerAsChild( o.group );
    return o;
  }

}
