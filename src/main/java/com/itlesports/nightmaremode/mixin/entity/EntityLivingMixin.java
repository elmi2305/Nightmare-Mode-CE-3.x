package com.itlesports.nightmaremode.mixin.entity;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(EntityLiving.class)
public abstract class   EntityLivingMixin extends EntityLivingBase {
    public EntityLivingMixin(World par1World) {
        super(par1World);
    }
    @Shadow public abstract ItemStack getCurrentItemOrArmor(int par1);
    @Shadow public abstract void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack);
    @Shadow public abstract ItemStack getHeldItem();
    @Shadow protected abstract boolean canDespawn();
    @Shadow private boolean persistenceRequired;
    @Shadow protected float[] equipmentDropChances;

    @Redirect(method = "entityLivingAddRandomArmor", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0))
    private float returnRandomFloatButLower(Random rand){
        return (rand.nextFloat() - 0.008F);
        // rand.nextFloat()   <  0.0033F ( original number: 0.0025F), from 1/400 to 1/303 chance
    }

    @Inject(method = "entityLivingAddRandomArmor", at = @At("TAIL"))
    private void chanceToSpawnWithLeatherArmor(CallbackInfo ci) {
        if (this.worldObj != null) {
            float streakModifier = 0.0f;
            List<Integer> leatherArmor = getLeatherArmor();
            for (int i = 1; i <= 4; i++) {
                if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                    if(this.rand.nextFloat() < (0.04f + NMUtils.getWorldProgress() * 0.02f) + streakModifier){
                        // 0.04f -> 0.06f -> 0.08f -> 0.10f
                        streakModifier += 0.05f;
                        this.setCurrentItemOrArmor(i, new ItemStack(leatherArmor.get(i - 1), 1, this.rand.nextInt(EnumArmorMaterial.CLOTH.getDurability(i - 1))));
                        this.equipmentDropChances[i] = -1f;
                    }
                }
            }
        }
    }
    @Inject(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLiving;entityDropItem(Lnet/minecraft/src/ItemStack;F)Lnet/minecraft/src/EntityItem;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void stopIfAttemptingToDropBadArmor(boolean par1, int par2, CallbackInfo ci, int var3, ItemStack itemToDrop){
        if(itemToDrop != null && getItemsToAvoidDropping().contains(itemToDrop.itemID)){
            ci.cancel();
        }
    }

    @Inject(method = "despawnEntity", at = @At(value = "TAIL"))
    private void manageDespawnDuringBloodMoon(CallbackInfo ci){
        if (this.canDespawn() && !this.persistenceRequired && this.ticksExisted % 300 == 299 && NMUtils.getIsBloodMoon()) {
            EntityPlayer nearestPlayer = this.worldObj.getClosestVulnerablePlayer(this.posX, this.posY, this.posZ, 128);

            if (nearestPlayer != null) {
                double verticalDistance = Math.abs(nearestPlayer.posY - this.posY);

                if (verticalDistance > 20) {
                    boolean isOnNonWoodMaterial = this.worldObj.getBlockMaterial(
                            (int) this.posX,
                            (int) (this.posY - 1),
                            (int) this.posZ
                    ) != Material.wood;

                    if (rand.nextInt(3) == 0 && isOnNonWoodMaterial) {
                        this.setDead();
                    }
                }
            } else {
                this.setDead();
            }
        }
        // mirrored in EntityMobMixin
    }

    @Unique private static List<Integer> leatherArmorList = new ArrayList<>(4);

    @Unique private static @NotNull List<Integer> getLeatherArmor() {
        if (leatherArmorList.isEmpty()) {
            leatherArmorList.add(Item.bootsLeather.itemID);
            leatherArmorList.add(Item.legsLeather.itemID);
            leatherArmorList.add(Item.plateLeather.itemID);
            leatherArmorList.add(Item.helmetLeather.itemID);
        }
        return leatherArmorList;
    }

    @Unique private static List<Integer> itemsNotDropped = new ArrayList<>();

    @Unique private static @NotNull List<Integer> getItemsToAvoidDropping() {
        if (itemsNotDropped.isEmpty()) {
            itemsNotDropped.add(BTWItems.woolBoots.itemID);
            itemsNotDropped.add(BTWItems.woolLeggings.itemID);
            itemsNotDropped.add(BTWItems.woolChest.itemID);
            itemsNotDropped.add(BTWItems.woolHelmet.itemID);
            itemsNotDropped.add(Item.bootsLeather.itemID);
            itemsNotDropped.add(Item.legsLeather.itemID);
            itemsNotDropped.add(Item.plateLeather.itemID);
            itemsNotDropped.add(Item.helmetLeather.itemID);
            itemsNotDropped.add(Item.swordWood.itemID);
            itemsNotDropped.add(Item.pickaxeStone.itemID);
            itemsNotDropped.add(BTWItems.steelSword.itemID);
            itemsNotDropped.add(BTWBlocks.carvedPumpkin.blockID);
            itemsNotDropped.add(BTWItems.boneClub.itemID);
            itemsNotDropped.add(Item.axeIron.itemID);
        }
        return itemsNotDropped;
    }
}
