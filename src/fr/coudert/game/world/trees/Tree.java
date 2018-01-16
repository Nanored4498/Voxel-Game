package fr.coudert.game.world.trees;

import fr.coudert.game.world.Chunk;
import fr.coudert.game.world.World;
import fr.coudert.game.world.blocks.Block;
import fr.coudert.game.world.blocks.WoodBlock;

public class Tree {

	public static void addTree(Chunk ch, int x, int y, int z, int type) {
		int aMin = ch.getX()*Chunk.SIZE, cMin = ch.getZ()*Chunk.SIZE;
		switch(type) {
		case 0: //Normal (Oak)
			for(byte i = 0; i < 9; i++) {
				float ii = i - 4.5f;
				int a = x + (int)ii - aMin;
				for(byte j = 0; j < 9; j++) {
					float jj = j - 4.5f;
					int b = y + (int)jj + 8;
					for(byte k = 0; k < 9; k++) {
						float kk = k - 4.5f;
						if(Math.sqrt(ii * ii + jj * jj + kk *kk) < 4.5f) {
							int c = z + (int)kk - cMin;
							if(!(ch.getBlock(a, b, c) instanceof WoodBlock))
								ch.setBlock(a, b, c, Block.OAK_LEAF);
						}
					}
				}
			}
			for(int i = 0; i < 8; i++)
				ch.setBlock(x-aMin, y + i, z-cMin, Block.OAK_WOOD);
			break;
		case 1: //Sapin (Fir)
			for(byte i = 0; i < 9; i++) {
				int ii = i - 4, a = x + ii - aMin;
				for(byte j = 0; j < 13; j++) {
					int jj = j - 3, b = y + jj + 6;
					for(byte k = 0; k < 9; k++) {
						int kk = k - 4;
						if(Math.sqrt(ii * ii + kk *kk) < 4.5f - 0.35f * j) {
							int c = z + kk - cMin;
							if(!(ch.getBlock(a, b, c) instanceof WoodBlock))
								ch.setBlock(a, b, c, Block.FIR_LEAF);
						}
					}
				}
			}
			for(int i = 0; i < 12; i++)
				ch.setBlock(x-aMin, y + i, z-cMin, Block.FIR_WOOD);
			break;
		}
	}

}