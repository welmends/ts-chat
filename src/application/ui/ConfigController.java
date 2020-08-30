package application.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import application.ts.TupleSpace;
import application.ts.TupleSpaceConstants;
import application.ui.constants.ConfigConstants;
import application.ui.constants.ImageConstants;
import application.ui.utils.ConfigComponentsArrayUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ConfigController extends Thread implements Initializable  {
	
	// FXML Variables
	@FXML HBox mainHBox;
	@FXML Button add_btn;
	@FXML Button power_btn;
	@FXML Circle on_circle;
	@FXML Circle off_circle;
	@FXML TextField add_tf;
	@FXML ScrollPane contactsScrollPane;
	@FXML VBox vboxOnScroll;
	
	// COM Variables
	private TupleSpace ts;
	
	// Controllers
	private ChatController chat;
	
	// Variables
	private HashMap<String, String> hash;
	private List<TitledPane> rooms_components;
	private List<Button> contacts_components;
	private ConfigComponentsArrayUtils componentsArray_utils;
	
	public void loadFromParent(TupleSpace ts, ChatController chat) {
		this.ts = ts;
		this.chat = chat;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialize Objects
		hash = new HashMap<String, String>();
		rooms_components = new ArrayList<TitledPane>();
		contacts_components = new ArrayList<Button>();
		componentsArray_utils = new ConfigComponentsArrayUtils(this, vboxOnScroll, hash, rooms_components, contacts_components);
		
		setupComponents();
		setAddBtnPressedBehavior();
		setPowerBtnPressedBehavior();
		setVBoxScrollsBehavior();
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(ConfigConstants.THREAD_SLEEP_TIME_MILLIS);
			} catch (InterruptedException e) {
				System.out.println("Error: ConfigController (thread)");
			}
			
			List<String> ts_rooms = ts.get_rooms_list();
			HashMap<String, String> ts_hash = ts.get_hash_rooms_contacts();
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					updateSpaceList(ts_rooms, ts_hash);
				}
			});
			
		}
	}
	
	private void setupComponents() {
		on_circle.setFill(ConfigConstants.COLOR_ONLINE);
		off_circle.setFill(ConfigConstants.COLOR_UNKNOWN);
		
		add_btn.setGraphic(ImageConstants.ADD_BTN_ICON);
		power_btn.setGraphic(ImageConstants.POWER_BTN_ICON);
	}
	
	private void updateSpaceList(List<String> ts_rooms, HashMap<String, String> ts_hash) {
		Boolean add_del;
		
		for (int i=0; i<ts_rooms.size(); i++) {
			add_del = true;
			for (int j=0; j<rooms_components.size(); j++) {
				if(ts_rooms.get(i).equals(rooms_components.get(j).getText())) {
					add_del = false;
					break;
				}
			}
			if(add_del) {
				componentsArray_utils.add_room_titledPane(ts_rooms.get(i));
			}
		}
		
		for (int i=0; i<rooms_components.size(); i++) {
			add_del = true;
			for (int j=0; j<ts_rooms.size(); j++) {
				if(rooms_components.get(i).getText().equals(ts_rooms.get(j))) {
					add_del = false;
					break;
				}
			}
			if(add_del) {
				componentsArray_utils.del_room_titledpane(rooms_components.get(i).getText());
			}
		}
		
		ts_hash.forEach((key, value) -> {
			if(hash.containsKey(key)) {
				if(!hash.get(key).equals(value)) {
					componentsArray_utils.del_contact_button(hash.get(key), key);
					componentsArray_utils.add_contact_button(ts.get_user_name(), value, key);
				}
			}else {
				componentsArray_utils.add_contact_button(ts.get_user_name(), value, key);
			}
		});
		
        vboxOnScroll.applyCss();
        vboxOnScroll.layout();
	}
	
	public void setRoomBtnPressedBehavior(Button b_room, TitledPane tp_room) {
		b_room.setOnAction((event)->{
			ts.select_room(tp_room.getText());
			ts.set_chat_type(TupleSpaceConstants.ROOM_CHAT);
			
			chat.chatLabelContact.setText(ConfigConstants.CHAT_LABEL_ROOM_PREFIX + ts.get_room_name());
			chat.clearChat();
			chat.loadChat();
			chat.disableChatTextField(false);
        });
    }
	
	public void setContactBtnPressedBehavior(Button b_contact) {
		b_contact.setOnAction((event)->{
			for (int i=0; i<contacts_components.size(); i++) {
				if(contacts_components.get(i).equals(b_contact)) {
					ts.set_contact_name(contacts_components.get(i).getText());
					ts.set_chat_type(TupleSpaceConstants.CONTACT_CHAT);
					
					chat.chatLabelContact.setText(ConfigConstants.CHAT_LABEL_CONTACT_PREFIX + ts.get_contact_name());
					chat.clearChat();
					chat.loadChat();
					chat.disableChatTextField(false);
	        		break;
				}
			}
        });
	}
	
	private void setAddBtnPressedBehavior() {
		add_btn.setOnAction((event)->{
			String room_name = add_tf.getText();
			add_tf.setText("");
			if(room_name.equals("")) {
				return;
			}
			if(!ts.add_room(room_name)) {
				return;
			}
			
			componentsArray_utils.add_room_titledPane(room_name);
        });
    }

	private void setPowerBtnPressedBehavior() {
		power_btn.setOnAction((event)->{
        	if (ts.has_connection()) {
        		ts.disconnect();
        		on_circle.setFill(ConfigConstants.COLOR_UNKNOWN);
        		off_circle.setFill(ConfigConstants.COLOR_OFFLINE);
        	} else {
        		ts.connect();
        		on_circle.setFill(ConfigConstants.COLOR_ONLINE);
        		off_circle.setFill(ConfigConstants.COLOR_UNKNOWN);
        	}
        });
    }
	
	private void setVBoxScrollsBehavior() {
		vboxOnScroll.heightProperty().addListener(new ChangeListener<Number>() {

	        @Override
	        public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
	        	if(arg1.intValue()!=0) {
	        		contactsScrollPane.setVvalue(1.0);
	        	}
	        }
	        
		});
	}
	
}