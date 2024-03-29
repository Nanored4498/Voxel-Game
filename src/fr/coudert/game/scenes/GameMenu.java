package fr.coudert.game.scenes;

import java.awt.Font;

import org.lwjgl.input.Mouse;

import fr.coudert.network.Client;
import fr.coudert.rendering.Camera;
import fr.coudert.rendering.SkyBox;
import fr.coudert.rendering.guis.*;

public class GameMenu extends Scene {

	private GuiComponent title;
	private GuiComponent playB, optionB, quitB, returnB;
	private GuiSlider sensitivityS, renderDistS;
	private GuiText sensitivityT, renderDistT;
	private boolean option;

	private static final float MUL_SENSI = 10.f;
	private static final float MIN_RENDER_DIST = 150.f;
	private static final float RANGE_RENDER_DIST = 600.f;

	public GameMenu(Game game) {
		option = false;
		title = new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 40), false), "Option", 0, -110).anchor(GuiComponent.C);
		playB = new GuiButton("Continuer à jouer", 0, -55, 300, 50) {
			public void onClick() {
				game.setPause(false);
				Mouse.setGrabbed(true);
			}
		}.anchor(GuiComponent.C);
		optionB = new GuiButton("Options", 0, 0, 300, 50) {
			public void onClick() {
				option = true;
			}
		}.anchor(GuiComponent.C);
		quitB = new GuiButton("Quitter", 0, 55, 300, 50) {
			public void onClick() {
				Client.stop(true);
				System.exit(1);
			}
		}.anchor(GuiComponent.C);
		sensitivityS = (GuiSlider) new GuiSlider(102, -55, 200, 50) {
			protected void onMove() {
				Camera.baseSensitivity = Camera.sensitivity = value;
				sensitivityT.setText("Sensibilite: "+((int)(value*MUL_SENSI)));
			}
		}.anchor(GuiComponent.C);
		sensitivityS.setValue(Camera.baseSensitivity);
		sensitivityT = (GuiText)
				new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 30), false), "Sensibilite: "+((int)(sensitivityS.getValue()*MUL_SENSI)), -110, -55)
				.anchor(GuiComponent.C);
		renderDistS = (GuiSlider) new GuiSlider(165, 0, 200, 50) {
			protected void onMove() {
				Camera.far = value*RANGE_RENDER_DIST+MIN_RENDER_DIST;
				SkyBox.updateVBO();
				renderDistT.setText("Distance de rendu: "+((int)(Camera.far)));
			}
		}.anchor(GuiComponent.C);
		renderDistS.setValue((Camera.far - MIN_RENDER_DIST) / RANGE_RENDER_DIST);
		renderDistT = (GuiText)
				new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 30), false), "Distance de rendu: "+((int)Camera.far), -110, 0)
				.anchor(GuiComponent.C);
		returnB = new GuiButton("Retour", 0, 55, 300, 50) {
			public void onClick() {
				option = false;
			}
		}.anchor(GuiComponent.C);
	}

	public void update() {
		if(option) {
			sensitivityS.update();
			renderDistS.update();
			returnB.update();
		} else {
			playB.update();
			optionB.update();
			quitB.update();
		}
	}

	public void render() {}

	public void renderGUI() {
		if(option) {
			title.renderGUI();
			sensitivityS.renderGUI();
			sensitivityT.renderGUI();
			renderDistS.renderGUI();
			renderDistT.renderGUI();
			returnB.renderGUI();
		} else {
			playB.renderGUI();
			optionB.renderGUI();
			quitB.renderGUI();
		}
	}

	public void displayResized() {
		playB.anchorUpdate();
		optionB.anchorUpdate();
		quitB.anchorUpdate();
		title.anchorUpdate();
		sensitivityS.anchorUpdate();
		sensitivityT.anchorUpdate();
		renderDistS.anchorUpdate();
		renderDistT.anchorUpdate();
		returnB.anchorUpdate();
	}

}