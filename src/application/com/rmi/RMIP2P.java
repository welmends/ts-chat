package application.com.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

import application.com.P2PInterface;
import application.com.P2PConstants;

public class RMIP2P extends UnicastRemoteObject implements P2PInterface, RMIP2PInterface, Runnable {
	
	private static final long serialVersionUID = 0L;
	private static RMIP2PInterface rmi_client;
	
	private Semaphore status_mutex, chat_mutex;
	
    private String server_link, client_link;
    
    private String chat_msg;
    private Boolean chat_stack_full;
    
    private Boolean connect_status, retrieve_status;
    private String peer_type;
    private String id;
    private String ip, local_ip;
    private int port;
    
	public RMIP2P() throws RemoteException{
		super();
		
		this.status_mutex = new Semaphore(1);
		this.chat_mutex = new Semaphore(1);
		
		this.server_link = "";
		this.client_link = "";
		
		this.chat_stack_full = false;
		
		this.connect_status = false;
		this.retrieve_status = false;
		
		this.peer_type = "";
		this.id = "";
		this.ip = "";
		this.local_ip = "";
		this.port = -1;
	}

	// P2P Interface Implementation - Technology
	@Override
	public void set_technology(final String technology_name) {
		return;
	}
	
	@Override
	public String get_technology_name() {
		return P2PConstants.RMI;
	}
	
	// P2P Interface Implementation - Connection
	@Override
	public void setup(String id, String ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
	}
	
	@Override
	public void setup(String id, String ip, String local_ip, int port) {
		this.id = id;
		this.ip = ip;
		this.local_ip = local_ip;
		this.port = port;
	}
	
	@Override
	public Boolean connect() {
        try {
        	server_link = "rmi://"+ip+":"+String.valueOf(port)+"/"+id+P2PConstants.CHAT_RMI_SERVER_NAME;
        	int length = Naming.list(server_link).length;
        	if(length==0) {
    			peer_type = "server";
    			bind();
    			return true;
    		}
        	else if(length==1) {
        		peer_type = "client";
    			server_link = "rmi://"+local_ip+":"+String.valueOf(port)+"/"+id+P2PConstants.CHAT_RMI_CLIENT_NAME;
    			client_link = "rmi://"+ip+":"+String.valueOf(port)+"/"+id+P2PConstants.CHAT_RMI_SERVER_NAME;
    			bind();
    			lookup();
    			RMIP2P.rmi_client.call_server_lookup(local_ip);
    			return true;
    		}else {
    			return false;
    		}
        } catch(Exception e){
        	System.out.println("[rmi][connect method]");
        	System.out.println(e);
        	return false;
        }
	}

	@Override
	public Boolean disconnect() {
		try {
			set_connect_status(false);
			set_retrieve_status(false);
			try {
				RMIP2P.rmi_client.call_peer_disconnect();
			} catch (Exception e) {
				System.out.println(e);
			}
			unbind();
			return true;
		} catch (Exception e) {
			System.out.println("[rmi][disconnect method]");
			System.out.println(e);
			return false;
		}
	}
	
	// P2P Interface Implementation - Thread
	@Override
	public void thread_call() {
		return;
	}
	
	@Override
    public void run(){
		try {
            while(true){
            	Thread.sleep(P2PConstants.THREAD_SLEEP_TIME_MILLIS);
            	if(is_connected() == false) {
            		throw new Exception("peer disconnect_status");
            	}
            	else {
            		RMIP2P.rmi_client.call_peer_test_connection();
            	}
            }
        } catch(Exception e) {
        	System.out.println("[rmi][run method]");
            System.out.println(e);
            connect();
        }
    }
		
	// P2P Interface Implementation - Getters
	@Override
    public String get_peer_type() {
    	return peer_type;
    }
    
	@Override
	public String get_id() {
		return id;
	}
	
	@Override
	public String get_ip_address() {
		return local_ip;
	}
	
	@Override
	public Integer get_port_number() {
		return port;
	}
	
	@Override
    public Boolean is_server() {
    	if(peer_type.equals("server")) {
    		return true;
    	}
    	return false;
    }
    
