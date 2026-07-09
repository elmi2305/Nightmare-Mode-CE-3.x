package com.itlesports.nightmaremode.mixin;

import api.item.items.SeedFoodItem;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.FoodItem;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWItems.class)
public class BTWItemsMixin {

    @Shadow public static Item carrot;
    @Shadow public static Item cookedCarrot;
    @Shadow public static Item boiledPotato;

    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 0), index = 2)
    private static int steelHelmet(int w){
        return 1;
    }
    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 1), index = 2)
    private static int steelChest(int w){
        return 4;
    }
    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 2), index = 2)
    private static int steelLegs(int w){
        return 3;
    }
    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 3), index = 2)
    private static int steelBoots(int w){
        return 1;
    }

    @Inject(method = "instantiateModItems", at = @At("TAIL"), remap = false)
    private static void replaceItems(CallbackInfo ci){
        carrot = new SeedFoodItem(22341, 1, 0.0f, BTWBlocks.floweringCarrotCrop.blockID).setAsBasicPigFood().setUnlocalizedName("fcItemCarrot").setTextureName("carrot");
        cookedCarrot = new FoodItem(22246, 1, 0.0f, false, "fcItemCarrotCooked").setAsBasicPigFood().setTextureName("btw:cooked_carrot");
        boiledPotato = new FoodItem(22242, 1, 0.0f, false, "fcItemPotatoBoiled").setAsBasicPigFood().setTextureName("btw:boiled_potato");

    }
}
