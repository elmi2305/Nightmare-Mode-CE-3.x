package com.itlesports.nightmaremode.mixin;

import api.item.items.SeedFoodItem;
import api.item.items.ToolItem;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.FoodItem;
import com.itlesports.nightmaremode.item.itemblock.NetherrackItemBlock;
import com.itlesports.nightmaremode.item.items.ItemPlaceableStick;
import com.itlesports.nightmaremode.mixin.interfaces.ItemAccessor;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
        int netherBrickID = BTWItems.netherBrick.itemID;
        Item.itemsList[netherBrickID] = null;
        BTWItems.netherBrick = new com.itlesports.nightmaremode.item.items.ItemPlaceableNetherBrick(netherBrickID - 256);
        BTWItems.haft = new ItemPlaceableStick(BTWItems.haft.itemID - 256)
                .setBuoyant().setIncineratedInCrucible().setUnlocalizedName("fcItemHaft")
                .setCreativeTab(net.minecraft.src.CreativeTabs.tabMaterials).setTextureName("btw:haft");
        carrot = new SeedFoodItem(22341, 1, 0.0f, BTWBlocks.floweringCarrotCrop.blockID).setAsBasicPigFood().setUnlocalizedName("fcItemCarrot").setTextureName("carrot");
        cookedCarrot = new FoodItem(22246, 1, 0.0f, false, "fcItemCarrotCooked").setAsBasicPigFood().setTextureName("btw:cooked_carrot");
        boiledPotato = new FoodItem(22242, 1, 0.0f, false, "fcItemPotatoBoiled").setAsBasicPigFood().setTextureName("btw:boiled_potato");

        ((ItemAccessor)pointyStick).invSetMaxDamage(1);
        ((ItemAccessor)Item.shovelStone).invSetMaxDamage(128);
        ((ToolItem) Item.shovelWood).addCustomEfficiencyMultiplier(0.5f);
        ((ToolItem) Item.shovelStone).addCustomEfficiencyMultiplier(1.3f);
        ((ToolItem) BTWItems.pointyStick).addCustomEfficiencyMultiplier(0.7f);
        ((ItemAccessor)Item.shovelWood).invSetMaxDamage(32);
        Item.itemsList[Block.netherrack.blockID] = new NetherrackItemBlock(Block.netherrack.blockID - 256)
                .setUnlocalizedName("netherrack");
    }
}
