package application.com.mom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MOM {
	
	public Boolean set;
	public String url;
	public String nickname;
	public String ip;
	public int port;
	
	private Boolean is_online;
	private String contactNickname;
	
	Semaphore mutex = new Semaphore(1);
	
	public MOM() {
		this.set = false;
		this.url = "";
		this.nickname = "";
		this.ip = "";
		this.port = -1;
		
		this.is_online = false;
		this.contactNickname = "";
		
		this.mutex = new Semaphore(1);
	}
	
	public Boolean is__online() {
		try {
			Boolean b;
			mutex.acquire();
			b = is_online;
			mutex.release();
			return b;
		} catch (InterruptedException e) {}
		return false;
	}
	
	public void set_online(Boolean b) {
		try {
			mutex.acquire();
			is_online = b;
			mutex.release();
		} catch (InterruptedException e) {}
	}
	
	public String get_contact_nickname() {
		try {
			String s;
			mutex.acquire();
			s = contactNickname;
			mutex.release();
			return s;
		} catch (InterruptedException e) {}
		return "";
	}
	
	public void set_contact_nickname(String s) {
		try {
			mutex.acquire();
			contactNickname = s;
			mutex.release();
		} catch (InterruptedException e) {}
	}
	
	public void setup(String url, String nickname, String ip, int port) {
		this.url = url;
		this.nickname = nickname;
		this.ip = ip;
		this.port = port;
		//Create queue
		this.set = true;
	}
	
	public void send(String queue, String msg) {
		if (this.set != true) {
			return;
		}
	}
	
	public String receive() {
		return "err";
	}
	
	public List<String> receiveQueue() {
		List<String> queue = new ArrayList<String>();
		return queue;
	}
	
	private void open_connection(String url, String queue) {
		return;
	}
	
	private void close_connection() {
		return;
	}
	
}
