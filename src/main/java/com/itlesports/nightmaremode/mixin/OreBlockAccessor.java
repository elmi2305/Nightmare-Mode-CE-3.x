package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.OreBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OreBlock.class)
public interface OreBlockAccessor {
    @Environment(value= EnvType.CLIENT)
    @Accessor("iconByMetadataArray")
    void setIconArray(Icon[] par1);
}
