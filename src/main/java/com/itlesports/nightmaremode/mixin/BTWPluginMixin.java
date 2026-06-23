package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.block.blocks.MushroomBlockBrown;
import btw.block.blocks.MushroomCapBlock;
import btw.block.blocks.legacy.LegacyMushroomCapBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import net.minecraft.src.Block;
import net.minecraft.src.BlockMushroom;
import net.minecraft.src.BlockMushroomCap;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWPlugin.class)
public abstract class BTWPluginMixin {
    @Shadow protected abstract void info(EmiRegistry registry, Item item, String info);
    @Shadow protected abstract void info(EmiRegistry registry, Block block, String info);
    @Shadow protected abstract void info(EmiRegistry registry, Item item, int metadata, String info);
    @Shadow protected abstract void info(EmiRegistry registry, Block block, int metadata, String info);

    @Inject(method = "addInfoRecipes", at = @At("TAIL"),remap = false)
    private void addNightmareInfo(EmiRegistry registry, CallbackInfo ci){
        this.info(registry, NMItems.rpg, 0, "nm.rpg.info");
        this.info(registry, NMItems.rifle, 0, "nm.rifle.info");
        this.info(registry, NMItems.bandage, 0, "nm.bandage.info");
        this.info(registry, NMItems.witchLocator, 0, "nm.alchemydust.info");
        this.info(registry, NMItems.ironKnittingNeedles, 0, "nm.ironneedles.info");
        this.info(registry, NMItems.bloodOrb, 0, "nm.bloodorb.info");
        this.info(registry, NMItems.bloodPickaxe, 0, "nm.bloodpickaxe.info");
        this.info(registry, NMItems.bloodAxe, 0, "nm.bloodtool.info");
        this.info(registry, NMItems.bloodShovel, 0, "nm.bloodtool.info");
        this.info(registry, NMItems.bloodHoe, 0, "nm.bloodtool.info");
        this.info(registry, NMItems.bloodSword, 0, "nm.bloodsword.info");
        this.info(registry, NMItems.bloodHelmet, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodChestplate, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodLeggings, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodBoots, 0, "nm.bloodarmor.info");
        this.info(registry, NMItems.bloodIngot, 0, "nm.bloodingot.info");
        this.info(registry, NMItems.darksunFragment, 0, "nm.eclipseshard.info");
        this.info(registry, NMItems.bloodMilk, 0, "nm.bloodMilk.info");
        this.info(registry, NMItems.creeperChop, 0, "nm.creeperChop.info");
        this.info(registry, NMItems.voidSack, 0, "nm.voidSack.info");
        this.info(registry, NMItems.charredFlesh, 0, "nm.charredFlesh.info");
        this.info(registry, NMItems.spiderFangs, 0, "nm.spiderFangs.info");
        this.info(registry, NMItems.fireRod, 0, "nm.fireRod.info");
        this.info(registry, NMItems.waterRod, 0, "nm.waterRod.info");
        this.info(registry, NMItems.sulfur, 0, "nm.sulfur.info");
        this.info(registry, NMItems.creeperTear, 0, "nm.creeperTear.info");
        this.info(registry, NMItems.silverLump, 0, "nm.silverLump.info");
        this.info(registry, NMItems.witheredBone, 0, "nm.witheredBone.info");
        this.info(registry, NMItems.voidMembrane, 0, "nm.voidMembrane.info");
        this.info(registry, NMItems.decayedFlesh, 0, "nm.decayedFlesh.info");
        this.info(registry, NMItems.ghastTentacle, 0, "nm.ghastTentacle.info");
        this.info(registry, NMItems.elementalRod, 0, "nm.elementalRod.info");
        this.info(registry, NMItems.shadowRod, 0, "nm.shadowRod.info");
        this.info(registry, NMItems.speedCoil, 0, "nm.speedCoil.info");
        this.info(registry, NMItems.starOfTheBloodGod, 0, "nm.bloodstar.info");
        this.info(registry, NMItems.calamari, 0, "nm.calamari.info");
        this.info(registry, NMItems.eclipseBow, 0, "nm.eclipsebow.info");
        this.info(registry, NMItems.magicArrow, 0, "nm.magicarrow.info");
        this.info(registry, NMItems.ironFishingPole, 0, "nm.ironfishingrod.info");
        this.info(registry, NMItems.templeLocator, 0, "nm.ancientsand.info");
        this.info(registry, NMItems.refinedDiamondIngot, 0, "nm.refineddiamond.info");
        this.info(registry, NMItems.witherSoul, 0, "nm.withersoul.info");
        this.info(registry, NMItems.obsidianShard, 0, "nm.obsidianshard.info");

        // blocks

        this.info(registry, NMBlocks.steelOre, 0, "nm.steelOre.info");
        this.info(registry, NMBlocks.bloodChest, 0, "nm.bloodChest.info");
        this.info(registry, NMBlocks.steelLocker, 0, "nm.steelLocker.info");
        this.info(registry, NMBlocks.blockRoad, 0, "nm.blockRoad.info");
        this.info(registry, NMBlocks.blockAsphalt, 0, "nm.blockAsphalt.info");
        this.info(registry, NMBlocks.stoneLadder, 0, "nm.upgradedLadder.info");
        this.info(registry, NMBlocks.ironLadder, 0, "nm.upgradedLadder.info");
        this.info(registry, NMBlocks.hellforge, 0, "nm.hellforge.info");
        this.info(registry, NMBlocks.asphaltLayer, 0, "nm.blockAsphalt.info");
        this.info(registry, NMBlocks.bloodSaw, 0, "nm.bloodSaw.info");
        this.info(registry, NMBlocks.disenchantmentTable, 0, "nm.disenchantmentTable.info");

        // vanilla blocks
        this.info(registry, Block.obsidian, 0, "nm.obsidian.info");
        this.info(registry, Block.obsidian, 1, "nm.crudeObsidian.info");
        this.info(registry, BTWBlocks.carvedPumpkin, 1, "nm.pumpkin.info");

        // vanilla items
        this.info(registry, Item.horseArmorDiamond, 0, "nm.horseArmor.info");
        this.info(registry, Item.horseArmorGold, 0, "nm.horseArmor.info");
        this.info(registry, Item.horseArmorIron, 0, "nm.horseArmor.info");
        this.info(registry, Item.appleGold, 0, "nm.goldenApple.info");
        this.info(registry, Item.appleGold, 1, "nm.goldenAppleEnchanted.info");
        this.info(registry, BTWItems.brownMushroom, 0, "nm.brownMushroom.info");

    }
}
