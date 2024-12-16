package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.OreBlock;
import net.minecraft.src.Icon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OreBlock.class)
public interface OreBlockAccessor {
    @Accessor("iconByMetadataArray")
    void setIconArray(Icon[] par1);
}
