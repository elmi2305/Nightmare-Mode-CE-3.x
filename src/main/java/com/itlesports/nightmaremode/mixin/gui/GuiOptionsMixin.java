package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.EnumOptions;
import net.minecraft.src.GuiOptions;
import net.minecraft.src.GuiSmallButton;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiOptions.class)
public class GuiOptionsMixin {
    @Inject(method = "initGui", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GuiSmallButton;displayString:Ljava/lang/String;", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD),locals = LocalCapture.CAPTURE_FAILHARD)
    private void manageIngameDifficultyDisplay(CallbackInfo ci, int var1, EnumOptions[] var2, int var3, int var4, EnumOptions var5, GuiSmallButton var6){
        var6.displayString = "Combat: " + BalanceProfile.name(BalanceProfile.combat(BalanceProfile.active()));
        var6.enabled = false;
    }
}
