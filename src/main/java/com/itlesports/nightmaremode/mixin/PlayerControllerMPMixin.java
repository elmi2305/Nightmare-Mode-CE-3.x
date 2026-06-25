package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {
    @Inject(method = "onPlayerRightClick", at = @At("RETURN"))
    private void logSignPlacing(EntityPlayer p, World w, ItemStack stack, int x, int y, int z, int par7, Vec3 par8Vec3, CallbackInfoReturnable<Boolean> cir) {
        // again, does not work for some reason
//        System.out.println("client: " + w.isRemote);
//        if (NightmareMode.getInstance().isGriefLogging() && !w.isRemote) {
//            System.out.println("was grief logging");
//            if(stack == null) return;
//            System.out.println("stack was correct");
//
//            if(stack.itemID == Item.sign.itemID){
//                System.out.println("stack was id sign - wrote everything to log");
//
//                String text = "Player " + p.getEntityName() + " placed Sign at " + x + " " + y + " " + z;
//                NightmareMode.appendLogLine(text);
//            }
//        }
    }
}
