package com.itlesports.nightmaremode.mixin;

import api.item.items.FireStarterItemPrimitive;
import api.item.items.SeedFoodItem;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.*;
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

    @Shadow public static Item netherStar;
    @Shadow public static Item potato;
    @Shadow public static Item bakedPotato;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceItems(CallbackInfo ci){
        horseArmorIron    = new ItemAdvancedHorseArmor(161, ItemAdvancedHorseArmor.ArmorTier.IRON).setUnlocalizedName("horsearmormetal").setTextureName("iron_horse_armor");
        horseArmorGold    = new ItemAdvancedHorseArmor(162, ItemAdvancedHorseArmor.ArmorTier.GOLD).setUnlocalizedName("horsearmorgold").setTextureName("gold_horse_armor");
        horseArmorDiamond = new ItemAdvancedHorseArmor(163, ItemAdvancedHorseArmor.ArmorTier.DIAMOND).setUnlocalizedName("horsearmordiamond").setTextureName("diamond_horse_armor");


        bootsDiamond = (ItemArmor) bootsDiamond.setTextureName("nightmare:nmDiamondBoots");
        helmetDiamond = (ItemArmor) helmetDiamond.setTextureName("nightmare:nmDiamondHelmet");
        plateDiamond  = (ItemArmor) plateDiamond.setTextureName("nightmare:nmDiamondChestplate");
        legsDiamond = (ItemArmor) legsDiamond.setTextureName("nightmare:nmDiamondLeggings");

        potato = new SeedFoodItem(136, 1, 0.0f, Block.potato.blockID).setAsBasicPigFood().setUnlocalizedName("potato").setTextureName("potato");
        bakedPotato = new ItemFood(137, 1, 0.0f, false).setAsBasicPigFood().setUnlocalizedName("potatoBaked").setTextureName("potato_baked");

        netherStar = netherStar.setMaxDamage(4);
    }
}
