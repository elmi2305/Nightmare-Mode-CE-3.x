package com.itlesports.nightmaremode.mixin.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockGlass.class)
public class BlockGlassMixin extends BlockBreakable {
    protected BlockGlassMixin(int i, String string, Material material, boolean bl) {
        super(i, string, material, bl);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon("NmGlass");
    }
}
