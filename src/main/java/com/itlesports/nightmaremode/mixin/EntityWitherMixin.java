package com.itlesports.nightmaremode.mixin;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.DireWolfEntity;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob {

    @Shadow public abstract void addPotionEffect(PotionEffect par1PotionEffect);

    @Shadow @Final private static IEntitySelector attackEntitySelector;
    @Unique int witherAttackTimer = 0;
    @Unique int witherSummonTimer = 0;
    @Unique boolean hasRevived = false;

    public EntityWitherMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void increaseXPYield(World par1World, CallbackInfo ci){
        this.experienceValue = 250;
        this.hasRevived = false;
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.witherAttackTimer = 500;
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, false, attackEntitySelector));
    }
    @Inject(method = "applyEntityAttributes", at = @At(value = "TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(8.0F);
    }

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;II)V"),index = 2)
    private int makeWitherDropSmiteScroll(int par2){
        return Enchantment.smite.effectId;
    }


    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void destroyBlocksAbove(CallbackInfo ci){
        EntityLivingBase target = this.getAttackTarget();
        if(target != null && this.posY - target.posY < 4){
            for(int i = -1; i < 1; i++){
                for(int j = -1; j < 1; j++){
                    this.destroyBlock(this.worldObj,(int)this.posX + i,(int)this.posY+3,(int)this.posZ + j);
                    this.destroyBlock(this.worldObj,(int)this.posX + i,(int)this.posY+4,(int)this.posZ + j);
                    this.destroyBlock(this.worldObj,(int)this.posX + i,(int)this.posY+5,(int)this.posZ + j);
                }
            }
        }
    }
    @Unique private void destroyBlock(World world,int x, int y, int z){
        if(world.getBlockId(x,y,z) != 0 && world.getBlockId(x,y,z) != Block.bedrock.blockID){
            world.destroyBlock(x,y,z,true);
        }
    }

    @Inject(method = "attackEntityWithRangedAttack", at = @At("TAIL"))
    private void manageRandomTeleport(EntityLivingBase attackTarget, float par2, CallbackInfo ci){
        if (this.rand.nextInt(6) == 0) {
            int xOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(5)+3);
            int zOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(5)+3);

            int xValue = MathHelper.floor_double(this.posX) + xOffset;
            int zValue = MathHelper.floor_double(this.posZ) + zOffset;
            int yValue = MathHelper.floor_double(this.posY) + this.rand.nextInt(-2,2);
            this.setPositionAndUpdate(xValue,yValue,zValue);
            if (this.hasRevived) {
                boolean mobGriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 5f + this.rand.nextFloat()*2,true, mobGriefing);
                System.out.println("Exploded");
            }
            this.worldObj.playSoundAtEntity(this,"mob.endermen.portal",2.0F,1.0F);
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void manageAnger(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        this.witherAttackTimer = (int) Math.min(this.witherAttackTimer + par2 * 2, this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 1500 : 4000);
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void attackTimer(CallbackInfo ci){
        if(!(this.getAttackTarget() instanceof EntityPlayer) && this.worldObj.getWorldTime() % 50 == 0){
            EntityPlayer tempTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this,40);
            if (tempTarget != null) {
                this.entityToAttack = tempTarget;
            }
        }

        if (this.witherAttackTimer < (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 1500 : 4000)) {
            this.witherAttackTimer += this.rand.nextInt(5)+1;
            if(this.hasRevived){this.witherAttackTimer += 3;}
        }
        if(this.entityToAttack instanceof EntityPlayer player && this.hasRevived){
            if(this.witherAttackTimer % 120 == 10){
                int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-5,5);
                int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-5,5);
                int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));
                player.setPositionAndUpdate(xValue,yValue,zValue);
                this.entityToAttack = player; // reassures the wither aggro in case it is lost
                player.worldObj.playSoundAtEntity(player,"mob.endermen.portal",2.0F,1.0F);
            }
            if (this.witherAttackTimer % 250 == 20){
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

    @Inject(method = "attackEntityFrom", at = @At(value = "HEAD"))
    private void manageRevive(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(this.getHealth() < 21 && !this.hasRevived && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            this.setHealth(300);
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText("A God does not fear death.");
            text2.setColor(EnumChatFormatting.BLACK);
            this.worldObj.getClosestPlayer(this.posX,this.posY,this.posZ,20).sendChatToPlayer(text2);
            this.hasRevived = true;
        }
    }


    @ModifyArg(method = "func_82216_a",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWither;func_82209_a(IDDDZ)V"), index = 4)
    private boolean modifyChanceForBlueSkulls(boolean par8){
        if(this.hasRevived){
            return this.rand.nextFloat()<0.03;
        }
        return this.rand.nextFloat()<0.01;
    }

    @Inject(method = "updateAITasks", at = @At("HEAD"))
    private void manageMinionSpawning(CallbackInfo ci){
        if (this.witherAttackTimer >= (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 1500 : 4000)){
            if (this.witherSummonTimer == 0){
                this.worldObj.playAuxSFX(2279, MathHelper.floor_double(this.posX),MathHelper.floor_double(this.posY),MathHelper.floor_double(this.posZ), 0);
                this.playSound("mob.ghast.scream",0.6F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            this.witherSummonTimer++;
            if (this.witherSummonTimer > (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 100) && witherSummonTimer < (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 40 : 140)){
                this.motionX = this.motionZ = 0;
            }
            if(this.witherSummonTimer == (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 40 : 100)){
                if (!this.hasRevived) {
                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        if(this.posY + 5 < yValue){
                            yValue = (int) (this.posY + 2);
                            xValue = (int) this.posX;
                            zValue = (int) this.posZ;
                        }

                        EntitySkeleton tempMinion = new EntitySkeleton(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue, zValue, 90, 90);
                        tempMinion.setSkeletonType(1);
                        if (this.rand.nextFloat() < 0.3) {
                            tempMinion.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
                        }
                        if (this.getAttackTarget() != null) {
                            tempMinion.entityToAttack = this.getAttackTarget();
                        }
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                } else if(this.rand.nextFloat()<0.7f){
                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        if(this.posY + 5 < yValue){
                            yValue = (int) (this.posY + 2);
                            xValue = (int) this.posX;
                            zValue = (int) this.posZ;
                        }

                        EntityFireCreeper tempMinion = new EntityFireCreeper(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue, zValue, 50, 50);
                        if (this.getAttackTarget() != null) {
                            tempMinion.entityToAttack = this.getAttackTarget();
                        }
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                } else if(this.getHealth()<100 && this.rand.nextFloat()<0.5f){

                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        if(this.posY + 5 < yValue){
                            yValue = (int) (this.posY + 2);
                            xValue = (int) this.posX;
                            zValue = (int) this.posZ;
                        }

                        EntityBlaze tempMinion = new EntityBlaze(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue + this.rand.nextInt(5), zValue, 50, 50);
                        if (this.getAttackTarget() != null) {
                            tempMinion.entityToAttack = this.getAttackTarget();
                        }
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                }
                else{
                    for(int i = 0; i<3; i++) {
                        int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                        int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                        int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                        if(this.posY + 5 < yValue){
                            yValue = (int) (this.posY + 2);
                            xValue = (int) this.posX;
                            zValue = (int) this.posZ;
                        }

                        DireWolfEntity tempMinion = new DireWolfEntity(this.worldObj);
                        tempMinion.setLocationAndAngles(xValue, yValue, zValue, 50, 50);
                        if (this.getAttackTarget() != null) {
                            tempMinion.entityToAttack = this.getAttackTarget();
                        }
                        this.worldObj.spawnEntityInWorld(tempMinion);
                    }
                }
                this.witherSummonTimer = 0;
                this.witherAttackTimer = 0;
            }
        }
    }
}
