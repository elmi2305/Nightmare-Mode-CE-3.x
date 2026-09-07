package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.FenceGateShape;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockDirectional;
import net.minecraft.src.BlockFenceGate;
import net.minecraft.src.Entity;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(BlockFenceGate.class)
public class BlockFenceGateMixin extends BlockDirectional {
    protected BlockFenceGateMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "getCollisionBoundingBoxFromPool", at = @At("HEAD"), cancellable = true)
    private void cornerCollisionBounds(World world, int x, int y, int z, CallbackInfoReturnable<AxisAlignedBB> cir) {
        int mask = FenceGateShape.cornerMask(world, x, y, z);
        if (mask == 0) return;
        if (BlockFenceGate.isFenceGateOpen(world.getBlockMetadata(x, y, z))) {
            cir.setReturnValue(null);
        } else {
            FenceGateShape.Box bounds = FenceGateShape.collisionBounds(mask);
            cir.setReturnValue(AxisAlignedBB.getAABBPool().getAABB(x + bounds.minX(), y, z + bounds.minZ(),
                    x + bounds.maxX(), y + 1.5, z + bounds.maxZ()));
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB query, List boxes, Entity entity) {
        int mask = FenceGateShape.cornerMask(world, x, y, z);
        if (mask == 0) {
            super.addCollisionBoxesToList(world, x, y, z, query, boxes, entity);
            return;
        }
        if (BlockFenceGate.isFenceGateOpen(world.getBlockMetadata(x, y, z))) return;
        for (FenceGateShape.Box part : FenceGateShape.collision(mask)) {
            AxisAlignedBB box = cornerBox(part, x, y, z);
            if (query.intersectsWith(box)) boxes.add(box);
        }
    }

    @Inject(method = "getBlockBoundsFromPoolBasedOnState", at = @At("HEAD"), cancellable = true)
    private void cornerSelectionBounds(IBlockAccess world, int x, int y, int z, CallbackInfoReturnable<AxisAlignedBB> cir) {
        int mask = FenceGateShape.cornerMask(world, x, y, z);
        if (mask != 0) {
            cir.setReturnValue(cornerBox(FenceGateShape.bounds(mask,
                    BlockFenceGate.isFenceGateOpen(world.getBlockMetadata(x, y, z))), 0, 0, 0));
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        int mask = FenceGateShape.cornerMask(world, x, y, z);
        if (mask == 0) return super.collisionRayTrace(world, x, y, z, start, end);
        MovingObjectPosition closest = null;
        boolean open = BlockFenceGate.isFenceGateOpen(world.getBlockMetadata(x, y, z));
        for (FenceGateShape.Box part : FenceGateShape.parts(mask, open)) {
            MovingObjectPosition hit = cornerBox(part, x, y, z).calculateIntercept(start, end);
            if (hit != null && (closest == null || start.squareDistanceTo(hit.hitVec) < start.squareDistanceTo(closest.hitVec))) {
                closest = hit;
            }
        }
        if (closest != null) {
            closest.blockX = x;
            closest.blockY = y;
            closest.blockZ = z;
        }
        return closest;
    }

    @Unique
    private static AxisAlignedBB cornerBox(FenceGateShape.Box box, int x, int y, int z) {
        return AxisAlignedBB.getAABBPool().getAABB(x + box.minX(), y + box.minY(), z + box.minZ(),
                x + box.maxX(), y + box.maxY(), z + box.maxZ());
    }

    public boolean isFallingBlock() {
        return NightmareMode.noSkybases || super.isFallingBlock();
    }

    public void onBlockAdded(World world, int i, int j, int k) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) {
            this.scheduleCheckForFall(world, i, j, k);
        }
        super.onBlockAdded(world,i,j,k);
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) {
            this.scheduleCheckForFall(world, i, j, k);
        }
        super.onNeighborBlockChange(world,i,j,k,iNeighborBlockID);
    }

    public void updateTick(World world, int i, int j, int k, Random rand) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) {
            this.checkForFall(world, i, j, k);
        }
        super.updateTick(world,i,j,k,rand);
    }

    public int tickRate(World par1World) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(par1World)) {
            return 4;
        }
        return super.tickRate(par1World);
    }
}
