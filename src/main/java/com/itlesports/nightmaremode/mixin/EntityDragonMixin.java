package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityDragon.class)
public abstract class EntityDragonMixin extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
    @Shadow
    private void createEnderPortal(int par1, int par2) {
    }

    public EntityDragonMixin(World par1World) {
        super(par1World);
    }

    @Redirect(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityDragon;createEnderPortal(II)V"))
    private void onlySpawnOnSecondDragonKill(EntityDragon instance, int var10, int var12) {
        if (BlockEndPortal.bossDefeated) {
            createEnderPortal(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
        } else {
            BlockEndPortal.bossDefeated = true;
        }
    }
}
