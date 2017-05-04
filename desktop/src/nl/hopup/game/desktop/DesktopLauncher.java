package nl.hopup.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import nl.hopup.game.HopUp;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Float";
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new HopUp(), config);
	}
}
