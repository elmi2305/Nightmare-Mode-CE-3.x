package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemAppleGold.class)
public abstract class ItemAppleGoldMixin extends ItemFood{
    @Unique
    private long timeUntilUsage;

    public ItemAppleGoldMixin(int par1, int par2, float par3, boolean par4) {
        super(par1, par2, par3, par4);
    }

    // REGULAR GOLDEN APPLE
    @Inject(method = "onFoodEaten",
            at = @At(value="INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayer;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void goldenAppleRandomEffects(ItemStack par3, World world, EntityPlayer entityPlayer, CallbackInfo ci){
        if (par3.getItemDamage() == 0 && !NightmareMode.noHit) {
            entityPlayer.addPotionEffect(new PotionEffect(Potion.field_76444_x.id,14400,4)); // absorption
        }
    }

    @Redirect(method = "onFoodEaten",
            at = @At(value="INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayer;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V",
                    ordinal = 0))
    private void noHitNoAbsorption(EntityPlayer instance, PotionEffect potionEffect){
        if(!NightmareMode.noHit){
            instance.addPotionEffect(potionEffect);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void decreaseStacksize(int j, int f, float bl, boolean par4, CallbackInfo ci){
        this.maxStackSize = 1;
    }

    // ENCHANTED GOLDEN APPLE
    @Inject(method = "onFoodEaten",
            at = @At(value="INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayer;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V",
                    ordinal = 3, shift = At.Shift.AFTER))
    private void giveExtraPotionEffects(ItemStack par3, World world, EntityPlayer entityPlayer, CallbackInfo ci){
        entityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 600, 3));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 800, 0));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 600, 0));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 2400, 0));
        if(!entityPlayer.isPotionActive(Potion.damageBoost)) {
            entityPlayer.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 600, 1));
        }
        entityPlayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 600, 1));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 600, 1));
        if (!NightmareMode.noHit) {
            entityPlayer.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 2400, 1)); // absorption
        }

        this.timeUntilUsage = world.getTotalWorldTime() + 1800; // enchanted gapple cooldown, 90s
    }

    @Inject(method = "onFoodEaten",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V",ordinal = 0,shift = At.Shift.AFTER))
    private void addCooldownForRegularGaps(ItemStack stack, World world, EntityPlayer player, CallbackInfo ci){
        this.timeUntilUsage = world.getTotalWorldTime() + 600; // regular gap cooldown, 30s
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isPotionActive(Potion.hunger) && world.getTotalWorldTime() >= this.timeUntilUsage) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        } else {
            player.onCantConsume();
        }
        return stack;
    }
}
