package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.Direction;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenBigFlower extends WorldGenerator {


    private int minFlowerHeight = 3;
    private int leafMeta;
    private int metaWood;
    private boolean vinesGrow;
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // overall stem height (taller than vanilla trees to make a "giant flower")
        int stemHeight = rand.nextInt(4) + 6;
        boolean canGenerate = true;

        // Make sure top won't exceed build limit
        if (y < 1 || y + stemHeight + 3 > 256) return false; // +3 for head layers

        // Choose a horizontal curvature direction: pick either X or Z (no diagonal)
        int curveDX = 0, curveDZ = 0;
        if (rand.nextBoolean()) {
            curveDX = rand.nextBoolean() ? 1 : -1;
        } else {
            curveDZ = rand.nextBoolean() ? 1 : -1;
        }

        // Limit total horizontal shift to 1..2 blocks (feedback #1)
        int totalHorizontalShift = 1 + rand.nextInt(2); // 1 or 2

        // Decide where to start curving (upper portion of the stem)
        int startCurveIndex = Math.max(1, stemHeight * 2 / 3); // start when about top third begins
        int availableStepsForShift = Math.max(1, stemHeight - startCurveIndex);
        int shiftSpacing = Math.max(1, (int)Math.ceil((double)availableStepsForShift / totalHorizontalShift));

        // Build the intended stem path while checking space
        int[] pathX = new int[stemHeight];
        int[] pathY = new int[stemHeight];
        int[] pathZ = new int[stemHeight];

        int accumulatedShift = 0;
        int shiftsDone = 0;
        for (int i = 0; i < stemHeight; ++i) {
            // apply a horizontal shift at spaced intervals after startCurveIndex
            if (i >= startCurveIndex && shiftsDone < totalHorizontalShift && ((i - startCurveIndex) % shiftSpacing == 0)) {
                accumulatedShift++;
                shiftsDone++;
            }
            pathX[i] = x + accumulatedShift * curveDX;
            pathY[i] = y + i;
            pathZ[i] = z + accumulatedShift * curveDZ;
        }

        // Head parameters (feedback #3: radius increased to 3)
        int headBaseY = y + stemHeight; // base (lowest) layer of the inverted pyramid (cup)
        int headTopY = headBaseY + 2;   // top layer (largest)
        int headRadius = 3;

        // Check world bounds and replaceable blocks along stem path and head area
        for (int i = 0; i < stemHeight && canGenerate; ++i) {
            int px = pathX[i], py = pathY[i], pz = pathZ[i];
            if (py < 0 || py >= 256) { canGenerate = false; break; }
            if (!canReplaceBlock(world, px, py, pz)) { canGenerate = false; break; }
        }

        outer:
        for (int hx = pathX[stemHeight - 1] - headRadius; hx <= pathX[stemHeight - 1] + headRadius && canGenerate; ++hx) {
            for (int hz = pathZ[stemHeight - 1] - headRadius; hz <= pathZ[stemHeight - 1] + headRadius && canGenerate; ++hz) {
                for (int hy = headBaseY; hy <= headTopY + 1 && canGenerate; ++hy) { // +1 for the extra upward center block check
                    if (hy < 0 || hy >= 256) { canGenerate = false; break outer; }
                    if (!canReplaceBlock(world, hx, hy, hz)) { canGenerate = false; break outer; }
                }
            }
        }

        if (!canGenerate) return false;

        // Ensure it is planted on dirt/grass
        int groundBlockId = world.getBlockId(x, y - 1, z);
        if (!(groundBlockId == Block.grass.blockID || groundBlockId == Block.dirt.blockID || groundBlockId == NMBlocks.flowerGrass.blockID)) return false;

        // place a dirt base (same as before)
        int flowerCount = 1;

        for (int i = 0; i < flowerCount; i++) {
            int offsetX = x + rand.nextInt(8) - 6;
            int offsetZ = z + rand.nextInt(8) - 6;

            generateFlowerAt(
                    world,
                    rand,
                    offsetX,
                    y,
                    offsetZ,
                    stemHeight
            );
        }

        return true;
    }

