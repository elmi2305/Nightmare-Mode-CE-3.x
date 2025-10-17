package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.nmgui.GuiAdvancedHorseArmor;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {
    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z", remap = false))
    private boolean disallowShiftClickingWithinAnvilGUI(int key){
        GuiContainer self = (GuiContainer)(Object)this;
        if(self instanceof GuiRepair){
            return false;
        }
        if(self instanceof GuiAdvancedHorseArmor){
            return false;
        }
        return Keyboard.isKeyDown(key);
    }
}
