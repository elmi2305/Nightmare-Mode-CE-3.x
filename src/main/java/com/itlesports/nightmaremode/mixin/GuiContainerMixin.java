package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiRepair;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {
    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z"))
    private boolean disallowShiftClickingWithinAnvilGUI(int key){
        if(((GuiContainer)(Object)this) instanceof GuiRepair){
            return false;
        }
        return Keyboard.isKeyDown(key);
    }
}
