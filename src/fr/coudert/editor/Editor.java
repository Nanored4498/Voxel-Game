package fr.coudert.editor;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.coudert.game.world.blocks.Block;
import fr.coudert.game.world.blocks.ColoredBlock;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.Color;
import fr.coudert.rendering.guis.*;
import fr.coudert.utils.Input;

public class Editor {

	public static final int SIZE = 64;

	private int list;
	private Block[][][] blocks;
	private EditorRaycast raycast;
	private ArrayList<GuiComponent> guis;
	private ArrayList<Block> addedBlocks;
	private Color blockColor;
	private int[] abx, aby;
	private byte blockIndex;
	private float distance;
	private Vec3 firstPos, secondPos;
	private byte mouseButton;
	String path = null;
	private ArrayList<Event> pastEvents, futurEvents;

	public Editor() {
		blocks = new Block[SIZE][SIZE][SIZE];
		raycast = new EditorRaycast(10, 12);
		addedBlocks = new ArrayList<Block>();
		TrueTypeFont font = new TrueTypeFont(new Font("Arial", Font.PLAIN, 18), true);
		guis = new ArrayList<GuiComponent>();
		guis.add(new GuiSlider(65, 35, 125, 18) {
			protected void onMove() {
				blockColor.r = value;
			}
		}); //0
		guis.add(new GuiSlider(65, 56, 125, 18) {
			protected void onMove() {
				blockColor.g = value;
			}
		}); //1
		guis.add(new GuiSlider(65, 77, 125, 18) {
			protected void onMove() {
				blockColor.b = value;
			}
		}); //2
		guis.add(new GuiButton("Add", 5, 100, 188, 20) {
			public void onClick() {
				if(addedBlocks.size() < 27) {
					boolean old = false;
					for(Block block : addedBlocks)
						if(block.getColor().equals(blockColor)) {
							old = true;
							break;
						}
					if(!old) {
						addEvent();
						addedBlocks.add(new ColoredBlock(new Color(blockColor)));
					}
					if(blockIndex == -1)
						blockIndex = 0;
				}
			}
		}); //3
		guis.add(new GuiButton("Save", 69, 2, 61, 22) {
			public void onClick() {
				if(path == null)
					path = FileManager.chooseFile(true);
				if(path == null)
					return;
				StringBuilder lines = new StringBuilder();
				for(Block b : addedBlocks)
					lines.append('i').append(b.getColor().getHex()).append("\n");
				for(byte x = 0; x < SIZE; x++)
					for(byte y = 0; y < SIZE; y++)
						for(byte z = 0; z < SIZE; z++)
							if(blocks[x][y][z] != null)
								lines.append('b').append(addedBlocks.indexOf(blocks[x][y][z])).append(",").append(x).append(",").append(y).append(",").append(z).append("\n");
				FileManager.writeFile(path, lines.toString());
			}
		}.anchor(GuiComponent.BL)); //4
		guis.add(new GuiButton("Load", 134, 2, 61, 22) {
			public void onClick() {
				String temp = FileManager.chooseFile(false);
				if(temp == null)
					return;
				path = temp;
				blocks = new Block[SIZE][SIZE][SIZE];
				addedBlocks.clear();
				pastEvents.clear();
				futurEvents.clear();
				String text = FileManager.readFile(path), word = "";
				char a = 'a';
				int b = 0;
				int[] infos = new int[3];
				for(int i = 0; i < text.length(); i++) {
					char c = text.charAt(i);
					if(a == 'a') {
						a = c;
					}
					else {
						if(c == '\n') {
							if(a == 'i') {
								addedBlocks.add(new ColoredBlock(new Color(Long.valueOf(word))));
							}
							else {
								blocks[infos[1]][infos[2]][Integer.valueOf(word)] = addedBlocks.get(infos[0]);
							}
							a = 'a';
							b= 0;
							word = "";
						} else if(c == ',') {
							infos[b] = Integer.valueOf(word);
							b ++;
							word = "";
						} else
							word += c;
					}
				}
				blockIndex = 0;
			}
		}.anchor(GuiComponent.BL)); //5
		guis.add(new GuiButton("New", 4, 2, 61, 22) {
			public void onClick() {
				blocks = new Block[SIZE][SIZE][SIZE];
				addedBlocks.clear();
				pastEvents.clear();
				futurEvents.clear();
				path = null;
			}
		}.anchor(GuiComponent.BL)); //6
		guis.add(new GuiText(font, "Voxel Editor v 1.0", 25, 3)); //7
		blockColor = new Color(0, 0, 0);
		abx = new int[] {12, 74, 136};
		aby = new int[] {130, 192, 254, 316, 378, 440, 502, 564, 626};
		blockIndex = -1;
		distance = -1;
		compile();
		pastEvents = new ArrayList<Event>();
		futurEvents = new ArrayList<Event>();
	}

