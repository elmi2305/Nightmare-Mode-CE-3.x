package com.itlesports.nightmaremode.mixin;

import api.item.items.PlaceAsBlockItem;
import btw.block.blocks.SaplingBlock;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.RailPlacementConfig;
import net.minecraft.src.Block;
import net.minecraft.src.BlockRailBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

@Mixin(PlaceAsBlockItem.class)
public abstract class PlaceAsBlockItemMixin {
    @Shadow public abstract int getBlockIDToPlace(World world, int damage, int facing, float clickX, float clickY, float clickZ);

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void automateRailPlacement(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                       int facing, float clickX, float clickY, float clickZ,
                                       CallbackInfoReturnable<Boolean> cir) {
        int railId = this.getBlockIDToPlace(world, stack.getItemDamage(), facing, clickX, clickY, clickZ);
        if (!(Block.blocksList[railId] instanceof BlockRailBase)) {
            return;
        }

        if (BlockRailBase.isRailBlockAt(world, x, y, z)) {
            int[] endpoint = this.findEndpoint(world, x, y, z);
            if (endpoint != null && this.extendRail(world, player, stack, railId, endpoint)) {
                cir.setReturnValue(true);
            }
            return;
        }

        int[] target = this.getPlacementTarget(world, x, y, z, facing);
        if (!world.doesBlockHaveSolidTopSurface(target[0], target[1] - 1, target[2])
                && this.placeSubstrate(world, player, target[0], target[1] - 1, target[2])) {
            return;
        }
    }

    @Inject(method = "canItemBeUsedByPlayer", at = @At("HEAD"), cancellable = true)
    private void allowRepairableRailPlacement(World world, int x, int y, int z, int facing,
                                              EntityPlayer player, ItemStack stack,
                                              CallbackInfoReturnable<Boolean> cir) {
        int railId = this.getBlockIDToPlace(world, stack.getItemDamage(), facing, 0.5F, 0.5F, 0.5F);
        if (railId <= 0 || !(Block.blocksList[railId] instanceof BlockRailBase)) {
            return;
        }
        
        if (BlockRailBase.isRailBlockAt(world, x, y, z)) {
            cir.setReturnValue(true);
            return;
        }

        int[] target = this.getPlacementTarget(world, x, y, z, facing);
        if (!world.doesBlockHaveSolidTopSurface(target[0], target[1] - 1, target[2])
                && this.hasAvailableSubstrate(player)) {
            cir.setReturnValue(true);
        }
    }

    private int[] getPlacementTarget(World world, int x, int y, int z, int facing) {
        Block clicked = Block.blocksList[world.getBlockId(x, y, z)];
        if (clicked != null && clicked.isGroundCover()) {
            facing = 1;
        } else if (clicked != null && !clicked.blockMaterial.isReplaceable()) {
            int[] offsetX = {0, 0, 0, 0, -1, 1};
            int[] offsetY = {-1, 1, 0, 0, 0, 0};
            int[] offsetZ = {0, 0, -1, 1, 0, 0};
            x += offsetX[facing];
            y += offsetY[facing];
            z += offsetZ[facing];
        }
        return new int[]{x, y, z};
    }

    private int[] findEndpoint(World world, int startX, int startY, int startZ) {
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(new int[]{startX, startY, startZ, 0});
        int[] best = null;

        while (!queue.isEmpty()) {
            int[] current = queue.removeFirst();
            String key = current[0] + ":" + current[1] + ":" + current[2];
            if (!visited.add(key) || current[3] > 4) {
                continue;
            }

            int[][] neighbors = this.getRailNeighbors(world, current[0], current[1], current[2]);
            if (neighbors.length <= 1 && (best == null || current[3] < best[3])) {
                best = current;
            }
            if (current[3] == 4) {
                continue;
            }
            for (int[] neighbor : neighbors) {
                queue.addLast(new int[]{neighbor[0], neighbor[1], neighbor[2], current[3] + 1});
            }
        }
        return best;
    }

