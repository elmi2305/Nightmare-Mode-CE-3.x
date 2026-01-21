package com.itlesports.nightmaremode.mixin.gui;

import btw.inventory.BTWContainers;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityDisenchantmentTable;
import com.itlesports.nightmaremode.nmgui.ContainerDisenchantment;
import com.itlesports.nightmaremode.nmgui.ContainerDisenchantmentGui;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BTWContainers.class)
public class BTWContainersMixin {
    @Environment(EnvType.CLIENT)
    @Inject(method = "getAssociatedGui", at = @At("HEAD"),cancellable = true)
    private static void registerAnvilGuiUsingBTWMod(EntityClientPlayerMP p, int containerID, CallbackInfoReturnable<GuiContainer> cir){
        if(containerID == ContainerDisenchantment.ID){
            cir.setReturnValue(new ContainerDisenchantmentGui(p.inventory, new TileEntityDisenchantmentTable()));
        }
    }
}
