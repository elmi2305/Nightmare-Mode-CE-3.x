package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.entity.EntityFishermanVillager;
import net.minecraft.src.ComponentScatteredFeature;
import net.minecraft.src.ComponentScatteredFeatureSwampHut;
import net.minecraft.src.EntityLivingData;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ComponentScatteredFeatureSwampHut.class)
public abstract class ComponentScatteredFeatureSwampHutMixin extends ComponentScatteredFeature {

    @Unique private boolean ifhy$hasFisherman;

    @Inject(method = "func_143012_a", at = @At("TAIL"))
    private void writeFisherman(NBTTagCompound tag, CallbackInfo ci) {
        tag.setBoolean("IfhyFisherman", this.ifhy$hasFisherman);
    }

    @Inject(method = "func_143011_b", at = @At("TAIL"))
    private void readFisherman(NBTTagCompound tag, CallbackInfo ci) {
        this.ifhy$hasFisherman = tag.getBoolean("IfhyFisherman");
    }

    @Inject(method = "addComponentParts", at = @At("TAIL"))
    private void spawnCureableFisherman(World world, Random random, StructureBoundingBox bounds,
                                        CallbackInfoReturnable<Boolean> cir) {
        if (world.isRemote || this.ifhy$hasFisherman || !cir.getReturnValueZ()) return;
        int x = this.getXWithOffset(3, 5);
        int y = this.getYWithOffset(2);
        int z = this.getZWithOffset(3, 5);
        if (!bounds.isVecInside(x, y, z)) return;

        EntityZombie fisherman = new EntityZombie(world);
        fisherman.setVillager(true);
        fisherman.villagerClass = EntityFishermanVillager.PROFESSION_ID;
        fisherman.preInitCreature();
        fisherman.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
        fisherman.onSpawnWithEgg((EntityLivingData)null);
        fisherman.setPersistent(true);
        world.spawnEntityInWorld(fisherman);
        this.ifhy$hasFisherman = true;
    }
}
