package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.BiomeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeDecorator.class)
public interface BiomeDecoratorAccess {
    @Accessor("treesPerChunk")
    void setTreesPerChunk(int var1);
    @Accessor("flowersPerChunk")
    void setFlowersPerChunk(int var1);
    @Accessor("grassPerChunk")
    void setGrassPerChunk(int var1);
    @Accessor("deadBushPerChunk")
    void setDeadBushPerChunk(int var1);
    @Accessor("reedsPerChunk")
    void setReedsPerChunk(int var1);
    @Accessor("cactiPerChunk")
    void setCactiPerChunk(int var1);
}
