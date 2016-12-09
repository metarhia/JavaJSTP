package com.metarhia.jstp.Connection;

public abstract class AbstractSocket {

    protected AbstractSocketListener socketListener;

    public AbstractSocket(AbstractSocketListener listener) {
        this.socketListener = listener;
    }

    public abstract void openConnection(String handshakeMessage);

    public abstract void sendMessage(String message);

    public abstract void pause();

    public abstract void pause(boolean clear);

    public abstract void resume();

    public abstract void resume(boolean clear);

    public abstract void close();

    public abstract String getHost();

    public abstract void setHost(String host);

    public abstract int getPort();

    public abstract void setPort(int port);

    public abstract boolean isSSLEnabled();

    public abstract void setSSLEnabled(boolean enabled);

    public void setSocketListener(AbstractSocketListener listener) {
        this.socketListener = listener;
    }

    public abstract boolean isConnected();

    public abstract boolean isClosed();

    public interface AbstractSocketListener {
        void onConnect();

        void onMessageReceived(String message);

        void onConnectionClosed(Exception... e);
    }
}