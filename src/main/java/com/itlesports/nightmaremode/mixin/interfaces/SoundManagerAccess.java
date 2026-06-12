package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.SoundManager;
import net.minecraft.src.SoundPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public interface SoundManagerAccess {
    @Accessor("sndSystem")
    SoundSystem getSoundSystem();
    @Accessor("soundPoolSounds")
    SoundPool getSoundPoolSounds();
}
