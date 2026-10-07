package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockTorch;
import net.minecraft.src.World;

import java.util.Random;

public class BlockSoulTorch extends BlockTorch {
    public BlockSoulTorch(int id) {
        super(id);
        setLightValue(0.85F);
        setHardness(0.5F);
        setResistance(2.0F);
        setStepSound(soundGlassFootstep);
        setUnlocalizedName("nmSoulTorch");
        setTextureName("nightmare:nmSoulTorch");
    }

    @Override
    public String getModId() {
        return "nightmare";
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int facing = world.getBlockMetadata(x, y, z);
        double particleX = x + 0.5;
        double particleY = y + 0.7;
        double particleZ = z + 0.5;
        if (facing >= 1 && facing <= 4) {
            particleY += 0.22;
            if (facing == 1) particleX -= 0.27;
            if (facing == 2) particleX += 0.27;
            if (facing == 3) particleZ -= 0.27;
            if (facing == 4) particleZ += 0.27;
        }
        // retain torch smoke without an orange flame over the soul-colored tip.
        world.spawnParticle("smoke", particleX, particleY, particleZ, 0.0, 0.0, 0.0);
    }
}
