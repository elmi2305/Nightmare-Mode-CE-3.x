package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
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

    @Redirect(method = "entityLivingAddRandomArmor", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0))
    private float returnRandomFloatButLower(Random rand){
        return (rand.nextFloat()-0.008F);
        // rand.nextFloat()   <  0.0033F ( original number: 0.0025F), from 1/400 to 1/303 chance
    }

    @Inject(method = "entityLivingAddRandomArmor", at = @At("TAIL"))
    private void chanceToSpawnWithLeatherArmor(CallbackInfo ci) {
        float streakModifier = 0.0f;
        for (int i = 1; i <= 4; i++) {
            if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                Random rand = new Random();
                if(rand.nextFloat() < (0.04f + NightmareUtils.getGameProgressMobsLevel(this.worldObj)*0.02) + streakModifier){
                    // 0.04f -> 0.06f -> 0.08f -> 0.10f
                    streakModifier += 0.05f;
                    List<ItemStack> leatherArmorList = getItemStacks();
                    this.setCurrentItemOrArmor(i, leatherArmorList.get(i-1));
                }
            }
        }
    }

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
//
//    @Unique private static @NotNull List<ItemStack> getIllegalItems() {
//        ItemStack boots = new ItemStack(Item.bootsLeather);
//        ItemStack pants = new ItemStack(Item.legsLeather);
//        ItemStack chest = new ItemStack(Item.plateLeather);
//        ItemStack helmet = new ItemStack(Item.helmetLeather);
//        ItemStack socks = new ItemStack(BTWItems.woolBoots);
//        ItemStack woolPants = new ItemStack(BTWItems.woolLeggings);
//        ItemStack woolShirt = new ItemStack(BTWItems.woolChest);
//        ItemStack woolHat = new ItemStack(BTWItems.woolHelmet);
//        ItemStack diamondSword = new ItemStack(Item.swordDiamond);
//        ItemStack goldenAxe = new ItemStack(Item.axeGold);
//        ItemStack woodSword = new ItemStack(Item.swordWood);
//        ItemStack boneClub = new ItemStack(BTWItems.boneClub);
//        ItemStack infusedSkull = new ItemStack(Item.skull,1,5);
//        ItemStack runedSkull = new ItemStack(Item.skull,1,1);
//
//        List<ItemStack> illegalItemList = new ArrayList<>(4);
//        illegalItemList.add(new ItemStack(Item.bootsLeather));
//        illegalItemList.add(new ItemStack(Item.legsLeather));
//        illegalItemList.add(new ItemStack(Item.plateLeather));
//        illegalItemList.add(new ItemStack(Item.helmetLeather));
//        illegalItemList.add(new ItemStack(BTWItems.woolBoots));
//        illegalItemList.add(new ItemStack(BTWItems.woolLeggings));
//        illegalItemList.add(new ItemStack(BTWItems.woolChest));
//        illegalItemList.add(new ItemStack(BTWItems.woolHelmet));
//        illegalItemList.add(new ItemStack(Item.swordDiamond));
//        illegalItemList.add(new ItemStack(Item.axeGold));
//        illegalItemList.add(new ItemStack(Item.swordWood));
//        illegalItemList.add(new ItemStack(BTWItems.boneClub));
//        illegalItemList.add(new ItemStack(Item.skull,1,5));
//        illegalItemList.add(new ItemStack(Item.skull,1,1));
//        return illegalItemList;
//    }

    @Unique public boolean checkNullAndCompareID(ItemStack par1ItemStack, ItemStack par2ItemStack){
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
