package com.itlesports.nightmaremode.mixin;

import api.item.items.SeedFoodItem;
import api.item.items.ToolItem;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.items.FoodItem;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.itemblock.NetherrackItemBlock;
import com.itlesports.nightmaremode.item.items.ItemPlaceableStick;
import com.itlesports.nightmaremode.mixin.interfaces.ItemAccessor;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
        multiplyDurability(3, Item.swordIron, Item.pickaxeIron, Item.axeIron, Item.shovelIron, Item.hoeIron,
                Item.helmetIron, Item.plateIron, Item.legsIron, Item.bootsIron, Item.shears, BTWItems.ironChisel,
                NMItems.ironScythe, NMItems.ironKnife, NMItems.ironLeafRake, NMItems.ironFishingPole,
                NMItems.ironFishingPoleBaited, NMItems.ironKnittingNeedles, NMItems.farmersFavoriteHoe);
        multiplyDurability(2, Item.swordDiamond, Item.pickaxeDiamond, Item.axeDiamond, Item.shovelDiamond, Item.hoeDiamond,
                Item.helmetDiamond, Item.plateDiamond, Item.legsDiamond, Item.bootsDiamond, BTWItems.diamondShears, BTWItems.diamondChisel,
                NMItems.diamondHammer, NMItems.diamondScythe, NMItems.diamondKnife, NMItems.diamondLeafRake,
                NMItems.diamondFishingPole, NMItems.diamondFishingPoleBaited);
        multiplyDurability(2, BTWItems.steelSword, BTWItems.steelPickaxe, BTWItems.steelAxe, BTWItems.steelShovel, BTWItems.steelHoe,
                BTWItems.battleaxe, BTWItems.mattock,
                BTWItems.plateHelmet, BTWItems.plateBreastplate, BTWItems.plateLeggings, BTWItems.plateBoots,
                NMItems.steelHammer, NMItems.steelFishingPole, NMItems.steelFishingPoleBaited);
        multiplyDurability(2, Item.helmetChain, Item.plateChain, Item.legsChain, Item.bootsChain);
        Item.itemsList[Block.netherrack.blockID] = new NetherrackItemBlock(Block.netherrack.blockID - 256)
                .setUnlocalizedName("netherrack");
    }

    @Unique
    private static void multiplyDurability(int multiplier, Item... items) {
        for (Item item : items) ((ItemAccessor)item).invSetMaxDamage(item.getMaxDamage() * multiplier);
    }
}
