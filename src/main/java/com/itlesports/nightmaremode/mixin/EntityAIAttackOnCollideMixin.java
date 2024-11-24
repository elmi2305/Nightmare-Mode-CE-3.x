package com.itlesports.nightmaremode.mixin;

import btw.entity.RottenArrowEntity;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
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
    @Unique int arrowCooldown = 10;

    @Inject(method = "updateTask", at = @At("TAIL"))
    private void increaseRangeOnToolHeld(CallbackInfo ci){
        EntityAIAttackOnCollide thisObj = (EntityAIAttackOnCollide)(Object)this;
        if(thisObj.attacker.getAttackTarget() != null
                && thisObj.attacker.getDistanceSqToEntity(thisObj.attacker.getAttackTarget()) < computeRangeForHeldItem(thisObj.attacker.getHeldItem()) // TODO: refactor the compute range method to take entity param instead of item stack
                && isHoldingIllegalItem(thisObj.attacker)
                && thisObj.attacker.worldObj.getDifficulty() == Difficulties.HOSTILE
                && thisObj.attackTick <= 1
                && thisObj.attacker.canEntityBeSeen(thisObj.attacker.getAttackTarget())) {
            thisObj.attacker.swingItem();
            thisObj.attacker.attackEntityAsMob(thisObj.attacker.getAttackTarget());
            thisObj.attackTick = 15;
        }
    }

    @Inject(method = "updateTask", at = @At("TAIL"))
    private void manageArrowDeflection(CallbackInfo ci){
        EntityAIAttackOnCollide thisObj = (EntityAIAttackOnCollide)(Object)this;
        if(thisObj.attacker.worldObj != null && thisObj.attacker.getAttackTarget() instanceof EntityPlayer targetPlayer && thisObj.attacker.worldObj.getDifficulty() == Difficulties.HOSTILE){
            if(isPlayerHoldingBow(targetPlayer) && isHoldingIllegalItem(thisObj.attacker)){
                List list = thisObj.attacker.worldObj.getEntitiesWithinAABBExcludingEntity(thisObj.attacker, thisObj.attacker.boundingBox.expand(2.0, 2.4, 2.0));
                arrowCooldown -= 1;
                if (arrowCooldown <= 0) {
                    arrowCooldown = 0;
                    for (Object tempEntity : list) {
                        if (!(tempEntity instanceof EntityArrow arrow)) continue;

                        if (!arrow.inGround && thisObj.attacker.rand.nextInt(2) == 0) {
                            EntityArrow newArrow = new EntityArrow(thisObj.attacker.worldObj,thisObj.attacker,targetPlayer,1f,6);
                            newArrow.copyLocationAndAnglesFrom(arrow);
                            newArrow.motionX = (arrow.motionX * -1)/1.5;
                            newArrow.motionY = arrow.motionY * -1;
                            newArrow.motionZ = (arrow.motionZ * -1) / 1.5;
                            thisObj.attacker.worldObj.playSoundAtEntity(arrow,"random.break",1.0f,5f);
                            arrow.setDead();
                            thisObj.worldObj.spawnEntityInWorld(newArrow);
                            arrowCooldown = 40;
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
        if (heldItem != null && getIllegalItems().contains(heldItem.itemID)) {
            if(getLesserRangeItems().contains(heldItem.itemID)){
                return 5;
            }
            return 9;
        }
        return 2;
    }

    @Unique private boolean isPlayerHoldingBow(EntityPlayer player){
        return player.getHeldItem() != null && (player.getHeldItem().itemID == Item.bow.itemID || player.getHeldItem().itemID == BTWItems.compositeBow.itemID);
    }

    @Unique private boolean isHoldingIllegalItem(EntityLiving entity){
        return entity.getHeldItem() != null && getIllegalItems().contains(entity.getHeldItem().itemID);
    }

    @Unique
    private static @NotNull List<Integer> getIllegalItems() {
        List<Integer> illegalItemList = new ArrayList<>(16);
        illegalItemList.add(Item.swordStone.itemID);
        illegalItemList.add(Item.swordIron.itemID);
        illegalItemList.add(Item.swordGold.itemID);
        illegalItemList.add(BTWItems.steelSword.itemID);
        illegalItemList.add(Item.axeStone.itemID);
        illegalItemList.add(Item.axeDiamond.itemID);
        illegalItemList.add(Item.axeIron.itemID);
        illegalItemList.add(Item.shovelIron.itemID);
        illegalItemList.add(Item.shovelStone.itemID);
        illegalItemList.add(Item.shovelGold.itemID);
        illegalItemList.add(Item.shovelDiamond.itemID);

        illegalItemList.add(BTWItems.boneClub.itemID);
        illegalItemList.add(Item.swordWood.itemID);
        illegalItemList.add(Item.swordDiamond.itemID);
        illegalItemList.add(Item.axeGold.itemID);
        illegalItemList.add(Item.pickaxeStone.itemID);

        return illegalItemList;
    }
    @Unique
    private static @NotNull List<Integer> getLesserRangeItems() {
        List<Integer> lesserRangeItemList = new ArrayList<>(14);
        lesserRangeItemList.add(BTWItems.boneClub.itemID);
        lesserRangeItemList.add(Item.swordWood.itemID);
        lesserRangeItemList.add(Item.swordDiamond.itemID);
        lesserRangeItemList.add(Item.axeGold.itemID);
        lesserRangeItemList.add(Item.pickaxeStone.itemID);
        return lesserRangeItemList;
    }
}
