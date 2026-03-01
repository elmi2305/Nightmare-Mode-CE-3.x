package com.itlesports.nightmaremode.entity.underworld;

import com.itlesports.nightmaremode.AITasks.EntityAIBurrow;
import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import net.minecraft.src.World;

import static com.itlesports.nightmaremode.util.NMFields.PACKET_CREEPER_FLOWER;

public class FlowerCreeper extends EntityCreeperVariant {
    public FlowerCreeper(World w) {
        super(w);
        this.tasks.addTask(1, new EntityAIBurrow(this, 5, 3f));
        this.variantType = PACKET_CREEPER_FLOWER;
        this.fuseTime = 60;
        this.canLunge = false;
        this.explosionRadius = 2;
    }

    @Override
    protected void onDeathEffect() {
        super.onDeathEffect();

        EntityPollenCloud cloud = new EntityPollenCloud(this.worldObj);
        cloud.setPosition(this.posX, this.posY, this.posZ);
        this.worldObj.spawnEntityInWorld(cloud);
    }

    @Override
    protected void onServerAndClientDeathEffect() {
        super.onServerAndClientDeathEffect();

    }
}
