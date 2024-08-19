package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin(EntityMob.class)
public class EntityMobMixin{

    @Inject(method = "entityMobAttackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void mobMagicImmunity(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        EntityMob thisObj = (EntityMob)(Object)this;
        if((par1DamageSource == DamageSource.magic || par1DamageSource == DamageSource.wither || par1DamageSource == DamageSource.fallingBlock) && (thisObj instanceof EntityWitch || thisObj instanceof EntitySpider || thisObj instanceof EntitySilverfish || thisObj instanceof EntityCreeper)){
            cir.setReturnValue(false);
        }
    }
//
//    @ModifyConstant(method = "entityMobAttackEntity", constant = @Constant(floatValue = 2.0f))
//    private float increaseTooledEntityRange(float constant){
//        return 5f;
//    }

//    @Inject(method = "entityMobAttackEntity", at = @At("HEAD"))
//    private void manageIncreasedRange(Entity par1Entity, float par2, CallbackInfo ci){
//        EntityMob thisObj = (EntityMob)(Object)this;
//        if(thisObj instanceof EntityZombie || thisObj instanceof EntitySkeleton){
//            ItemStack heldItem = thisObj.getHeldItem();
//            if(heldItem != null && getIllegalItems().contains(heldItem.itemID)){
//                if (thisObj.attackTime <= 0 && par2 < 5.0f) {
//                    thisObj.attackTime = 20;
//                    thisObj.attackEntityAsMob(par1Entity);
//                }
//            }
//        }
//    }
//    @Redirect(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityMob;entityMobAttackEntity(Lnet/minecraft/src/Entity;F)V"))
//    private void manageIncreasedRange(EntityMob instance, Entity par1Entity, float par2){
//        if(instance instanceof EntityZombie || instance instanceof EntitySkeleton){
//            ItemStack heldItem = instance.getHeldItem();
//            if(heldItem != null && getIllegalItems().contains(heldItem.itemID)){
//                if (instance.attackTime <= 0 && par2 < 5.0f) {
//                    instance.attackTime = 20;
//                    instance.attackEntityAsMob(par1Entity);
//                }
//            }
//        }
//        if (instance.attackTime <= 0 && par2 < 2.0f && par1Entity.boundingBox.maxY > instance.boundingBox.minY && par1Entity.boundingBox.minY < instance.boundingBox.maxY) {
//            instance.attackTime = 20;
//            instance.attackEntityAsMob(par1Entity);
//        }
//    }

    @Unique private static @NotNull List<Integer> getIllegalItems() {
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
