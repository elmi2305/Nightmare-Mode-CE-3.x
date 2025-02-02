package com.itlesports.nightmaremode;

import btw.entity.BroadheadArrowEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class EntityMagicArrow extends BroadheadArrowEntity {

    public EntityMagicArrow(World world, EntityLivingBase entityLiving, float f) {
        super(world, entityLiving, f);
    }

    protected float getDamageMultiplier() {
        return 1.5F;
    }

    public Item getCorrespondingItem() {
        return NMItems.magicArrow;
    }

    public Packet getSpawnPacketForThisEntity() {
        return new Packet23VehicleSpawn(this, getVehicleSpawnPacketType(), this.shootingEntity == null ? this.entityId : this.shootingEntity.entityId);
    }

    public int getTrackerViewDistance() {
        return 64;
    }

    public int getTrackerUpdateFrequency() {
        return 20;
    }

    public boolean getTrackMotion() {
        return false;
    }

    public boolean shouldServerTreatAsOversized() {
        return false;
    }

    public static int getVehicleSpawnPacketType() {
        return 101;
    }
}
