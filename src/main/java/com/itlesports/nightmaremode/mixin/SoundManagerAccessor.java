package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public interface SoundManagerAccessor {
    @Accessor("sndSystem")
    SoundSystem getSoundSystem();
}
