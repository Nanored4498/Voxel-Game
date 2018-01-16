package fr.coudert.game.scenes;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;

import org.lwjgl.opengl.Display;

import fr.coudert.game.GameMain;
import fr.coudert.game.world.Chunk;
import fr.coudert.game.world.World;
import fr.coudert.rendering.guis.*;

public class LoadingWorld extends Scene {

	private GuiText text;
	private GuiProgressBar loading;
	private boolean loadGame;
	private World world;

	public LoadingWorld() {
		text = (GuiText) new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 40), true), "", 0, -40).anchor(GuiComponent.C);
		text.centerText();
		loading = (GuiProgressBar) new GuiProgressBar(0, 10, 350, 40).anchor(GuiComponent.C);
		loadGame = false;
		Chunk.createArrayGrass();
	}

	public void update() {
		if(loadGame) {
			Game.instance.init(world);
			GameMain.setScene(Game.instance);
		}
	}

	public void render() {
		
	}

	public void renderGUI() {
		text.renderGUI();
		loading.renderGUI();
		glColor3f(0.2f, 0.2f, 0.9f);
		Gui.renderQuad(0, 0, Display.getWidth(), Display.getHeight());
	}

	public void displayResized() {
		text.anchorUpdate();
		loading.anchorUpdate();
	}

	public void createWorld(long seed) {
		world = new World(seed, this);
	}

	public void setInfos(String text, float progress) {
		this.text.setText(text);
		loading.setProgression(progress);
	}

	public void loadGame() {
		loadGame = true;
	}

	public boolean isCreatingVBOs() { return loadGame; }

}