package com.metarhia.jstp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.metarhia.jstp.connection.Connection;
import com.metarhia.jstp.connection.SessionPolicy;
import com.metarhia.jstp.connection.SimpleSessionPolicy;
import com.metarhia.jstp.transport.Transport;

public class TestUtils {

  public static void simulateConnect(Connection connection, Transport transport) {
    when(transport.isConnected()).thenReturn(true);
    connection.onTransportConnected();
  }

  public static void simulateDisconnect(Connection connection, Transport transport) {
    when(transport.isConnected()).thenReturn(false);
    connection.onTransportClosed();
  }

  public static ConnectionSpy createConnectionSpy() {
    return createConnectionSpy(null, true);
  }

  public static ConnectionSpy createConnectionSpy(Transport transport,
                                                  boolean transportConnected) {
    if (transport == null) {
      transport = mock(Transport.class);
      when(transport.isConnected()).thenReturn(transportConnected);
    }
    SessionPolicy sessionPolicy = new SimpleSessionPolicy();
    Connection connection = spy(new Connection(transport, sessionPolicy));
    sessionPolicy.setConnection(connection);
    return new ConnectionSpy(connection, transport);
  }

  public static class ConnectionSpy {

    public Connection connection;

    public Transport transport;

    public ConnectionSpy(Connection connection, Transport transport) {
      this.connection = connection;
      this.transport = transport;
    }
  }

  public static class TestData<T, F> {

    public T input;
    public F expected;

    public TestData(T input, F expected) {
      this.input = input;
      this.expected = expected;
    }
  }
}
