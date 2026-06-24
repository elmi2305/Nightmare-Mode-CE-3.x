package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockSign.class)
public abstract class BlockSignMixin extends BlockContainer {
    protected BlockSignMixin(int i, Material material) {
        super(i, material);
    }

    @Override
    public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase entity, ItemStack item) {

        // does not work
//        if(NightmareMode.getInstance().isGriefLogging() && !w.isRemote){
//            String text = "Player " + entity.getEntityName() + " placed Sign at " + x + " " + y + " " + z;
//            NightmareMode.appendLogLine(text);
//        }
        super.onBlockPlacedBy(w, x, y, z, entity, item);
    }
}
