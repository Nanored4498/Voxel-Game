package fr.coudert.game.scenes;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.lwjgl.input.Mouse;

import fr.coudert.game.GameMain;
import fr.coudert.game.ServerMain;
import fr.coudert.maths.Vec3;
import fr.coudert.network.Client;
import fr.coudert.network.packets.ConnectPack;
import fr.coudert.rendering.*;
import fr.coudert.rendering.guis.*;

public class Menu extends Scene {

	private ArrayList<GuiComponent> guis;

	public Menu() {
		guis = new ArrayList<GuiComponent>();
		guis.add(new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 40), true), "ALPHA", 0, -100).anchor(GuiComponent.C));
		guis.add(new GuiButton("Jouer", 0, -20, 300, 50) {
			public void onClick() {
				String name = JOptionPane.showInputDialog(null, "Quel est votre pseudo ?", "Pseudo", JOptionPane.QUESTION_MESSAGE);
//				String address = JOptionPane.showInputDialog(null, "Quel est l'addresse du serveur à rejoindre ?", "Adresse", JOptionPane.QUESTION_MESSAGE);
				String address = "localhost";
				GameMain.setPlayerName(name);
				LoadingWorld loadingWorld = new LoadingWorld();
				loadingWorld.setInfos("Wating an answer from the server", 0);
				Client.connect(address, 4498);
				Client.send(new ConnectPack(name));
				GameMain.setScene(loadingWorld);
				Mouse.setGrabbed(true);
			}
		}.anchor(GuiComponent.C));
		guis.add(new GuiButton("Serveur", 0, 40, 300, 50) {
			public void onClick() {
				ServerMain.main(null);
			}
		}.anchor(GuiComponent.C));
		guis.add(new GuiButton("Quitter", 0, 100, 300, 50) {
			public void onClick() {
				System.exit(1);
			}
		}.anchor(GuiComponent.C));
	}

	public void update() {
		for(GuiComponent g : guis)
			g.update();
	}

	public void render() {
		Shader.SKYBOX.bind();
		SkyBox.SKY_1.render(Vec3.ZERO);
		Shader.unBind();
	}

	public void renderGUI() {
		Gui.color(Color.WHITE);
		for(GuiComponent g : guis)
			g.renderGUI();
	}

	public void displayResized() {
		for(GuiComponent g : guis)
			g.anchorUpdate();
	}

}