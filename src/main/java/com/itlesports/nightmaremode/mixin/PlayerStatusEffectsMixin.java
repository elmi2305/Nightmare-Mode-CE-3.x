package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.util.status.PlayerStatusEffects;
import btw.util.status.StatusEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerStatusEffects.class)
public class PlayerStatusEffectsMixin {
    @Mutable @Shadow @Final public static StatusEffect HURT;
    @Mutable @Shadow @Final public static StatusEffect INJURED;
    @Mutable @Shadow @Final public static StatusEffect WOUNDED;
    @Mutable @Shadow @Final public static StatusEffect CRIPPLED;
    @Mutable @Shadow @Final public static StatusEffect DYING;

    @Mutable @Shadow @Final public static StatusEffect STARVING;
    @Mutable @Shadow @Final public static StatusEffect PECKISH;
    @Mutable @Shadow @Final public static StatusEffect HUNGRY;
    @Mutable @Shadow @Final public static StatusEffect FAMISHED;
    @Mutable @Shadow @Final public static StatusEffect EMACIATED;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void noHit(CallbackInfo ci){
        if (NightmareMode.noHit) {
            HURT = PlayerStatusEffects.createHealthEffect(1, 1.0f, "").build();
            INJURED = PlayerStatusEffects.createHealthEffect(2, 1, "").build();
            WOUNDED = PlayerStatusEffects.createHealthEffect(3, 1, "").build();
            CRIPPLED = PlayerStatusEffects.createHealthEffect(4, 1, "").build();
            DYING = PlayerStatusEffects.createHealthEffect(5, 1, "NoHit").build();
        } else if(NightmareMode.nite){
            HURT = PlayerStatusEffects.createHealthEffect(1, 1.0f, "hurt").build();
            INJURED = PlayerStatusEffects.createHealthEffect(2, 1.0f, "injured").build();
            WOUNDED = PlayerStatusEffects.createHealthEffect(3, 1.0f, "wounded").build();
            CRIPPLED = PlayerStatusEffects.createHealthEffect(4, 1.0f, "crippled").build();
            DYING = PlayerStatusEffects.createHealthEffect(5, 1.0f, "dying").build();
            STARVING = PlayerStatusEffects.createHungerEffect(5, 1.0f, "starving").build();
            PECKISH = PlayerStatusEffects.createHungerEffect(1, 1.0F, "peckish").build();
            HUNGRY = PlayerStatusEffects.createHungerEffect(2, 1.0f, "hungry").build();
            FAMISHED = PlayerStatusEffects.createHungerEffect(3, 1.0f, "famished").build();
            EMACIATED = PlayerStatusEffects.createHungerEffect(4, 1.0f, "emaciated").build();
        }
    }
}
