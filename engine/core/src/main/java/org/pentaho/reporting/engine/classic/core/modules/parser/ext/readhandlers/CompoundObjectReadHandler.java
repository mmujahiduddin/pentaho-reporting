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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CompoundObjectReadHandler extends BasicObjectReadHandler {
  private HashMap basicObjects;
  private HashMap compoundObjects;

  public CompoundObjectReadHandler( final ObjectDescription objectDescription ) {
    super( objectDescription );
    basicObjects = new HashMap();
    compoundObjects = new HashMap();
  }

  protected HashMap getBasicObjects() {
    return basicObjects;
  }

  protected HashMap getCompoundObjects() {
    return compoundObjects;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final ObjectDescription objectDescription = getObjectDescription();
    final Iterator basicObjectsEntries = basicObjects.entrySet().iterator();
    while ( basicObjectsEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) basicObjectsEntries.next();
      final String name = (String) entry.getKey();
      final BasicObjectReadHandler readHandler = (BasicObjectReadHandler) entry.getValue();
      objectDescription.setParameter( name, readHandler.getObject() );
    }

    final Iterator compoundObjectsEntries = compoundObjects.entrySet().iterator();
    while ( compoundObjectsEntries.hasNext() ) {
      final Map.Entry entry = (Map.Entry) compoundObjectsEntries.next();
      final String name = (String) entry.getKey();
      final CompoundObjectReadHandler readHandler = (CompoundObjectReadHandler) entry.getValue();
      objectDescription.setParameter( name, readHandler.getObject() );
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

    if ( "basic-object".equals( tagName ) ) {
      return handleBasicObject( atts );
    } else if ( "compound-object".equals( tagName ) ) {
      return handleCompoundObject( atts );
    }
    return null;
  }

  protected XmlReadHandler handleBasicObject( final Attributes atts ) throws ParseException {
    final String name = atts.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
    }

    final ClassFactory fact = getClassFactory();
    final ObjectDescription currentOd = getObjectDescription();
    final Class paramDesc = currentOd.getParameterDefinition( name );
    if ( paramDesc == null ) {
      currentOd.getParameterDefinition( name );
      throw new ParseException( "The parameter type for '" + name + "' is not known.", getLocator() );
    }
    final ObjectDescription objectDescription = ObjectFactoryUtility.findDescription( fact, paramDesc, getLocator() );

    final BasicObjectReadHandler readHandler = new BasicObjectReadHandler( objectDescription );
    basicObjects.put( name, readHandler );
    return readHandler;
  }

  protected XmlReadHandler handleCompoundObject( final Attributes atts ) throws ParseException {
    final String name = atts.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
    }

    final ClassFactory fact = getClassFactory();
    final ObjectDescription currentObjDesc = getObjectDescription();
    final Class parameterDefinition = currentObjDesc.getParameterDefinition( name );
    if ( parameterDefinition == null ) {
      throw new ParseException( "No such parameter description: " + name, getLocator() );
    }
    final ObjectDescription objectDescription =
        ObjectFactoryUtility.findDescription( fact, parameterDefinition, getLocator() );

    final CompoundObjectReadHandler readHandler = new CompoundObjectReadHandler( objectDescription );
    compoundObjects.put( name, readHandler );
    return readHandler;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    handleStartParsing( attrs );
  }
}
