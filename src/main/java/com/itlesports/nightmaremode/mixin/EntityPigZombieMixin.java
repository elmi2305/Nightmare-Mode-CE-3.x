package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.SharedMonsterAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityPigZombie.class)
public class EntityPigZombieMixin {
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void attackNearestPlayer(CallbackInfo ci){
        EntityPigZombie thisObj = (EntityPigZombie)(Object)this;
        List list = thisObj.worldObj.getEntitiesWithinAABBExcludingEntity(thisObj, thisObj.boundingBox.expand(3.0, 3.0, 3.0));
        for (Object tempEntity : list) {
            if (!(tempEntity instanceof EntityPlayer player)) continue;
            thisObj.entityToAttack = player;
            break;
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        EntityPigZombie thisObj = (EntityPigZombie)(Object)this;
        thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16 + 4*NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj));
        thisObj.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3 + 2*NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj));
    }
}
