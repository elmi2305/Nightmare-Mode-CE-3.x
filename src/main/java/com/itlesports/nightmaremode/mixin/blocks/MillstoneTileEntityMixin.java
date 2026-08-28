package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MillstoneBlock;
import btw.block.tileentity.MillstoneTileEntity;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import com.itlesports.nightmaremode.block.tileEntities.ObsidianMillstoneTileEntity;
import net.minecraft.src.Block;
import btw.crafting.manager.MillStoneCraftingManager;
import com.itlesports.nightmaremode.skill.SkillLockedBulkCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Potion;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MillstoneTileEntity.class)
public abstract class MillstoneTileEntityMixin extends TileEntity {
    @Inject(method = "updateEntity", at = @At("TAIL"))
    private void polluteWhileMilling(CallbackInfo ci) {
        MillstoneTileEntity self = (MillstoneTileEntity)(Object)this;
        if (this.worldObj == null || this.worldObj.isRemote || !self.isGrinding() || this.worldObj.getTotalWorldTime() % 20L != 0L) return;
        ItemStack input = self.stackMilling;
        float amount = input != null && input.itemID == Block.netherrack.blockID ? 12.0F : 2.0F;
        if (self instanceof ObsidianMillstoneTileEntity) amount *= 0.3F;
        ChunkPollutionManager.pollute(this.worldObj, this.xCoord, this.yCoord, this.zCoord, amount);
    }


    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 200))
    private int fasterMillstones(int constant){
        return constant * 8;
    }

    @Redirect(
            method = {"grindContents", "validateContentsForGrinding"},
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/crafting/manager/MillStoneCraftingManager;hasRecipeForSingleIngredient(Lnet/minecraft/src/ItemStack;)Z"))
    private boolean filterLockedMillstoneRecipe(MillStoneCraftingManager manager, ItemStack input) {
        return SkillLockedBulkCrafting.hasSingleIngredientRecipe(manager, input, this.worldObj);
    }

    @Redirect(
            method = "grindContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/crafting/manager/MillStoneCraftingManager;getCraftingResult(Lnet/minecraft/src/ItemStack;)Ljava/util/List;"))
    private List<ItemStack> filterLockedMillstoneResult(MillStoneCraftingManager manager, ItemStack input) {
        return SkillLockedBulkCrafting.getSingleIngredientResult(manager, input, this.worldObj);
    }
}
