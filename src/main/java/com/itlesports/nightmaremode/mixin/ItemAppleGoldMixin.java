package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(ItemAppleGold.class)
public abstract class ItemAppleGoldMixin extends ItemFood{
    public ItemAppleGoldMixin(int par1, int par2, float par3, boolean par4) {
        super(par1, par2, par3, par4);
    }

    // REGULAR GOLDEN APPLE
    @Inject(method = "onFoodEaten",
            at = @At(value="INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayer;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void goldenAppleRandomEffects(ItemStack par3, World world, EntityPlayer entityPlayer, CallbackInfo ci){
        if (par3.getItemDamage()==0) {
            entityPlayer.addPotionEffect(new PotionEffect(Potion.field_76444_x.id,14400,4)); // absorption
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void decreaseStacksize(int j, int f, float bl, boolean par4, CallbackInfo ci){
        this.maxStackSize = 2;
    }

//        List<Integer> potionIDList= new ArrayList<>();
//        for(int i = 1; i <= 22; i++){
//            potionIDList.add(i);
//        }
//        potionIDList.remove(5);
//        potionIDList.remove(5);
//        Random rand = new Random();
//        for(int i = 0; i < 3; i++) {
//            int id = potionIDList.get(rand.nextInt(potionIDList.size()));
//            int amplifier = rand.nextInt(2);
//
//            entityPlayer.addPotionEffect(new PotionEffect(id,300,amplifier));
//        }

    // ENCHANTED GOLDEN APPLE
    @Inject(method = "onFoodEaten",
            at = @At(value="INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayer;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V",
                    ordinal = 3, shift = At.Shift.AFTER))
    private void giveExtraPotionEffects(ItemStack par3, World world, EntityPlayer entityPlayer, CallbackInfo ci){
        entityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 600, 4));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 800, 0));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 600, 0));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 2400, 0));
        if(!entityPlayer.isPotionActive(Potion.damageBoost)) {
            entityPlayer.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 600, 1));
        }
        entityPlayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 600, 1));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 600, 1));
        entityPlayer.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 2400, 1)); // absorption
    }
}
