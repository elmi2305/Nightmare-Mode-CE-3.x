package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiButton.class)
public class GuiButtonMixin extends Gui {
    @Shadow public int id;

    @ModifyConstant(method = "drawButton", constant = @Constant(intValue = 0xFFFFA0))
    private int changeButtonColorOnHover(int constant){
        if(this.id == 2305){

            return 0xC74C50; // light red
        }
        return constant;
    }
    @ModifyArgs(method = "drawButton", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"))
    private void changeColor(Args args){
        if (this.id == 2305 || NightmareMode.bloodmare) {
            args.set(0,1f);
            args.set(1,0f);
            args.set(2,0f);
        }
    }



}
