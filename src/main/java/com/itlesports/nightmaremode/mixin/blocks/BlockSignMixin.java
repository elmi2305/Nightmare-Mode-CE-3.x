package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.BlockSign;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockSign.class)
public abstract class BlockSignMixin extends BlockContainer {

    protected BlockSignMixin(int i, Material material) {
        super(i, material);
    }

    @Override
    public int onBlockPlaced(World w, int x, int y, int z, int par5, float par6, float par7, float par8, int par9) {
        if(NightmareMode.getInstance().isGriefLogging() && !w.isRemote){
            String text = "Sign placed at " + x + " " + y + " " + z + ". Nearest Player: " + w.getClosestPlayer(x,y,z, 32).username;
            NightmareMode.appendLogLine(text);
        }
        return super.onBlockPlaced(w, x, y, z, par5, par6, par7, par8, par9);
    }
}
