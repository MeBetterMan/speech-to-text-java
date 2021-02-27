import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class StartWebsocketUtil extends Thread {
    //启动

    @Override
    public void run() {
        String host = PropertiesUtil.readKeyFromConfig("ws_local_server_ip");
        int port = Integer.parseInt(PropertiesUtil.readKeyFromConfig("ws_local_port"));

        WebSocketServer server = new SimpleServer(new InetSocketAddress(host, port));
        server.run();
    }
}
