package application.ui.constants;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageConstants {
	public static Image CHAT_TOP_ICON = new Image(ImageConstants.class.getResourceAsStream("/resources/images/chat_icon.png"), 40, 40, true, true);
	public static ImageView ADD_BTN_ICON = new ImageView(new Image(ImageConstants.class.getResourceAsStream("/resources/images/add.png"), 15, 15, true, true));
	public static ImageView POWER_BTN_ICON = new ImageView(new Image(ImageConstants.class.getResourceAsStream("/resources/images/power.png"), 15, 15, true, true));
}
