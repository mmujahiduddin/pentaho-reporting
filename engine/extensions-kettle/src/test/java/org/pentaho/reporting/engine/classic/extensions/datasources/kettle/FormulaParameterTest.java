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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;

public class FormulaParameterTest {

  @Test
  public void create() {
    FormulaParameter formulaParameter = FormulaParameter.create( "TEST_REPORT_FIELD_NAME", "TEST_TRANS_PARAM_NAME" );
    assertEquals( "=[TEST_REPORT_FIELD_NAME]", formulaParameter.getFormula() );
  }

  @Test
  public void convert_formula_parameter_to_parameter_mapping() {
    FormulaParameter formulaParameter = new FormulaParameter( "TEST_TRANS_PARAM_NAME", "=[TEST_REPORT_FIELD_NAME]" );
    ParameterMapping[] actualResult = FormulaParameter.convert( new FormulaParameter[] { formulaParameter } );
    assertEquals( 1, actualResult.length );
    assertEquals( "TEST_TRANS_PARAM_NAME", actualResult[0].getAlias() );
    assertEquals( "TEST_REPORT_FIELD_NAME", actualResult[0].getName() );
  }

  @Test
  public void convert_formula_parameter_to_parameter_mapping_1() {
    FormulaParameter formulaParameter = new FormulaParameter( "TEST_TRANS_PARAM_NAME", "=FALSE()" );
    ParameterMapping[] actualResult = FormulaParameter.convert( new FormulaParameter[] { formulaParameter } );
    assertEquals( 1, actualResult.length );
    assertEquals( "TEST_TRANS_PARAM_NAME", actualResult[0].getAlias() );
    assertEquals( "there is no name since =FLASE() function have no parameters", "", actualResult[0].getName() );
  }

  /**
   * Should be able to parse complex functions
   * see PRD-5511
   */
  @Test
  public void convert_formula_parameter_to_parameter_mapping_2() {
    FormulaParameter fp = new FormulaParameter( "TEST_TRANS_PARAM_NAME",
                                                "=CSVTEXT([TEST_REPORT_FIELD_NAME];FALSE();\",\";\"'\")" );
    ParameterMapping[] actualResult = FormulaParameter.convert( new FormulaParameter[] { fp } );
    assertEquals( 1, actualResult.length );
    assertEquals( "TEST_TRANS_PARAM_NAME", actualResult[0].getAlias() );
    assertEquals( "TEST_REPORT_FIELD_NAME", actualResult[0].getName() );
  }

  /**
   * Simulates many parameters passed.
   */
  @Test
  public void convert_formula_parameter_to_parameter_mapping_3() {
    FormulaParameter fp = new FormulaParameter( "par1",
                                                "=CSVTEXT([TEST_REPORT_FIELD_NAME_1];FALSE();\",\";\"'\")" );
    FormulaParameter fp2 = new FormulaParameter( "par2",
                                                 "=ABS([TEST_REPORT_FIELD_NAME_2])" );
    FormulaParameter fp3 = new FormulaParameter( "par3", "=ABS(13)" );
    ParameterMapping[] actualResult = FormulaParameter.convert( new FormulaParameter[] { fp, fp2, fp3} );
    assertEquals( 3, actualResult.length );

    assertEquals( "par1", actualResult[0].getAlias() );
    assertEquals( "TEST_REPORT_FIELD_NAME_1", actualResult[0].getName() );

    assertEquals( "par2", actualResult[1].getAlias() );
    assertEquals( "TEST_REPORT_FIELD_NAME_2", actualResult[1].getName() );

    assertEquals( "par3", actualResult[2].getAlias() );
    assertEquals( "13 for formula =ABS(13) is a static value, not a parameter", "", actualResult[2].getName() );
  }

  /**
   * Formula with 1+ parameters and string value.
   * Currently returns only one result, if formula contains multiple parameter
   * references seems we only have one in return...
   */
  @Test
  public void convert_formula_parameter_to_parameter_mapping_4() {
    FormulaParameter fp1 = new FormulaParameter( "par1",
                                                  "=CONCATENATE([GUID]; \"Hello world!\"; [GUIH])" );
    ParameterMapping[] actualResult = FormulaParameter.convert( new FormulaParameter[] { fp1} );
  }

  @Test
  public void convert_parameter_mapping_to_formula_parameter() {
    ParameterMapping parameterMapping = new ParameterMapping( "TEST_REPORT_FIELD_NAME", "TEST_TRANS_PARAM_NAME" );
    FormulaParameter[] actualResult = FormulaParameter.convert( new ParameterMapping[] { parameterMapping } );
    FormulaParameter expectedResult = new FormulaParameter( "TEST_TRANS_PARAM_NAME", "=[TEST_REPORT_FIELD_NAME]" );
    assertArrayEquals( new FormulaParameter[] { expectedResult }, actualResult );
  }

}
