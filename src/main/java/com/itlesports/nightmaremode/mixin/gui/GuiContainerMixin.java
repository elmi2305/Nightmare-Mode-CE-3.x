package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.util.NMInventoryLocks;
import com.itlesports.nightmaremode.nmgui.GuiAdvancedHorseArmor;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {
    @Shadow private void drawSlotInventory(Slot slot) {}

    @Shadow
    public Container inventorySlots;

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z", remap = false))
    private boolean disallowShiftClickingWithinAnvilGUI(int key){
        GuiContainer self = (GuiContainer)(Object)this;
        if(self instanceof GuiAdvancedHorseArmor){
            return false;
        }
        return Keyboard.isKeyDown(key);
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiContainer;drawGuiContainerForegroundLayer(II)V"))
    private void drawLockedPlayerInventorySlots(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || this.inventorySlots == null) {
            return;
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int lockedSlotColor = 0xFFC6C6C6;
        if (mc.currentScreen instanceof GuiInventory) {
            int progress = Math.max(mc.thePlayer.inGloomCounter + (mc.thePlayer.getGloomLevel() - 1) * 200, 0);
            float darkness = Math.min(progress / 250.0F, 1.0F) * 0.95F;
            int shade = Math.round(198.0F * (1.0F - darkness));
            lockedSlotColor = 0xFF000000 | shade << 16 | shade << 8 | shade;
        }

        for (Object slotObj : this.inventorySlots.inventorySlots) {
            Slot slot = (Slot)slotObj;
            if (slot.inventory instanceof InventoryPlayer inv && !NMInventoryLocks.isMainInventorySlotUnlocked(inv.player, slot.getSlotIndex())) {
                Gui.drawRect(
                        slot.xDisplayPosition - 1,
                        slot.yDisplayPosition - 1,
                        slot.xDisplayPosition + 16 + 1,
                        slot.yDisplayPosition + 16 + 1,
                        lockedSlotColor
                );
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
