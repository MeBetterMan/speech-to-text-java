import com.alibaba.fastjson.JSONObject;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPClientHandler extends IoHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(TCPClientHandler.class);
    private final JSONObject values;

    public TCPClientHandler(JSONObject values) {
        this.values = values;
    }

    @Override
    public void sessionOpened(IoSession session) {
        System.out.println(values.toJSONString() + "\n");
        session.write(values.toJSONString() + "\n");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        // TODO Auto-generated method stub
        super.exceptionCaught(session, cause);
        System.out.println(cause.getMessage());
        System.out.println("exceptionCaught");
    }

    //收到消息
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        System.out.println(message);
        if (message.toString().contains("res")) {
            System.out.println(message);
        } else {
            PushADPCM.dataDispose(message);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // TODO Auto-generated method stub
        super.messageSent(session, message);

        System.out.println("messageSent");

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        super.sessionClosed(session);
        System.out.println("sessionClosed");
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        super.sessionCreated(session);
        System.out.println("sessionCreated");
    }
}