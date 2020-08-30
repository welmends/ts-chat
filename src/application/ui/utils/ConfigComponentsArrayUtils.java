package application.ui.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import application.ui.ConfigController;
import application.ui.constants.ConfigConstants;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConfigComponentsArrayUtils {
	
	private ConfigController config;
	private VBox vboxOnScroll;
	private HashMap<String, String> hash;
	private List<TitledPane> rooms_components;
	private List<Button> contacts_components;
	
	
	public ConfigComponentsArrayUtils(ConfigController config, VBox vboxOnScroll){
		this.config = config;
		this.vboxOnScroll = vboxOnScroll;
		this.hash = new HashMap<String, String>();
		this.rooms_components = new ArrayList<TitledPane>();
		this.contacts_components = new ArrayList<Button>();
	}
	
	@SuppressWarnings("unchecked")
	public void updateComponentsList(String user_name, List<String> ts_rooms, HashMap<String, String> ts_hash) {
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
				add_room_titledPane(ts_rooms.get(i));
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
				del_room_titledpane(rooms_components.get(i).getText());
			}
		}
		
		ts_hash.forEach((key, value) -> {
			if(hash.containsKey(key)) {
				if(!hash.get(key).equals(value)) {
					del_contact_button(hash.get(key), key);
					add_contact_button(user_name, value, key);
				}
			}else {
				add_contact_button(user_name, value, key);
			}
		});
		
		((HashMap<String, String>) hash.clone()).forEach((key, value) -> {
			if(ts_hash.containsKey(key)) {
				if(!ts_hash.get(key).equals(value)) {
					del_contact_button(value, key);
					add_contact_button(user_name, ts_hash.get(key), key);
				}
			}else {
				del_contact_button(value, key);
			}
		});
		
        vboxOnScroll.applyCss();
        vboxOnScroll.layout();
	}
	
	private void add_room_titledPane(String room_name) {
		HBox h = new HBox();
		
		Button b_enter = new Button();
		b_enter.setText(ConfigConstants.ENTER_ROOM_BUTTON_TEXT);
		b_enter.setStyle(ConfigConstants.ROOM_BUTTON_STYLE);
		
		Button b_leave = new Button();
		b_leave.setText(ConfigConstants.LEAVE_ROOM_BUTTON_TEXT);
		b_leave.setStyle(ConfigConstants.ROOM_BUTTON_STYLE);

		h.getChildren().addAll(b_enter, b_leave);
		
		TitledPane tp = new TitledPane();
		tp.setText(room_name);
		tp.setStyle(ConfigConstants.TITLED_PANE_STYLE);
		tp.setContentDisplay(ConfigConstants.ROOM_BUTTON_CONTENT_DISPLAY);
		tp.setGraphic(h);
		tp.setContent(new VBox());
		
		h.translateXProperty().bind(Bindings.createDoubleBinding(() -> 
			tp.getWidth() - h.getLayoutX() - h.getWidth() - ConfigConstants.ROOM_BUTTON_GRAPHIC_MARGIN_RIGHT, tp.widthProperty())
		);
		
		config.setRoomBtnPressedBehavior(tp, b_enter, b_leave);
		
		rooms_components.add(tp);
		
        vboxOnScroll.getChildren().add(tp);
	}
	
	private void add_contact_button(String ts_user_name, String room_name, String contact_name) {
		Button b = new Button();
		b.setText(contact_name);
		b.setStyle(ConfigConstants.CONTACT_BUTTON_STYLE);
		b.setPrefWidth(ConfigConstants.CONTACT_BUTTON_PREF_WIDTH);
		config.setContactBtnPressedBehavior(b);
		if(contact_name.equals(ts_user_name)) {
			b.setDisable(true);
		}
		
		contacts_components.add(b);
		
		for (int i=0; i<rooms_components.size(); i++) {
			if(rooms_components.get(i).getText().equals(room_name)) {
				VBox content = (VBox) rooms_components.get(i).getContent();
				content.getChildren().add(b);
			}
		}
		
		hash.put(contact_name, room_name);
	}
	
	private void del_room_titledpane(String room_name) {
		for (int i=0; i<rooms_components.size(); i++) {
			if(rooms_components.get(i).getText().equals(room_name)) {
				vboxOnScroll.getChildren().remove(rooms_components.get(i));
				rooms_components.remove(i);
				break;
			}
		}
	}
	
	private void del_contact_button(String room_name, String contact_name) {
		for (int i=0; i<rooms_components.size(); i++) {
			if(rooms_components.get(i).getText().equals(room_name)) {
				for (int j=0; j<contacts_components.size(); j++) {
					if(contacts_components.get(j).getText().equals(contact_name)) {
						VBox content = (VBox) rooms_components.get(i).getContent();
						content.getChildren().remove(contacts_components.get(j));
						contacts_components.remove(j);
		        		break;
					}
				}
				break;
			}
		}
		
		hash.remove(contact_name, room_name);
	}
	
}
