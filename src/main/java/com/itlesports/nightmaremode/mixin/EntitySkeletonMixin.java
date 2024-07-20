package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.entity.InfiniteArrowEntity;
import btw.entity.RottenArrowEntity;
import btw.entity.mob.behavior.SkeletonArrowAttackBehavior;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMob {
    @Shadow public abstract void setSkeletonType(int par1);

    @Shadow public abstract void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack);

    @Shadow public abstract int getSkeletonType();

    public EntitySkeletonMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void removeHideFromSun(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(EntityAIFleeSun.class);
        this.tasks.removeAllTasksOfClass(EntityAIRestrictSun.class);
    }

    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 0.25))
    private double increaseMoveSpeed(double constant){
        return constant+NightmareUtils.getGameProgressMobsLevel(this.worldObj)*0.015;
        // 0.25 -> 0.265 -> 0.28 -> 0.295
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void increaseHealth(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16.0 + NightmareUtils.getGameProgressMobsLevel(this.worldObj)*6);
        // 16.0 -> 22.0 -> 28.0 -> 34.0
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0 * (NightmareUtils.getGameProgressMobsLevel(this.worldObj)+1));
        // not really important this is just melee damage
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySkeleton;checkForCatchFireInSun()V"))
    private void doNothing(EntitySkeleton instance){}

    @Inject(method = "addRandomArmor", at = @At("TAIL"))
    private void manageSkeletonVariants(CallbackInfo ci){
        this.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 10000000,0));
        int progress = NightmareUtils.getGameProgressMobsLevel(this.worldObj);
        if (progress >= 2 && rand.nextFloat() < 0.13 + ((progress-2)*0.07)){
            // 13% -> 20%
            this.setSkeletonType(4); // ender skeleton
            this.clearActivePotions();

            ItemStack var1 = new ItemStack(Item.skull,1,1);
            ItemStack var2 = new ItemStack(Item.bow,1);
            this.setCurrentItemOrArmor(4, var1);
            this.setCurrentItemOrArmor(0, var2);
            this.setCurrentItemOrArmor(1, setItemColor(new ItemStack(BTWItems.woolBoots), 1052688)); // black
            this.setCurrentItemOrArmor(2, setItemColor(new ItemStack(BTWItems.woolLeggings), 1052688)); // black
            this.setCurrentItemOrArmor(3, setItemColor(new ItemStack(BTWItems.woolChest), 1052688)); // black

        } else if (progress >= 1 && rand.nextFloat()<0.09 + ((progress-1)*0.02)) {
            // 9% -> 11% -> 13%
            this.setSkeletonType(3); // fire skeleton

            Entity magmaCube = new EntityMagmaCube(this.worldObj);
            magmaCube.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.worldObj.spawnEntityInWorld(magmaCube);
            magmaCube.mountEntity(this);
            this.setFire(1000000);

        } else if(progress <= 3 && rand.nextFloat() < 0.06 + (progress*0.02)) {
            // 6% -> 8% -> 10% -> 12%
            this.setSkeletonType(2); // ice skeleton
            ItemStack var1 = new ItemStack(BTWItems.woolHelmet, 1);
            this.setCurrentItemOrArmor(4, setItemColor(var1, 13260));
//            if (rand.nextFloat()<0.5f) {
//                this.setSkeletonType(2); // ice skeleton
//                ItemStack var1 = new ItemStack(BTWItems.woolHelmet,1);
//                this.setCurrentItemOrArmor(4,setItemColor(var1, 13260));
//            } else{
//                this.setSkeletonType(5); // pumpkin head skeleton
//
//                this.setCurrentItemOrArmor(0, null);
//                this.setCurrentItemOrArmor(1, null);
//                this.setCurrentItemOrArmor(2, null);
//                this.setCurrentItemOrArmor(3, null);
//                this.setCurrentItemOrArmor(4, new ItemStack(Block.pumpkin));
//                this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 10000000, 0));
//                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.1);
//                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.1);
//            }
        }
        // overall chances to be a variant: 6% -> 17% -> 34% -> 45%
    }
    

    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void deleteArrowEntity(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow, int iPowerLevel, int iPunchLevel, int iFlameLevel){
        if(this.getSkeletonType()==3){arrow.setDead();}
    }
    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 12.0f))
    private float reduceArrowSpread(float constant){
        return 6.0f - NightmareUtils.getGameProgressMobsLevel(this.worldObj)*2;
        // 6.0 -> 4.0 -> 2.0 -> 0.0
    }




    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntitySkeleton;playSound(Ljava/lang/String;FF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void determineWhatProjectileToShoot(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow){
        if(this.getSkeletonType()==3){
            for(int i = -1; i<=1; i++) {
                double var3 = target.posX - this.posX + i;
                double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
                double var7 = target.posZ - this.posZ + i;

                EntitySmallFireball var11 = new EntitySmallFireball(this.worldObj, this, var3, var5, var7);
                this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
                this.worldObj.spawnEntityInWorld(var11);
            }
        }
    }



    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void chanceToSetArrowOnFire(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow, int iPowerLevel, int iPunchLevel, int iFlameLevel){
        if(rand.nextInt(15) < ((NightmareUtils.getGameProgressMobsLevel(this.worldObj)+1)*3 ) && this.getSkeletonType()!=4 && this.getSkeletonType() != 2){
            // n<3 -> n<6 -> n<9 -> n<12
            arrow.setFire(400);
            arrow.setDamage(MathHelper.floor_double(4.0 + (NightmareUtils.getGameProgressMobsLevel(this.worldObj) * 3)));
            // 4 -> 7 -> 10 -> 13
            arrow.playSound("fire.fire", 1.0f, this.rand.nextFloat() * 0.4f + 0.8f);
        }
    }


    // doesn't work
//    @Inject(method = "setCombatTask", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void setRangedAttackIfEnderSkeleton(CallbackInfo ci){
//        if(Objects.equals(this.getHeldItem(), new ItemStack(Item.enderPearl, 1))){ // if holding a pearl
//            this.tasks.addTask(4, this.aiRangedAttack);
//            this.tasks.removeTask(this.aiMeleeAttack);
//        }
//    }
    // doesn't work


    @Unique
    private ItemStack setItemColor(ItemStack item, int color){
        NBTTagCompound var3 = item.getTagCompound();
        if (var3 == null) {
            var3 = new NBTTagCompound();
            item.setTagCompound(var3);
        }
        NBTTagCompound var4 = var3.getCompoundTag("display");
        if (!var3.hasKey("display")) {
            var3.setCompoundTag("display", var4);
        }

        var4.setInteger("color", color);
        item.setTagCompound(var3);
        return item;
    }
}
