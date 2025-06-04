package fr.coudert.utils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Input {

	private static boolean[] currentKeys = new boolean[224];
	private static boolean[] currentButtons = new boolean[5];
	private static int wheel;

	public static void update() {
		wheel = Mouse.getDWheel();
		for(int i = 0; i < 224; i++)
			currentKeys[i] = Keyboard.isKeyDown(i);
		for(int i = 0; i < 5; i++)
			currentButtons[i] = Mouse.isButtonDown(i);
	}

	public static boolean getKeyDown(int keyCode) {
		return Keyboard.isKeyDown(keyCode) && !currentKeys[keyCode];
	}

	public static boolean getKeyUp(int keyCode) {
		return !Keyboard.isKeyDown(keyCode) && currentKeys[keyCode];
	}

	public static boolean getMouseDown(int MouseButton) {
		return Mouse.isButtonDown(MouseButton) && !currentButtons[MouseButton];
	}

	public static boolean getMouseUp(int MouseButton) {
		return !Mouse.isButtonDown(MouseButton) && currentButtons[MouseButton];
	}

	public static int getDWheel() { return wheel; }

	public static char getKeyChar(int keyCode, boolean maj) {
		String name = Keyboard.getKeyName(keyCode);
		if(name.length() == 1) {
			char c = name.charAt(0);
			if(!maj && 'A' <= c && c <= 'Z')
				c += 'a' - 'A';
			return c;
		}
		else if(keyCode == 57)
			return ' ';
		else if(name.startsWith("NUMPAD"))
			return name.charAt(6);
		return 0;
	}

}