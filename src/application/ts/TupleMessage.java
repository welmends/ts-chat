package application.ts;

import net.jini.core.entry.Entry;

@SuppressWarnings("serial")
public class TupleMessage implements Entry {
	public String sender_name;
	public String receiver_name;
	public String room_name;
    public String content;
    
    public TupleMessage() {
    	
    }
}
