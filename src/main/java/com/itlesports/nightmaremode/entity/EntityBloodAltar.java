package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodBone;
import net.minecraft.src.*;

public class EntityBloodAltar extends EntityLiving implements IBossDisplayData {
    private TileEntityBloodBone altar;

    public EntityBloodAltar(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());

        this.setSize(0.1F, 0.1F);
        this.isImmuneToFire = true;
        this.noClip = true;
        this.renderDistanceWeight = 4.0f;
    }

    public void notifyOfSacrifice(){
        if(this.altar == null) return;
        this.altar.sacrifice();
    }


    public void bindToAltar(TileEntityBloodBone altar) {
        this.altar = altar;
        this.setPosition(altar.xCoord + 0.5, altar.yCoord + 0.5, altar.zCoord + 0.5);
    }
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setAttribute(0);
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).setAttribute(128);
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage).setAttribute(0);
    }

    @Override
    public String getEntityName() {
        return I18n.getString("entity.entityBloodAltar.name");
    }

    @Override
    public void onUpdate() {
        if((altar == null || altar.isInvalid()) && !this.worldObj.isRemote) {
            this.setDead();
            return;
        }
        super.onUpdate();
//        if (this.ticksExisted % 60 == 0) {
//            System.out.println("I'm alive on the " + (worldObj.isRemote ? "CLIENT" : "SERVER"));
//        }
        if(!altar.isActive()) return;
        // Sync position
        if (this.ticksExisted % 120 == 0) {
            System.out.println("I'm active on the " + (worldObj.isRemote ? "CLIENT" : "SERVER"));
        }
        this.setPosition(altar.xCoord + 0.5, altar.yCoord + 0.5, altar.zCoord + 0.5);
        this.setHealth(128 - altar.getSuccessfulIncrements());
//        if (this.ticksExisted % 60 == 0) {
//            System.out.println("My position is: " + (altar.xCoord + 0.5) + " " + (altar.yCoord + 0.5) + " " + (altar.zCoord + 0.5));
//        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
    }

    @Override
    protected boolean canDespawn() { return false; }

    @Override
    public boolean canBePushed() { return false; }
}