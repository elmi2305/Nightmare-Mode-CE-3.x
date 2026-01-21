package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.behavior.VillagerBreedBehavior;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerBreedBehavior.class)
public class VillagerBreedBehaviorMixin {
    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playSoundAtEntity(Lnet/minecraft/src/Entity;Ljava/lang/String;FF)V"))
    private void avoidPlayingBreedingSound(World instance, Entity entity, String par1Entity, float par2Str, float par3){}
    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityVillager;jump()V"))
    private void avoidJumpingIntoEachOther(EntityVillager instance){}
    @ModifyArg(method = "giveBirth", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playAuxSFX(IIIII)V"), index = 0)
    private int changeSoundId(int par1){
        return 1000; // this is the poof sound of the dispenser block. it sounds less bad than the sound that's normally used
    }
}
