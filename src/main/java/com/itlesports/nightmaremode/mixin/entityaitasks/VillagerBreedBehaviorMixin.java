package com.itlesports.nightmaremode.mixin.entityaitasks;

import btw.entity.mob.behavior.VillagerBreedBehavior;
import com.itlesports.nightmaremode.entity.EntityFishermanVillager;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerBreedBehavior.class)
public class VillagerBreedBehaviorMixin {
    // this whole mixin essentially makes the villager breeding "dance" more kid-friendly, removing the noises, jumping animation and changing the noise upon birth

    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSoundAtEntity(Lnet/minecraft/src/Entity;Ljava/lang/String;FF)V"))
    private void avoidPlayingBreedingSound(World instance, Entity par1Entity, String par2Str, float par3, float par4){}
    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityVillager;jump()V"))
    private void avoidJumpingIntoEachOther(EntityVillager instance){}
    @ModifyArg(method = "giveBirth", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playAuxSFX(IIIII)V"), index = 0)
    private int changeSoundId(int par1){
        return 1000; // this is the poof sound of the dispenser block. it sounds less bad than the sound that's normally used
    }

    @ModifyArg(method = "giveBirth", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityVillager;spawnBabyVillagerWithProfession(Lnet/minecraft/src/EntityAgeable;I)Lnet/minecraft/src/EntityVillager;"), index = 1)
    private int giveFarmersASmallChanceForAFisherman(int profession) {
        return profession == 0 ? this.fishermanProfessionOrFarmer(profession) : profession;
    }

    @Shadow private EntityVillager villager;
    @Shadow private EntityVillager mate;

    @Unique
    private int fishermanProfessionOrFarmer(int profession) {
        return this.villager.getProfessionFromClass() == 0 && this.mate.getProfessionFromClass() == 0
                && this.villager.worldObj.rand.nextInt(20) == 0
                ? EntityFishermanVillager.PROFESSION_ID
                : profession;
    }
}
