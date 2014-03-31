package com.orionsword.qpid;

import org.apache.qpid.AMQException;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.jms.Connection;
import org.apache.qpid.url.URLSyntaxException;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Message;

/**
 * Class intended to abstract QPID message interchange.
 * <p>
 *     Each object will reprent a channel of communication between the client code
 *     and a SERVER-EXCHANGE-SUBJECT side providing method to easily:
 *     <ul>
 *     <li>Listen to messages.</li>
 *     <li>Emit new messages.</li>
 *     </ul>
 * </p>
 * @author Pablo.Francisco.Pérez.Hidalgo
 * @since 2013-03-22
 */
public class QPIDManager {

    public QPIDManager(String exchange,String ipUrl, int tcpPort) {
        this(exchange, null, ipUrl, tcpPort, null, null);
    }

    public QPIDManager(String exchange, String subject, String ipUrl, int tcpPort)
    {
        this(exchange,subject,ipUrl,tcpPort,null,null);
    }

    public QPIDManager(String exchange,String ipUrl, int tcpPort,String user, String password) {
        this(exchange, null, ipUrl, tcpPort, user, password);
    }

    public QPIDManager(String exchange, String subject, String ipUrl, int tcpPort, String user, String password) {
        log = new QPIDLogger();
        receivers = new HashMap<String, MessageConsumer>();
        try {
            String connectionString = "amqp://" + (user==null?defaultUser:user) +
                    ":" +
                    ((user==null && password == null)?defaultPassword:password)
                    + "@ALL_GS/?brokerlist='tcp://" + ipUrl + ":"+Integer.toString(tcpPort) + "'";
            con = new AMQConnection(connectionString);
            con.start();
            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest4subject = new AMQAnyDestination("ADDR:" + exchange + (subject == null?"":("/"+subject)));
            sender = session.createProducer(dest4subject);
            sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (AMQException ex) {
            log.error(ex.getMessage());
        } catch (URLSyntaxException ex) {
            log.error(ex.getMessage());
        } catch (JMSException ex) {
            log.error(ex.getMessage());
        } catch (URISyntaxException ex) {
            log.error(ex.getMessage());
        }
    }

    public boolean sendText(String msgText)
    {
        if(msgText == null) return false;
        TextMessage m;
        try {
            m = session.createTextMessage(msgText);
        } catch (JMSException ex) {
            log.warn(ex.getMessage());
            return false;
        }
        return send(m);
    }

    public boolean sendMap(Map<String,Object> data)
    {
        if(data == null) return false;
        MapMessage m;
        try {
            m = session.createMapMessage();
            for(Map.Entry<String,Object> e: data.entrySet())
            {
                m.setObject(e.getKey(), e.getValue());
            }
        } catch (JMSException ex) {
            log.warn(ex.getMessage());
            return false;
        }
        return send(m);
    }

    private boolean send(Message m)
    {
        try {
            sender.send(m);
        } catch (JMSException ex) {
            log.warn("send:"  + ex.getMessage());
            return false;
        }
        return true;
    }

    //Listening manager

    public boolean startMessageListening(String label, MessageProcessor processCode)
    {
        if(receivers.containsKey(label)) return false;
        try {
            MessageConsumer mc = session.createConsumer(dest4subject);
            mc.setMessageListener(processCode);
            receivers.put(label, mc);
        } catch (JMSException ex) {
            return false;
        }
        return true;
    }

    public boolean isListening(String id)
    {
        return receivers.containsKey(id);
    }

    public boolean stopListening(String label)
    {
        MessageConsumer mc = receivers.get(label);
        if(mc == null) return false;
        try {
            mc.close();
        } catch (JMSException ex) {
            log.error("stopListening:\t"+ex.getMessage());
            return false;
        }
        receivers.remove(label);
        return true;
    }

    public Message blockingReceive(String label)
    {
        MessageConsumer mc = receivers.get(label);
        if(mc == null) return null;
        try {
            return mc.receive();
        } catch (JMSException ex) {
            return null;
        }
    }

    //End listning manager

    public void killAllThreads()
    {
        try {
            sender.close();
        } catch (JMSException ex) {
            ;
        }
        for(Map.Entry<String, MessageConsumer> e: receivers.entrySet())
        {
            try {
                e.getValue().close();
            } catch (JMSException ex) {
                log.error("stopListening:\t"+ex.getMessage());
                break;
            }
        }
        try {
            session.close();
            con.stop();
            con.close();
        } catch (JMSException ex) {
            log.error("killAllThreads:\t"+ex.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        killAllThreads();
        super.finalize();
    }

    private Session session;
    private Connection con;
    private Destination dest4subject;
    private javax.jms.MessageProducer sender;
    //private MessageConsumer receiver;
    private QPIDLogger log;
    private String defaultUser = "guest";
    private String defaultPassword = "guest";
    private Map<String, MessageConsumer> receivers;
}
