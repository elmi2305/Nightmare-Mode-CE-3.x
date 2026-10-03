package com.itlesports.nightmaremode.mixin;

import api.item.items.ProgressiveCraftingItem;
import com.itlesports.nightmaremode.util.EasyBalance;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressiveCraftingItem.class)
public abstract class ProgressiveCraftingItemMixin {
    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    private void additionalEasyProgress(ItemStack stack, World world, EntityPlayer player, CallbackInfo ci) {
        ProgressiveCraftingItem item = (ProgressiveCraftingItem)(Object)this;
        int elapsed = item.getMaxItemUseDuration(stack) - player.getItemInUseCount();
        if (!world.isRemote && com.itlesports.nightmaremode.world.BalanceProfile.isEasy()
                && elapsed > item.getItemUseWarmupDuration() && player.getItemInUseCount() % 4 == 0) {
            int normalProgress = 1 + (Integer)world.getDifficultyParameter(api.world.difficulty.DifficultyParam.ProgressiveCraftingAdditionalProgressPerTick.class);
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - normalProgress * 2));
        }
    }
}
