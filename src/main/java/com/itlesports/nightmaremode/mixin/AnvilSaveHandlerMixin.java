package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.underworld.WorldProviderUnderworld;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(AnvilSaveHandler.class)
public class AnvilSaveHandlerMixin extends SaveHandler {
    public AnvilSaveHandlerMixin(File par1File, String par2Str, boolean par3) {
        super(par1File, par2Str, par3);
    }

    @Inject(method = "getChunkLoader", at = @At("RETURN"), cancellable = true)
    private void addSavingForUnderworld(WorldProvider wp, CallbackInfoReturnable<IChunkLoader> cir){
        File file = this.getWorldDirectory();
        if (wp instanceof WorldProviderUnderworld) {
            File file2 = new File(file, "DIM_UNDERWORLD");
            file2.mkdirs();
            cir.setReturnValue(new AnvilChunkLoader(file2));
        }
    }
}
