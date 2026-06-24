package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.LogSettings;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockContainer.class)
public class BlockContainerMixin extends Block {
    protected BlockContainerMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void doLogging(World w, int x, int y, int z, int m, int par6, CallbackInfo ci) {
        if(NightmareMode.getInstance().isGriefLogging() && !w.isRemote){
            LogSettings ls = NightmareMode.getInstance().getLogSettings();
            if(!ls.logIndirectBreaks) return;
            String text = this.getLocalizedName() + " destroyed at " + x + " " + y + " " + z + ". Nearest: " + w.getClosestPlayer(x,y,z, 32).username;

            if(ls.logContainers){
                NightmareMode.appendLogLine(text);
            }
        }
    }
}