/* -----------------------------
   Helper methods used above
   ----------------------------- */

    private boolean canReplaceBlock(World world, int px, int py, int pz) {
        int id = world.getBlockId(px, py, pz);
        return id == 0 || id == Block.leaves.blockID || id == Block.grass.blockID || id == Block.dirt.blockID || id == NMBlocks.plantMatter.blockID || id == NMBlocks.flowerGrass.blockID;
    }

    private void placeLeafIfAir(World world, int px, int py, int pz) {
        if (py < 0 || py >= 256) return;
        int id = world.getBlockId(px, py, pz);
        if (id == 0 || id == Block.leaves.blockID) {
            this.setBlockAndMetadata(world, px, py, pz, Block.leaves.blockID, this.leafMeta);
        }
    }

    /**
     * Place a circular petal layer (filled circle) at given Y with given radius.
     * Uses leaves for petal material.
     */
    private void placePetalLayer(World world, int centerX, int layerY, int centerZ, int radius) {
        if (layerY < 0 || layerY >= 256) return;
        int rSq = radius * radius;
        for (int px = centerX - radius; px <= centerX + radius; ++px) {
            for (int pz = centerZ - radius; pz <= centerZ + radius; ++pz) {
                int dx = px - centerX;
                int dz = pz - centerZ;
                if (dx * dx + dz * dz <= rSq) {
                    if (canReplaceBlock(world, px, layerY, pz)) {
                        this.setBlockAndMetadata(world, px, layerY, pz, Block.leaves.blockID, this.leafMeta);
                    }
                }
            }
        }
    }

    /**
     * Build an inverted pyramid (cup) centered at (centerX, centerZ) starting at baseY and going up 'height' layers.
     * The topmost layer will have the largest radius; the base (lowest) will have radius = 1 (or minimum).
     *
     * Example with height=3 and topRadius=3:
     *   y+2 radius 3
     *   y+1 radius 2
     *   y   radius 1
     */
// --- new placeInvertedPyramid: creates a hollow cup (ring layers) + an outward "lip" / base extension ---
    private void placeInvertedPyramid(World world, int centerX, int baseY, int centerZ, int topRadius, int height) {
        // Build ring layers from lowest (baseY) up to baseY + height - 1,
        // with radius increasing each layer so top layer is largest.
        // We place only the perimeter for a cup effect (hollow interior).
        for (int layer = 0; layer < height; ++layer) {
            int layerY = baseY + layer;
            if (layerY < 0 || layerY >= 256) continue;
            // radius increases toward the top
            int radiusAtLayer = Math.max(1, topRadius - (height - 1 - layer));
            int innerRadiusSq = (radiusAtLayer - 1) * (radiusAtLayer - 1);
            int outerRadiusSq = radiusAtLayer * radiusAtLayer;
            for (int px = centerX - radiusAtLayer; px <= centerX + radiusAtLayer; ++px) {
                for (int pz = centerZ - radiusAtLayer; pz <= centerZ + radiusAtLayer; ++pz) {
                    int dx = px - centerX;
                    int dz = pz - centerZ;
                    int distSq = dx * dx + dz * dz;
                    // place only perimeter (outer ring) to make a cup (hollow inside)

                    if (dx * dx + dz * dz <= outerRadiusSq * outerRadiusSq) {
                        if (canReplaceBlock(world, px, layerY, pz)) {
                            this.setBlockAndMetadata(world, px, layerY, pz, Block.leaves.blockID, this.leafMeta);
                        }
                    }
                }
            }
        }

        // Add a base extension (lip) around the lowest layer so the cup reads like a tulip rim.
        int lipY = baseY + height;
        if (lipY >= 0 && lipY < 256) {
            int lipRadius = Math.max(1, topRadius); // same or slightly larger than top radius
            int outerLipSq = lipRadius * lipRadius;
            int innerLipSq = (lipRadius - 1) * (lipRadius - 1);
            for (int px = centerX - lipRadius; px <= centerX + lipRadius; ++px) {
                for (int pz = centerZ - lipRadius; pz <= centerZ + lipRadius; ++pz) {
                    int dx = px - centerX;
                    int dz = pz - centerZ;
                    int distSq = dx * dx + dz * dz;
                    // place a thin lip ring at lipY
                    if (distSq <= outerLipSq && distSq > innerLipSq) {
                        if (canReplaceBlock(world, px, lipY, pz)) {
                            this.setBlockAndMetadata(world, px, lipY, pz, Block.leaves.blockID, this.leafMeta);
                        }
                    }
                }
            }
        }

        lipY = baseY - 1;
        if (lipY >= 0 && lipY < 256) {
            int lipRadius = 2; // same or slightly larger than top radius
            int outerLipSq = lipRadius * lipRadius;
            int innerLipSq = (lipRadius - 1) * (lipRadius - 1);
            for (int px = centerX - lipRadius; px <= centerX + lipRadius; ++px) {
                for (int pz = centerZ - lipRadius; pz <= centerZ + lipRadius; ++pz) {
                    int dx = px - centerX;
                    int dz = pz - centerZ;
                    int distSq = dx * dx + dz * dz;
                    // place a thin lip ring at lipY
                    if (distSq <= outerLipSq && distSq > innerLipSq) {
                        if (canReplaceBlock(world, px, lipY, pz)) {
                            this.setBlockAndMetadata(world, px, lipY, pz, Block.leaves.blockID, this.leafMeta);
                        }
                    }
                }
            }
        }
    }


    // --- new helper: generate a single tulip-like flower (shaft + tip) at (baseX, baseY, baseZ) ---
