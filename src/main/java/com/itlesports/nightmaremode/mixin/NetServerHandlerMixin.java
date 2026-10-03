package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.world.SandboxRules;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet107CreativeSetSlot;
import net.minecraft.src.Packet10Flying;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetServerHandler.class)
public abstract class NetServerHandlerMixin {
    @org.spongepowered.asm.mixin.Unique private boolean balanceProfileAccepted;
    @org.spongepowered.asm.mixin.Unique private int balanceHandshakeTicks;

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void verifyClientBalance(net.minecraft.src.Packet250CustomPayload packet, CallbackInfo ci) {
        if (!"NM|Balance1".equals(packet.channel)) return;
        this.balanceProfileAccepted = com.itlesports.nightmaremode.world.BalanceProfile.acceptsWireProfile(packet.data);
        if (!this.balanceProfileAccepted) ((NetServerHandler)(Object)this).kickPlayerFromServer(
                "Journey difficulty does not match this server. Change difficulty and restart the game.");
        ci.cancel();
    }

    @Inject(method = "networkTick", at = @At("TAIL"))
    private void enforceBalanceHandshake(CallbackInfo ci) {
        if (!this.balanceProfileAccepted && ++this.balanceHandshakeTicks == 200)
            ((NetServerHandler)(Object)this).kickPlayerFromServer("Matching Journey difficulty is required. Change difficulty and restart the game.");
    }

    @Inject(method = {"handleFlying", "handleBlockDig", "handlePlace", "handleUseEntity", "handleWindowClick",
            "handleCreativeSetSlot", "handleClientCommand", "handleCloseWindow", "handleEnchantItem"}, at = @At("HEAD"), cancellable = true)
    private void blockPlayBeforeBalanceHandshake(CallbackInfo ci) {
        if (!this.balanceProfileAccepted) ci.cancel();
    }
    @Shadow public EntityPlayerMP playerEntity;

    @Inject(method = "handleCreativeSetSlot", at = @At("HEAD"), cancellable = true)
    private void nightmareMode$rejectLockedCreativeItem(Packet107CreativeSetSlot packet, CallbackInfo ci) {
        if (NightmareMode.lockDownCreative && packet.itemStack != null
                && !SandboxRules.mayCreate(this.playerEntity.worldObj, packet.itemStack)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleFlying", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityPlayerMP;addExhaustionForJump()V"))
    private void countServerJump(Packet10Flying packet, CallbackInfo ci) {
        SkillHandler.incrementJumps(this.playerEntity);
    }
}