    private int[][] getRailNeighbors(World world, int x, int y, int z) {
        java.util.ArrayList<int[]> neighbors = new java.util.ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] direction : directions) {
            for (int yOffset = -1; yOffset <= 1; ++yOffset) {
                if (BlockRailBase.isRailBlockAt(world, x + direction[0], y + yOffset, z + direction[1])) {
                    neighbors.add(new int[]{x + direction[0], y + yOffset, z + direction[1]});
                    break;
                }
            }
        }
        return neighbors.toArray(new int[0][]);
    }

    private boolean extendRail(World world, EntityPlayer player, ItemStack railStack, int railId, int[] endpoint) {
        int[][] neighbors = this.getRailNeighbors(world, endpoint[0], endpoint[1], endpoint[2]);
        int dx;
        int dz;
        if (neighbors.length == 1) {
            dx = Integer.signum(endpoint[0] - neighbors[0][0]);
            dz = Integer.signum(endpoint[2] - neighbors[0][2]);
        } else {
            int direction = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            dx = direction == 1 ? -1 : direction == 3 ? 1 : 0;
            dz = direction == 0 ? 1 : direction == 2 ? -1 : 0;
        }

        int targetX = endpoint[0] + dx;
        int targetY = endpoint[1];
        int targetZ = endpoint[2] + dz;

        // Match normal block placement: grass and other replaceable blocks can
        // be replaced. A solid next block instead provides the support for an
        // upward-sloping rail when its top is clear.
        if (!this.isReplaceable(world, targetX, targetY, targetZ)) {
            if (!world.doesBlockHaveSolidTopSurface(targetX, targetY, targetZ)
                    || !this.isReplaceable(world, targetX, targetY + 1, targetZ)) {
                return false;
            }
            ++targetY;
        }
        if (!world.doesBlockHaveSolidTopSurface(targetX, targetY - 1, targetZ)
                && !this.placeSubstrate(world, player, targetX, targetY - 1, targetZ)) {
            return false;
        }
        if (!world.canPlaceEntityOnSide(railId, targetX, targetY, targetZ, false, 1, player, railStack)) {
            return false;
        }

        if (world.setBlockWithNotify(targetX, targetY, targetZ, railId)) {
            Block rail = Block.blocksList[railId];
            rail.onBlockPlacedBy(world, targetX, targetY, targetZ, player, railStack);
            rail.onPostBlockPlaced(world, targetX, targetY, targetZ, 0);
            world.playSoundEffect(targetX + 0.5F, targetY + 0.5F, targetZ + 0.5F,
                    rail.stepSound.getPlaceSound(), (rail.stepSound.getVolume() + 1.0F) / 2.0F,
                    rail.stepSound.getPitch() * 0.8F);
            if (!player.capabilities.isCreativeMode) {
                --railStack.stackSize;
            }
            return true;
        }
        return false;
    }

    private boolean isReplaceable(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        return blockId == 0 || Block.blocksList[blockId].blockMaterial.isReplaceable();
    }

    private boolean placeSubstrate(World world, EntityPlayer player, int x, int y, int z) {
        if (!world.isAirBlock(x, y, z) && !Block.blocksList[world.getBlockId(x, y, z)].blockMaterial.isReplaceable()) {
            return false;
        }

        for (int substrateId : RailPlacementConfig.getSubstrateBlockIds()) {
            if (!player.capabilities.isCreativeMode && !player.inventory.hasItem(substrateId)) {
                continue;
            }
            if (!world.canPlaceEntityOnSide(substrateId, x, y, z, false, 1, player, new ItemStack(substrateId, 1, 0))) {
                continue;
            }
            if (world.setBlockWithNotify(x, y, z, substrateId)) {
                if (!player.capabilities.isCreativeMode) {
                    player.inventory.consumeInventoryItem(substrateId);
                }
                return true;
            }
        }
        return false;
    }

    private boolean hasAvailableSubstrate(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return !RailPlacementConfig.getSubstrateBlockIds().isEmpty();
        }
        for (int substrateId : RailPlacementConfig.getSubstrateBlockIds()) {
            if (player.inventory.hasItem(substrateId)) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = "onItemUse", at = @At("RETURN"))
    private void trackSaplingPlanting(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                      int facing, float clickX, float clickY, float clickZ,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || player == null || world.isRemote) {
            return;
        }
        Block placed = Block.blocksList[this.getBlockIDToPlace(world, stack.getItemDamage(), facing, clickX, clickY, clickZ)];
        if (placed == Block.sapling || placed instanceof SaplingBlock) {
            SkillHandler.incrementSaplingsPlanted(player);
        }
    }
}
