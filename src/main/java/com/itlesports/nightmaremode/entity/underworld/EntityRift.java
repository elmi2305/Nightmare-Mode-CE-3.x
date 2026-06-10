package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static com.itlesports.nightmaremode.util.NMFields.UNDERWORLD_DIMENSION;

public class EntityRift extends EntityLiving implements EntityWithCustomPacket {
    public EntityRift(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.noClip = false;
        this.setSize(1.0F, 5.0F);
        this.renderDistanceWeight = 20.0D;
        this.isImmuneToFire = true;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override protected void despawnEntity() {}

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        super.onCollideWithPlayer(par1EntityPlayer);
        if (par1EntityPlayer instanceof EntityPlayerMP player) {
            if (NightmareMode.devMode && player.isSneaking()) {
                player.mcServer.getConfigurationManager().transferPlayerToDimension(player, UNDERWORLD_DIMENSION);
            } else {
                ChatMessageComponent text = new ChatMessageComponent();
                text.addText("<???> Nice try. ");
                text.setColor(EnumChatFormatting.RED);
                player.sendChatToPlayer(text);
            }
        }
    }

    @Override
    public boolean doesEntityApplyToSpawnCap() {
        return false;
    }

    @Override
    public boolean isEntityAlive() {return true;}

    public EntityRift(World world, double x, double y, double z) {
        this(world);
        this.setPositionAndUpdate(x,y,z);
    }

        @Override
    public ItemStack getHeldItem() {
        return null;
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    public float knockbackMagnitude() {
        return 0f;
    }
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }


    @Override public ItemStack getCurrentItemOrArmor(int var1) {
        return null;
    }

    @Override public void setCurrentItemOrArmor(int var1, ItemStack var2) {}

    @Override public ItemStack[] getLastActiveItems() {
        return new ItemStack[0];
    }
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return false;
    }
    @Override protected void damageEntity(DamageSource par1DamageSource, float par2) {}
    @Override public void moveEntity(double dMoveX, double dMoveY, double dMoveZ) {}
    @Override public void moveEntityWithHeading(float par1, float par2) {}
    @Override public void moveFlying(float par1, float par2, float par3) {}
    @Override public boolean isPushedByWater() {return false;}
    @Override protected boolean pushOutOfBlocks(double par1, double par3, double par5) {return false;}

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(NMFields.PACKET_RIFT);
            dataStream.writeInt(this.entityId);
            new Packet24MobSpawn(this).writePacketData(dataStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Packet250CustomPayload("btw|SE", byteStream.toByteArray());
    }

    @Override
    public int getTrackerViewDistance() {
        return 80;
    }

    @Override
    public int getTrackerUpdateFrequency() {
        return 3;
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
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.motionX = this.motionY = this.motionZ = 0.0D;

    }
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
    }
}