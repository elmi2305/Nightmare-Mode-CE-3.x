package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSlot.class)
public class GuiSlotMixin {

    @ModifyConstant(method = "drawScreen", constant = @Constant(intValue = 110, ordinal = 2))
    private int increaseWidthOrWhatever(int constant){
        GuiSlot guiSlot = (GuiSlot)(Object)this;
        if(guiSlot instanceof GuiWorldSlot) {
            return constant + 36;
        }
        return constant;
    }

    @ModifyConstant(method = "drawScreen", constant = @Constant(intValue = 110, ordinal = 0))
    private int increaseWidthOrWhatever0(int constant){
        GuiSlot guiSlot = (GuiSlot)(Object)this;
        if(guiSlot instanceof GuiWorldSlot) {
            return constant + 36;
        }
        return constant;
    }

}
