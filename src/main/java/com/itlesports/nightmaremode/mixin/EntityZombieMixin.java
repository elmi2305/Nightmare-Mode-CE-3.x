package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehaviorHostile;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulty;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityZombie.class)
public abstract class EntityZombieMixin extends EntityMob{

    public EntityZombieMixin(World par1World) {
        super(par1World);
    }

    @Shadow public abstract boolean isVillager();
    @Unique public void onKilledBySun() {
        if (!this.worldObj.isRemote) {
            EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
            skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            skeleton.setHealth(skeleton.getMaxHealth() - this.rand.nextInt(7) - 2);
            for (int i = 0; i < 5; i++) {
                skeleton.setCurrentItemOrArmor(0, this.getCurrentItemOrArmor(0));
            }
            if (skeleton.getCurrentItemOrArmor(0) == null) {
                if (rand.nextInt(25) == 0) {
                    skeleton.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                }
            }
            if (skeleton.getCurrentItemOrArmor(4) == null){
                if (rand.nextInt(10)==0) {
                    ItemStack var2 = new ItemStack(Item.skull,1,2);
                    skeleton.setCurrentItemOrArmor(4,var2);
                }
            }
            if(NightmareUtils.getGameProgressMobsLevel(this.worldObj) >= 1 && this.rand.nextFloat()<=0.2941){
                skeleton.setSkeletonType(1);
            }
            this.worldObj.spawnEntityInWorld(skeleton);
            this.setDead();
        }
    }



