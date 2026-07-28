package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MillstoneBlock;
import net.minecraft.src.Icon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MillstoneBlock.class)
public interface MillstoneBlockAccessor {
    @Accessor("iconsBySide")
    void nightmareMode$setIconsBySide(Icon[] icons);

    @Accessor("iconsBySideFull")
    void nightmareMode$setIconsBySideFull(Icon[] icons);

    @Accessor("iconsBySideOn")
    void nightmareMode$setIconsBySideOn(Icon[] icons);

    @Accessor("iconsBySideOnFull")
    void nightmareMode$setIconsBySideOnFull(Icon[] icons);
}
