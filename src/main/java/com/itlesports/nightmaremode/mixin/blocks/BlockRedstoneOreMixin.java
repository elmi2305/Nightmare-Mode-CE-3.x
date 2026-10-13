package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import api.block.blocks.OreBlockStaged;
import api.item.items.PickaxeItem;
import api.item.util.ItemUtils;
import btw.block.blocks.RedstoneOreBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(RedstoneOreBlock.class)
public abstract class BlockRedstoneOreMixin extends OreBlockStaged {
    public BlockRedstoneOreMixin(int blockID) {
        super(blockID);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        if (world.isRemote) return;
        if (this.canSilkHarvest(metadata) && EnchantmentHelper.getSilkTouchModifier(player)) {
            super.harvestBlock(world, player, x, y, z, metadata);
            return;
        }
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        player.addHarvestBlockExhaustion(this.blockID, x, y, z, metadata);
        int count = this.getCrystalCount(player.getCurrentEquippedItem(),
                EnchantmentHelper.getFortuneModifier(player), world.rand);
        this.dropItemsIndividually(world, x, y, z, NMItems.redstoneCrystal.itemID, count, 0, 1.0F);
        this.dropItemsIndividually(world, x, y, z, BTWItems.stone.itemID, 6, this.getStrata(metadata), 1.0F);
    }

    @Override
    protected void ejectItemsOnGoodPickConversion(ItemStack stack, World world, int x, int y, int z,
                                                  int metadata, int facing) {
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
        int count = this.getCrystalCount(stack, fortune, world.rand);
        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                new ItemStack(NMItems.redstoneCrystal, count), facing);
    }

    @Unique
    private int getCrystalCount(ItemStack stack, int fortune, Random random) {
        int toolLevel = stack != null && stack.getItem() instanceof PickaxeItem pick
                ? pick.toolMaterial.getHarvestLevel()
                : stack != null && stack.getItem() instanceof ItemPickaxe pick
                ? pick.getToolMaterial().getHarvestLevel() : -1;
        int base = BalanceProfile.isEasy() || toolLevel >= EnumToolMaterial.EMERALD.getHarvestLevel()
                ? 2 : 1 + (toolLevel >= EnumToolMaterial.IRON.getHarvestLevel() && random.nextInt(4) == 0 ? 1 : 0);
        return this.applyFortune(base, fortune, random);
    }

    @Unique
    private int applyFortune(int base, int fortune, Random random) {
        return base * (fortune > 0 ? Math.max(0, random.nextInt(fortune + 2) - 1) + 1 : 1);
    }
    @Inject(method = "idDropped", at = @At("HEAD"), cancellable = true)
    private void dropRedstoneCrystal(int metadata, Random random, int fortune,
                                     CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(NMItems.redstoneCrystal.itemID);
    }

    @Inject(method = "quantityDropped", at = @At("HEAD"), cancellable = true)
    private void dropRedstoneCrystalAmount(Random random, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BalanceProfile.isEasy() ? 2 : 1);
    }

    @Inject(method = "quantityDroppedWithBonus", at = @At("HEAD"), cancellable = true)
    private void applyFortuneToRedstoneCrystal(int fortune, Random random,
                                                 CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.applyFortune(BalanceProfile.isEasy() ? 2 : 1, fortune, random));
    }

    @Inject(method = "idDroppedOnConversion", at = @At("HEAD"), cancellable = true, remap = false)
    private void convertToRedstoneCrystal(boolean dropPiles, int metadata,
                                          CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(NMItems.redstoneCrystal.itemID);
    }

    @Inject(method = "quantityDroppedOnConversion", at = @At("HEAD"), cancellable = true, remap = false)
    private void convertRedstoneCrystalAmount(Random random, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BalanceProfile.isEasy() ? 2 : 1);
    }
}
