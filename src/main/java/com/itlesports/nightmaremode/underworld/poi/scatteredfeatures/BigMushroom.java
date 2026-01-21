package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;
import java.io.InputStream;
import java.util.Random;

public class BigMushroom extends ComponentScatteredFeature {
    public BigMushroom(Random random, int x, int z) {
        super(random, x, 64, z, 64,64,64); // Larger bounding box for massive mushroom (adjust as needed)
    }

    int[] blockIDs = new int[] {
            0, // index 0 = air
            Block.mushroomCapBrown.blockID,
            NMBlocks.mushroomStem.blockID,
            NMBlocks.mushroomFloorPartial.blockID,
            Block.planks.blockID,
            NMBlocks.mushInnardsBreakable.blockID,
            Block.blockIron.blockID,
            Block.brick.blockID,
            Block.bookShelf.blockID,
            Block.cobblestoneMossy.blockID,
            Block.obsidian.blockID,
            BTWBlocks.gearBox.blockID,
            BTWBlocks.planter.blockID,
            Block.netherrack.blockID,
            Block.slowSand.blockID,
            Block.glowStone.blockID,
            Block.sponge.blockID,
            Block.blockLapis.blockID,
    };

    int[] meta = new int[] {
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

    // === MAIN MUSHROOM GENERATION ============================================
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        String path = "structures/mushroom.nbt";
        placeFromNBT(
                world,
                box,
                this.boundingBox.minX,
                this.boundingBox.minY,
                this.boundingBox.minZ,
                path,
                blockIDs,
                meta
        );
        return true;
    }

    public static void placeFromNBT(World world, StructureBoundingBox box, int baseX, int baseY, int baseZ, String resourcePath, int[] blockIDs, int[] meta) {
        try {
            InputStream in = NightmareMode.class
                    .getClassLoader()
                    .getResourceAsStream(resourcePath);

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

                int wx = baseX + x;
                int wy = baseY + y;
                int wz = baseZ + z;

                if (!box.isVecInside(wx, wy, wz)) continue;

                world.setBlockAndMetadata(
                        wx, wy, wz,
                        blockIDs[state],
                        meta[state]
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void place(World world, int blockID, int metadata, int localX, int localY, int localZ, StructureBoundingBox boundingBox) {
        this.placeBlockAtCurrentPosition(world, blockID, metadata, localX, localY, localZ, boundingBox);
    }

}