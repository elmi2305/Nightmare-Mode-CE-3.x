package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehaviorHostile;
import btw.item.BTWItems;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ZombieBreakBarricadeBehaviorHostile.class)
public class ZombieBreakBarricadeBehaviorHostileMixin extends ZombieBreakBarricadeBehavior{
    public ZombieBreakBarricadeBehaviorHostileMixin(EntityLiving par1EntityLiving) {
        super(par1EntityLiving);
    }

    @Inject(method = "updateTask", at = @At("HEAD"))
    private void tooLZombieBreaksFaster(CallbackInfo ci){
        if (this.associatedEntity.getHeldItem() != null) {
            ItemStack heldItem = this.associatedEntity.getHeldItem();

            if(getFastItems().contains(heldItem.itemID)){
                this.breakingTime += 1;
                if(this.targetBlock.blockMaterial == Material.ground && heldItem.getDisplayName().contains("Shovel")){
                    this.breakingTime += 3;
                }
            }
            if(this.breakingTime > 240){
                this.breakingTime = 240;
            }
        }
    }
    @Unique private int targetCanBeMovedToCounter = 0;
    @Unique private int timesTriggered = 0;
    @Inject(method = "continueExecuting", at = @At("HEAD"),cancellable = true)
    private void manageWhenToStop(CallbackInfoReturnable<Boolean> cir){
        Entity target = this.associatedEntity.getAttackTarget();
        if(this.associatedEntity.isAirBorne){
            cir.setReturnValue(false);
        }
        if (target == null) return;
        if (this.associatedEntity.ticksExisted % 3 != 0) return;


        // all runs every 3rd tick

        double doorPosYMinus1 = this.doorPosY - 1.0F;
        double doorPosYMinus2 = this.doorPosY - 2.0F;

        double dDistSqToDoor = Math.min(
                Math.min(
                        this.associatedEntity.getDistanceSq(this.doorPosX, this.doorPosY, this.doorPosZ),
                        this.associatedEntity.getDistanceSq(this.doorPosX, doorPosYMinus1, this.doorPosZ)
                ),
                this.associatedEntity.getDistanceSq(this.doorPosX, doorPosYMinus2, this.doorPosZ)
        );

        double dDistSqToTarget = this.associatedEntity.getDistanceSqToEntity(target);

        if (dDistSqToTarget <= computeRangeForHeldItem(this.associatedEntity.getHeldItem()) && this.associatedEntity.canEntityBeSeen(target)) {
            this.associatedEntity.swingItem();
            this.associatedEntity.attackEntityAsMob(target);
            this.associatedEntity.attackTime = 20;
            cir.setReturnValue(false);
        }

        if (dDistSqToTarget < dDistSqToDoor ||
                (
                        (this.associatedEntity.getEntitySenses().canSee(target) || this.associatedEntity.canEntityBeSeen(target))
                         && this.associatedEntity.getNavigator().getPathToXYZ(target.posX, target.posY, target.posZ) != null
                )
        ) {
            this.targetCanBeMovedToCounter = Math.min(this.targetCanBeMovedToCounter + 1, 20);
        } else{
            this.targetCanBeMovedToCounter = Math.max(this.targetCanBeMovedToCounter - 1, 0);
        }
        if(this.targetCanBeMovedToCounter == 20 && this.breakingTime < 200 && this.timesTriggered != 3) {
            cir.setReturnValue(false);
            this.timesTriggered = Math.min(this.timesTriggered + 1, 3);
        }

        if(this.targetCanBeMovedToCounter == 0 && this.breakingTime == 0 && this.timesTriggered > 0 && this.associatedEntity.ticksExisted % 30 == 0){
            --this.timesTriggered;
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
        // this method is mirrored in EntityAIAttackOnCollideMixin. So are all the lists used in this method. Updating one means you need to update the other too
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

    @Unique
    private static @NotNull List<Integer> getFastItems() {
        List<Integer> fastItemList = new ArrayList<>(14);
        fastItemList.add(Item.swordStone.itemID);
        fastItemList.add(Item.swordIron.itemID);
        fastItemList.add(Item.swordGold.itemID);
        fastItemList.add(Item.axeStone.itemID);
        fastItemList.add(Item.axeDiamond.itemID);
        fastItemList.add(Item.axeIron.itemID);
        fastItemList.add(Item.shovelIron.itemID);
        fastItemList.add(Item.shovelStone.itemID);
        fastItemList.add(Item.shovelGold.itemID);
        fastItemList.add(Item.shovelDiamond.itemID);
        fastItemList.add(Item.pickaxeStone.itemID);

        fastItemList.add(BTWItems.boneClub.itemID);
        fastItemList.add(Item.swordWood.itemID);
        fastItemList.add(Item.swordDiamond.itemID);
        fastItemList.add(Item.axeGold.itemID);
        return fastItemList;
    }
}
