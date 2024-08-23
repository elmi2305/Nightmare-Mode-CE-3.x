package com.itlesports.nightmaremode.mixin;

import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.world.biome.BiomeDecoratorBase;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BiomeDecorator.class)
public abstract class BiomeDecoratorMixin implements BiomeDecoratorAccessor, BiomeDecoratorBase {
    @Shadow public abstract void decorate(World par1World, Random par2Random, int par3, int par4);

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
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 8);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void generateLavaPillows(CallbackInfo ci){
        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
    }

    @Inject(method = "generateOres", at = @At("TAIL"))
    private void manageLavaPillowGen(CallbackInfo ci){
        BiomeDecorator thisObj = (BiomeDecorator)(Object)this;
        thisObj.genStandardOre1(30, this.lavaPillowGenThirdStrata, 0, 24);
//        lava pillow generation currently crashes
        thisObj.genStandardOre1(8, this.silverfishGenFirstStrata, 50, 80);
        thisObj.genStandardOre1(16, this.silverfishGenSecondStrata, 24, 50);
        thisObj.genStandardOre1(24, this.silverfishGenThirdStrata, 0, 24);
    }


//    @Redirect(method = "genStandardOre1", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 0))
//    private int betterRandomGenX(Random instance, int i){
//        return instance.nextInt(15);
//    }
//    @Redirect(method = "genStandardOre1", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 2))
//    private int betterRandomGenZ(Random instance, int i){
//        return instance.nextInt(15);
//    }


//    @Unique
//    private void genLavaPillow(int par1, WorldGenerator par2WorldGenerator, int par3, int par4) {
//        for (int var5 = 0; var5 < par1; ++var5) {
//            int var6 = this.getChunkX() + this.getRandomGenerator().nextInt(16) + 8;
//            int var7 = this.getRandomGenerator().nextInt(par4 - par3) + par3;
//            int var8 = this.getChunkZ() + this.getRandomGenerator().nextInt(16) + 8;
//            par2WorldGenerator.generate(this.getCurrentWorld(), this.getRandomGenerator(), var6, var7, var8);
//        }
//    }
}
