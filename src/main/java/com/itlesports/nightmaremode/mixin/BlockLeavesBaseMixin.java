package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.JungleSpiderEntity;
import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockLeavesBase.class)
public class BlockLeavesBaseMixin extends Block {
    protected BlockLeavesBaseMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "addCollisionBoxesToList", at = @At("HEAD"))
    private void creeperCollideWithLeaves(World world, int x, int y, int z, AxisAlignedBB aabb, List bbList, Entity entity, CallbackInfo ci){
//        if (entity instanceof EntityCreeper creeper && creeper.getCreeperState() == 1) {
//            super.addCollisionBoxesToList(world, x, y, z, aabb, bbList, entity);
//        }
    }
}
