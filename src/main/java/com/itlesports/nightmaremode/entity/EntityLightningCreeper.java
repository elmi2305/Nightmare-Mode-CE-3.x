package com.itlesports.nightmaremode.entity;

import api.entity.EntityWithCustomPacket;
import api.entity.mob.KickingAnimal;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class EntityLightningCreeper extends EntityCreeper implements EntityWithCustomPacket {
    private boolean determinedToExplode = false;
    private int timeSinceIgnited;
    private int fuseTime = 80;
    private final int explosionRadius = 3;
    private byte patienceCounter = 60;

    public EntityLightningCreeper(World par1World) {
        super(par1World);
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
//        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
    }

    protected void entityInit() {
        super.entityInit();
    }

    private int failCountNullWorld = 0;
    private int failCountNoThunderOrProgress = 0;

    @Override
    public boolean getCanSpawnHere() {
        if (this.worldObj == null) return false;
        int worldProgress = NMUtils.getWorldProgress();
        boolean bCanSpawn = false;

        if (this.worldObj.isThundering() || worldProgress > 0) {
            bCanSpawn = true;
        } else {
            failCountNoThunderOrProgress++;
        }

        boolean finalSpawn = bCanSpawn && super.getCanSpawnHere() && this.posY > 50;

//        if (finalSpawn) {
//            ChatMessageComponent text = new ChatMessageComponent();
//            text.addText("Mob spawned at x: " + Math.floor(this.posX) +
//                    " y: " + Math.floor(this.posY) +
//                    " z: " + Math.floor(this.posZ) + "\n");
//            text.addText("Spawn condition fail counts:\n");
//            text.addText("- No thunder & no world progress: " + failCountNoThunderOrProgress);
//            text.setColor(EnumChatFormatting.BLUE);
//            EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this, -1);
//            if (player != null) {
//                player.sendChatToPlayer(text);
//            }
//        }

        return finalSpawn;
    }



    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32);
    }

    @Override
    protected boolean isValidLightLevel() {
        int z;
        int y;
        int x = MathHelper.floor_double(this.posX);
        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y = MathHelper.floor_double(this.boundingBox.minY), z = MathHelper.floor_double(this.posZ)) > this.rand.nextInt(32)) {
            return false;
        }
        int blockLightValue = this.worldObj.getBlockLightValueNoSky(x, y, z);
        if (blockLightValue > 0) {
            return false;
        }
        int naturalLightValue = this.worldObj.getBlockNaturalLightValue(x, y, z);
        if (this.worldObj.isThundering()) {
            naturalLightValue = Math.min(naturalLightValue, 3);
        }
        return naturalLightValue <= this.rand.nextInt(9);
    }

    @Override
    public void onUpdate() {
        if(!this.hasAttackTarget() && this.ticksExisted % 10 == 0){
            EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this,70);
            if(player != null && !player.capabilities.isCreativeMode){
                this.setAttackTarget(player);
                this.getMoveHelper().setMoveTo(player.posX,player.posY,player.posZ, 1.0f);
            }
        }
        super.onUpdate();
    }

    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setShort("Fuse", (short)this.fuseTime);
        par1NBTTagCompound.setByte("ExplosionRadius", (byte)this.explosionRadius);
        par1NBTTagCompound.setByte("fcNeuteredState", (byte)this.getNeuteredState());
        par1NBTTagCompound.setShort("timeSinceIgnited", (short)this.timeSinceIgnited);
        par1NBTTagCompound.setByte("patienceCounter", this.patienceCounter);
    }

    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("Fuse")) {
            this.fuseTime = par1NBTTagCompound.getShort("Fuse");
        }

        if (par1NBTTagCompound.hasKey("timeSinceIgnited")) {
            this.timeSinceIgnited = par1NBTTagCompound.getShort("timeSinceIgnited");
        }

        if (par1NBTTagCompound.hasKey("patienceCounter")) {
            this.patienceCounter = par1NBTTagCompound.getByte("patienceCounter");
        }
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            EntityLightningCreeper par1EntityLivingBase = this;
            dataStream.writeInt(22);

            dataStream.writeInt(this.entityId);
            new Packet24MobSpawn(par1EntityLivingBase).writePacketData(dataStream);
            dataStream.writeInt(this.timeSinceIgnited);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Packet250CustomPayload("btw|SE", byteStream.toByteArray());
    }

    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (par1DamageSource.getEntity() instanceof EntitySkeleton && this.getNeuteredState() == 0) {
            int var2 = Item.record13.itemID + this.rand.nextInt(Item.recordWait.itemID - Item.record13.itemID + 1);
            this.dropItem(var2, 1);
        }
    }


    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        super.dropFewItems(bKilledByPlayer, iLootingModifier);

        int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0) {
            int var4 = this.rand.nextInt(3);
            // 0 - 2
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }

    public boolean interact(EntityPlayer player) {
        return false;
    }

    public void playLivingSound() {
        if (this.getNeuteredState() > 0) {
            String var1 = this.getLivingSound();
            if (var1 != null) {
                this.playSound(var1, 0.25F, this.getSoundPitch() + 0.25F);
            }
        } else {
            super.playLivingSound();
        }

    }

    protected String getLivingSound() {
        return this.getNeuteredState() > 0 ? "mob.creeper.say" : super.getLivingSound();
    }
    @Override
    public void onKickedByAnimal(KickingAnimal kickingAnimal) {}

    public void checkForScrollDrop() {}

    public boolean getIsDeterminedToExplode() {
        return this.determinedToExplode;
    }

    public int getTrackerViewDistance() {
        return 80;
    }

    public int getTrackerUpdateFrequency() {
        return 3;
    }

    public boolean getTrackMotion() {
        return true;
    }

    public boolean shouldServerTreatAsOversized() {
        return false;
    }

    public void setTimeSinceIgnited(int timeSinceIgnited) {
        this.timeSinceIgnited = timeSinceIgnited;
    }

}
