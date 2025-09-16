package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.entity.EntityObsidianFish;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockObsidian.class)
public class BlockObsidianMixin extends Block {
    protected BlockObsidianMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void lowerResistance(int par1, CallbackInfo ci){
        this.setResistance(64);
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return NMItems.obsidianShard.itemID;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
        if (!world.isRemote) {
            this.dropItemsIndividually(world, i, j, k, NMItems.obsidianShard.itemID, Math.min(world.rand.nextInt(3) + 3 + iFortuneModifier, 8), 0, 1.0F);
        }
    }


    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        super.onBlockHarvested(world, x, y, z, meta, player);


        if(world.rand.nextInt(8) == 0){
            EntityObsidianFish fish = new EntityObsidianFish(world);
            fish.setPositionAndUpdate(x + 0.5, y + 0.1, z + 0.5);
            fish.setAttackTarget(player);
            world.spawnEntityInWorld(fish);
        }
    }
}
