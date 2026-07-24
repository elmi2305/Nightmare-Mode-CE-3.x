package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.crafting.BrewingStandRecipeHelper;
import com.itlesports.nightmaremode.crafting.manager.BrewingStandRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.BrewingStandRecipe;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityBrewingStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityBrewingStand.class)
public class TileEntityBrewingStandMixin {
    @Shadow private ItemStack[] brewingItemStacks;
    @Shadow private int brewTime;

    @Unique private int nightmareMode$customIngredientMetadata = Integer.MIN_VALUE;
    @Unique private float nightmareMode$customBrewTimeMultiplier = -1.0F;

    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 400))
    private int reduceBrewTime(int constant){
        float multiplier = BrewingStandRecipeManager.instance.getBatchTimeMultiplier(
                this.brewingItemStacks[3], this.getBottleSlots());
        if (multiplier <= 0.0F) {
            this.nightmareMode$customIngredientMetadata = Integer.MIN_VALUE;
            this.nightmareMode$customBrewTimeMultiplier = -1.0F;
            multiplier = 1.0F;
        } else {
            this.nightmareMode$customIngredientMetadata = this.brewingItemStacks[3].getItemDamage();
            this.nightmareMode$customBrewTimeMultiplier = multiplier;
        }
        return BrewingStandRecipeHelper.getBrewTime((TileEntityBrewingStand)(Object)this, multiplier);
    }

    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void restartCustomBrewWhenIngredientChanges(CallbackInfo ci) {
        if (this.brewTime <= 0 || this.nightmareMode$customIngredientMetadata == Integer.MIN_VALUE) {
            return;
        }

        ItemStack ingredient = this.brewingItemStacks[3];
        float multiplier = BrewingStandRecipeManager.instance.getBatchTimeMultiplier(ingredient, this.getBottleSlots());
        if (ingredient == null
                || ingredient.getItemDamage() != this.nightmareMode$customIngredientMetadata
                || multiplier <= 0.0F
                || Float.compare(multiplier, this.nightmareMode$customBrewTimeMultiplier) != 0) {
            this.brewTime = 0;
            this.nightmareMode$customIngredientMetadata = Integer.MIN_VALUE;
            this.nightmareMode$customBrewTimeMultiplier = -1.0F;
        }
    }

    @Inject(method = "canBrew", at = @At("HEAD"), cancellable = true)
    private void canBrewCustomRecipe(CallbackInfoReturnable<Boolean> cir) {
        if (this.brewingItemStacks[3] != null
                && this.brewingItemStacks[3].stackSize > 0
                && BrewingStandRecipeManager.instance.hasMatchingRecipe(this.brewingItemStacks[3], this.getBottleSlots())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "brewPotions", at = @At("HEAD"), cancellable = true)
    private void brewCustomRecipe(CallbackInfo ci) {
        ItemStack ingredient = this.brewingItemStacks[3];
        if (!BrewingStandRecipeManager.instance.hasMatchingRecipe(ingredient, this.getBottleSlots())) {
            return;
        }

        for (int slot = 0; slot < 3; ++slot) {
            BrewingStandRecipe recipe = BrewingStandRecipeManager.instance.getMatchingRecipe(ingredient, this.brewingItemStacks[slot]);
            if (recipe != null) {
                this.brewingItemStacks[slot] = recipe.getOutput();
            }
        }
        this.consumeIngredient(ingredient);
        this.nightmareMode$customIngredientMetadata = Integer.MIN_VALUE;
        this.nightmareMode$customBrewTimeMultiplier = -1.0F;
        ci.cancel();
    }

    @Inject(method = "isItemValidForSlot", at = @At("HEAD"), cancellable = true)
    private void allowCustomBrewingInputs(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) {
            return;
        }
        if (slot == 3 && BrewingStandRecipeManager.instance.isIngredient(stack)) {
            cir.setReturnValue(true);
        } else if (slot >= 0 && slot < 3 && BrewingStandRecipeManager.instance.isBottleInput(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private ItemStack[] getBottleSlots() {
        return new ItemStack[]{this.brewingItemStacks[0], this.brewingItemStacks[1], this.brewingItemStacks[2]};
    }

    @Unique
    private void consumeIngredient(ItemStack ingredient) {
        if (Item.itemsList[ingredient.itemID].hasContainerItem()) {
            this.brewingItemStacks[3] = new ItemStack(Item.itemsList[ingredient.itemID].getContainerItem());
        } else {
            --ingredient.stackSize;
            if (ingredient.stackSize <= 0) {
                this.brewingItemStacks[3] = null;
            }
        }
    }

    @Inject(method = "brewPotions", at = @At("TAIL"))
    private void trackSkillPotionBrewing(CallbackInfo ci) {
        TileEntity tile = (TileEntity)(Object)this;
        if (tile.worldObj == null || tile.worldObj.isRemote) {
            return;
        }

        int brewed = 0;
        for (int i = 0; i < 3; ++i) {
            ItemStack stack = this.brewingItemStacks[i];
            if (stack != null && stack.itemID == Item.potion.itemID) {
                brewed++;
            }
        }
        if (brewed <= 0) {
            return;
        }

        EntityPlayer player = tile.worldObj.getClosestPlayer(
                (double)tile.xCoord + 0.5D,
                (double)tile.yCoord + 0.5D,
                (double)tile.zCoord + 0.5D,
                8.0D
        );
        SkillHandler.incrementPotionsBrewed(player, brewed);
    }
}
