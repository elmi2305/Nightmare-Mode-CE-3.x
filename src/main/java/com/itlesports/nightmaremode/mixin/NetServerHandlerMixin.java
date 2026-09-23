package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.world.SandboxRules;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet107CreativeSetSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin {
    @Shadow public EntityPlayerMP playerEntity;

    @Inject(method = "handleCreativeSetSlot", at = @At("HEAD"), cancellable = true)
    private void nightmareMode$rejectLockedCreativeItem(Packet107CreativeSetSlot packet, CallbackInfo ci) {
        if (NightmareMode.lockDownCreative && packet.itemStack != null
                && !SandboxRules.mayCreate(this.playerEntity.worldObj, packet.itemStack)) {
            ci.cancel();
        }
    }
}
