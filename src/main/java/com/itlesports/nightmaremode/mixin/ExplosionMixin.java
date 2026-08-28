package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.minecraft.src.Explosion;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow private World worldObj;
    @Shadow public double explosionX;
    @Shadow public double explosionY;
    @Shadow public double explosionZ;
    @Shadow public float explosionSize;

    @Inject(method = "doExplosionA", at = @At("TAIL"))
    private void polluteFromExplosion(CallbackInfo ci) {
        ChunkPollutionManager.pollute(this.worldObj,
                MathHelper.floor_double(this.explosionX), MathHelper.floor_double(this.explosionY),
                MathHelper.floor_double(this.explosionZ), 450.0F * Math.max(1.0F, this.explosionSize));
    }
}
