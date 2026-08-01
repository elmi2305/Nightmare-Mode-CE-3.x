package com.itlesports.nightmaremode.structure;

import btw.entity.mob.villager.BlacksmithVillagerEntity;
import btw.entity.mob.villager.LibrarianVillagerEntity;
import btw.entity.mob.villager.PriestVillagerEntity;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import net.minecraft.src.Block;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;

import java.util.Random;

public abstract class NetherVillagerPost extends NMStructure {
    private static final PaletteEntry[] PLACEHOLDER_PALETTE = createPlaceholderPalette();
    private int spawnedVillagerMask;
    private boolean loggedGeneration;

    protected NetherVillagerPost() {}

    protected NetherVillagerPost(Random random, int x, int z, int sizeX, int sizeY, int sizeZ) {
        super(random, x, 64, z, sizeX, sizeY, sizeZ);
        this.shouldGenerateAir = true;
    }

    protected abstract int getVillagerProfession();

    protected abstract double getVillagerHorizontalOffset();

    protected abstract double getVillagerVerticalOffset();

    protected abstract int getTier();

    @Override
    protected PaletteEntry[] getPalette() {
        return PLACEHOLDER_PALETTE;
    }

    @Override
    protected void afterStructurePlaced(World world, Random random, StructureBoundingBox box) {
        int centerX = this.boundingBox.getCenterX();
        int centerZ = this.boundingBox.getCenterZ();
        double centerY = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY + 1) / 2.0D;

        if (!this.loggedGeneration) {
            System.out.println("Generated tier " + getTier() + " Nether villager post at "
                    + centerX + ", " + (int) centerY + ", " + centerZ);
            this.loggedGeneration = true;
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
            EntityVillager villager = createVillager(world);
            villager.setLocationAndAngles(villagerX, villagerY, villagerZ, random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntityInWorld(villager);
            this.spawnedVillagerMask |= 1 << index;
        }
    }

    private EntityVillager createVillager(World world) {
        if (getVillagerProfession() == 1) {
            return new LibrarianVillagerEntity(world);
        }
        if (getVillagerProfession() == 2) {
            return new PriestVillagerEntity(world);
        }
        return new BlacksmithVillagerEntity(world);
    }

    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);
        tag.setInteger("NmVillagers", this.spawnedVillagerMask);
        tag.setBoolean("NmLogged", this.loggedGeneration);
    }

    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);
        this.spawnedVillagerMask = tag.getInteger("NmVillagers");
        this.loggedGeneration = tag.getBoolean("NmLogged");
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