	private void compile() {
		list = glGenLists(1);
		glNewList(list, GL_COMPILE);
		glColor3f(0.4f, 0.4f, 0.4f);
		glBegin(GL_QUADS);
			glVertex3i(0, 0, 0);
			glVertex3i(SIZE, 0, 0);
			glVertex3i(SIZE, 0, SIZE);
			glVertex3i(0, 0, SIZE);
		glEnd();
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glLineWidth(2);
		glColor3f(0.5f, 0.5f, 0.5f);
		glBegin(GL_QUADS);
			for(byte x = SIZE/2 - 8; x < SIZE/2 + 8; x++) {
				for(byte z = SIZE/2 - 8; z < SIZE/2 + 8; z++) {
					glVertex3i(x, 0, z);
					glVertex3i(x + 1, 0, z);
					glVertex3i(x + 1, 0, z + 1);
					glVertex3i(x, 0, z + 1);
				}
			}
		glEnd();
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glEndList();
	}

	public void update() {
		if(Input.getKeyDown(Keyboard.KEY_Z) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !pastEvents.isEmpty()) {
			futurEvents.add(new Event(blocks, addedBlocks));
			Event lastEvent = pastEvents.get(pastEvents.size()-1);
			blocks = lastEvent.getBlocks();
			addedBlocks = lastEvent.getAddedBlocks();
			pastEvents.remove(lastEvent);
		}
		if(Input.getKeyDown(Keyboard.KEY_Y) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !futurEvents.isEmpty()) {
			pastEvents.add(new Event(blocks, addedBlocks));
			Event lastEvent = futurEvents.get(futurEvents.size()-1);
			blocks = lastEvent.getBlocks();
			addedBlocks = lastEvent.getAddedBlocks();
			futurEvents.remove(lastEvent);
		}
		if(Mouse.isGrabbed()) {
			raycast.update(this);
			if(raycast.getBlock() != null) {
				for(byte b = 0; b <= 1; b++) {
					if(Input.getMouseDown(b) && blockIndex != -1 && distance == -1) {
						firstPos = b == 0 ? raycast.getBlockPos() : getNewBlockPos();
						distance = EditorMain.pos.copy().sub(firstPos).length();
						raycast.setLength(distance);
						mouseButton = b;
					}
				}
				if(Input.getMouseDown(2))
					blockIndex = (byte) addedBlocks.indexOf(raycast.getBlock());
			}
			if(distance != -1) {
				if(raycast.getBlock() != null && mouseButton == 1)
					secondPos = getNewBlockPos();
				else
					secondPos = EditorMain.pos.copy().add(EditorMain.dir.copy().mul(distance));
				if(Input.getMouseUp(mouseButton))
					mouseUp(mouseButton == 1 ? addedBlocks.get(blockIndex) : null);
			}
			if(blockIndex != -1) {
				int wheel = Mouse.getDWheel();
				if(wheel > 0) {
					blockIndex ++;
					if(blockIndex >= addedBlocks.size())
						blockIndex = 0;
				}
				if(wheel < 0) {
					blockIndex --;
					if(blockIndex < 0)
						blockIndex = (byte) (addedBlocks.size() - 1);
				}
			}
		}
		for(GuiComponent component : guis)
			component.update();
		if(Input.getMouseUp(0)) {
			int mx = Mouse.getX(), my = Display.getHeight() - Mouse.getY();
			for(byte b = 0; b < addedBlocks.size(); b++) {
				if(mx >= abx[b % 3] && my >= aby[b / 3] && mx <= abx[b % 3] + 50 && my <= aby[b / 3] + 50 && b != blockIndex) {
					if(mx >= abx[b % 3] + 42 && my <= aby[b / 3] + 8) {
						addEvent();
						for(byte x = 0; x < SIZE; x++)
							for(byte y = 0; y < SIZE; y++)
								for(byte z = 0; z < SIZE; z++)
									if(blocks[x][y][z] == addedBlocks.get(b))
										blocks[x][y][z] = null;
						addedBlocks.remove(b);
					} else
						blockIndex = b;
					break;
				}
			}
		}
	}

