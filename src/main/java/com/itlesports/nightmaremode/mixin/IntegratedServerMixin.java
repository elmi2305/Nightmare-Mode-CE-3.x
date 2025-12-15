package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
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

    @Shadow @Final private ILogAgent serverLogAgent;

    public IntegratedServerMixin(File par1File) {
        super(par1File);
    }

    @Inject(method = "loadAllWorlds", at = @At(value = "FIELD", target = "Lnet/minecraft/src/IntegratedServer;worldServers:[Lnet/minecraft/src/WorldServer;",ordinal = 0, shift = At.Shift.AFTER))
    private void setWorldServerSize(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci){
        this.worldServers = new WorldServer[4];
    }

    @Override
    public WorldServer worldServerForDimension(int par1) {
        if(par1 == NightmareMode.UNDERWORLD_DIMENSION){
            return (this.worldServers[3]);
        }
        return super.worldServerForDimension(par1);
    }

    @Inject(method = "loadAllWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/IntegratedServer;initialWorldChunkLoad()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void customWorldDimensionCode(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci, ISaveHandler var7, boolean isGlobal){
        MinecraftServer serv = (MinecraftServer) (Object)this;
        this.worldServers[3] =  new WorldServerMulti(
                serv,
                var7,
                par2Str,
                2,
                this.theWorldSettings,
                this.worldServers[3],
                this.theProfiler,
                this.getLogAgent());
        this.worldServers[3].addWorldAccess(new WorldManager(serv, this.worldServers[3]));

    }
}
