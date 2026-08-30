package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.JungleSpiderEntity;
import com.itlesports.nightmaremode.entity.variants.EntityDeadzonePigman;
import com.itlesports.nightmaremode.entity.variants.EntityShadowZombie;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MobSpawnerBaseLogic.class)
public abstract class MobSpawnerBaseLogicMixin {
    @Unique private static final String SKY_ZIGGURATH_WITHER_SKELETON = "NmSkyZiggurathWitherSkeleton";
    @Unique private static final String SKY_ZIGGURATH_JUNGLE_SPIDER = "NmSkyZiggurathJungleSpider";
    @Unique private static final String SKY_ZIGGURATH_DEADZONE_PIGMAN = "NmSkyZiggurathDeadzonePigman";
    @Unique private static final String SKY_ZIGGURATH_SHADOW_ZOMBIE = "NmSkyZiggurathShadowZombie";

    @Shadow private Entity field_98291_j;

    @Shadow public abstract World getSpawnerWorld();

    @Shadow private String mobID;

    @Redirect(method = {"updateSpawner", "func_98281_h"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityList;createEntityByName(Ljava/lang/String;Lnet/minecraft/src/World;)Lnet/minecraft/src/Entity;"))
    private Entity ifhy$createSkyZiggurathSpawnerMob(String mobID, World world) {
        if (SKY_ZIGGURATH_WITHER_SKELETON.equals(mobID)) {
            EntitySkeleton skeleton = new EntitySkeleton(world);
            skeleton.setSkeletonType(1);
            // The renderer creates its preview entity before assigning a world.
            if (world != null) {
                skeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
            }
            return skeleton;
        }
        if (SKY_ZIGGURATH_JUNGLE_SPIDER.equals(mobID)) {
            return new JungleSpiderEntity(world);
        }
        if (SKY_ZIGGURATH_DEADZONE_PIGMAN.equals(mobID)) {
            return new EntityDeadzonePigman(world);
        }
        if (SKY_ZIGGURATH_SHADOW_ZOMBIE.equals(mobID)) {
            return new EntityShadowZombie(world);
        }
        return EntityList.createEntityByName(mobID, world);
    }

    @Inject(method = "func_98281_h", at = @At("RETURN"), cancellable = true)
    private void silverfishSpawnerMakesWitherSkellies(CallbackInfoReturnable<Entity> cir){
        if (this.field_98291_j instanceof EntitySilverfish) {
            EntitySkeleton skeleton = new EntitySkeleton(this.getSpawnerWorld());
            skeleton.setSkeletonType(1);
            cir.setReturnValue(skeleton);
        }
    }

    @ModifyArg(method = "func_98265_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private Entity spawnWitherSkeletonsInsteadOfSilverfish(Entity par1Entity){
        if (Objects.equals(this.mobID, "Silverfish")) {
            EntitySkeleton skeleton = new EntitySkeleton(par1Entity.worldObj);
            skeleton.copyLocationAndAnglesFrom(par1Entity);
            skeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
            skeleton.setSkeletonType(1);
            return skeleton;
        }
        return par1Entity;
    }


    @Inject(method = "hasReachedSpawnCap", at = @At("RETURN"),cancellable = true)
    private void uncapSpawner(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
