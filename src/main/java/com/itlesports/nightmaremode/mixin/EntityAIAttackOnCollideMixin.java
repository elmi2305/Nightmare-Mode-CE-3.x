package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.RottenArrowEntity;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityAIAttackOnCollide.class)
public abstract class EntityAIAttackOnCollideMixin {
    @Unique private int arrowCooldown = 10;

    @Inject(method = "updateTask", at = @At("TAIL"))
    private void increaseRangeOnToolHeld(CallbackInfo ci){
        EntityAIAttackOnCollide thisObj = (EntityAIAttackOnCollide)(Object)this;
        if(thisObj.attacker.getAttackTarget() != null){
            if(thisObj.attacker.getDistanceSqToEntity(thisObj.attacker.getAttackTarget()) < computeRangeForHeldItem(thisObj.attacker.getHeldItem())
                    && thisObj.attacker.worldObj.getDifficulty() == Difficulties.HOSTILE
                    && thisObj.attackTick <= 1
                    && thisObj.attacker.canEntityBeSeen(thisObj.attacker.getAttackTarget())){
                thisObj.attacker.swingItem();
                thisObj.attacker.attackEntityAsMob(thisObj.attacker.getAttackTarget());
                thisObj.attackTick = 13;
            }
            if(NightmareUtils.getIsMobEclipsed(thisObj.attacker)){
                if(thisObj.attacker.getDistanceSqToEntity(thisObj.attacker.getAttackTarget()) < 3){
                    thisObj.attacker.swingItem();
                    thisObj.attacker.attackEntityAsMob(thisObj.attacker.getAttackTarget());
                    thisObj.attackTick = 20;
                }
            }
        }
    }

    @Inject(method = "updateTask", at = @At("TAIL"))
    private void manageArrowDeflection(CallbackInfo ci){
        EntityAIAttackOnCollide thisObj = (EntityAIAttackOnCollide)(Object)this;

        if(thisObj.attacker.worldObj != null && thisObj.attacker.getAttackTarget() instanceof EntityPlayer targetPlayer && thisObj.attacker.worldObj.getDifficulty() == Difficulties.HOSTILE){
            if(isPlayerHoldingBow(targetPlayer) && (isHoldingLongRangeItem(thisObj.attacker)) || NightmareUtils.getIsMobEclipsed(thisObj.attacker)){
                List list = thisObj.attacker.worldObj.getEntitiesWithinAABBExcludingEntity(thisObj.attacker, thisObj.attacker.boundingBox.expand(2.5, 2.4, 2.5));
                this.arrowCooldown -= 1;
                if (this.arrowCooldown <= 0) {
                    this.arrowCooldown = 0;
                    for (Object tempEntity : list) {
                        if (!(tempEntity instanceof EntityArrow arrow)) continue;

                        if (!arrow.inGround) {
                            EntityArrow newArrow = new EntityArrow(thisObj.attacker.worldObj,thisObj.attacker,targetPlayer,1f,6);
                            newArrow.copyLocationAndAnglesFrom(arrow);
                            newArrow.motionX = -arrow.motionX / 1.5;
                            newArrow.motionY = -arrow.motionY;
                            newArrow.motionZ = -arrow.motionZ / 1.5;
                            thisObj.attacker.worldObj.playSoundAtEntity(arrow,"random.break",1.0f,5f);
                            arrow.setDead();
                            thisObj.worldObj.spawnEntityInWorld(newArrow);
                            this.arrowCooldown = 40;
                            thisObj.attacker.swingItem();
                            break;
                        }

                        if (tempEntity instanceof RottenArrowEntity rottenArrow){
                            thisObj.attacker.worldObj.playSoundAtEntity(rottenArrow,"random.break",1.0f,5f);
                            rottenArrow.setDead();
                            thisObj.attacker.swingItem();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Unique private int computeRangeForHeldItem(ItemStack heldItem){
        if (heldItem != null && getLongRangeItems().contains(heldItem.itemID)) {
            if(getLesserRangeItems().contains(heldItem.itemID)){
                return 5;
            }
            return 10;
        }
        return NightmareMode.isAprilFools ? 7 : 2;
        // this method is mirrored in ZombieBreakBarricadeBehaviorHostileMixin. So are all the lists used in this method. Updating one means you need to update the other too
    }

    @Unique private boolean isPlayerHoldingBow(EntityPlayer player){
        return player.getHeldItem() != null && (player.getHeldItem().itemID == Item.bow.itemID || player.getHeldItem().itemID == BTWItems.compositeBow.itemID);
    }

    @Unique private boolean isHoldingLongRangeItem(EntityLiving entity){
        return entity.getHeldItem() != null && getLongRangeItems().contains(entity.getHeldItem().itemID);
    }

    @Unique
    private static @NotNull List<Integer> getLongRangeItems() {
        List<Integer> longRangeItemList = new ArrayList<>(16);
        longRangeItemList.add(Item.swordStone.itemID);
        longRangeItemList.add(Item.swordIron.itemID);
        longRangeItemList.add(Item.swordGold.itemID);
        longRangeItemList.add(BTWItems.steelSword.itemID);
        longRangeItemList.add(Item.axeStone.itemID);
        longRangeItemList.add(Item.axeDiamond.itemID);
        longRangeItemList.add(Item.axeIron.itemID);
        longRangeItemList.add(Item.shovelIron.itemID);
        longRangeItemList.add(Item.shovelStone.itemID);
        longRangeItemList.add(Item.shovelGold.itemID);
        longRangeItemList.add(Item.shovelDiamond.itemID);

        longRangeItemList.add(BTWItems.boneClub.itemID);
        longRangeItemList.add(Item.swordWood.itemID);
        longRangeItemList.add(Item.swordDiamond.itemID);
        longRangeItemList.add(Item.axeGold.itemID);
        longRangeItemList.add(Item.pickaxeStone.itemID);

        return longRangeItemList;
    }
    @Unique
    private static @NotNull List<Integer> getLesserRangeItems() {
        List<Integer> lesserRangeItemList = new ArrayList<>(14);
        lesserRangeItemList.add(BTWItems.boneClub.itemID);
        lesserRangeItemList.add(Item.swordWood.itemID);
        lesserRangeItemList.add(Item.swordDiamond.itemID);
        lesserRangeItemList.add(Item.axeGold.itemID);
        return lesserRangeItemList;
    }
}
