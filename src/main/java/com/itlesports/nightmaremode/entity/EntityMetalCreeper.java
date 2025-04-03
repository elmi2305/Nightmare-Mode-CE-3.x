package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.EntityWithCustomPacket;
import btw.entity.mob.KickingAnimal;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityMetalCreeper extends EntityCreeper implements EntityWithCustomPacket {
    private boolean determinedToExplode = false;
    private int timeSinceIgnited;
    private int fuseTime = 60;
    private final int explosionRadius = 3;
    private byte patienceCounter = 60;


    public EntityMetalCreeper(World par1World) {
        super(par1World);
    }

    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public boolean getCanSpawnHere() {
        return (NightmareMode.moreVariants || NightmareMode.isAprilFools) && super.getCanSpawnHere();
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
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((double)0.18f);
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(par1DamageSource.getEntity() instanceof EntityPlayer target){
            Item heldItem = null;
            if(target.getHeldItem() != null){
                heldItem = target.getHeldItem().getItem();
            }

            int progress =  NightmareUtils.getWorldProgress(this.worldObj);

            if(heldItem != null){
                if (itemsThatCanAttackTheZombie.contains(heldItem.itemID)) {
                    return super.attackEntityFrom(par1DamageSource, par2);
                } else{
                    this.playSound("random.break",0.5f, 1.8f);
                    target.getHeldItem().attemptDamageItem(this.rand.nextInt(4 + progress * 2) + progress + 1, this.rand);
                }
            }
            return false;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    private static final List<Integer> itemsThatCanAttackTheZombie = new ArrayList<>(Arrays.asList(
            Item.pickaxeStone.itemID,
            Item.pickaxeIron.itemID,
            Item.pickaxeDiamond.itemID,
            BTWItems.steelPickaxe.itemID,
            BTWItems.mattock.itemID,
            NMItems.bloodPickaxe.itemID
    ));


    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            EntityMetalCreeper par1EntityLivingBase = this;
            dataStream.writeInt(19);

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
        if (this.getNeuteredState() == 0 && this.rand.nextInt(8) == 0) {
            this.dropItem(Item.fireballCharge.itemID, 1);
        }


        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
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
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears) {
            if (!this.worldObj.isRemote) {
                boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                if (this.getPowered()) {
                    this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)(this.explosionRadius * 2),false, var2);
                } else {
                    this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)this.explosionRadius, false, var2);
                }
                this.setDead();
            }
        }
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

    public void onKickedByAnimal(KickingAnimal kickingAnimal) {
        this.determinedToExplode = true;
    }

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
