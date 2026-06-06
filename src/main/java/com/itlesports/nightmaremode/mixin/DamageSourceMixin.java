package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.interfaces.DamageSourceExt;
import net.minecraft.src.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements DamageSourceExt {
    @Shadow private boolean isUnblockable;
    @Shadow private float hungerDamage;

    @Override public void nightmareMode$setHungerDrain(float drain) {
        this.hungerDamage = drain;
    }
    @Override public void nightmareMode$setUnblockable(boolean isUnblockable) {
        this.isUnblockable = isUnblockable;
    }
}
