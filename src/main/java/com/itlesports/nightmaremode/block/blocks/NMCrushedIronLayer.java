package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.NMBlockGroundLayer;
import net.minecraft.src.*;

import java.util.Random;

public class NMCrushedIronLayer extends NMBlockGroundLayer {
    public NMCrushedIronLayer(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Override
    public void updateTick(World w, int x, int y, int z, Random rand) {
        super.updateTick(w, x, y, z, rand);
        if(this.ticksExisted > 40){
            if(w.isRainingAtPos(x,y + 1,z) && rand.nextInt(4) == 0){
                w.destroyBlock(x, y, z, false);
                w.setBlockWithNotify(x,y,z, NMBlocks.blockWashedIronLayer.blockID);
            }
        }
    }

}
