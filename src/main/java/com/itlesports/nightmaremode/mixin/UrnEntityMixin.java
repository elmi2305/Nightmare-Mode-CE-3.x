package com.itlesports.nightmaremode.mixin;

import btw.entity.UrnEntity;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UrnEntity.class)
public class UrnEntityMixin {
    @Inject(method = "attemptToCreateWither", at = @At("HEAD"),cancellable = true)
    private static void witherSummoningRestrictions(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(j < 60){
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText("The Wither must be summoned above sea level.");
            text2.setColor(EnumChatFormatting.BLACK);
            world.getClosestPlayer(i,j,k,-1).sendChatToPlayer(text2);
            cir.setReturnValue(false);
        } else if(j > 200){
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText("The Wither cannot be summoned this high.");
            text2.setColor(EnumChatFormatting.BLACK);
            world.getClosestPlayer(i,j,k,-1).sendChatToPlayer(text2);
            cir.setReturnValue(false);
        }
        // this prints a chat message for every single wither-creating block in the soul urns range. but it works so I'm not complaining
    }
}
