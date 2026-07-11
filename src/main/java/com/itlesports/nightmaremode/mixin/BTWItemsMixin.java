package com.itlesports.nightmaremode.mixin;

import api.item.items.SeedFoodItem;
import api.item.items.ToolItem;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.FoodItem;
import com.itlesports.nightmaremode.mixin.interfaces.ItemAccessor;
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


    @Shadow
    public static Item pointyStick;

    @Inject(method = "instantiateModItems", at = @At("TAIL"), remap = false)
    private static void replaceItems(CallbackInfo ci){
        carrot = new SeedFoodItem(22341, 1, 0.0f, BTWBlocks.floweringCarrotCrop.blockID).setAsBasicPigFood().setUnlocalizedName("fcItemCarrot").setTextureName("carrot");
        cookedCarrot = new FoodItem(22246, 1, 0.0f, false, "fcItemCarrotCooked").setAsBasicPigFood().setTextureName("btw:cooked_carrot");
        boiledPotato = new FoodItem(22242, 1, 0.0f, false, "fcItemPotatoBoiled").setAsBasicPigFood().setTextureName("btw:boiled_potato");

        ((ItemAccessor)pointyStick).invSetMaxDamage(1);
        ((ToolItem) BTWItems.pointyStick).addCustomEfficiencyMultiplier(0.7f);
        ((ItemAccessor)Item.shovelWood).invSetMaxDamage(3);
    }
}
