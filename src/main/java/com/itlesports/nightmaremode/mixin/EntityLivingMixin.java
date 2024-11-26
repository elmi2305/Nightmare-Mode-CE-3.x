package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBase {
    public EntityLivingMixin(World par1World) {
        super(par1World);
    }

    @Shadow public abstract ItemStack getCurrentItemOrArmor(int par1);

    @Shadow public abstract void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack);

    @Shadow public abstract ItemStack getHeldItem();

    @Shadow protected abstract boolean canDespawn();

    @Shadow private boolean persistenceRequired;

    @Redirect(method = "entityLivingAddRandomArmor", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0))
    private float returnRandomFloatButLower(Random rand){
        return (rand.nextFloat()-0.008F);
        // rand.nextFloat()   <  0.0033F ( original number: 0.0025F), from 1/400 to 1/303 chance
    }

    @Inject(method = "entityLivingAddRandomArmor", at = @At("TAIL"))
    private void chanceToSpawnWithLeatherArmor(CallbackInfo ci) {
        if (this.worldObj != null) {
            float streakModifier = 0.0f;
            for (int i = 1; i <= 4; i++) {
                if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                    if(rand.nextFloat() < (0.04f + NightmareUtils.getWorldProgress(this.worldObj)*0.02) + streakModifier){
                        // 0.04f -> 0.06f -> 0.08f -> 0.10f
                        streakModifier += 0.05f;
                        List<ItemStack> leatherArmorList = getItemStacks();
                        this.setCurrentItemOrArmor(i, leatherArmorList.get(i-1));
                    }
                }
            }
        }
    }

    @Inject(method = "despawnEntity", at = @At(value = "TAIL"))
    private void manageDespawnDuringBloodMoon(CallbackInfo ci){
        if (this.canDespawn() && !this.persistenceRequired && this.ticksExisted % 200 == 199 && NightmareUtils.getIsBloodMoon()) {
            EntityPlayer testPlayer = this.worldObj.getClosestVulnerablePlayer(this.posX,this.posY,this.posZ,128);
            if(testPlayer != null){
                if((testPlayer.posY - this.posY > 20) || (this.posY - testPlayer.posY > 20)){
                    if(rand.nextInt(3)==0 && this.worldObj.getBlockMaterial((int) this.posX, (int) (this.posY-1), (int) this.posZ) != Material.wood){
                        this.setDead();
                    }
                }
            } else{
                this.setDead();
            }
        }
    }

//    @Redirect(method = "isInsideSpawnAreaAroundChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getMobSpawnRangeInChunks()I"))
//    private int manageBloodMoonSpawningNearPlayer(World world){
//        if(NightmareUtils.getIsBloodMoon(world)){
//            return 0;
//        }
//        return world.getClampedViewDistanceInChunks() - 2;
//    }
//
//    @Inject(method = "isInsideSpawnAreaAroundOriginalSpawn", at = @At("HEAD"), cancellable = true)
//    private void manageBloodMoonSpawningNearPlayer(CallbackInfoReturnable<Boolean> cir){
//        if(NightmareUtils.getIsBloodMoon(this.worldObj)){
//            cir.setReturnValue(false);
//        }
//    }


        @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLiving;entityDropItem(Lnet/minecraft/src/ItemStack;F)Lnet/minecraft/src/EntityItem;"))
    private EntityItem doNotDropItemsIfVariant(EntityLiving instance, ItemStack itemStack, float v){
        if (itemStack.stackSize == 0) {
            return null;
        } else {
            if (checkNullAndCompareID(new ItemStack(Item.swordDiamond), instance.getHeldItem())
            || checkNullAndCompareID(new ItemStack(Item.swordWood), instance.getHeldItem())
            || checkNullAndCompareID(new ItemStack(Item.axeGold), instance.getHeldItem())

            || checkNullAndCompareID(new ItemStack(Item.helmetLeather), instance.getCurrentItemOrArmor(4))
            || checkNullAndCompareID(new ItemStack(Item.plateLeather), instance.getCurrentItemOrArmor(3))
            || checkNullAndCompareID(new ItemStack(Item.legsLeather), instance.getCurrentItemOrArmor(2))
            || checkNullAndCompareID(new ItemStack(Item.bootsLeather), instance.getCurrentItemOrArmor(1))

            || checkNullAndCompareID(new ItemStack(BTWItems.woolBoots), instance.getCurrentItemOrArmor(1))
            || checkNullAndCompareID(new ItemStack(BTWItems.woolLeggings), instance.getCurrentItemOrArmor(2))
            || checkNullAndCompareID(new ItemStack(BTWItems.woolChest), instance.getCurrentItemOrArmor(3))
            || checkNullAndCompareID(new ItemStack(BTWItems.woolHelmet), instance.getCurrentItemOrArmor(4))
            ) {
                return null; // yes it's all hardcoded, might make it not sometime down the road
            } else {
                EntityItem var3 = new EntityItem(this.worldObj, this.posX, this.posY + (double) v, this.posZ, itemStack);
                var3.delayBeforeCanPickup = 10;
                this.worldObj.spawnEntityInWorld(var3);
                return var3;
            }
        }
    }

    @Unique
    public boolean checkNullAndCompareID(ItemStack par1ItemStack, ItemStack par2ItemStack){
        if(par1ItemStack != null && par2ItemStack != null){
            return par1ItemStack.itemID == par2ItemStack.itemID;
        } else {return false;}
    }

    @Unique
    private static @NotNull List<ItemStack> getItemStacks() {
        ItemStack boots = new ItemStack(Item.bootsLeather);
        ItemStack pants = new ItemStack(Item.legsLeather);
        ItemStack chest = new ItemStack(Item.plateLeather);
        ItemStack helmet = new ItemStack(Item.helmetLeather);

        List<ItemStack> leatherArmorList = new ArrayList<>(4);
        leatherArmorList.add(boots);
        leatherArmorList.add(pants);
        leatherArmorList.add(chest);
        leatherArmorList.add(helmet);
        return leatherArmorList;
    }
}
