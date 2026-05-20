package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MushroomCapBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.entity.EntityMushWorm;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.BlockMushroomCap;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(MushroomCapBlock.class)
public class MushroomCapBlockMixin extends BlockMushroomCap {
    @Shadow @Final protected int mushroomType;

    public MushroomCapBlockMixin(int i, Material material, int j) {
        super(i, material, j);
    }

    @Inject(method = "idDropped", at = @At("HEAD"),cancellable = true)
    private void changeIDDropped(int iMetadata, Random rand, int iFortuneModifier, CallbackInfoReturnable<Integer> cir){
        if (this.mushroomType != 0) {
            cir.setReturnValue(BTWItems.redMushroom.itemID);
            return;
        }
        if(NMUtils.getWorldProgress() > 0){
            cir.setReturnValue(BTWItems.brownMushroom.itemID);
        }
        cir.setReturnValue(0);
    }

    @Override
    public void breakBlock(World w, int x, int y, int z, int side, int meta) {

        if (w.rand.nextInt(4) == 0 && !w.isRemote) {
            EntityMushWorm worm = new EntityMushWorm(w);
            worm.setPositionAndUpdate(x + 0.5f,y,z + 0.5f);
            w.spawnEntityInWorld(worm);
        }
        super.breakBlock(w, x, y, z, side, meta);
    }
}
