package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import net.minecraft.src.BiomeDecorator;
import net.minecraft.src.WorldGenMinable;
import net.minecraft.src.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeDecorator.class)
public class BiomeDecoratorMixin {
                                // MEA code. credit to Pot_tx. lets silverfish spawn in all biomes.
    @Unique
    protected WorldGenerator silverfishGenFirstStrata;
    @Unique
    protected WorldGenerator silverfishGenSecondStrata;
    @Unique
    protected WorldGenerator silverfishGenThirdStrata;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void setSilverfishGen(CallbackInfo ci) {
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 4);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
    }
}
