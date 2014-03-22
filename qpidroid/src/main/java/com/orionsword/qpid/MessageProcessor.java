package com.orionsword.qpid;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Interface for QPID message processor classes
 * <p>
 *     Objects of classes implementing this interface will be passed
 *     to the QPID manager so their {@link com.orionsword.qpid.MessageProcessor#onMessage(Message)}  processMessage}
 * </p>
 * @author Pablo.Francisco.Pérez.Hidalgo
 * @since 2013-03-22
 */
public interface MessageProcessor extends MessageListener {
    @Override
    void onMessage(Message message);
}
