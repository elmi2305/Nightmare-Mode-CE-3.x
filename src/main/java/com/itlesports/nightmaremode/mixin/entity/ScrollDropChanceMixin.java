package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({EntityBat.class, EntityBlaze.class, EntityCreeper.class, EntityEnderman.class,
        EntityGhast.class, EntityMagmaCube.class, EntityPigZombie.class, EntitySkeleton.class,
        EntitySilverfish.class, EntitySlime.class, EntitySpider.class, EntityWitch.class,
        EntityZombie.class})
public class ScrollDropChanceMixin {
    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE",
            target = "Ljava/util/Random;nextInt(I)I"))
    private int reduceScrollDropChance(int bound) {
        return 2000;
    }
}