// returns true if created (false if blocked)
    private boolean generateFlowerAt(World world, Random rand, int baseX, int baseY, int baseZ, int stemHeight) {
        // small per-flower stem height and gentle curvature (max 1-2 blocks total)
//        int stemHeight = rand.nextInt(9) + 7; // 6..8
        if (baseY < 1 || baseY + stemHeight + 4 >= 256) return false;

        // choose axis to curve along
        int curveDX = 0, curveDZ = 0;
        if (rand.nextBoolean()) curveDX = rand.nextBoolean() ? 1 : -1;
        else curveDZ = rand.nextBoolean() ? 1 : -1;

        // limit horizontal shift to 0..2 and spread it across upper stem
        int totalShift = rand.nextInt(3) + 1;
        int startCurve = Math.max(1, stemHeight * 2 / 3);
        int steps = Math.max(1, stemHeight - startCurve);
        int spacing = (totalShift == 0) ? Integer.MAX_VALUE : Math.max(1, (int)Math.ceil((double)steps / totalShift));

        int accumulatedShift = 0;
        int shiftsDone = 0;
        int curX = baseX, curZ = baseZ;

        // compute intended path and check stem space
        int[] pathX = new int[stemHeight];
        int[] pathY = new int[stemHeight];
        int[] pathZ = new int[stemHeight];

        // check space first
        for (int i = 0; i < stemHeight; ++i) {
            if (i >= startCurve && shiftsDone < totalShift && ((i - startCurve) % spacing == 0)) {
                accumulatedShift++;
                shiftsDone++;
            }
            curX = baseX + accumulatedShift * curveDX;
            curZ = baseZ + accumulatedShift * curveDZ;

            int py = baseY + i;

            pathX[i] = curX;
            pathY[i] = py;
            pathZ[i] = curZ;

            if (py < 0 || py >= 256) return false;
            if (!canReplaceBlock(world, curX, py, curZ)) return false;
        }

        // compute head parameters
        int headBaseY = baseY + stemHeight;
        int topRadius = 3;
        int headHeight = 3;

        // final tip coords from last path entry
        int tipX = pathX[stemHeight - 1];
        int tipZ = pathZ[stemHeight - 1];


        // check head space
        for (int hx = tipX - topRadius; hx <= tipX + topRadius; ++hx) {
            for (int hz = tipZ - topRadius; hz <= tipZ + topRadius; ++hz) {
                for (int hy = headBaseY - 1; hy <= headBaseY + headHeight; ++hy) { // include lip (baseY-1)
                    if (hy < 0 || hy >= 256) return false;
                    if (!canReplaceBlock(world, hx, hy, hz)) return false;
                }
            }
        }

        // place a dirt base under original anchor if necessary
        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID && groundId != NMBlocks.flowerGrass.blockID && groundId != NMBlocks.flowerDirt    .blockID) return false;
        this.setBlock(world, baseX, baseY - 1, baseZ, Block.dirt.blockID);
