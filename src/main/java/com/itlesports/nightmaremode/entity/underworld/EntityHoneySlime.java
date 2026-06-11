package com.itlesports.nightmaremode.entity.underworld;

import net.minecraft.src.EntitySlime;
import net.minecraft.src.World;

public class EntityHoneySlime extends EntitySlime {
    public EntityHoneySlime(World par1World) {
        super(par1World);
        int var2 = 1 << (this.rand.nextInt(2) + 2);
        this.setSlimeSize(var2);

    }
}
