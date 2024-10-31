package com.itlesports.nightmaremode;

import btw.block.BTWBlocks;
import btw.entity.EntityWithCustomPacket;
import btw.entity.SpiderWebEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class NightmareFlameEntity extends EntitySnowball implements EntityWithCustomPacket {
    public NightmareFlameEntity(World world) {
        super(world);
    }

    public NightmareFlameEntity(World world, EntityLiving entityLiving) {
        super(world, entityLiving);
    }

    public NightmareFlameEntity(World world, double d, double e, double f) {
        super(world, d, e, f);
    }

    @Override
    protected void onImpact(MovingObjectPosition movingObjectPosition) {
        if (movingObjectPosition.entityHit != null) {
            movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 5);

        } else {
            int n = movingObjectPosition.blockX;
            int n2 = movingObjectPosition.blockY;
            int n3 = movingObjectPosition.blockZ;
            switch (movingObjectPosition.sideHit) {
                case 1: {
                    ++n2;
                    break;
                }
                case 0: {
                    --n2;
                    break;
                }
                case 2: {
                    --n3;
                    break;
                }
                case 3: {
                    ++n3;
                    break;
                }
                case 5: {
                    ++n;
                    break;
                }
                case 4: {
                    --n;
                }
            }
            if (this.worldObj.isAirBlock(n, n2, n3) && !this.worldObj.isAirBlock(n, n2-1, n3)) {
                this.worldObj.setBlockWithNotify(n, n2, n3, Block.fire.blockID);
            }
        }
        this.setDead();
    }


    @Override
    public int getTrackerViewDistance() {
        return 64;
    }

    @Override
    public int getTrackerUpdateFrequency() {
        return 10;
    }

    @Override
    public boolean getTrackMotion() {
        return true;
    }

    @Override
    public boolean shouldServerTreatAsOversized() {
        return false;
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(14);
            dataStream.writeInt(this.entityId);
            dataStream.writeInt(MathHelper.floor_double(this.posX * 32.0));
            dataStream.writeInt(MathHelper.floor_double(this.posY * 32.0));
            dataStream.writeInt(MathHelper.floor_double(this.posZ * 32.0));
            dataStream.writeByte((byte)(this.motionX * 128.0));
            dataStream.writeByte((byte)(this.motionY * 128.0));
            dataStream.writeByte((byte)(this.motionZ * 128.0));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Packet250CustomPayload("btw|SE", byteStream.toByteArray());
    }
}
