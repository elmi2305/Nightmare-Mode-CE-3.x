package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.item.items.ItemHammer;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "damageItem", at = @At("HEAD"), cancellable = true)
    private void preserveHammerDurability(int amount, EntityLivingBase user, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (amount > 0 && stack.getItem() instanceof ItemHammer && user instanceof EntityPlayer player
                && player.rand.nextFloat() < SkillHandler.getPlayerData(player).hammerDurabilitySaveChance) {
            ci.cancel();
        }
    }
}
