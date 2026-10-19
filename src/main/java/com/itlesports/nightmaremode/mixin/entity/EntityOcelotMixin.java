package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityOcelot;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Item;
import com.itlesports.nightmaremode.util.NMFishFood;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityOcelot.class)
public abstract class EntityOcelotMixin {
    @Unique private boolean nightmareMode$wasTamedBeforeInteract;

    @Redirect(method = {"interact", "isBreedingItem"}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/src/ItemStack;itemID:I"))
    private int acceptRawFish(ItemStack stack) {
        return NMFishFood.ocelotFoodID(stack);
    }

    @Redirect(method = "checkForLooseFood", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/ItemStack;getItem()Lnet/minecraft/src/Item;"))
    private Item eatLooseRawFish(ItemStack stack) {
        return NMFishFood.isRawFish(stack) ? Item.fishRaw : stack.getItem();
    }

    @Inject(method = "interact", at = @At("HEAD"))
    private void captureSkillTameState(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        this.nightmareMode$wasTamedBeforeInteract = ((EntityTameable)(Object)this).isTamed();
    }

    @Inject(method = "interact", at = @At("RETURN"))
    private void trackSkillOcelotTaming(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        EntityTameable ocelot = (EntityTameable)(Object)this;
        if (!this.nightmareMode$wasTamedBeforeInteract
                && ocelot.isTamed()
                && player != null
                && player.getCommandSenderName().equalsIgnoreCase(ocelot.getOwnerName())) {
            SkillHandler.incrementAnimalsTamed(player);
        }
    }
}
