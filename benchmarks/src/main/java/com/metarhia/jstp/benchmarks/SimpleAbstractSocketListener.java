package com.metarhia.jstp.benchmarks;

import com.metarhia.jstp.connection.AbstractSocket.AbstractSocketListener;
import com.metarhia.jstp.core.JSInterfaces.JSObject;

/**
 * Created by lundibundi on 3/16/17.
 */
public class SimpleAbstractSocketListener implements AbstractSocketListener {

  @Override
  public void onConnected() {

  }

  @Override
  public void onMessageReceived(JSObject message) {

  }

  @Override
  public void onConnectionClosed() {

  }

  @Override
  public void onError(Exception e) {

  }
}
