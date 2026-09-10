package com.itlesports.nightmaremode.underworld;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.ComponentScatteredFeature;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;

import java.util.Random;

public class ComponentScatteredFeatureAltar extends ComponentScatteredFeature {
    private final int steelBlockId = BTWBlocks.soulforgedSteelBlock.blockID;
    private final int torchBlockId = Block.torchRedstoneActive.blockID;
    private final int redstoneDustId = Block.redstoneWire.blockID;
    private final int chestBlockId = NMBlocks.bloodChest.blockID;

    public ComponentScatteredFeatureAltar() {
    }

    public ComponentScatteredFeatureAltar(Random random, int i, int j, int k, int l, int m, int n) {
        super(random, i, j, k, l, m, n);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox box) {

        this.fillWithBlocks(
                world, box,
                0, 0, 0,
                4, 0, 4,
                steelBlockId, steelBlockId,
                false
        );

        this.fillWithBlocks(
                world, box,
                1, 1, 1,
                3, 2, 3,
                0, 0,
                false
        );

        this.fillWithBlocks(
                world, box,
                1, 1, 1,
                3, 1, 3,
                steelBlockId, steelBlockId,
                false
        );

        this.placeBlockAtCurrentPosition(
                world, redstoneDustId, 0,
                2, 2, 1, box
        );
        this.placeBlockAtCurrentPosition(world, redstoneDustId, 0, 2, 2, 2, box);
        this.placeBlockAtCurrentPosition(world, redstoneDustId, 0, 2, 2, 2, box);
        this.placeBlockAtCurrentPosition(world, redstoneDustId, 0, 2, 2, 2, box);
        this.placeBlockAtCurrentPosition(world, redstoneDustId, 0, 1, 2, 2, box);
        this.placeBlockAtCurrentPosition(world, redstoneDustId, 0, 3, 2, 2, box);

        this.placeBlockAtCurrentPosition(world, torchBlockId, 0, 0, 1, 0, box);
        this.placeBlockAtCurrentPosition(world, torchBlockId, 0, 4, 1, 0, box);
        this.placeBlockAtCurrentPosition(world, torchBlockId, 0, 0, 1, 4, box);
        this.placeBlockAtCurrentPosition(world, torchBlockId, 0, 4, 1, 4, box);

        this.placeBlockAtCurrentPosition(world, chestBlockId, 0, 2, 1, 4, box);

        return true;
    }
}

