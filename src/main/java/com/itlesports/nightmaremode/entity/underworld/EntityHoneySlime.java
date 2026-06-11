package com.itlesports.nightmaremode.entity.underworld;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.World;

import static com.itlesports.nightmaremode.util.NMFields.SLIME_HONEY;

public class EntityHoneySlime extends EntitySlimeVariant {
    public EntityHoneySlime(World par1World) {
        super(par1World);
        this.slimeType = SLIME_HONEY;
    }

    @Override
    protected int getInitialSize() {
        return  1 << (this.rand.nextInt(2) + 2);
    }

    @Override
    protected int getTrailBlockId() {
        return NMBlocks.honeyCover.blockID;
    }
    @Override
    protected EntitySlimeVariant createInstance() {
        return new EntityHoneySlime(this.worldObj);
    }
}
