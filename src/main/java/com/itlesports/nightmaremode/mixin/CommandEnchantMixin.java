package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.CommandEnchant;
import net.minecraft.src.ICommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandEnchant.class)
public abstract class CommandEnchantMixin {
    @Inject(method = "processCommand", at = @At("HEAD"), cancellable = true)
    private void nightmareMode$disableEnchantCommand(ICommandSender sender, String[] args, CallbackInfo ci) {
        if (!NightmareMode.lockDownCreative) return;
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("/enchant is disabled while Sandbox restrictions are active."));
        ci.cancel();
    }
}
