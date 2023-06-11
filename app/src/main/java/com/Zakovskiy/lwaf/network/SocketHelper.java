package com.Zakovskiy.lwaf.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;

import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.Config;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class SocketHelper implements Serializable {

    public ScheduledFuture lwafCheckConnectionTimeoutFuture;
    private static final SocketHelper sSocketHelper = new SocketHelper(Config.ADDRESS, Config.PORT);
    public String lwafServerIp;
    public int lwafServerPort;
    public Socket lwafSocket;
    public InputStream lwafInputStream = null;
    private Set<SocketListener> lwafSocketListeners = Collections.newSetFromMap(new ConcurrentHashMap());
    public boolean lwafStopReceiver = false;
    public boolean lwafSocketConnected = false;
    public boolean lwafEnableCheckerConnectionTimer = false;

    private SocketHelper(String ip, int port) {
        this.lwafServerIp = ip;
        this.lwafServerPort = port;
    }

    public static SocketHelper getSocketHelper() {
        return sSocketHelper;
    }

    public interface SocketListener {
        void onConnected();

        void onDisconnected();

        void onReceive(JsonNode json);

        void onReceiveError(String str);
    }


    public void sendData(JSONObject obj) {
        sendData(obj.toString());
    }

    public void sendData(final String str) {
        Logs.debug("SEND TO SOCKET: " + str);
        if (this.lwafSocket != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        new PrintWriter(new BufferedWriter(new OutputStreamWriter(SocketHelper.this.lwafSocket.getOutputStream())), true).println(str);
                    } catch (IOException e) {
                        Logs.debug("SEND TO SOCKET ERROR: " + e.getMessage());
                        if (e.getMessage().contains("Socket is closed")) {
                            SocketHelper.this.sendDisconnectedToSubscribers();
                        }
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void startReceiver() {
        this.lwafStopReceiver = false;
        if (this.lwafSocket != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        InputStream inputStream = SocketHelper.this.lwafSocket.getInputStream();
                        DataInputStream dataInputStream = new DataInputStream(inputStream);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        while (!SocketHelper.this.lwafStopReceiver) {
                            int availableBytes = dataInputStream.available();
                            if (availableBytes > 0) {
                                byte[] buffer = new byte[availableBytes];
                                int bytesRead = dataInputStream.read(buffer);
                                if (bytesRead != -1) {
                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                    processReceivedData(byteArrayOutputStream);
                                }
                            }
                        }

                        Logs.debug("SOCKET RECEIVER STOP");
                    } catch (Exception e) {
                        Logs.debug("startReceiver DISCONNECT");
                        SocketHelper.this.socketDisconnect(false);
                        e.printStackTrace();
                    }
                }

                private void processReceivedData(ByteArrayOutputStream byteArrayOutputStream) throws IOException, JSONException {
                    byte[] data = byteArrayOutputStream.toByteArray();
                    int startIndex = 0;
                    int endIndex;
                    while ((endIndex = findNextMessageIndex(data, startIndex)) != -1) {
                        byte[] messageBytes = Arrays.copyOfRange(data, startIndex, endIndex);
                        String message = new String(messageBytes);
                        processMessage(message);
                        startIndex = endIndex + 1;
                    }

                    byteArrayOutputStream.reset();
                    if (startIndex < data.length) {
                        byteArrayOutputStream.write(data, startIndex, data.length - startIndex);
                    }
                }

                private int findNextMessageIndex(byte[] data, int startIndex) {
                    for (int i = startIndex; i < data.length; i++) {
                        if (data[i] == '\n') {
                            return i;
                        }
                    }
                    return -1;
                }

                private void processMessage(String message) throws JSONException {
                    JsonNode convertJsonStringToJsonNode = JsonUtils.convertJsonStringToJsonNode(message.trim());
                    if (convertJsonStringToJsonNode == null) {
                        if (!message.equals("p")) {
                            SocketHelper.this.sendErrorToSubscribers("WRONG JSON ERROR");
                        }
                    } else {
                        Logs.debug("READ FROM SOCKET: " + message);
                        SocketHelper.this.sendToSubscribers(convertJsonStringToJsonNode);
                    }
                }
            }).start();
        }
    }


    public void subscribe(SocketListener socketListener) {
        this.lwafSocketListeners.add(socketListener);
    }

    public void unsubscribe(Object obj) {
        Logs.info("UNSUB" + obj.getClass().toString());
        if (obj instanceof SocketListener) {
            Iterator<SocketListener> it = this.lwafSocketListeners.iterator();
            while (it.hasNext()) {
                if (it.next().equals(obj)) {
                    it.remove();
                }
            }
            return;
        }

        throw new IllegalArgumentException("UnSubscriber must be an instance of SocketListener");
    }

    public void setEnableCheckerConnectionTimer(boolean z) {
        this.lwafEnableCheckerConnectionTimer = z;
    }

    public void socketConnect() {
        this.lwafStopReceiver = false;
        new Thread(new Runnable() {
            public void run() {
                if (SocketHelper.this.lwafCheckConnectionTimeoutFuture != null) {
                    SocketHelper.this.lwafCheckConnectionTimeoutFuture.cancel(false);
                }
                try {
                    Logs.debug("socketConnect TRY TO CONNECT");
                    if (SocketHelper.this.lwafSocket == null || SocketHelper.this.lwafSocket.isClosed()) {
                        Socket unused = SocketHelper.this.lwafSocket = new Socket();
                    }
                    SocketHelper.this.lwafSocket.connect(new InetSocketAddress(SocketHelper.this.lwafServerIp, SocketHelper.this.lwafServerPort), Config.LOADING_DIALOG_SHOWING_TIMEOUT_MILLIS);
                    SocketHelper.this.lwafSocket.setKeepAlive(true);
                    if (SocketHelper.this.lwafSocket.isConnected()) {
                        Logs.debug("socketConnect isConnected");
                        boolean unused2 = SocketHelper.this.lwafSocketConnected = true;
                        SocketHelper.this.startReceiver();
                        SocketHelper.this.sendConnectedToSubscribers();
                    } else {
                        Logs.debug("socketConnect is_not_Connected");
                        SocketHelper.this.sendErrorToSubscribers("CONNECTION ERROR");
                        SocketHelper.this.socketDisconnect(false);
                    }
                } catch (IOException unused4) {
                    Logs.debug("socketConnect CONNECTION TRY UNSUCCESSFUL");
                    SocketHelper.this.sendErrorToSubscribers("CONNECTION TRY UNSUCCESSFUL");
                    SocketHelper.this.socketDisconnect(false);
                } catch (Throwable th) {
                    Logs.debug("socketConnect startCheckerConnectionTimer");
                    throw th;
                }
                Logs.debug("socketConnect startCheckerConnectionTimer");
            }
        }).start();
    }

    public void socketDisconnect(boolean z) {
        ScheduledFuture scheduledFuture;
        stopReceiver();
        if (z && (scheduledFuture = this.lwafCheckConnectionTimeoutFuture) != null) {
            scheduledFuture.cancel(false);
        }
        Socket socket = this.lwafSocket;
        if (socket != null) {
            try {
                socket.close();
                this.lwafSocketConnected = false;
                sendDisconnectedToSubscribers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendConnectedToSubscribers() {
        for (SocketListener onConnected : this.lwafSocketListeners) {
            onConnected.onConnected();
        }
    }

    public void sendToSubscribers(JsonNode jsonNode) throws JSONException {
        for (SocketListener onReceive : this.lwafSocketListeners) {
            onReceive.onReceive(jsonNode);
        }
    }

    public void sendErrorToSubscribers(String str) {
        for (SocketListener onReceiveError : this.lwafSocketListeners) {
            onReceiveError.onReceiveError(str);
        }
    }

    public void sendDisconnectedToSubscribers() {
        for (SocketListener onDisconnected : this.lwafSocketListeners) {
            onDisconnected.onDisconnected();
        }
    }

    private void stopReceiver() {
        this.lwafStopReceiver = true;
    }
}