	public void displayResized() {
		for(GuiComponent guiComponent : guis)
			guiComponent.anchorUpdate();
	}

	private Vec3 getNewBlockPos() {
		if(raycast.getBlockPos().y == -1)
			return new Vec3(raycast.getBlockPos().x, 0, raycast.getBlockPos().z);
		else
			return raycast.getBlockPos().copy().add(raycast.getPoint().copy().sub(raycast.getBlockPos()).check());
	}

	private void mouseUp(Block block) {
		addEvent();
		float xMax = Math.max(firstPos.x, secondPos.x), yMax = Math.max(firstPos.y, secondPos.y), zMax = Math.max(firstPos.z, secondPos.z);
		for(byte x = (byte) Math.min(firstPos.x, secondPos.x); x <= xMax; x++)
			for(byte y = (byte) Math.min(firstPos.y, secondPos.y); y <= yMax; y++)
				for(byte z = (byte) Math.min(firstPos.z, secondPos.z); z <= zMax; z++)
					setBlock(x, y, z, block);
		distance = -1;
		raycast.setLength(10);
	}

	private void addEvent() {
		futurEvents.clear();
		pastEvents.add(new Event(blocks, addedBlocks));
		if(pastEvents.size() > 15)
			pastEvents.remove(0);
	}

	public void render() {
		glCallList(list);
		glBegin(GL_QUADS);
		float s = Block.S;
		for(byte x = 0; x < SIZE; x++) {
			for(byte y = 0; y < SIZE; y++) {
				for(byte z = 0; z < SIZE; z++) {
					Block block = blocks[x][y][z];
					if(block != null) {
						glColor4f(block.getColor().r * 0.9f, block.getColor().g * 0.9f, block.getColor().b * 0.9f, block.getColor().a);
						glVertex3f(x, y, z); //Front
						glVertex3f(x + s, y, z);
						glVertex3f(x + s, y + s, z);
						glVertex3f(x, y + s, z);

						glVertex3f(x, y, z + s); //Back
						glVertex3f(x, y + s, z + s);
						glVertex3f(x + s, y + s, z + s);
						glVertex3f(x + s, y, z + s);

						glColor4f(block.getColor().r * 0.8f, block.getColor().g * 0.8f, block.getColor().b * 0.8f, block.getColor().a);
						glVertex3f(x, y, z); //Left
						glVertex3f(x, y + s, z);
						glVertex3f(x, y + s, z + s);
						glVertex3f(x, y, z + s);

						glVertex3f(x + s, y, z); //Right
						glVertex3f(x + s, y, z + s);
						glVertex3f(x + s, y + s, z + s);
						glVertex3f(x + s, y + s, z);

						glColor4f(block.getColor().r * 0.7f, block.getColor().g * 0.7f, block.getColor().b * 0.7f, block.getColor().a);
						glVertex3f(x, y, z); //Down
						glVertex3f(x, y, z + s);
						glVertex3f(x + s, y, z + s);
						glVertex3f(x + s, y, z);

						glColor4f(block.getColor().r, block.getColor().g, block.getColor().b, block.getColor().a);
						glVertex3f(x, y + s, z); //Up
						glVertex3f(x + s, y + s, z);
						glVertex3f(x + s, y + s, z + s);
						glVertex3f(x, y + s, z + s);
					}
				}
			}
		}
		glEnd();
		glColor4f(0, 0, 0, 1);
		if(distance != -1) {
			if(mouseButton == 0)
				glColor4f(1, 0, 0, 1);
			renderSelection((int) secondPos.x, (int) secondPos.y, (int) secondPos.z);
			renderSelection(firstPos.x, firstPos.y, firstPos.z);
			glDisable(GL_CULL_FACE);
			renderSelection(firstPos.x + 0.5f, firstPos.y + 0.5f, firstPos.z + 0.5f, (int) secondPos.x + 0.5f, (int) secondPos.y + 0.5f, (int) secondPos.z + 0.5f);
			glEnable(GL_CULL_FACE);
		} else if(raycast.getBlock() != null)
			renderSelection(raycast.getBlockPos().x, raycast.getBlockPos().y, raycast.getBlockPos().z);
	}

