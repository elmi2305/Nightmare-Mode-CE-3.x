package com.itlesports.nightmaremode.structure;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.EntityNetherPostVillager;
import com.itlesports.nightmaremode.entity.EntityTier1NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier2NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier3NetherVillager;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import net.minecraft.src.Block;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;

import java.util.Random;

public abstract class NetherVillagerPost extends NMStructure {
    private static PaletteEntry[] PLACEHOLDER_PALETTE = null;
    private int spawnedVillagerMask;
    private boolean loggedGeneration;
    private boolean progressionGemPlaced;

    protected NetherVillagerPost() {}

    protected NetherVillagerPost(Random random, int x, int z, int sizeX, int sizeY, int sizeZ) {
        super(random, x, 45, z, sizeX, sizeY, sizeZ);
        this.shouldGenerateAir = true;
    }

    protected abstract int getVillagerProfession();

    protected abstract double getVillagerHorizontalOffset();

    protected abstract double getVillagerVerticalOffset();

    protected abstract int getTier();

    @Override
    protected PaletteEntry[] getPalette() {
        if (PLACEHOLDER_PALETTE == null) {
            PLACEHOLDER_PALETTE = createPlaceholderPalette();
        }
        return PLACEHOLDER_PALETTE;
    }

    @Override
    protected void afterStructurePlaced(World world, Random random, StructureBoundingBox box) {
        int centerX = this.boundingBox.getCenterX();
        int centerZ = this.boundingBox.getCenterZ();
        double centerY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY + 1) / 2.0D;

        if (!this.loggedGeneration) {
            System.out.println("Generated tier " + getTier() + " Nether villager post at "
                    + centerX + " " + (int) centerY + " " + centerZ);
            this.loggedGeneration = true;
        }

        int gemY = this.boundingBox.minY + 1;
        if (!this.progressionGemPlaced && box.isVecInside(centerX, gemY, centerZ)) {
            world.setBlockAndMetadataWithNotify(centerX, gemY, centerZ,
                    NMBlocks.netherProgressionGems.blockID, this.getTier() - 1);
            this.progressionGemPlaced = true;
        }

        double horizontalOffset = getVillagerHorizontalOffset();
        double villagerY = centerY - getVillagerVerticalOffset();
        double[] xSigns = {1.0D, 1.0D, -1.0D, -1.0D};
        double[] zSigns = {1.0D, -1.0D, 1.0D, -1.0D};
        for (int index = 0; index < 4; ++index) {
            if ((this.spawnedVillagerMask & 1 << index) != 0) {
                continue;
            }
            double villagerX = centerX + xSigns[index] * horizontalOffset;
            double villagerZ = centerZ + zSigns[index] * horizontalOffset;
            if (!box.isVecInside((int) Math.floor(villagerX), (int) Math.floor(villagerY), (int) Math.floor(villagerZ))) {
                continue;
            }
            EntityNetherPostVillager villager = createVillager(world);
            villager.setPostGroup(centerX, centerZ, index);
            villager.setLocationAndAngles(villagerX, villagerY, villagerZ, random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntityInWorld(villager);
            this.spawnedVillagerMask |= 1 << index;
        }
    }

    private EntityNetherPostVillager createVillager(World world) {
        if (getTier() == 1) {
            return new EntityTier1NetherVillager(world);
        }
        if (getTier() == 2) {
            return new EntityTier2NetherVillager(world);
        }
        return new EntityTier3NetherVillager(world);
    }

    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);
        tag.setInteger("NmVillagers", this.spawnedVillagerMask);
        tag.setBoolean("NmLogged", this.loggedGeneration);
        tag.setBoolean("NmGemPlaced", this.progressionGemPlaced);
    }

    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);
        this.spawnedVillagerMask = tag.getInteger("NmVillagers");
        this.loggedGeneration = tag.getBoolean("NmLogged");
        this.progressionGemPlaced = tag.getBoolean("NmGemPlaced");
        this.shouldGenerateAir = true;
    }

    private static PaletteEntry[] createPlaceholderPalette() {
        PaletteEntry[] palette = new PaletteEntry[512];
        palette[0] = block(0, 0);
        for (int index = 1; index < palette.length; ++index) {
            palette[index] = block(Block.netherBrick.blockID, 0);
        }
        return palette;
    }
}
