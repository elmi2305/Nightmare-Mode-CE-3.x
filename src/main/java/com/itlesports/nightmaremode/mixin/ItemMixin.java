package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Shadow public static Item horseArmorIron;
    @Shadow public static Item horseArmorGold;
    @Shadow public static Item horseArmorDiamond;
    @Shadow public static ItemArmor bootsDiamond;
    @Shadow public static ItemArmor helmetDiamond;
    @Shadow public static ItemArmor plateDiamond;
    @Shadow public static ItemArmor legsDiamond;


    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceItems(CallbackInfo ci){
        horseArmorIron    = new ItemAdvancedHorseArmor(161, ItemAdvancedHorseArmor.ArmorTier.IRON).setUnlocalizedName("horsearmormetal").setTextureName("iron_horse_armor");
        horseArmorGold    = new ItemAdvancedHorseArmor(162, ItemAdvancedHorseArmor.ArmorTier.GOLD).setUnlocalizedName("horsearmorgold").setTextureName("gold_horse_armor");
        horseArmorDiamond = new ItemAdvancedHorseArmor(163, ItemAdvancedHorseArmor.ArmorTier.DIAMOND).setUnlocalizedName("horsearmordiamond").setTextureName("diamond_horse_armor");


        bootsDiamond = (ItemArmor) bootsDiamond.setTextureName("nmDiamondBoots");
        helmetDiamond = (ItemArmor) helmetDiamond.setTextureName("nmDiamondHelmet");
        plateDiamond  = (ItemArmor) plateDiamond.setTextureName("nmDiamondChestplate");
        legsDiamond = (ItemArmor) legsDiamond.setTextureName("nmDiamondLeggings");
    }
}
