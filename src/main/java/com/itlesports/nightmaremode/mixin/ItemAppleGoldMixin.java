package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static btw.community.nightmaremode.NightmareMode.APPLE_COOLDOWN;

@Mixin(ItemAppleGold.class)
public abstract class ItemAppleGoldMixin extends ItemFood{
    public ItemAppleGoldMixin(int par1, int par2, float par3, boolean par4) {
        super(par1, par2, par3, par4);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void decreaseStacksize(int j, int f, float bl, boolean par4, CallbackInfo ci){
        this.maxStackSize = 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isPotionActive(Potion.hunger) && world.getTotalWorldTime() >= player.getData(APPLE_COOLDOWN)) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        } else {
            player.onCantConsume();
        }
        return stack;
    }
}
