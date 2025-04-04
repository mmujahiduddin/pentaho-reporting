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


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.spi.NamingManager;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;

public class JndiConnectionProviderIT {


  @BeforeClass
  public static void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( !NamingManager.hasInitialContextFactoryBuilder() ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  @Test( expected = SQLException.class )
  public void testCreateConnectionWithoutPath() throws SQLException {
    JndiConnectionProvider provider = new JndiConnectionProvider();
    provider.createConnection( "user", "password" );
  }

  @Test( expected = SQLException.class )
  public void testCreateConnectionWithUnknownPath() throws SQLException {
    JndiConnectionProvider provider = new JndiConnectionProvider();
    provider.setConnectionPath( "incorrect" );
    Connection connection = provider.createConnection( "pentaho_user", "password" );
    assertThat( connection, is( notNullValue() ) );
  }

  @Test
  public void testCreateConnection() throws SQLException {
    JndiConnectionProvider provider = new JndiConnectionProvider( "SampleData", "pentaho_user", "password" );
    assertThat( provider.getConnectionPath(), is( equalTo( "SampleData" ) ) );
    assertThat( provider.getUsername(), is( equalTo( "pentaho_user" ) ) );
    assertThat( provider.getPassword(), is( equalTo( "password" ) ) );

    Connection connection = provider.createConnection( null, null );
    assertThat( connection, is( notNullValue() ) );

    provider = new JndiConnectionProvider( "SampleDataAdmin", null, null );
    assertThat( provider.getConnectionPath(), is( equalTo( "SampleDataAdmin" ) ) );
    assertThat( provider.getUsername(), is( nullValue() ) );
    assertThat( provider.getPassword(), is( nullValue() ) );

    connection = provider.createConnection( null, null );
    assertThat( connection, is( notNullValue() ) );
  }

  @Test
  public void testEquals() {
    JndiConnectionProvider provider = new JndiConnectionProvider();
    assertThat( provider.equals( provider ), is( equalTo( true ) ) );
    assertThat( provider.equals( null ), is( equalTo( false ) ) );
    assertThat( provider.equals( "incorrect" ), is( equalTo( false ) ) );

    JndiConnectionProvider newProvider = new JndiConnectionProvider();
    provider.setConnectionPath( "SampleData" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setConnectionPath( "SampleDataAdmin" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setConnectionPath( "SampleData" );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );
    provider.setConnectionPath( null );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setConnectionPath( null );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );

    provider.setPassword( "password" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setPassword( "passwordAdmin" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setPassword( "password" );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );
    provider.setPassword( null );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setPassword( null );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );

    provider.setUsername( "pentaho_user" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setUsername( "admin" );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setUsername( "pentaho_user" );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );
    provider.setUsername( null );
    assertThat( provider.equals( newProvider ), is( equalTo( false ) ) );
    newProvider.setUsername( null );
    assertThat( provider.equals( newProvider ), is( equalTo( true ) ) );
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void testGetConnectionHash() {
    JndiConnectionProvider provider = new JndiConnectionProvider( "test_path", "user_name", null );
    Object result = provider.getConnectionHash();
    assertThat( result, is( instanceOf( List.class ) ) );
    List<Object> list = (List<Object>) result;
    assertThat( list.size(), is( equalTo( 3 ) ) );
    assertThat( (String) list.get( 0 ), is( equalTo( provider.getClass().getName() ) ) );
    assertThat( (String) list.get( 1 ), is( equalTo( "test_path" ) ) );
    assertThat( (String) list.get( 2 ), is( equalTo( "user_name" ) ) );
  }
}
