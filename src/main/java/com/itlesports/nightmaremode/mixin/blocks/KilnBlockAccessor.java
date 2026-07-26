package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.KilnBlock;
import net.minecraft.src.Icon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KilnBlock.class)
public interface KilnBlockAccessor {
    @Accessor("cookIcons")
    Icon[] nightmareMode$getCookIcons();
}
