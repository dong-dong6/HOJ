package top.hcode.hoj.remoteJudge.task.Impl.acwing;

import cn.hutool.core.util.ReUtil;
import org.java_websocket.client.WebSocketClient;

import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

public class WebSocketWithCookies extends WebSocketClient {

    private Map<String, String> cookies;
    public String code;
    public String result;
    private StatusListener statusListener;
    public WebSocketWithCookies(URI serverUri,String code,Map<String, String> cookies) {
        super(serverUri);
        this.cookies = cookies;
        this.code = code;
    }

    public void setStatusListener(StatusListener listener) {
        this.statusListener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Opened connection");
        send(code);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received: " + message);
        String s = ReUtil.get("status\":\\s*\"(\\w+)", message, 1);
        result = s;
        //System.out.println("当前判题状态" + s);
        if (statusListener != null) {
            statusListener.onStatusReceived(s);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void connect() {
        if (cookies != null && !cookies.isEmpty()) {
            String cookieHeader = cookies.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .reduce((c1, c2) -> c1 + ";" + c2)
                    .orElse("");
            System.out.println(cookieHeader);
            addHeader("Cookie", cookieHeader);
            addHeader("Origin", "https://www.acwing.com");
        }
        super.connect();
    }

    interface StatusListener {
        void onStatusReceived(String status);
    }
}