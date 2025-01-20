package com.itlesports.nightmaremode.mixin.gui;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.ContainerRepair;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ContainerRepair.class)
public class ContainerRepairMixin {
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
        return Item.itemsList[repairItem.itemID].getIsRepairable(stack1,stack2);
    }
}
