package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.LogSettings;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockTNT.class)
public class BlockTNTMixin {
    @Inject(method = "primeTnt", at = @At("HEAD"))
    private void logTNTIgnition(World w, int x, int y, int z, int par5, EntityLivingBase placer, CallbackInfo ci){
        if(NightmareMode.getInstance().isGriefLogging() && !w.isRemote){
            LogSettings ls = NightmareMode.getInstance().getLogSettings();
            if(!ls.logIndirectBreaks) return;

            String text = "TNT at " + x + " " + y + " " + z + " by " + (placer != null ? placer.getEntityName() : w.getClosestPlayer(x,y,z, 32).username);
            NightmareMode.appendLogLine(text);
        }
    }
}
