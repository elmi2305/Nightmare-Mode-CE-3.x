package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(EntityLivingBase.class)
public interface EntityLivingBaseAccess {
    @Accessor("activePotionsMap")
    HashMap getActivePotionEffects();
}
