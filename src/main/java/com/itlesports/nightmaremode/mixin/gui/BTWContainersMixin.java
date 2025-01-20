package com.itlesports.nightmaremode.mixin.gui;

import btw.inventory.BTWContainers;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static btw.inventory.BTWContainers.anvilContainerID;

@Mixin(BTWContainers.class)
public class BTWContainersMixin {
    @Inject(method = "getAssociatedGui", at = @At("HEAD"),cancellable = true)
    private static void registerAnvilGuiUsingBTWMod(EntityClientPlayerMP entityclientplayermp, int containerID, CallbackInfoReturnable<GuiContainer> cir){
        if(containerID == anvilContainerID){
            cir.setReturnValue(new GuiRepair(entityclientplayermp.inventory, entityclientplayermp.worldObj, 0, 0, 0));
        }
    }
    // functionally useless for now
}
