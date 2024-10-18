package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.DireWolfEntity;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob {

    @Shadow public abstract void addPotionEffect(PotionEffect par1PotionEffect);

    @Unique int witherAttackTimer = 0;
    @Unique int witherSummonTimer = 0;
    @Unique boolean hasRevived = false;

    public EntityWitherMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "isArmored", constant = @Constant(floatValue = 2.0f))
    private float witherMeleeSooner(float constant){
        return 1.66f; // starts melee at 60% health instead of 50%. this means 180hp instead of 150hp
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void attackTimer(CallbackInfo ci){
        if(!(this.getAttackTarget() instanceof EntityPlayer) && this.worldObj.getWorldTime() % 100 == 0){
            EntityPlayer tempTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this,40);
            if (tempTarget != null) {
                this.setAttackTarget(tempTarget);
            }
        }

        if (witherAttackTimer < (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 2000 : 4000)) {
            witherAttackTimer+= this.rand.nextInt(5)+1;
            if(hasRevived){witherAttackTimer += 3;}
        }
        if(this.entityToAttack instanceof EntityPlayer player && hasRevived){
            if(witherAttackTimer%400 == 10){
                int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-5,5);
                int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-5,5);
                int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));
                player.setPositionAndUpdate(xValue,yValue,zValue);
                this.entityToAttack = player; // reassures the wither aggro in case it is lost
                player.worldObj.playSoundAtEntity(player,"mob.endermen.portal",2.0F,1.0F);
            }
            if (witherAttackTimer%400 == 20){
                player.setFire(80);
            }
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void manageWitherImmunities(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (par1DamageSource.isExplosion() || par1DamageSource.isFireDamage() || par1DamageSource.isMagicDamage()){
            cir.setReturnValue(false);
        }
    }

    @ModifyArg(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityMob;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"),index = 1)
    private float witherDamageCap(float par2) {
        if(par2 > 200){return 400;} // if you want to instakill it with creative
        if(par2 > 20 && (!WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly() && this.getHealth() < 40)){return 20;}
        return par2;
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityMob;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z",shift = At.Shift.AFTER))
    private void manageRevive(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(this.getHealth()<21 && !hasRevived && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            this.setHealth(300);
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText("A God does not fear death.");
            text2.setColor(EnumChatFormatting.BLACK);
            Minecraft.getMinecraft().thePlayer.sendChatToPlayer(text2);
            if(this.getAttackTarget() instanceof EntityPlayer player){
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100,0));
            }
            hasRevived = true;
        }
    }
    @ModifyArg(method = "func_82216_a",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWither;func_82209_a(IDDDZ)V"), index = 4)
    private boolean modifyChanceForBlueSkulls(boolean par8){
        if(hasRevived){
            return this.rand.nextFloat()<0.03;
        }
        return this.rand.nextFloat()<0.01;
    }

    @Inject(method = "updateAITasks", at = @At("HEAD"))
    private void manageMinionSpawning(CallbackInfo ci){
        if (witherAttackTimer >= (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 2000 : 4000)){
            if (witherSummonTimer == 0){
                this.worldObj.playAuxSFX(2279, MathHelper.floor_double(this.posX),MathHelper.floor_double(this.posY),MathHelper.floor_double(this.posZ), 0);
                this.playSound("mob.ghast.scream",0.6F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            witherSummonTimer++;
            if (witherSummonTimer > (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 100) && witherSummonTimer < (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 40 : 140)){
                this.motionX = this.motionZ = 0;
            }
            if(witherSummonTimer == (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 40 : 100)){
                if (!hasRevived) {
                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        EntitySkeleton tempMinion = new EntitySkeleton(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue, zValue, 50, 50);
                        tempMinion.setSkeletonType(1);
                        if (this.rand.nextFloat() < 0.3) {
                            tempMinion.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
                        }
                        tempMinion.entityToAttack = this.getAttackTarget();
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                } else if(this.rand.nextFloat()<0.7f){
                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        EntityFireCreeper tempMinion = new EntityFireCreeper(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue, zValue, 50, 50);
                        tempMinion.entityToAttack = this.getAttackTarget();
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                } else if(this.getHealth()<100 && this.rand.nextFloat()<0.5f){

                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        EntityBlaze tempMinion = new EntityBlaze(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue + this.rand.nextInt(5), zValue, 50, 50);
                        tempMinion.entityToAttack = this.getAttackTarget();
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                }
                else{
                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        DireWolfEntity tempMinion = new DireWolfEntity(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue, zValue, 50, 50);
                        tempMinion.entityToAttack = this.getAttackTarget();
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                }
                witherSummonTimer = 0;
                witherAttackTimer = 0;
            }
        }
    }
}
