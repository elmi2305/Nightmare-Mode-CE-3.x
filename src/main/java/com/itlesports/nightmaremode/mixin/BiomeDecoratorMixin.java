package com.itlesports.nightmaremode.mixin;

import btw.BTWAddon;
import btw.block.BTWBlocks;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BiomeDecorator.class)
public abstract class BiomeDecoratorMixin implements BiomeDecoratorAccessor{
                                // MEA code. credit to Pot_tx. lets silverfish spawn in all biomes.
    @Unique
    protected WorldGenerator silverfishGenFirstStrata;
    @Unique
    protected WorldGenerator silverfishGenSecondStrata;
    @Unique
    protected WorldGenerator silverfishGenThirdStrata;
    @Unique
    protected WorldGenerator lavaPillowGenThirdStrata;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void setSilverfishGen(CallbackInfo ci) {
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 4);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void generateLavaPillows(CallbackInfo ci){
        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
    }

    @Inject(
            method = "decorate()V",
            at = @At(value = "TAIL")
    )
    private void addSilverfishGenToDecoration(CallbackInfo ci) {
        this.genSilverfish();
        this.genLavaPillow();
    }

    @Unique
    protected void genSilverfish() {
        BiomeDecorator thisObj = (BiomeDecorator)(Object)this;

        Random rand = new Random();
        for (int i = 0; i< 9; i++) {
            int x = thisObj.chunk_X + rand.nextInt(16);
            int y = rand.nextInt(64);
            int z = thisObj.chunk_Z + rand.nextInt(16);

            if ( y <= 48 + rand.nextInt( 2 ) && thisObj.currentWorld == null)
            {
                if ( y <= 24 + rand.nextInt( 2 ) )
                {
                    silverfishGenThirdStrata.generate(thisObj.currentWorld, rand, x, y, z);
                }
                silverfishGenSecondStrata.generate(thisObj.currentWorld, rand, x, y, z);
            }
            silverfishGenFirstStrata.generate(thisObj.currentWorld, rand, x, y, z);
        }
    }

    @Unique
    protected void genLavaPillow() {
        BiomeDecorator thisObj = (BiomeDecorator)(Object)this;
        Random rand = new Random();
        for (int i = 0; i< 24; i++) {
            int x = thisObj.chunk_Z + rand.nextInt(16);
            int y = rand.nextInt(24);
            int z = thisObj.chunk_X + rand.nextInt(16);
            if (y <= 24 + rand.nextInt(2)) {
                lavaPillowGenThirdStrata.generate(thisObj.currentWorld, rand, x, y, z);
            }
        }
    }
}
