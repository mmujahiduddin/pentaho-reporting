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


package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionMetaDataBuilder extends MetaDataBuilder<ExpressionMetaDataBuilder> {

  private Class<? extends Expression> impl;
  private Class<?> resultType;

  private LinkedHashMap<String, ExpressionPropertyMetaData> properties;
  private int layoutComputation;

  public ExpressionMetaDataBuilder() {
    this.properties = new LinkedHashMap<String, ExpressionPropertyMetaData>();
    this.resultType = Object.class;
    this.layoutComputation = DefaultExpressionMetaData.NO_LAYOUT_PROCESSOR;
  }

  protected ExpressionMetaDataBuilder self() {
    return this;
  }

  public ExpressionMetaDataBuilder impl( final Class<? extends Expression> expressionClass ) {
    this.impl = expressionClass;
    return self();
  }

  public String getName() {
    if ( impl == null ) {
      return null;
    }
    return impl.getName();
  }

  public ExpressionMetaDataBuilder resultType( final Class<?> resultType ) {
    this.resultType = resultType;
    return self();
  }

  public ExpressionMetaDataBuilder properties( final Map<String, ExpressionPropertyMetaData> properties ) {
    this.properties.putAll( properties );
    return self();
  }

  public ExpressionMetaDataBuilder property( final ExpressionPropertyMetaData p ) {
    this.properties.put( p.getName(), p );
    return self();
  }

  public ExpressionMetaDataBuilder layoutComputation( final int layoutComputation ) {
    this.layoutComputation = layoutComputation;
    return self();
  }

  public Class<? extends Expression> getImpl() {
    return impl;
  }

  public Class<?> getResultType() {
    return resultType;
  }

  public LinkedHashMap<String, ExpressionPropertyMetaData> getProperties() {
    return properties;
  }

  public int getLayoutComputation() {
    return layoutComputation;
  }
}