// edit ^

        // place stem and small leaves
        accumulatedShift = 0;
        shiftsDone = 0;
        for (int i = 0; i < stemHeight; ++i) {
            if (i >= startCurve && shiftsDone < totalShift && ((i - startCurve) % spacing == 0)) {
                accumulatedShift++;
                shiftsDone++;
            }
            int sx = baseX + accumulatedShift * curveDX;
            int sz = baseZ + accumulatedShift * curveDZ;
            int sy = baseY + i;

            int existing = world.getBlockId(sx, sy, sz);
            if (existing == 0 || existing == Block.leaves.blockID) {
                this.setBlockAndMetadata(world, sx, sy, sz, NMBlocks.plantMatter.blockID, this.metaWood);
            }

// a few small leaves attached low on the stem (stem of a few leaves)
            if (i > 0 && i <= Math.min(3, stemHeight / 3)) {
                if (curveDX != 0) {
                    placeLeafIfAir(world, sx, sy, sz + 1);
                    placeLeafIfAir(world, sx, sy, sz - 1);
                } else {
                    placeLeafIfAir(world, sx + 1, sy, sz);
                    placeLeafIfAir(world, sx - 1, sy, sz);
                }
                if (rand.nextInt(3) == 0) {
                    int dx = (curveDX != 0) ? curveDX : (rand.nextBoolean() ? 1 : -1);
                    int dz = (curveDZ != 0) ? curveDZ : (rand.nextBoolean() ? 1 : -1);
                    placeLeafIfAir(world, sx + dx, sy, sz + dz);
                }
            }
            // small branches occasionally (subtle, not spammy)
            if (i > 2 && rand.nextInt(4) == 0) {
                generateMiniBranch(world, rand, sx, sy, sz);
            }
        }

        // place inverted cup and lip
        placeInvertedPyramid(world, curX, headBaseY, curZ, topRadius, headHeight);

        // place a small filled layer at the very top center to give the tulip 'bulk' (use petals/leaves)
        int topCenterY = headBaseY + headHeight;
        if (topCenterY < 256 && canReplaceBlock(world, curX, topCenterY, curZ)) {
            this.setBlockAndMetadata(world, curX, topCenterY, curZ, Block.leaves.blockID, this.leafMeta);
        }

        return true;
    }







    private void growVines(World par1World, int par2, int par3, int par4, int par5) {
        this.setBlockAndMetadata(par1World, par2, par3, par4, Block.vine.blockID, par5);
        int var6 = 4;

        while (par1World.getBlockId(par2, --par3, par4) == 0 && var6 > 0) {
            this.setBlockAndMetadata(par1World, par2, par3, par4, Block.vine.blockID, par5);
            --var6;
        }
        return;
    }

    private void generateMiniBranch(
            World world,
            Random rand,
            int x,
            int y,
            int z
    ) {
        int dirX = rand.nextInt(3) - 1;
        int dirZ = rand.nextInt(3) - 1;

        if (dirX == 0 && dirZ == 0) return;

        int length = 1 + rand.nextInt(2); // 1–2 blocks

        for (int i = 1; i <= length; i++) {
            setBlock(world, x + dirX * i, y, z + dirZ * i, Block.leaves.blockID);
        }
    }

}
