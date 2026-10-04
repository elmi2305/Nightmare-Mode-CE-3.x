package com.itlesports.nightmaremode.mixin.blocks;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockClay.class)
public abstract class BlockClayMixin extends Block {
    protected BlockClayMixin(int blockId, Material material) {
        super(blockId, material);
    }

    @Inject(method = "quantityDroppedWithBonus", at = @At("HEAD"), cancellable = true)
    private void increaseEasyClayBallDrops(int fortune, Random random, CallbackInfoReturnable<Integer> cir) {
        if (BalanceProfile.isEasy()) {
            cir.setReturnValue(1 + random.nextInt(2));
        }
    }

    @Inject(method = "dropComponentItemsOnBadBreak", at = @At("HEAD"), cancellable = true)
    private void increaseEasyClayPileDrops(World world, int x, int y, int z, int metadata, float chance,
                                           CallbackInfoReturnable<Boolean> cir) {
        if (!BalanceProfile.isEasy()) return;
        if (!world.isRemote) {
            this.dropItemsIndividually(world, x, y, z, BTWItems.clayPile.itemID,
                    1 + world.rand.nextInt(2), 0, chance);
            this.dropItemsIndividually(world, x, y, z, BTWItems.dirtPile.itemID, 4, 0, chance);
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "dropBlockAsItemWithChance", at = @At("TAIL"))
    private void chanceToBeInfested(World world, int i, int j, int k, int iMetaData, float fChance, int iFortuneModifier, CallbackInfo ci){
        if(world.rand.nextFloat() < 0.08){
            EntitySilverfish silverfish = new EntitySilverfish(world);

            silverfish.setPositionAndUpdate(i+0.5,j,k+0.5);
            silverfish.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 100000, 0));
            silverfish.setAttackTarget(world.getClosestPlayerToEntity(silverfish,10));

            world.spawnEntityInWorld(silverfish);
        }
    }
}
