package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.BiomeDecorator;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeDecorator.class)
public interface BiomeDecoratorAccessor {
    @Accessor("currentWorld")
    World getCurrentWorld();
    @Accessor("chunk_X")
    int getChunkX();
    @Accessor("chunk_Z")
    int getChunkZ();
}