	@Override
    public Boolean is_client() {
    	if(peer_type.equals("client")) {
    		return true;
    	}
    	return false;
    }
	
	@Override
    public Boolean is_connected() {
		Boolean status = false;
		try {
			status_mutex.acquire();
			status = connect_status;
			status_mutex.release();
			return status;
		} catch (Exception e) {
			System.out.println("[rmi][is_connected method]");
			System.out.println(e);
			return false;
		}
    }
	
	@Override
    public Boolean was_retrieved() {
		Boolean status = false;
		try {
			status_mutex.acquire();
			status = retrieve_status;
			status_mutex.release();
			return status;
		} catch (Exception e) {
			System.out.println("[rmi][was_retrieved method]");
			System.out.println(e);
			return false;
		}
    }
	
 	// P2P Interface Implementation - Setters
	@Override
	public void set_connect_status(Boolean status) {
		try {
			status_mutex.acquire();
			connect_status = status;
			status_mutex.release();
		} catch (Exception e) {
			System.out.println("[rmi][set_connect_status method]");
			System.out.println(e);
		}
	}
	
	@Override
	public void set_retrieve_status(Boolean status) {
		try {
			status_mutex.acquire();
			retrieve_status = status;
			status_mutex.release();
		} catch (Exception e) {
			System.out.println("[rmi][set_retrieve_status method]");
			System.out.println(e);
		}
	}
	
    // P2P Interface Implementation - Chat Stack Full
	@Override
    public Boolean chat_stack_full() {
    	Boolean stack_full = false;
		if(is_connected()==false) {
			return stack_full;
		}
		try {
			chat_mutex.acquire();
			stack_full = chat_stack_full;
			if(stack_full == true) { chat_stack_full = false; }
			chat_mutex.release();
		} catch (Exception e) {
			System.out.println("[rmi][chat_stack_full method]");
			System.out.println(e);
		}
    	return stack_full;
    }
	
	// P2P Interface Implementation - Chat Getter
	@Override
	public String get_chat_msg() {
		return chat_msg;
	}
	
	// P2P Interface Implementation - Calls
	@Override
	public void send_chat_msg_call(String msg) {
		if(is_connected()==false) {
			return;
		}
		try {
			RMIP2P.rmi_client.send_chat_msg(msg);
		} catch (RemoteException e) {
			System.out.println("[rmi][send_chat_msg_call method]");
			System.out.println(e);
		}
    }
	
	// RMI Interface Implementation
	@Override
	public void send_chat_msg(String msg) {
		chat_msg = msg;
		try {
			chat_mutex.acquire();
			chat_stack_full = true;
			chat_mutex.release();
		} catch (Exception e) {
			System.out.println("[rmi][send_chat_msg method]");
			System.out.println(e);
		}
    }
	
	@Override
	public void call_server_lookup(String client_ip) {
		local_ip = client_ip;
		client_link = "rmi://"+local_ip+":"+String.valueOf(port)+"/"+id+P2PConstants.CHAT_RMI_CLIENT_NAME;
		lookup();
	}
	
	@Override
	public void call_peer_disconnect() {
		try {
			set_connect_status(false);
		} catch (Exception e) {
			System.out.println("[rmi][call_peer_disconnect method]");
			System.out.println(e);
		}
		unbind();
	}
	
	public void call_peer_test_connection() {
		return;
	}
	
	// RMI Connection Methods
	private Boolean bind() {
		try {
			Naming.bind(server_link, this);
			return true;
		} catch(Exception e){
			System.out.println("[rmi][bind method]");
			System.out.println(e);
			return false;
		}
	}
	
	private Boolean unbind() {
		try {
			Naming.unbind(server_link);
			UnicastRemoteObject.unexportObject(rmi_client, false);
			return true;
		} catch(Exception e){
			System.out.println("[rmi][unbind method]");
			System.out.println(e);
			return false;
		}
	}
	
	private Boolean lookup() {
		try {
			rmi_client = (RMIP2PInterface)Naming.lookup(client_link);
			set_connect_status(true);
			new Thread(this).start();
			return true;
		} catch(Exception e){
			System.out.println("[rmi][lookup method]");
			System.out.println(e);
			return false;
		}
	}

}