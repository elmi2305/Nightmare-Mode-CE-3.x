package com.itlesports.nightmaremode.entity;

import btw.entity.attribute.BTWAttributes;
import api.item.items.PickaxeItem;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class EntityObsidianFish extends EntitySilverfish {
    public EntityObsidianFish(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
    }


    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(12f);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.5F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0F);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(10.0F);
    }

    protected boolean canTriggerWalking() {
        return false;
    }


    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int looting) {
        if (bKilledByPlayer && NMUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (looting * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }
        }
//        if (!NMUtils.getIsMobEclipsed(this)) {
            this.dropItem(NMItems.obsidianShard.itemID, this.rand.nextInt(3) + 1);
//        }
    }

    protected Entity findPlayerToAttack() {
        double var1 = 24.0F;
        return this.worldObj.getClosestVulnerablePlayerToEntity(this, var1);
    }

    protected String getLivingSound() {
        return "mob.silverfish.say";
    }

    protected String getHurtSound() {
        return "mob.silverfish.hit";
    }

    protected String getDeathSound() {
        return "mob.silverfish.kill";
    }

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

        if(src.getEntity() instanceof EntityPlayer p){
            if(p.getHeldItem() == null){
                return true;
            } else{
                if(p.getHeldItem().getItem() instanceof PickaxeItem){
                    return false;
                } else{
                    for (int i = 0; i < 2; i++) {
                        double offsetX = (this.rand.nextDouble() - 0.5D);
                        double offsetY = this.rand.nextDouble() * this.height * 1.2D;
                        double offsetZ = (this.rand.nextDouble() - 0.5D);

                        this.worldObj.playAuxSFX(2278, (int) (this.posX + offsetX), (int) (this.posY + offsetY), (int) (this.posZ + offsetZ), 0);
                    }
                    p.getHeldItem().attemptDamageItem(8, p.rand);
                    return true;
                }
            }
        }
        return false;
    }

    protected void attackEntity(Entity par1Entity, float par2) {
        if (this.attackTime <= 0 && par2 < 1.2F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            this.attackEntityAsMob(par1Entity);
        }

    }

    protected void playStepSound(int par1, int par2, int par3, int par4) {
        this.playSound("mob.silverfish.step", 0.15F, 1.0F);
    }

    protected int getDropItemId() {
        return 0;
    }



    public void onUpdate() {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    protected void updateEntityActionState() {
        super.updateEntityActionState();
        if (!this.worldObj.isRemote) {
            if (this.entityToAttack == null && !this.hasPath()) {
                int var1 = MathHelper.floor_double(this.posX);
                int var2 = MathHelper.floor_double(this.posY + (double)0.5F);
                int var3 = MathHelper.floor_double(this.posZ);
                int var11 = this.rand.nextInt(6);
                int var5 = this.worldObj.getBlockId(var1 + Facing.offsetsXForSide[var11], var2 + Facing.offsetsYForSide[var11], var3 + Facing.offsetsZForSide[var11]);
                if (var5 != 0) {
                    this.updateWanderPath();
                }
            } else if (this.entityToAttack != null && !this.hasPath()) {
                this.entityToAttack = null;
            }
        }
    }

    public float getBlockPathWeight(int par1, int par2, int par3) {
        return this.worldObj.getBlockId(par1, par2 - 1, par3) == Block.stone.blockID ? 10.0F : super.getBlockPathWeight(par1, par2, par3);
    }

    protected boolean isValidLightLevel() {
        return true;
    }

    public boolean getCanSpawnHere() {
        return false;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    public void checkForScrollDrop() {
    }
}
