package application.ts;

import net.jini.core.entry.Entry;

@SuppressWarnings("serial")
public class Tuple implements Entry {
	public String sender_nickname;
	public String receiver_nickname;
    public String content;
    
    public Tuple() {
    	
    }
}
