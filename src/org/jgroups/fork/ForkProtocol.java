package org.jgroups.fork;

import org.jgroups.Event;
import org.jgroups.Message;
import org.jgroups.protocols.FORK;
import org.jgroups.stack.Protocol;

/**
 * Acts as bottom protool of a fork-stack. Knows about the fork-stack-id and inserts it into the ForkHeader of messages
 * sent down the stack
 * @author Bela Ban
 * @since  3.4
 */
public class ForkProtocol extends Protocol {
    protected final String       fork_stack_id;

    public ForkProtocol(String fork_stack_id) {
        this.fork_stack_id=fork_stack_id;
    }

    public Object down(Event evt) {
        if(evt.getType() == Event.MSG) {
            Message msg=(Message)evt.getArg();
            FORK.ForkHeader hdr=(FORK.ForkHeader)msg.getHeader(FORK.ID);
            if(hdr == null)
                msg.putHeader(FORK.ID, hdr=new FORK.ForkHeader(fork_stack_id, null));
            else
                hdr.setForkStackId(fork_stack_id);
        }
        return down_prot.down(evt);
    }
}