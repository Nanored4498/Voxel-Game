package fr.coudert.game.scenes;

public abstract class Scene {

	public abstract void update();

	public abstract void render();

	public abstract void renderGUI();
	
	public abstract void displayResized();

}