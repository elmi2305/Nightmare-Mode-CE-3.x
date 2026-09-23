package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.world.SandboxRules;
import com.itlesports.nightmaremode.mixin.interfaces.ContainerCreativeAccess;
import net.minecraft.src.Container;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.GuiContainerCreative;
import net.minecraft.src.InventoryEffectRenderer;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(GuiContainerCreative.class)
public abstract class GuiContainerCreativeMixin extends InventoryEffectRenderer {
    public GuiContainerCreativeMixin(Container container) { super(container); }

    @Inject(method = "setCurrentCreativeTab", at = @At("RETURN"))
    private void nightmareMode$filterTab(CreativeTabs tab, CallbackInfo ci) {
        nightmareMode$filterItems();
    }

    @Inject(method = "updateCreativeSearch", at = @At("RETURN"))
    private void nightmareMode$filterSearch(CallbackInfo ci) {
        nightmareMode$filterItems();
    }

    @Unique private void nightmareMode$filterItems() {
        if (!NightmareMode.lockDownCreative || this.mc == null || this.mc.theWorld == null) return;
        ContainerCreativeAccess creative = (ContainerCreativeAccess)this.inventorySlots;
        for (Iterator iterator = creative.nightmareMode$getItemList().iterator(); iterator.hasNext();) {
            if (!SandboxRules.mayCreate(this.mc.theWorld, (ItemStack)iterator.next())) iterator.remove();
        }
        creative.nightmareMode$scrollTo(0.0F);
    }
}
