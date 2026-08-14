package com.itlesports.nightmaremode.mixin.blocks;

import btw.item.BTWItems;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemHammer;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.itemblock.ObsidianItemBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow public static Block obsidian;
    @Shadow @Final public int blockID;

    @Unique private Icon ifhyHardenedEndstoneIcon;

    @Shadow protected abstract void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack stack);
    @Shadow protected abstract boolean checkForFall(World world, int x, int y, int z);

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void performObsidianRewrite(CallbackInfo ci){
        Item.itemsList[obsidian.blockID] = new ObsidianItemBlock(obsidian.blockID - 256);
    }

    @Inject(method = "harvestBlock", at = @At("HEAD"), cancellable = true)
    private void applyHammerRecipeDrops(World world, EntityPlayer player, int x, int y, int z, int meta, CallbackInfo ci){
        ItemStack heldStack = player.getHeldItem();
        if (this.blockID == Block.blockClay.blockID && !this.canHarvestClayBall(heldStack)) {
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
            player.addHarvestBlockExhaustion(this.blockID, x, y, z, meta);
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(BTWItems.clayPile));
            for (int i = 0; i < 6; ++i) {
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(BTWItems.dirtPile));
            }
            ci.cancel();
            return;
        }

        if (heldStack == null || !(heldStack.getItem() instanceof ItemHammer)) {
            return;
        }

        HammerRecipe recipe = HammerCraftingManager.instance.getRecipe((Block)(Object)this, meta);
        if (recipe == null) {
            return;
        }
        if (!recipe.canPlayerUseHammer(heldStack, player)) {
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, meta);
            ci.cancel();
            return;
        }

        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        player.addHarvestBlockExhaustion(this.blockID, x, y, z, meta);

        if (recipe.isFinalHit(meta)) {
            recipe.chargePlayerExperience(player);
            for (ItemStack output : recipe.getOutput()) {
                if (output != null) {
                    this.dropBlockAsItem_do(world, x, y, z, output.copy());
                }
            }
        } else {
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, recipe.getNextHitMetadata(meta));
        }

        ci.cancel();
    }

    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("HEAD"), cancellable = true)
    private void gateHardenedEndstone(EntityPlayer player, World world, int x, int y, int z,
                                      CallbackInfoReturnable<Float> cir) {
        if (this.blockID == Block.whiteStone.blockID && world.getBlockMetadata(x, y, z) == 1) {
            ItemStack held = player.getCurrentEquippedItem();
            if (held == null || held.getItem() != NMItems.enderPickaxe) cir.setReturnValue(0.0F);
        }
    }

    @Inject(method = "registerIcons", at = @At("TAIL"))
    private void registerHardenedEndstoneTexture(IconRegister register, CallbackInfo ci) {
        if (this.blockID == Block.whiteStone.blockID) {
            this.ifhyHardenedEndstoneIcon = register.registerIcon("nightmare:ifhyHardenedEndstone");
        }
    }

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
    private void useHardenedEndstoneTexture(int side, int metadata, CallbackInfoReturnable<Icon> cir) {
        if (this.blockID == Block.whiteStone.blockID && metadata == 1 && this.ifhyHardenedEndstoneIcon != null) {
            cir.setReturnValue(this.ifhyHardenedEndstoneIcon);
        }
    }

    @Inject(method = "canConvertBlock", at = @At("HEAD"), cancellable = true)
    private void allowEnderHoeTilling(ItemStack stack, World world, int x, int y, int z,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (this.blockID == Block.whiteStone.blockID && stack != null && stack.getItem() == NMItems.enderHoe) {
            cir.setReturnValue(world.isAirBlock(x, y + 1, z));
        }
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void tillEndstone(ItemStack stack, World world, int x, int y, int z, int side,
                              CallbackInfoReturnable<Boolean> cir) {
        if (this.blockID == Block.whiteStone.blockID && stack != null && stack.getItem() == NMItems.enderHoe
                && world.isAirBlock(x, y + 1, z)) {
            world.setBlockAndMetadataWithNotify(x, y, z, NMBlocks.endFarmland.blockID, 7);
            if (!world.isRemote) world.playAuxSFX(2291, x, y, z, 0);
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean canHarvestClayBall(ItemStack heldStack) {
        return heldStack != null && (heldStack.itemID == Item.shovelDiamond.itemID
                || heldStack.itemID == NMItems.bloodShovel.itemID
                || heldStack.itemID == BTWItems.steelShovel.itemID);
    }

    @Inject(method = "isFallingBlock", at = @At("HEAD"), cancellable = true)
    private void makeCompressionBlocksFall(CallbackInfoReturnable<Boolean> cir) {
        if (this.isCompressionBlock()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onBlockAdded", at = @At("TAIL"))
    private void scheduleCompressionBlockFall(World world, int x, int y, int z, CallbackInfo ci) {
        if (this.isCompressionBlock()) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 2);
        }
    }

    @Inject(method = "onNeighborBlockChange", at = @At("TAIL"))
    private void scheduleCompressionBlockFallAfterNeighborChange(World world, int x, int y, int z, int neighborId, CallbackInfo ci) {
        if (this.isCompressionBlock() && !world.isUpdatePendingThisTickForBlock(x, y, z, this.blockID)) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 2);
        }
    }

    @Inject(method = "updateTick", at = @At("HEAD"))
    private void checkCompressionBlockFall(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (this.isCompressionBlock()) {
            this.checkForFall(world, x, y, z);
        }
    }

    @Unique
    private boolean isCompressionBlock() {
        int id = this.blockID;
        return id == Block.blockGold.blockID
                || id == Block.blockIron.blockID
                || id == Block.blockDiamond.blockID
                || id == Block.blockEmerald.blockID
                || id == Block.blockLapis.blockID
                || id == Block.coalBlock.blockID
                || id == Block.blockRedstone.blockID
                || id == Block.blockNetherQuartz.blockID
                || id == BTWBlocks.diamondIngot.blockID
                || id == BTWBlocks.charcoalBlock.blockID
                || id == BTWBlocks.nethercoalBlock.blockID
                || NMBlocks.blockBloodIngot != null && id == NMBlocks.blockBloodIngot.blockID
                || NMBlocks.blockRefinedDiamondIngot != null && id == NMBlocks.blockRefinedDiamondIngot.blockID;
    }

    @Inject(method = "canMobsSpawnOn", at = @At("HEAD"),cancellable = true)
    private void mobSpawnOnWood(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsBloodMoon()){
            cir.setReturnValue(true);
        }
    }
}
