package com.itlesports.nightmaremode.mixin;

import api.item.items.FireStarterItemPrimitive;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWItems.class)
public class BTWItemsMixin {
    @Shadow public static Item firePlough;
    @Shadow public static Item bowDrill;

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
        firePlough = new FireStarterItemPrimitive(22313, 330, 0.04f, -0.1f, 0.1f, 0.0011f).setUnlocalizedName("fcItemFireStarterSticks").setTextureName("btw:fire_plough");
        bowDrill = new FireStarterItemPrimitive(22314, 400, 0.025f, -0.1f, 0.1f , 0.0056f).setUnlocalizedName("fcItemFireStarterBow").setTextureName("btw:bow_drill");

        // old stats:
//        firePlough = new FireStarterItemPrimitive(22313, 250, 0.05f, -0.1f, 0.1f, 0.001f).setUnlocalizedName("fcItemFireStarterSticks").setTextureName("btw:fire_plough");
//        bowDrill = new FireStarterItemPrimitive(22314, 250, 0.025f, -0.1f, 0.1f, 0.004f).setUnlocalizedName("fcItemFireStarterBow").setTextureName("btw:bow_drill");

    }
}
