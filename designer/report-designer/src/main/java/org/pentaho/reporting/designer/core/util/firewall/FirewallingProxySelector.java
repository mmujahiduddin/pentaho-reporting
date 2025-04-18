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


package org.pentaho.reporting.designer.core.util.firewall;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.UtilMessages;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Todo: Document me!
 * <p/>
 * Date: 11.11.2009 Time: 21:50:46
 *
 * @author Thomas Morgner.
 */
public class FirewallingProxySelector extends ProxySelector {
  private ProxySelector defaultSelector;
  private static final String LOCALHOST = "localhost";
  private static final String LOCALHOST_IP = "127.0.0.1";
  private static final String LOCALHOST_LOCALDOMAIN = "localhost.localdomain";

  public FirewallingProxySelector( final ProxySelector defaultSelector ) {
    this.defaultSelector = defaultSelector;
  }

  /**
   * Selects all the applicable proxies based on the protocol to access the resource with and a destination address to
   * access the resource at. The format of the URI is defined as follow: <UL> <LI>http URI for http connections</LI>
   * <LI>https URI for https connections <LI>ftp URI for ftp connections</LI> <LI><code>socket://host:port</code><br>
   * for tcp client sockets connections</LI> </UL>
   *
   * @param uri The URI that a connection is required to
   * @return a List of Proxies. Each element in the the List is of type {@link java.net.Proxy Proxy}; when no proxy is
   * available, the list will contain one element of type {@link java.net.Proxy Proxy} that represents a direct
   * connection.
   * @throws IllegalArgumentException if either argument is null
   */
  public List<Proxy> select( final URI uri ) {
    if ( WorkspaceSettings.getInstance().isOfflineMode() ) {
      if ( LOCALHOST.equalsIgnoreCase( uri.getHost() ) ) {
        return selectDefault( uri );
      }
      if ( LOCALHOST_IP.equalsIgnoreCase( uri.getHost() ) ) {
        return selectDefault( uri );
      }
      if ( LOCALHOST_LOCALDOMAIN.equalsIgnoreCase( uri.getHost() ) ) {
        return selectDefault( uri );
      }

      throw new IllegalArgumentException(
        UtilMessages.getInstance().getString( "FirewallingProxySelector.FilterMessage" ) );
    }

    return selectDefault( uri );
  }

  private List<Proxy> selectDefault( final URI uri ) {
    if ( defaultSelector != null ) {
      return defaultSelector.select( uri );
    }

    final List<Proxy> list = new ArrayList<Proxy>();
    list.add( Proxy.NO_PROXY );
    return list;
  }

  /**
   * Called to indicate that a connection could not be established to a proxy/socks server. An implementation of this
   * method can temporarily remove the proxies or reorder the sequence of proxies returned by select(String, String),
   * using the address and they kind of IOException given.
   *
   * @param uri The URI that the proxy at sa failed to serve.
   * @param sa  The socket address of the proxy/SOCKS server
   * @param ioe The I/O exception thrown when the connect failed.
   * @throws IllegalArgumentException if either argument is null
   */
  public void connectFailed( final URI uri, final SocketAddress sa, final IOException ioe ) {
    defaultSelector.connectFailed( uri, sa, ioe );
  }
}
