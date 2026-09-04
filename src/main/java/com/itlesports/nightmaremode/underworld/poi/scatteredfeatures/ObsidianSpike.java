package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;

import java.io.InputStream;
import java.util.Random;

public class ObsidianSpike extends ComponentScatteredFeature {
    public ObsidianSpike() {
    } // required

    private static final int[] paletteIDs = new int[]{
            Block.jukebox.blockID, // jukebox
            NMBlocks.underStones.blockID,
            NMBlocks.underStones.blockID,
            NMBlocks.underStones.blockID,
            NMBlocks.underStones.blockID,
            NMBlocks.underStones.blockID,
            Block.hay.blockID, // hay bale
            NMBlocks.underStones.blockID,
            NMBlocks.underFlowerDirts.blockID, // flower grass
            NMBlocks.underStones.blockID,
            NMBlocks.underStones.blockID,
            NMBlocks.underStones.blockID,
            BTWBlocks.unlitCampfire.blockID, // unlit campfire
            NMBlocks.yellowFlowerRoots.blockID, // yellow roots
    };
 // unused

    private static int[] meta = new int[]{
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
    };

    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        String path = "structures/spike.nbt";
        placeFromNBT(world, box, path, paletteIDs);
        return true;
    }

    public void placeFromNBT(World world, StructureBoundingBox box, String resourcePath, int[] blockIDs) {
        try {
            InputStream in = NightmareMode.class.getClassLoader().getResourceAsStream(resourcePath);

            if (in == null) {
                System.err.println("NBT resource not found: " + resourcePath);
                return;
            }

            NBTTagCompound root = CompressedStreamTools.readCompressed(in);
            in.close();

            NBTTagList blocks = root.getTagList("blocks");
            for (int i = 0; i < blocks.tagCount(); i++) {
                NBTTagCompound block = (NBTTagCompound) blocks.tagAt(i);

                NBTTagList posTag = block.getTagList("pos");
                int x = ((NBTTagInt) posTag.tagAt(0)).data;
                int y = ((NBTTagInt) posTag.tagAt(1)).data;
                int z = ((NBTTagInt) posTag.tagAt(2)).data;

                int state = block.getInteger("state");
                if (state <= 0 || state >= blockIDs.length) continue;
                int mappedID = getBlockID(state);

                if(mappedID == BTWBlocks.plainCandle.blockID){
                    mappedID = BTWBlocks.coloredCandle[this.structureRand.nextInt(BTWBlocks.coloredCandle.length)].blockID;
                }
                this.place(world, mappedID, getMeta(state, mappedID), x, y, z, box);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getBlockID(int index){
        if(index > paletteIDs.length){
            System.out.println("ID too big for the list");
            return paletteIDs[paletteIDs.length - 1];
        } else{
            return paletteIDs[index];
        }
    }

    private int getMeta(int index, int mappedBlockID){
        if(index > meta.length){
            System.out.println("meta too big for the list");
            return meta[meta.length - 1];
        }

        int valueToReturn = meta[index];
        int special = 0;
        if(valueToReturn < 0){
            // negative metadata calculates the meta as a random number from 0 to positive meta
            valueToReturn = this.structureRand.nextInt(-valueToReturn);
            return valueToReturn;
        }
        if(mappedBlockID == Block.oreIron.blockID){
            special = this.structureRand.nextInt(1,3);
        }
        if(mappedBlockID == BTWBlocks.soulforge.blockID){
            special = this.structureRand.nextInt(4) + 2;
        }

        if(special != 0){
            return special;
        }

        // no special behavior needed
        return meta[index];
    }

    private void place(World world, int blockID, int metadata, int localX, int localY, int localZ, StructureBoundingBox boundingBox) {
        this.placeBlockAtCurrentPosition(world, blockID, metadata, localX, localY, localZ, boundingBox);
    }

    public ObsidianSpike(Random random, int x, int z) {
        super(random, x, 63, z, 64, 64, 64);
        this.structureRand.setSeed(random.nextLong());
    }

    private final Random structureRand = new Random();
}
