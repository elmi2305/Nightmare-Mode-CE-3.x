package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(EntityAIAttackOnCollide.class)
public abstract class EntityAIAttackOnCollideMixin {
    @Shadow public World worldObj;

    @Inject(method = "updateTask", at = @At("TAIL"))
    private void increaseRangeOnToolHeld(CallbackInfo ci) {
        EntityAIAttackOnCollide ai = (EntityAIAttackOnCollide)(Object)this;

        EntityLivingBase attacker = ai.attacker;
        EntityLivingBase target = ai.attacker.getAttackTarget();

        if (target == null) return;

        double distanceSq = attacker.getDistanceSqToEntity(target);
        int computedRange = computeRangeForHeldItem(attacker.getHeldItem());

        boolean isHostile = attacker.worldObj.getDifficulty() == Difficulties.HOSTILE;
        boolean canSeeTarget = attacker.canEntityBeSeen(target);

        if(target.ridingEntity instanceof EntityHorse horse){
            if(attacker.getDistanceSqToEntity(horse) < 6 && isHostile && ai.attackTick <= 1 && attacker.canEntityBeSeen(horse)){
                attacker.swingItem();
                attacker.attackEntityAsMob(horse);
                ai.attackTick = 8 - NMUtils.getWorldProgress() * 2;
            }
        }


        if (distanceSq < computedRange && isHostile && ai.attackTick <= 1 && canSeeTarget) {
            attacker.swingItem();
            attacker.attackEntityAsMob(target);
            ai.attackTick = 13 - NMUtils.getWorldProgress() * 2;
        }

        if (NMUtils.getIsMobEclipsed(attacker) && distanceSq < 3) {
            attacker.swingItem();
            attacker.attackEntityAsMob(target);
            ai.attackTick = 20;
        }
    }

    @Unique
    private int computeRangeForHeldItem(ItemStack heldItem) {
        if (heldItem == null) return NightmareMode.isAprilFools ? 7 : 2;

        int itemId = heldItem.itemID;
        if (NMUtils.LONG_RANGE_ITEMS.contains(itemId)) {
            return NMUtils.LESSER_RANGE_ITEMS.contains(itemId) ? 5 : 10;
        }
        return NightmareMode.isAprilFools ? 7 : 2;
    }
}