	private void renderSelection(float x, float y, float z) {
		renderSelection(x, y, z, x + 1, y + 1, z + 1);
	}

	private void renderSelection(float x, float y, float z, float x2, float y2, float z2) {
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glLineWidth(3);
		glBegin(GL_QUADS);
			glVertex3f(x, y, z);
			glVertex3f(x2, y, z);
			glVertex3f(x2, y2, z);
			glVertex3f(x, y2, z);

			glVertex3f(x, y, z2);
			glVertex3f(x, y2, z2);
			glVertex3f(x2, y2, z2);
			glVertex3f(x2, y, z2);
			
			glVertex3f(x, y, z);
			glVertex3f(x, y, z2);
			glVertex3f(x2, y, z2);
			glVertex3f(x2, y, z);
			
			glVertex3f(x, y2, z);
			glVertex3f(x2, y2, z);
			glVertex3f(x2, y2, z2);
			glVertex3f(x, y2, z2);
			
			glVertex3f(x, y, z);
			glVertex3f(x, y2, z);
			glVertex3f(x, y2, z2);
			glVertex3f(x, y, z2);
			
			glVertex3f(x2, y, z);
			glVertex3f(x2, y, z2);
			glVertex3f(x2, y2, z2);
			glVertex3f(x2, y2, z);
		glEnd();
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}

	public void renderGUI() {
		int mx = Mouse.getX(), my = Display.getHeight() - Mouse.getY();
		for(byte b = 0; b < addedBlocks.size(); b++) {
			if(b == blockIndex) {
				Gui.color(addedBlocks.get(b).getColor());
				Gui.renderQuad(abx[b % 3] - 5, aby[b / 3] - 5, 60, 60);
				Gui.color(Color.WHITE);
				Gui.renderQuad(abx[b % 3] - 6, aby[b / 3] - 6, 62, 62);
			} else {
				if(mx >= abx[b % 3] && my >= aby[b / 3] && mx <= abx[b % 3] + 50 && my <= aby[b / 3] + 50 && b != blockIndex) {
					Gui.color(new Color(1, 0, 0.2f, 0.8f));
					Gui.renderQuad(abx[b % 3] + 42, aby[b / 3] , 8, 8);
				}
				Gui.color(addedBlocks.get(b).getColor());
				Gui.renderQuad(abx[b % 3], aby[b / 3], 50, 50);
			}
		}
		Gui.color(Color.WHITE);
		for(GuiComponent component : guis)
			component.renderGUI();
		Gui.color(blockColor);
		Gui.renderQuad(5, 40, 50, 50);
		glColor4f(0.3f, 0.3f, 0.3f, 0.8f);
		Gui.renderQuad(0, 0, 198, Display.getHeight());
		glColor4f(0.2f, 0.2f, 0.2f, 0.8f);
		Gui.renderQuad(198, 0, 2, Display.getHeight());
		glColor4f(0, 0, 0, 1);
		glRectf(Display.getWidth() / 2 - 2, Display.getHeight() / 2 - 2, Display.getWidth() / 2 + 2, Display.getHeight() / 2 + 2);
	}

	public Block getBlock(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0 || x >= SIZE || y >= SIZE || z >= SIZE)
			return null;
		else
			return blocks[x][y][z];
	}

	public void setBlock(byte x, byte y, byte z, Block block) {
		if(x < 0 || y < 0 || z < 0 || x >= SIZE || y >= SIZE || z >= SIZE)
			return;
		else
			blocks[x][y][z] = block;
	}

}