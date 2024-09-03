package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.SharedMonsterAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
            if (this.isPlayerWearingGoldArmor(player)) continue;
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

    @Unique boolean isPlayerWearingGoldArmor(EntityPlayer player){
        return (player.getCurrentItemOrArmor(1) != null && player.getCurrentItemOrArmor(1).itemID == Item.bootsGold.itemID)
                || (player.getCurrentItemOrArmor(2) != null && player.getCurrentItemOrArmor(2).itemID == Item.legsGold.itemID)
                || (player.getCurrentItemOrArmor(3) != null && player.getCurrentItemOrArmor(3).itemID == Item.plateGold.itemID)
                || (player.getCurrentItemOrArmor(4) != null && player.getCurrentItemOrArmor(4).itemID == Item.helmetGold.itemID);
    }
}
