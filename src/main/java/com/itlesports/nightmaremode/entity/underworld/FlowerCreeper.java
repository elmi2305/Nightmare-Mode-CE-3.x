package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import com.itlesports.nightmaremode.AITasks.EntityAIBurrow;
import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.World;

import static com.itlesports.nightmaremode.util.NMFields.CREEPER_FLOWER;

public class FlowerCreeper extends EntityCreeperVariant {
    public FlowerCreeper(World par1World) {
        super(par1World);
        this.tasks.addTask(1, new EntityAIBurrow(this, 5, 6f));
        this.variantType = CREEPER_FLOWER;
        this.fuseTime = 60;
    }
}
