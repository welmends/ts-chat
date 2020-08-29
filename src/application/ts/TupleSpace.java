package application.ts;

import java.util.concurrent.Semaphore;

import javafx.util.Pair;
import net.jini.space.JavaSpace;

public class TupleSpace {
	private Lookup lookup;
	private JavaSpace space;
	
    private Boolean is_connected;
    private String nickname;
    private String contact_nickname;
    private String ip;
    private Integer port;
    
    private Semaphore mutex;
    
    // Constructor
	public TupleSpace() {
		this.is_connected = false;
		this.ip = "";
		this.port = -1;
		this.nickname = "";
		this.contact_nickname = "";
		this.mutex = new Semaphore(0);
	}
	
	// Setup
	public void setup(String ip, Integer port, String nickname) {
		this.ip = ip;
		this.port = port;
		this.nickname = nickname;
	}
	
    public Boolean connect(){
    	this.lookup = new Lookup(JavaSpace.class);
		this.space = (JavaSpace) this.lookup.getService();
		System.out.print("Procurando pelo servico JavaSpace... ");
        if (space != null) {
        	System.out.println("SUCESS");
        	this.is_connected = true;
        }else {
        	System.out.println("FAIL");
        	this.is_connected = false;
        }
		return this.is_connected;
    }
    
    public Boolean disconnect(){
    	this.is_connected = false;
    	return false;
    }
    
    // Message Control
    public void send_message(String content) {
        Tuple tuple = new Tuple();
        tuple.sender_nickname = get_nickname();
        tuple.receiver_nickname = get_contact_nickname();
        tuple.content = content;
        try {
			this.space.write(tuple, null, 60 * 1000);
		} catch (Exception e) {
			System.out.println("Error: TupleSpace (send_message)");
		}
    }
    
    public Pair<Boolean, String> receive_message() {
    	Pair<Boolean, String> pair = new Pair<Boolean, String>(false, "<error>");
        try {
        	Tuple template = new Tuple();
        	template.sender_nickname = get_contact_nickname();
        	template.receiver_nickname = get_nickname();
        	Tuple tuple = (Tuple) this.space.take(template, null, 5 * 1000);
        	if(tuple!=null) {
        		pair = new Pair<Boolean, String>(true, tuple.content);
        	}
		} catch (Exception e) {
			System.out.println("Error: TupleSpace (receive_message)");
		}
        return pair;
    }
    
    // Getters
    public Boolean has_connection() {
    	return this.is_connected;
    }
    
    public String get_ip_address() {
    	return this.ip;
    }
    
    public Integer get_port_number() {
    	return this.port;
    }
    
    public String get_nickname() {
    	return this.nickname;
    }
    
    public String get_contact_nickname() {
    	try { mutex.acquire(); } catch (Exception e) {}
    	String nick = this.contact_nickname;
    	try { mutex.release(); } catch (Exception e) {}
    	return nick;
    }
    
    // Setters
    public void set_contact_nickname(String contact_nickname) {
    	try { mutex.acquire(); } catch (Exception e) {}
    	this.contact_nickname = contact_nickname;
    	try { mutex.release(); } catch (Exception e) {}
    }
    
}
