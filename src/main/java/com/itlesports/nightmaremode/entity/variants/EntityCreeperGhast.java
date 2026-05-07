package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.World;

public class EntityCreeperGhast extends EntityGhast {
    public EntityCreeperGhast(World par1World) {
        super(par1World);
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && NMUtils.getIsEclipse();
    }
}
