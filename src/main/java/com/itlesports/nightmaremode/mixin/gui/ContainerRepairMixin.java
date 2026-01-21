package com.itlesports.nightmaremode.mixin.gui;

import api.item.items.ToolItem;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.bloodItems.IBloodTool;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ContainerRepair.class)
public class ContainerRepairMixin {
    @Shadow private IInventory inputSlots;
    @Unique
    private static final List<Integer> steelToolsAndArmor = new ArrayList<>(Arrays.asList(
            BTWItems.steelSword.itemID,
            BTWItems.steelPickaxe.itemID,
            BTWItems.steelAxe.itemID,
            BTWItems.steelShovel.itemID,
            BTWItems.mattock.itemID,
            BTWItems.battleaxe.itemID,
            BTWItems.steelHoe.itemID,
            BTWItems.plateHelmet.itemID,
            BTWItems.plateBreastplate.itemID,
            BTWItems.plateLeggings.itemID,
            BTWItems.plateBoots.itemID
    ));

    @Unique
    private static final List<Integer> bloodToolsAndArmor = new ArrayList<>(Arrays.asList(
            NMItems.bloodPickaxe.itemID,
            NMItems.bloodAxe.itemID,
            NMItems.bloodShovel.itemID,
            NMItems.bloodHoe.itemID,
            NMItems.bloodSword.itemID,

            NMItems.bloodHelmet.itemID,
            NMItems.bloodChestplate.itemID,
            NMItems.bloodLeggings.itemID,
            NMItems.bloodBoots.itemID
    ));
    @Unique List<Integer> ironSet = Arrays.asList(
            Item.swordIron.itemID,
            Item.shovelIron.itemID,
            Item.pickaxeIron.itemID,
            Item.axeIron.itemID,
            Item.hoeIron.itemID,
            BTWItems.ironChisel.itemID,

            Item.helmetIron.itemID,
            Item.plateIron.itemID,
            Item.legsIron.itemID,
            Item.bootsIron.itemID
    );
    @Unique List<Integer> goldSet = Arrays.asList(
            Item.swordGold.itemID,
            Item.shovelGold.itemID,
            Item.pickaxeGold.itemID,
            Item.axeGold.itemID,
            Item.hoeGold.itemID,

            Item.helmetGold.itemID,
            Item.plateGold.itemID,
            Item.legsGold.itemID,
            Item.bootsGold.itemID
    );
    @Unique List<Integer> diamondSet = Arrays.asList(
            Item.swordDiamond.itemID,
            Item.shovelDiamond.itemID,
            Item.pickaxeDiamond.itemID,
            Item.axeDiamond.itemID,
            Item.hoeDiamond.itemID,
            BTWItems.diamondChisel.itemID,
            BTWItems.diamondShears.itemID,
            Item.helmetDiamond.itemID,
            Item.plateDiamond.itemID,
            Item.legsDiamond.itemID,
            Item.bootsDiamond.itemID
    );


    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/NBTTagList;tagCount()I"))
    private int removeAncientManuscriptProtectionAnvil(NBTTagList instance) {
        return 0;
    }

    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Item;getIsRepairable(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/ItemStack;)Z"))
    private boolean manageSteelRepair(Item repairItem, ItemStack stack1, ItemStack stack2){
        if(steelToolsAndArmor.contains(repairItem.itemID)){
            return stack2.getItem().itemID == BTWItems.soulforgedSteelIngot.itemID;
        }
        if(bloodToolsAndArmor.contains(repairItem.itemID)){
            return stack2.getItem().itemID == NMItems.bloodIngot.itemID;
        }
        if(ironSet.contains(repairItem.itemID)){
            return stack2.getItem().itemID == Item.ingotIron.itemID;
        }
        if(goldSet.contains(repairItem.itemID)){
            return stack2.getItem().itemID == Item.ingotGold.itemID;
        }
        if(diamondSet.contains(repairItem.itemID)){
            return stack2.getItem().itemID == Item.diamond.itemID || stack2.getItem().itemID == BTWItems.diamondIngot.itemID;
        }
        return Item.itemsList[repairItem.itemID].getIsRepairable(stack1,stack2);
    }


    @ModifyConstant(method = "updateRepairOutput", constant = @Constant(intValue = 40))
    private int increaseCap(int constant){
        return 100;
    }
//    @ModifyConstant(method = "updateRepairOutput", constant = @Constant(intValue = 100, ordinal = 0))
//    private int reduceScalingForDamageRepaired(int constant){
//        return 100;
//        // the damage that is repaired with the operation scales by dividing by 100. this increases the denominator
//    }
    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I"))
    private int ignoreEnchantmentsApplied(Map instance){
        return 0;
    }

    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;getRepairCost()I", ordinal =  0))
    private int changeFirstItemRepairCostInitial(ItemStack instance){
        return instance.getRepairCost() / 2;
    }
    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;getRepairCost()I", ordinal =  1))
    private int changeSecondItemRepairCostInitial(ItemStack instance){
        return instance.getRepairCost() / 2;
    }

    @ModifyArg(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;setRepairCost(I)V"))
    private int changeCostIncreasePerSuccessfulRepair(int par1){
        ItemStack stack = this.inputSlots.getStackInSlot(0);
        if(stack != null && (stack.getItem() instanceof IBloodTool || (stack.getItem() instanceof ToolItem ti && ti.toolMaterial == EnumToolMaterial.SOULFORGED_STEEL)) ){
            return par1 - 2;
            // no penalty on repairing blood or tools
        }
        return par1 - 1;
        // 1 level of penalty every repair on everything
    }

    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Enchantment;getWeight()I"))
    private int makeAllEnchantmentsHaveSameWeight(Enchantment instance){
        return 10;
    }
    @Unique
    private boolean isFirstEnchantment;


    @Inject(method = "updateRepairOutput", at = @At("HEAD"))
    private void ensureFirstTime(CallbackInfo ci){
        if(!this.isFirstEnchantment){
            this.isFirstEnchantment = true;
        }
    }
    @Redirect(method = "updateRepairOutput", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 2))
    private boolean doNotCheckEnchantmentsNormally(Iterator itr){
        if(itr.hasNext() && this.isFirstEnchantment){
            this.isFirstEnchantment = false;
            return true;
        }
        return false;
    }
}