    @Unique private boolean canEntitySeeSun(){
        if(this.worldObj.isDaytime() && !this.worldObj.isRainingAtPos((int)this.posX, (int)this.posY, (int)this.posZ) && !this.isChild() && !this.inWater){
            return this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double) this.getEyeHeight()), MathHelper.floor_double(this.posZ));
        } else {return false;}
    }

    @Unique boolean doNotDropItems;

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void transformIntoSkeletonOnFireDeath(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (!this.isEntityInvulnerable() && canEntitySeeSun()) {
            if ((!this.isVillager() && this.getHealth() <= par2) && !isCrystalHead(this)) {this.onKilledBySun();}
        }
    }
    @Inject(method = "checkForLooseFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playAuxSFX(IIIII)V"))
    private void healZombie(CallbackInfo ci){
        this.heal(4.0F);
        this.addPotionEffect(new PotionEffect(Potion.damageBoost.id,80,0));
        this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,160,1));
    }
    @Inject(method = "addRandomArmor",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityZombie;entityLivingAddRandomArmor()V",
                    shift = At.Shift.AFTER))
    private void chanceToSpawnWithWeapon(CallbackInfo ci) {
        EntityZombie thisObj = (EntityZombie)(Object)this;
        if (thisObj.worldObj != null && !this.isVillager()) {
            if (rand.nextInt(50) == 0) {
                ItemStack var1 = new ItemStack(BTWItems.boneClub);
                thisObj.setCurrentItemOrArmor(0, var1);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + (NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj))*1.5));
                // 3.0 -> 4.0 -> 6.0 -> 7.0
                doNotDropItems = true;
            } else if (rand.nextInt(18) == 0) {
                ItemStack var1 = new ItemStack(Item.swordWood);
                this.setCurrentItemOrArmor(0, var1);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + NightmareUtils.getGameProgressMobsLevel(this.worldObj));
                // 2.0 -> 3.0 -> 4.0 -> 5.0
                doNotDropItems = true;
            }

            if (NightmareUtils.getGameProgressMobsLevel(this.worldObj) == 1) {
                if (rand.nextInt(18) == 0) {
                    ItemStack var1 = new ItemStack(Item.axeGold);
                    ItemStack var2 = new ItemStack(Item.helmetGold);
                    this.setCurrentItemOrArmor(0, var1);
                    this.setCurrentItemOrArmor(4, var2);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.34f);
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(16.0);
                    doNotDropItems = true;
                }
            } else if (rand.nextInt(22) == 0 && NightmareUtils.getGameProgressMobsLevel(this.worldObj) > 1) {
                ItemStack var1 = new ItemStack(Item.swordDiamond);
                doNotDropItems = true;
                this.setCurrentItemOrArmor(0, var1);
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(36.0);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (NightmareUtils.getGameProgressMobsLevel(this.worldObj)-2)*3));
                // 12.0 -> 15.0
            }
        }
    }

    @Inject(method = "addRandomArmor", // changes iron sword damage specifically
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityZombie;setCurrentItemOrArmor(ILnet/minecraft/src/ItemStack;)V",
                    ordinal = 0))
    private void setDamageIfIronSword(CallbackInfo ci){
        if (this.worldObj!= null) {
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(4.0 + (NightmareUtils.getGameProgressMobsLevel(this.worldObj)-1)*1.75));
        }
        // 4.0 -> 5.0 -> 7.0 -> 9.0
    }
    @Inject(method = "addRandomArmor", // summons crystalhead zombie on a 1% chance
            at = @At("TAIL"))
    private void chanceToSpawnCrystalHead(CallbackInfo ci){
        if (this.worldObj != null) {
            if(NightmareUtils.getGameProgressMobsLevel(this.worldObj)>=2 && rand.nextFloat()<=0.025f){
                summonCrystalHeadAtPos();
            }
        }
    }
    @ModifyConstant(method = "addRandomArmor",constant = @Constant(floatValue = 0.05F))
    private float modifyChanceToHaveIronTool(float constant){
        if (this.worldObj != null) {
            if(NightmareUtils.getGameProgressMobsLevel(this.worldObj)==3){return 0.3f;}
            else {
                return (float)(0.05F + (NightmareUtils.getGameProgressMobsLevel(this.worldObj)*0.03));
            }
        }
        return 0.05f;
        // 0.05f -> 0.08f -> 0.11f -> 0.30f
    }

    @ModifyConstant(method = "addRandomArmor", constant = @Constant(floatValue = 0.99F))
    private float makeSpecialZombiesNotDropHeldItem(float constant){
        if(this.getHeldItem() != null){
            if(this.getHeldItem().equals(new ItemStack(Item.swordDiamond))
                    || this.getHeldItem().equals(new ItemStack(Item.swordWood))
                    || this.getHeldItem().equals(new ItemStack(Item.axeGold))
                    || this.getHeldItem().equals(new ItemStack(BTWItems.boneClub))) {
                return 0;
            }
        }
        return 0.99f;
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityMob;onUpdate()V"))
    private void deleteEndCrystalIfZombieDied(CallbackInfo ci){
        if (this.worldObj != null) {
            if(isCrystalHead(this) && this.getHealth() < 1.0f){
                this.riddenByEntity.setDead();
            }
        }
    }


    @Unique
    private void summonCrystalHeadAtPos(){
        Entity crystalhead = new EntityZombie(this.worldObj);
        Entity crystal = new EntityEnderCrystal(this.worldObj);
        crystal.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        crystalhead.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        this.worldObj.spawnEntityInWorld(crystal);
        this.worldObj.spawnEntityInWorld(crystalhead);
        crystal.mountEntity(crystalhead);

        ItemStack var1 = new ItemStack(Item.skull,1,1);
        crystalhead.setCurrentItemOrArmor(4, var1);
        crystalhead.setCurrentItemOrArmor(1, setItemColor(new ItemStack(BTWItems.woolBoots))); // black
        crystalhead.setCurrentItemOrArmor(2, setItemColor(new ItemStack(BTWItems.woolLeggings))); // black
        crystalhead.setCurrentItemOrArmor(3, setItemColor(new ItemStack(BTWItems.woolChest))); // black
        this.setDead();
    }

    @Unique
    private ItemStack setItemColor(ItemStack item){
        NBTTagCompound var3 = item.getTagCompound();
        if (var3 == null) {
            var3 = new NBTTagCompound();
            item.setTagCompound(var3);
        }
        NBTTagCompound var4 = var3.getCompoundTag("display");
        if (!var3.hasKey("display")) {
            var3.setCompoundTag("display", var4);
        }

        var4.setInteger("color", 1052688);
        item.setTagCompound(var3);
        return item;
    }

    @Unique
    public boolean isCrystalHead(Entity par1Entity){
        return par1Entity.riddenByEntity instanceof EntityEnderCrystal;
    }
}
