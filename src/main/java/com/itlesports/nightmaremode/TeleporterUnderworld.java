package com.itlesports.nightmaremode;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;

public class TeleporterUnderworld extends Teleporter {
    private static final int DIM_UNDERWORLD = NightmareMode.UNDERWORLD_DIMENSION;
    private static final int PORTAL_ID      = NMBlocks.underworldPortal.blockID;

    public TeleporterUnderworld(WorldServer world) {
        super(world);
    }


    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float yaw) {
        // If we’re coming *back* from the Underworld, carve out/reset portal frame like the End does:
        if (this.worldServerInstance.provider.dimensionId == DIM_UNDERWORLD) {
            int px = MathHelper.floor_double(entity.posX);
            int py = MathHelper.floor_double(entity.posY) - 1;
            int pz = MathHelper.floor_double(entity.posZ);
            int dx = 1, dz = 0;

            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    for (int k = -1; k < 3; k++) {
                        int wx = px + j * dx + i * dz;
                        int wy = py + k;
                        int wz = pz + j * dz - i * dx;
                        boolean isFrame = (k < 0);
                        this.worldServerInstance.setBlock(
                                wx, wy, wz,
                                isFrame ? BTWBlocks.soulforgedSteelBlock.blockID : 0
                        );
                    }
                }
            }

            entity.setLocationAndAngles(px + 0.5, py, pz + 0.5, entity.rotationYaw, 0f);
            entity.motionX = entity.motionY = entity.motionZ = 0;
            return;
        }

        // Normal path: try existing portal → make one → retry find
        if (this.placeInExistingPortal(entity, x, y, z, yaw)) return;
        this.makePortal(entity);
        this.placeInExistingPortal(entity, x, y, z, yaw);
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw) {
        int range = 128;
        double bestDistSq = -1;
        int bestX = 0, bestY = 0, bestZ = 0;

        int entX = MathHelper.floor_double(entity.posX);
        int entZ = MathHelper.floor_double(entity.posZ);
        long key = ChunkCoordIntPair.chunkXZ2Int(entX, entZ);

        boolean fromCache = destinationCoordinateCache.containsItem(key);
        if (fromCache) {
            PortalPosition pos = (PortalPosition) destinationCoordinateCache.getValueByKey(key);
            bestDistSq = 0;
            bestX = pos.posX;
            bestY = pos.posY;
            bestZ = pos.posZ;
            pos.lastUpdateTime = worldServerInstance.getTotalWorldTime();
        } else {
            // scan for nearest Underworld‐portal block
            for (int i = entX - range; i <= entX + range; i++) {
                double dx = (i + 0.5) - entity.posX;
                for (int j = entZ - range; j <= entZ + range; j++) {
                    double dz = (j + 0.5) - entity.posZ;
                    for (int k = worldServerInstance.getActualHeight() - 1; k >= 0; k--) {
                        if (worldServerInstance.getBlockId(i, k, j) != PORTAL_ID) continue;
                        // drop to base of portal column
                        while (worldServerInstance.getBlockId(i, k - 1, j) == PORTAL_ID) k--;
                        double dy = (k + 0.5) - entity.posY;
                        double distSq = dx*dx + dy*dy + dz*dz;
                        if (bestDistSq < 0 || distSq < bestDistSq) {
                            bestDistSq = distSq;
                            bestX = i;
                            bestY = k;
                            bestZ = j;
                        }
                        break;
                    }
                }
            }
        }

        if (bestDistSq >= 0) {
            if (!fromCache) {
                destinationCoordinateCache.add(key,
                        new PortalPosition(this, bestX, bestY, bestZ, worldServerInstance.getTotalWorldTime())
                );
                destinationCoordinateKeys.add(key);
            }

            double px = bestX + 0.5;
            double py = bestY + 0.5;
            double pz = bestZ + 0.5;

            // Figure out portal orientation for motion/rotation
            int side = -1;
            if (worldServerInstance.getBlockId(bestX - 1, bestY, bestZ) == PORTAL_ID) side = 2;
            if (worldServerInstance.getBlockId(bestX + 1, bestY, bestZ) == PORTAL_ID) side = 0;
            if (worldServerInstance.getBlockId(bestX, bestY, bestZ - 1) == PORTAL_ID) side = 3;
            if (worldServerInstance.getBlockId(bestX, bestY, bestZ + 1) == PORTAL_ID) side = 1;

            int dir = entity.getTeleportDirection();
            if (side >= 0) {
                int var31 = Direction.rotateLeft[side];
                int var32 = Direction.offsetX[side];
                int var33 = Direction.offsetZ[side];
                int var34 = Direction.offsetX[var31];
                int var35 = Direction.offsetZ[var31];
                boolean var36 = !this.worldServerInstance.isAirBlock(bestX + var32 + var34, bestY, bestZ + var33 + var35) || !this.worldServerInstance.isAirBlock(bestX + var32 + var34, bestY + 1, bestZ + var33 + var35);
                boolean var37 = !this.worldServerInstance.isAirBlock(bestX + var32, bestY, bestZ + var33) || !this.worldServerInstance.isAirBlock(bestX + var32, bestY + 1, bestZ + var33);
                if (var36 && var37) {
                    side = Direction.rotateOpposite[side];
                    var31 = Direction.rotateOpposite[var31];
                    var32 = Direction.offsetX[side];
                    var33 = Direction.offsetZ[side];
                    var34 = Direction.offsetX[var31];
                    var35 = Direction.offsetZ[var31];
                    int var49 = bestX - var34;
                    px -= (double)var34;
                    int var22 = bestZ - var35;
                    pz -= (double)var35;
                    var36 = !this.worldServerInstance.isAirBlock(var49 + var32 + var34, bestY, var22 + var33 + var35) || !this.worldServerInstance.isAirBlock(var49 + var32 + var34, bestY + 1, var22 + var33 + var35);
                    var37 = !this.worldServerInstance.isAirBlock(var49 + var32, bestY, var22 + var33) || !this.worldServerInstance.isAirBlock(var49 + var32, bestY + 1, var22 + var33);
                }

                float var38 = 0.5F;
                float var39 = 0.5F;
                if (!var36 && var37) {
                    var38 = 1.0F;
                } else if (var36 && !var37) {
                    var38 = 0.0F;
                } else if (var36 && var37) {
                    var39 = 0.0F;
                }

                px += (double)((float)var34 * var38 + var39 * (float)var32);
                pz += (double)((float)var35 * var38 + var39 * (float)var33);
                float var40 = 0.0F;
                float var41 = 0.0F;
                float var42 = 0.0F;
                float var43 = 0.0F;
                if (side == dir) {
                    var40 = 1.0F;
                    var41 = 1.0F;
                } else if (side == Direction.rotateOpposite[dir]) {
                    var40 = -1.0F;
                    var41 = -1.0F;
                } else if (side == Direction.rotateRight[dir]) {
                    var42 = 1.0F;
                    var43 = -1.0F;
                } else {
                    var42 = -1.0F;
                    var43 = 1.0F;
                }

                double var44 = entity.motionX;
                double var46 = entity.motionZ;
                entity.motionX = var44 * (double)var40 + var46 * (double)var43;
                entity.motionZ = var44 * (double)var42 + var46 * (double)var41;
                entity.rotationYaw = yaw - (float)(dir * 90) + (float)(side * 90);
            } else {
                entity.motionX = entity.motionY = entity.motionZ = 0;
            }

            entity.setLocationAndAngles(px, py, pz, entity.rotationYaw, entity.rotationPitch);
            return true;
        }

        return false;
    }

    @Override
    public boolean makePortal(Entity entity) {
        byte searchRadius = 16;
        double bestDistSq = -1.0;
        int entX = MathHelper.floor_double(entity.posX);
        int entY = MathHelper.floor_double(entity.posY);
        int entZ = MathHelper.floor_double(entity.posZ);

        int bestX = entX, bestY = entY, bestZ = entZ;
        int bestDir = 0;
        int randDir = entity.rand.nextInt(4);

        // Find the best spot
        for (int x = entX - searchRadius; x <= entX + searchRadius; x++) {
            double dx = x + 0.5 - entity.posX;
            for (int z = entZ - searchRadius; z <= entZ + searchRadius; z++) {
                double dz = z + 0.5 - entity.posZ;
                for (int y = this.worldServerInstance.getActualHeight() - 1; y >= 0; y--) {
                    if (!this.worldServerInstance.isAirBlock(x, y, z)) continue;
                    // drop to ground
                    while (y > 0 && this.worldServerInstance.isAirBlock(x, y - 1, z)) y--;

                    // try all 4 orientations
                    outer:
                    for (int dirOffset = randDir; dirOffset < randDir + 4; dirOffset++) {
                        int dxOff = dirOffset % 2;
                        int dzOff = 1 - dxOff;
                        if (dirOffset % 4 >= 2) { dxOff = -dxOff; dzOff = -dzOff; }

                        // check 3×4×4 volume for portal frame + interior
                        for (int px = 0; px < 3; px++) {
                            for (int pz = 0; pz < 4; pz++) {
                                for (int py = -1; py < 4; py++) {
                                    int wx = x + (pz - 1) * dxOff + px * dzOff;
                                    int wy = y + py;
                                    int wz = z + (pz - 1) * dzOff - px * dxOff;

                                    boolean shouldBeFrame = (py < 0);
                                    if (shouldBeFrame) {
                                        if (!this.worldServerInstance.getBlockMaterial(wx, wy, wz).isSolid())
                                            continue outer;
                                    } else {
                                        if (!this.worldServerInstance.isAirBlock(wx, wy, wz))
                                            continue outer;
                                    }
                                }
                            }
                        }

                        double dy = y + 0.5 - entity.posY;
                        double distSq = dx*dx + dy*dy + dz*dz;
                        if (bestDistSq < 0 || distSq < bestDistSq) {
                            bestDistSq = distSq;
                            bestX = x;
                            bestY = y;
                            bestZ = z;
                            bestDir = dirOffset % 4;
                        }
                    }
                }
            }
        }

        // Fallback: clamp Y
        if (bestDistSq < 0) {
            bestY = MathHelper.clamp_int(bestY, 70, this.worldServerInstance.getActualHeight() - 10);
        }

        // Decompose direction
        int dxOff = bestDir % 2;
        int dzOff = 1 - dxOff;
        if (bestDir % 4 >= 2) { dxOff = -dxOff; dzOff = -dzOff; }

        // Build steel frame + portal blocks
        for (int px = 0; px < 4; px++) {
            for (int pz = 0; pz < 4; pz++) {
                for (int py = -1; py < 4; py++) {
                    int wx = bestX + (pz - 1) * dxOff + px * dzOff;
                    int wy = bestY + py;
                    int wz = bestZ + (pz - 1) * dzOff - px * dxOff;
                    boolean isFrame = (px == 0 || px == 3 || py == -1 || py == 3);
                    int id = isFrame
                            ? BTWBlocks.soulforgedSteelBlock.blockID
                            : NMBlocks.underworldPortal.blockID;
                    this.worldServerInstance.setBlock(wx, wy, wz, id, 0, 2);
                }
            }
        }

        // Notify neighbors so clients update rendering
        for (int px = 0; px < 4; px++) {
            for (int py = -1; py < 4; py++) {
                for (int pz = 0; pz < 4; pz++) {
                    int wx = bestX + (pz - 1) * dxOff + px * dzOff;
                    int wy = bestY + py;
                    int wz = bestZ + (pz - 1) * dzOff - px * dxOff;
                    this.worldServerInstance.notifyBlocksOfNeighborChange(
                            wx, wy, wz,
                            this.worldServerInstance.getBlockId(wx, wy, wz)
                    );
                }
            }
        }

        return true;
    }

}
