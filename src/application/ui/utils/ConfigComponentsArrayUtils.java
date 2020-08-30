package application.ui.utils;

import java.util.HashMap;
import java.util.List;

import application.ui.ConfigController;
import application.ui.constants.ConfigConstants;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class ConfigComponentsArrayUtils {
	
	private ConfigController config;
	private VBox vboxOnScroll;
	private HashMap<String, String> hash;
	private List<TitledPane> rooms_components;
	private List<Button> contacts_components;
	
	
	public ConfigComponentsArrayUtils(ConfigController config, VBox vboxOnScroll, HashMap<String, String> hash, List<TitledPane> rooms_components, List<Button> contacts_components){
		this.config = config;
		this.vboxOnScroll = vboxOnScroll;
		this.hash = hash;
		this.rooms_components = rooms_components;
		this.contacts_components = contacts_components;
	}
	
	public void add_room_titledPane(String room_name) {
		Button b = new Button(ConfigConstants.ROOM_BUTTON_TEXT);
		b.setStyle(ConfigConstants.ROOM_BUTTON_STYLE);
		b.setContentDisplay(ConfigConstants.ROOM_BUTTON_CONTENT_DISPLAY);
		
		TitledPane tp = new TitledPane();
		tp.setText(room_name);
		tp.setGraphic(b);
		tp.setContent(new VBox());
		
		b.translateXProperty().bind(Bindings.createDoubleBinding(() -> 
			tp.getWidth() - b.getLayoutX() - b.getWidth() - ConfigConstants.ROOM_BUTTON_GRAPHIC_MARGIN_RIGHT, tp.widthProperty())
		);
		
		config.setRoomBtnPressedBehavior(b, tp);
		
		rooms_components.add(tp);
		
        vboxOnScroll.getChildren().add(tp);
	}
	
	public void add_contact_button(String ts_user_name, String room_name, String contact_name) {
		Button b = new Button();
		b.setText(contact_name);
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
	
	public void del_room_titledpane(String room_name) {
		for (int i=0; i<rooms_components.size(); i++) {
			if(rooms_components.get(i).getText().equals(room_name)) {
				vboxOnScroll.getChildren().remove(rooms_components.get(i));
				rooms_components.remove(i);
				break;
			}
		}
	}
	
	public void del_contact_button(String room_name, String contact_name) {
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
