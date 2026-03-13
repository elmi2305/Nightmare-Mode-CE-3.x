package com.itlesports.nightmaremode.mixin;

import btw.world.BTWDifficulties;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {
    @Shadow @Final private WorldSettings theWorldSettings;

    public IntegratedServerMixin(File par1File) {
        super(par1File);
    }

    @Inject(method = "loadAllWorlds", at = @At(value = "FIELD", target = "Lnet/minecraft/src/IntegratedServer;worldServers:[Lnet/minecraft/src/WorldServer;",ordinal = 0, shift = At.Shift.AFTER))
    private void setWorldServerSize(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci){
        this.worldServers = new WorldServer[4];
    }

    @Override
    public WorldServer worldServerForDimension(int par1) {
        if(par1 == NMFields.UNDERWORLD_DIMENSION){
            return (this.worldServers[3]);
        }
        return super.worldServerForDimension(par1);
    }

    @Inject(method = "loadAllWorlds", at = @At(value = "INVOKE", target = "Lapi/AddonHandler;initializeDifficultyCommon(Lapi/world/difficulty/Difficulty;)V"))
    private void customWorldDimensionCode(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci){
        MinecraftServer serv = (MinecraftServer) (Object)this;
        ISaveHandler var7;
        if (this.getActiveAnvilConverter().isWorldGlobal(par1Str)) {
            var7 = this.getActiveAnvilConverter().getSaveLoader2(par1Str, true);
            this.setDifficulty(BTWDifficulties.HOSTILE_LOCKED);
        } else {
            var7 = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);
        }
        this.worldServers[3] =  new WorldServerMulti(
                serv,
                var7,
                par2Str,
                NMFields.UNDERWORLD_DIMENSION,
                this.theWorldSettings,
                this.worldServers[3],
                this.theProfiler,
                this.getLogAgent());
        this.getConfigurationManager().setPlayerManager(this.worldServers);
        this.worldServers[3].addWorldAccess(new WorldManager(serv, this.worldServers[3]));
    }
}
