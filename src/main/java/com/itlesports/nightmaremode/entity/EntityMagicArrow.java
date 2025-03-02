package com.itlesports.nightmaremode.entity;

import btw.entity.BroadheadArrowEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class EntityMagicArrow extends BroadheadArrowEntity {

    public EntityMagicArrow(World world, EntityLivingBase entityLiving, float f) {
        super(world, entityLiving, f);
    }

    public Item getCorrespondingItem() {
        return NMItems.magicArrow;
    }

    public Packet getSpawnPacketForThisEntity() {
        return new Packet23VehicleSpawn(this, getVehicleSpawnPacketType(), this.shootingEntity == null ? this.entityId : this.shootingEntity.entityId);
    }

    public static int getVehicleSpawnPacketType() {
        return 101;
    }
}
