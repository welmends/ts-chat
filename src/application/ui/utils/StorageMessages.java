package application.ui.utils;

import java.util.ArrayList;
import java.util.List;

public class StorageMessages {
	
	public List<String> messages;
	public List<String> directions;
	
	
	public StorageMessages() {
		messages = new ArrayList<String>();
		directions = new ArrayList<String>();
	}
	
	public void push_back(String msg, String dir) {
		messages.add(msg);
		directions.add(dir);
	}
	
}
