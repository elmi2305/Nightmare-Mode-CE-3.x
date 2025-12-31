package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySilverfish.class)
public class EntitySilverfishMixin extends EntityMob{
    public EntitySilverfishMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySilverfish;attackEntityAsMob(Lnet/minecraft/src/Entity;)Z"))
    private void infectPlayer(Entity par1Entity, float par2, CallbackInfo ci){
        if(par1Entity instanceof EntityPlayer target && this.worldObj != null){
            if (this.rand.nextFloat() < 0.05 * NMUtils.getNiteMultiplier() && NMUtils.getWorldProgress()>1) {
                this.setDead();
                target.addPotionEffect(new PotionEffect(Potion.wither.id,300,0));
            }
            if (this.rand.nextInt(2) == 0) {
                target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 0));
            } else target.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 60, 0));
            if(NMUtils.getIsMobEclipsed(this)){
                target.addPotionEffect(new PotionEffect(Potion.poison.id, 260, 0));
                this.setDead();
            }
        }
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int looting) {
        if (bKilledByPlayer && NMUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (looting * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.silverLump.itemID;

            int var4 = this.rand.nextInt(3);
            if (looting > 0) {
                var4 += this.rand.nextInt(looting + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }
        if (!NMUtils.getIsMobEclipsed(this)) {
            this.dropItem(Item.clay.itemID, this.rand.nextInt(3)+1); // drops clay regardless of dimension, dropping more in the end
        }
        super.dropFewItems(bKilledByPlayer, looting);
    }
}
