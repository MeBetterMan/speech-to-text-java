import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;

public class PullADPCM {

    public static void main(String[] args) throws URISyntaxException {

        //启动websocket server
        StartWebsocketUtil server = new StartWebsocketUtil();
        server.start();
        //启动匿名线程 向所有连接的客户端回传数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String rsp = "";
                    try {
                         rsp = Client.RspQue.removeLast().toString();
                    }catch (Exception e){
                        //如果没有数据 就等待生产者生产数据
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        //System.out.println("等待生成数据");
                    }
                    for (WebSocket client : SimpleServer.Clients) {
                        if(client.isClosed()){
                            continue;
                        }
                        //System.out.println("发送给前端:" + rsp);
                        client.send(rsp);
                    }
                }
            }
        }).start();

        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(30000);
        TextLineCodecFactory lineCodec = new TextLineCodecFactory(Charset.forName("UTF-8"));
        lineCodec.setDecoderMaxLineLength(1024 * 10240);
        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(lineCodec));
        JSONObject params = new JSONObject();
        params.put("msg", "subscribe");
        params.put("device", Integer.parseInt(PropertiesUtil.readKeyFromConfig("device_id")));
        connector.setHandler(new TCPClientHandler(params));
        connector.connect(new InetSocketAddress(PropertiesUtil.readKeyFromConfig("TCP_IP"), Integer.parseInt(PropertiesUtil.readKeyFromConfig("TCP_PORT"))));
    }
}