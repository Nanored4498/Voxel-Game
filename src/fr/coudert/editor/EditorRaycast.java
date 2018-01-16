package fr.coudert.editor;

import fr.coudert.game.world.blocks.Block;
import fr.coudert.maths.Vec3;

public class EditorRaycast {

	private Block block;
	private Vec3 blockPos, point;
	private int pointsNum;
	private float pointsDistance;

	public EditorRaycast(float length, int accurancy) { //max length = 10 & max accurancy = 12
		pointsNum = (int) (length * accurancy);
		pointsDistance = 1f / accurancy;
	}

	public void setLength(float length) {
		pointsNum = (int) (length / pointsDistance);
	}

	public void update(Editor editor) {
		Vec3 point = EditorMain.pos.copy();
		Vec3 add = EditorMain.dir.copy().mul(pointsDistance);
		for(byte i = 0; i < pointsNum; i++) {
			point = point.add(add);
			int x = (int) point.x, y = (int) point.y, z = (int) point.z;
			block = editor.getBlock(x, y, z);
			if(point.y < 0) {
				block = Block.GRASS;
				y = -1;
			}
			if(block != null) {
				blockPos = new Vec3(x, y, z);
				this.point = point;
				return;
			}
		}
	}

	public Block getBlock() { return block; }
	public Vec3 getBlockPos() { return blockPos; }
	public Vec3 getPoint() { return point; }

}