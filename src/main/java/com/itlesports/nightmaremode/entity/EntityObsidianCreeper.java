package com.itlesports.nightmaremode.entity;

import api.entity.EntityWithCustomPacket;
import api.entity.mob.KickingAnimal;
import api.item.items.PickaxeItem;
import btw.entity.attribute.BTWAttributes;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class EntityObsidianCreeper extends EntityCreeper implements EntityWithCustomPacket {
    private boolean determinedToExplode = false;
    private int timeSinceIgnited;
    private int fuseTime = 50;
    private final int explosionRadius = 3;
    private byte patienceCounter = 60;


    public EntityObsidianCreeper(World par1World) {
        super(par1World);
    }

    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public boolean getCanSpawnHere() {
        return (this.posY < 20 || this.dimension == -1) && super.getCanSpawnHere();
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
        int progress = NMUtils.getWorldProgress();
        double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.25 : 1;
        int eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 20 : 0;


        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((24 + progress * 6) * bloodMoonModifier + eclipseModifier) * NMUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((double)0.18f);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(6f + NMUtils.getWorldProgress() * 2);
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    public boolean attackEntityFrom(DamageSource source, float dmg) {
        if (this.isEntityInvulnerable() || this.isBlacklistedDamage(source)) {
            return false;
        }
        return super.attackEntityFrom(source, dmg);
    }

    private boolean isBlacklistedDamage(DamageSource src) {
        if(src == DamageSource.fall
                || src == DamageSource.fallingBlock
                || src == DamageSource.lava
                || src == DamageSource.inFire
                || src == DamageSource.onFire
                || src == DamageSource.drown)
        {return true;}

        if(src.getEntity() instanceof EntityLivingBase p){
            if(p.getHeldItem() == null){
                return true;
            } else{
                if(p.getHeldItem().getItem() instanceof PickaxeItem){
                    return false;
                } else{
                    for (int i = 0; i < 2; i++) {
                        double offsetY = this.rand.nextDouble() * this.height * 1.2D;
                        this.worldObj.playAuxSFX(2278, (int) this.posX, (int) (this.posY + offsetY), (int) this.posZ, 0);
                    }
                    p.getHeldItem().attemptDamageItem(3, p.rand);
                    return true;
                }
            }
        }
        if(src == DamageSource.magic){
            return false;
        }
        return true;
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            EntityObsidianCreeper par1EntityLivingBase = this;
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
        if (this.getNeuteredState() == 0) {
            this.dropItem(Block.obsidian.blockID, this.rand.nextInt(2) + 1);

            this.dropItem(NMItems.obsidianShard.itemID, this.rand.nextInt(4) + 2);

        }


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
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears) {
            playersCurrentItem.attemptDamageItem(6, this.rand);
            this.playSound("random.break", 1.0f, 1.0f);
//            if (!this.worldObj.isRemote) {
//                boolean var2 = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
//                if (this.getPowered()) {
//                    this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)(this.explosionRadius * 2),false, var2);
//                } else {
//                    this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, (float)this.explosionRadius, false, var2);
//                }
//                this.setDead();
//            }
        }
        return false;
    }

    public void playLivingSound() {
        if (this.getNeuteredState() > 0) {
            String var1 = this.getLivingSound();
            if (var1 != null) {
                this.playSound(var1, 0.25F, this.getSoundPitch() - 0.25F);
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
