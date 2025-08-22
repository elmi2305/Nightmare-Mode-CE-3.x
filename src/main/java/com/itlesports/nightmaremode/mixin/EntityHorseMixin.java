package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.KickingAnimal;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityHorse.class)
public abstract class EntityHorseMixin extends KickingAnimal {
    public EntityHorseMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 20.0d))
    private double increaseHP(double constant){
        return 24.0 * NMUtils.getNiteMultiplier();
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,4);
    }
    @Inject(method = "isSubjectToHunger", at = @At("HEAD"),cancellable = true)
    private void manageEclipseHunger(CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsMobEclipsed(this)){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NMUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (lootingLevel * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.greg.itemID;

            int var4 = this.rand.nextInt(3);
            if (lootingLevel > 0) {
                var4 += this.rand.nextInt(lootingLevel + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }

        }
    }
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void horseSpeed(CallbackInfo ci){
        if(this.ticksExisted % 120 != 0) return;
        int originalHealth = 24;
        double eclipseModifier = NMUtils.getIsEclipse() ? 1.5 : 1;
        if(this.getMaxHealth() != originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier);
        }

        float speed = (float) ((NMUtils.getIsMobEclipsed(this) ? 0.4f : 0.225f) * NMUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(speed);
    }
}
