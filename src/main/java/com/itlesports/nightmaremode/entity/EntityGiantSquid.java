package com.itlesports.nightmaremode.entity;

import btw.entity.mob.BTWSquidEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public final class EntityGiantSquid extends BTWSquidEntity implements IBossDisplayData {
    private int strikeCooldown;

    public EntityGiantSquid(World world) {
        super(world);
        setSize(3.1F, 3.1F);
        experienceValue = 35;
        setPersistent(true);
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(120);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(0.6);
    }

    @Override protected void updateEntityActionState() {}

    @Override public void onLivingUpdate() {
        setAir(300);
        super.onLivingUpdate();
        if (worldObj.isRemote || !isEntityAlive()) return;
        EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity(this, 64);
        if (target == null) { motionX *= 0.9; motionZ *= 0.9; return; }
        double dx = target.posX - posX, dy = target.posY + 0.6 - posY, dz = target.posZ - posZ;
        double distance = Math.max(1, Math.sqrt(dx * dx + dy * dy + dz * dz));
        double speed = ticksExisted % 100 < 24 ? 0.38 : 0.19;
        motionX = dx / distance * speed;
        motionY = dy / distance * speed;
        motionZ = dz / distance * speed;
        rotationYaw = (float)(Math.atan2(-dx, dz) * 180 / Math.PI);
        squidPitch = (float)(-Math.atan2(dy, Math.hypot(dx, dz)) * 180 / Math.PI);
        if (--strikeCooldown <= 0 && distance < 4.2 && canEntityBeSeen(target)) {
            target.attackEntityFrom(DamageSource.causeMobDamage(this), 6);
            target.addVelocity(dx / distance * 0.65, 0.25, dz / distance * 0.65);
            target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 0));
            playSound("random.splash", 1, 0.6F);
            strikeCooldown = 30;
        }
        if (ticksExisted % 100 == 0) {
            playSound("random.splash", 1.5F, 0.5F);
            if (distance < 10 && canEntityBeSeen(target)) target.addPotionEffect(new PotionEffect(Potion.blindness.id, 45, 0));
        }
    }

    @Override protected void dropFewItems(boolean playerKill, int looting) {
        if (playerKill) {
            entityDropItem(new ItemStack(NMItems.giantSquidMantle), 0);
            entityDropItem(new ItemStack(NMItems.calamari, 4 + rand.nextInt(3)), 0);
        }
    }

    @Override public AxisAlignedBB getVisualBoundingBox() { return boundingBox.expand(3.5, 3.5, 3.5); }

    @Override public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        strikeCooldown = tag.getInteger("StrikeCooldown");
    }

    @Override public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("StrikeCooldown", strikeCooldown);
    }
}
