package com.itlesports.nightmaremode.mixin;

import btw.entity.RottenArrowEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityAIAttackOnCollide.class)
public abstract class EntityAIAttackOnCollideMixin implements EntityAIAttackOnCollideAccessor, EntityArrowAccessor{
    @Shadow public EntityCreature attacker;
    @Unique int arrowCooldown = 10;


    @Inject(method = "updateTask", at = @At("TAIL"))
    private void increaseRangeOnToolHeld(CallbackInfo ci){
        EntityAIAttackOnCollide thisObj = (EntityAIAttackOnCollide)(Object)this;
        if(thisObj.attacker.getAttackTarget() != null && thisObj.attacker.getDistanceSqToEntity(thisObj.attacker.getAttackTarget()) < computeRangeForHeldItem(thisObj.attacker.getHeldItem()) && isHoldingIllegalItem(thisObj.attacker) && thisObj.attacker.canEntityBeSeen(thisObj.attacker.getAttackTarget())){
            thisObj.attacker.swingItem();
            thisObj.attacker.attackEntityAsMob(thisObj.attacker.getAttackTarget());
        }
    }

    @Inject(method = "updateTask", at = @At("TAIL"))
    private void manageArrowDeflection(CallbackInfo ci){
        EntityAIAttackOnCollide thisObj = (EntityAIAttackOnCollide)(Object)this;
        if(thisObj.attacker.worldObj != null && thisObj.attacker.getAttackTarget() instanceof EntityPlayer targetPlayer){
            if(isPlayerHoldingBow(targetPlayer) && isHoldingIllegalItem(thisObj.attacker)){
                System.out.println("EntityAIAttackOnCollide about to make a list");
                List list = thisObj.attacker.worldObj.getEntitiesWithinAABBExcludingEntity(thisObj.attacker, thisObj.attacker.boundingBox.expand(2.0, 2.4, 2.0));
                arrowCooldown -= 1;
                if (arrowCooldown <= 0) {
                    for (Object tempEntity : list) {
                        arrowCooldown = 0;
                        if (tempEntity instanceof RottenArrowEntity rottenArrow){
                            thisObj.attacker.worldObj.playSoundAtEntity(rottenArrow,"random.break",1.0f,5f);
                            rottenArrow.setDead();
                            break;
                        }
                        if (!(tempEntity instanceof EntityArrow arrow)) continue;
                        if (!arrow.inGround && thisObj.attacker.rand.nextFloat()<0.5f) {
                            EntityArrow newArrow = new EntityArrow(thisObj.attacker.worldObj,thisObj.attacker,thisObj.attacker.getAttackTarget(),1f,6);
                            newArrow.motionX = (arrow.motionX * -1)/1.5;
                            newArrow.motionY = arrow.motionY * -1;
                            newArrow.motionZ = (arrow.motionZ * -1) / 1.5;
                            newArrow.posX = arrow.posX;
                            newArrow.posY = arrow.posY;
                            newArrow.posZ = arrow.posZ;
                            thisObj.attacker.worldObj.playSoundAtEntity(arrow,"random.break",1.0f,5f);
                            arrow.setDead();
                            thisObj.worldObj.spawnEntityInWorld(newArrow);
                            arrowCooldown = 20 + thisObj.attacker.rand.nextInt(20);
                            thisObj.attacker.swingItem();
                        }
                        break;
                    }
                }
            }
        }
    }


    @Inject(method = "updateTask", at = @At("TAIL"))
    private void manageJumpAttack(CallbackInfo ci){
        EntityAIAttackOnCollide thisObj = (EntityAIAttackOnCollide)(Object)this;
        if(thisObj.attacker instanceof EntityZombie zombie
                && zombie.getHeldItem() == null
                && zombie.ticksExisted % 50 ==  49
                && thisObj.attackTick <= 0
                && zombie.rand.nextInt(2)==0
                && zombie.getAttackTarget() instanceof EntityPlayer target
                && zombie.getDistanceSqToEntity(zombie.getAttackTarget()) < 30
                && zombie.onGround
                && zombie.canEntityBeSeen(target)
//                && zombie.getEntitySenses().canSee(target)
        ) {
            double var1 = target.posX - zombie.posX;
            double var2 = target.posZ - zombie.posZ;
            Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
            vector.normalize();
            zombie.motionX = vector.xCoord * 0.2;
            zombie.motionY = 0.34;
            zombie.motionZ = vector.zCoord * 0.2;
        }
    }
    @Unique private int computeRangeForHeldItem(ItemStack heldItem){
        if (heldItem != null && getIllegalItems().contains(heldItem.itemID)) {
            if((heldItem.itemID == Item.swordWood.itemID || heldItem.itemID == BTWItems.boneClub.itemID)){
                return 4;
            } else{return 8;}
        } return 2;
    }

    @Unique private boolean isPlayerHoldingBow(EntityPlayer player){
        return player.getHeldItem() != null && (player.getHeldItem().itemID == Item.bow.itemID || player.getHeldItem().itemID == BTWItems.compositeBow.itemID);
    }

    @Unique private boolean isHoldingIllegalItem(EntityLiving entity){
        return entity.getHeldItem() != null && getIllegalItems().contains(entity.getHeldItem().itemID);
    }

    @Unique
    private static @NotNull List<Integer> getIllegalItems() {
        List<Integer> illegalItemList = new ArrayList<>(14);
        illegalItemList.add(BTWItems.boneClub.itemID);
        illegalItemList.add(Item.swordWood.itemID);
        illegalItemList.add(Item.swordStone.itemID);
        illegalItemList.add(Item.swordIron.itemID);
        illegalItemList.add(Item.swordGold.itemID);
        illegalItemList.add(Item.swordDiamond.itemID);
        illegalItemList.add(Item.axeGold.itemID);
        illegalItemList.add(Item.axeStone.itemID);
        illegalItemList.add(Item.axeDiamond.itemID);
        illegalItemList.add(Item.axeIron.itemID);
        illegalItemList.add(Item.shovelIron.itemID);
        illegalItemList.add(Item.shovelStone.itemID);
        illegalItemList.add(Item.shovelGold.itemID);
        illegalItemList.add(Item.shovelDiamond.itemID);
        return illegalItemList;
    }
}
