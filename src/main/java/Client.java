import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {

    public Client(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public Client(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //send("Hello, it is me. Mario :)");
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason+" remote:" + remote);
    }

    public static LinkedList<Object> RspQue = new LinkedList<>();

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
        if(!message.isEmpty() ){
            Client.RspQue.addFirst(message);
        }else{
            System.out.println("引擎返回空数据");
        }
        JSONArray hypotheses = JSON.parseObject(message.toString()).getJSONObject("result").getJSONArray("hypotheses");
        StringBuilder resWords = new StringBuilder();
        for (Object h : hypotheses) {
            try {
                resWords.append(URLDecoder.decode(JSON.parseObject(h.toString()).getString("transcript"), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        System.out.println("翻译汉字为：" + resWords.toString());
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    public static WebSocketClient client = null;

    public static void sendAudioMsg(byte[] audio) throws URISyntaxException, InterruptedException {
        if (client == null || client.isClosed()) {
            client = new Client(new URI(PropertiesUtil.readKeyFromConfig("ws_remote_server")));
            client.connectBlocking();
        }else if (client.getReadyState().equals(ReadyState.CLOSING) || client.getReadyState().equals(ReadyState.CLOSED)){
            client.reconnectBlocking();
        }
        client.send(audio);
    }

    public static void main(String[] args) {

        Client client = null;
        try {
            client = new Client(new URI(PropertiesUtil.readKeyFromConfig("ws_server")));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.connect();

        client.send("fff");
    }
}