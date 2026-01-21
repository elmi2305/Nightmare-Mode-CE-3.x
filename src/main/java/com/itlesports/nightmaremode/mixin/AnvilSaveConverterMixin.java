package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.SaveFormatExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mixin(AnvilSaveConverter.class)
public abstract class AnvilSaveConverterMixin extends SaveFormatOld {
    public AnvilSaveConverterMixin(File par1File) {
        super(par1File);
    }

    @Shadow protected abstract int getSaveVersion();

    @Unique private WorldInfo tempWorldInfo;
    @Inject(method = "getSaveList", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;getWorldName()Ljava/lang/String;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void saveWorldInfoTemp(CallbackInfoReturnable<List> cir, ArrayList var1, File[] var2, File[] var3, int var4, int var5, File var6, String var7, WorldInfo var8){
        this.tempWorldInfo = var8;
    }

    @Inject(method = "getSaveList", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addConfigsToSaveHandler(CallbackInfoReturnable<List> cir, ArrayList var1, File[] var2, File[] var3, int var4, int var5, File var6, String var7, WorldInfo var8){
        String var10 = var8.getWorldName();
        if (var10 == null || MathHelper.stringNullOrLengthZero(var10)) {
            var10 = var7;
        }
        long var11 = 0L;
        boolean var9 = var8.getSaveVersion() != this.getSaveVersion();

        SaveFormatComparator sfc = new SaveFormatComparator(var7, var10, var8.getLastTimePlayed(), var11, var8.getGameType(), var9, var8.isHardcoreModeEnabled(), var8.areCommandsAllowed());
        int[] activeConfArray = this.tempWorldInfo.getData(NightmareMode.CONFIGS_CREATED);

        ((SaveFormatExt) sfc).nightmareMode$setConfArray(activeConfArray);
        this.tempWorldInfo = null;
        var1.add(sfc);
    }

    @Redirect(method = "getSaveList", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z"))
    private boolean doNothing(ArrayList instance, Object e){
        return false;
    }
}
