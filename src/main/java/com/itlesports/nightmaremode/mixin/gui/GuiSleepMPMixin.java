package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiSleepMP;
import net.minecraft.src.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiSleepMP.class)
public class GuiSleepMPMixin extends GuiChat {
    @ModifyArg(method = "initGui", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private Object increaseHeightOfSleepButton(Object e){
        return new GuiButton(1, this.width / 2 - 100, this.height - 90, I18n.getString("multiplayer.stopSleeping"));
    }
}
