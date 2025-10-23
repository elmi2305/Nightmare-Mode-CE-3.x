package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.DireWolfEntity;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMDifficultyParam;
import com.itlesports.nightmaremode.entity.EntityShadowZombie;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityDragon.class)
public abstract class EntityDragonMixin extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
    @Unique private boolean isCharging;
    @Unique private int boundingBoxIntersectionTicks;
    @Unique private int chargeCooldown = 100 + this.rand.nextInt(400);
    @Unique private int forceTargetCooldown = 50;

    @Shadow private void createEnderPortal(int par1, int par2) {}
    @Shadow private Entity target;

    @Shadow protected abstract boolean func_82195_e(DamageSource par1DamageSource, float par2);

    @Shadow public EntityDragonPart dragonPartHead;
    @Shadow public double targetX;
    @Shadow public double targetY;
    @Shadow public double targetZ;
    @Unique long attackTimer;

    public EntityDragonMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityDragon;setDead()V"))
    private void convertAllNearbyEndermenToShadowZombies(CallbackInfo ci){
        List list = this.worldObj.getEntitiesWithinAABB(EntityEnderman.class, this.boundingBox.expand(200.0, 150.0, 200.0));
        for(Object tempEntity : list){
            EntityShadowZombie zombie = new EntityShadowZombie(((EntityEnderman)tempEntity).worldObj);
            zombie.copyLocationAndAnglesFrom((EntityEnderman)tempEntity);
            this.worldObj.spawnEntityInWorld(zombie);
            zombie.setHealth(20 + zombie.rand.nextInt(10) * 3);
            ((EntityEnderman)tempEntity).setDead();
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(200d * NMUtils.getNiteMultiplier());
    }

    @Redirect(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityDragon;createEnderPortal(II)V"))
    private void onlySpawnOnSecondDragonKill(EntityDragon instance, int var10, int var12) {
        if (BlockEndPortal.bossDefeated || !this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            createEnderPortal(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
            NightmareMode.getInstance().shouldStackSizesIncrease = true;
        } else {
            BlockEndPortal.bossDefeated = true;
        }
    }

    @ModifyArg(method = "func_82195_e", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLiving;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"),index = 1)
    private float manageDamageCap(float par2){
        if(par2 > 20){
            return 20 + (par2 - 20) / 6;
        }
        return par2;
    }
    @Inject(method = "attackEntityFromPart", at = @At("HEAD"),cancellable = true)
    private void dragonDoesNotStopChargeWhenHit(EntityDragonPart par1EntityDragonPart, DamageSource par2DamageSource, float par3, CallbackInfoReturnable<Boolean> cir){
        if (par1EntityDragonPart != this.dragonPartHead) {
            par3 = par3 / 4.0F + 1.0F;
        }
        if(par2DamageSource.isExplosion()){
            par3 /= 3;
        }

        if (this.isCharging) {
            if (par2DamageSource.getEntity() instanceof EntityPlayer || par2DamageSource.isExplosion()) {
                this.func_82195_e(par2DamageSource, par3);
            }
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityDragon;setNewTarget()V"))
    private void upgradedSetNewTarget(EntityDragon instance){
        instance.forceNewTarget = false;
        boolean bTargetSelected = false;
        if (this.rand.nextInt(2) == 0 && !this.worldObj.playerEntities.isEmpty()) {
            this.target = (Entity)this.worldObj.playerEntities.get(this.rand.nextInt(this.worldObj.playerEntities.size()));
            long lTargetChangedDimensionTime = ((EntityPlayer)this.target).timeOfLastDimensionSwitch;
            long lWorldTime = this.worldObj.getWorldTime();
            if (lWorldTime < lTargetChangedDimensionTime || lWorldTime - lTargetChangedDimensionTime > 600L) {
                bTargetSelected = true;
            }
        }
        boolean shouldRetarget = !this.isCharging || this.rand.nextInt(12) == 0;
        if (shouldRetarget) {
            if (!bTargetSelected) {
                double var6;
                double var4;
                double var2;
                boolean var1 = false;
                do {
                    this.targetX = 0.0;
                    this.targetY = 70.0f + this.rand.nextFloat() * 50.0f;
                    this.targetZ = 0.0;
                    this.targetX += (double)(this.rand.nextFloat() * 120.0f - 60.0f);
                    this.targetZ += (double)(this.rand.nextFloat() * 120.0f - 60.0f);
                } while (!(var1 = (var2 = this.posX - this.targetX) * var2 + (var4 = this.posY - this.targetY) * var4 + (var6 = this.posZ - this.targetZ) * var6 > 100.0));
                this.target = null;
            }
        }
    }
//    @Inject(method = "setNewTarget", at = @At(value = ""))

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void attackPlayer(CallbackInfo ci){
        this.attackTimer++;

        if(this.attackTimer % 4 == 0){
            EntityPlayer target = this.worldObj.getClosestVulnerablePlayer(this.posX,this.posY,this.posZ,80);

            if (target != null) {
                if(this.target != target){
                    this.forceTargetCooldown--;
                }
                if(this.forceTargetCooldown <= 0) {
                    this.target = target;
                    this.forceTargetCooldown = 30 + (this.rand.nextInt(14) + 3) * 3;
                    if(BlockEndPortal.bossDefeated){
                        this.forceTargetCooldown -= 15;
                    }
                }
                this.setAttackTarget(target);
            }
            if(this.dimension == 1 && !WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly()){
                WorldUtils.gameProgressSetEndDimensionHasBeenAccessedServerOnly();
            }
        }
        if (!this.isCharging) {
            this.chargeCooldown--;
        }


        if(!(this.target instanceof EntityPlayer player)) return;
        int threshold = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 25 : 40;


        if(this.boundingBox.expand(1,1,1).intersectsWith(player.boundingBox)){
            this.boundingBoxIntersectionTicks += 1;
        } else{
            this.boundingBoxIntersectionTicks = 0;
        }
        if(this.attackTimer == threshold && this.rand.nextInt(20) == 0 && !this.isCharging && this.chargeCooldown <= 0){
            this.isCharging = true;
            this.chargeCooldown = 200 + this.rand.nextInt(200);
        }
        if((this.getDistanceSqToEntity(player) < 5 || this.boundingBoxIntersectionTicks > 12) && this.isCharging){
            this.isCharging = false;
        }

//        this.isCharging = this.isCharging
//                ? !(this.getDistanceSqToEntity(player) < 4 || this.boundingBoxIntersectionTicks > 10)
//                : (this.attackTimer == threshold && this.rand.nextInt(2) == 0);

        if(this.attackTimer > threshold && !this.worldObj.isRemote){
            double var3 = player.posX - this.posX;
            double var5 = player.boundingBox.minY + (double) (player.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
            double var7 = player.posZ - this.posZ;

            EntityLargeFireball var11 = new EntityLargeFireball(this.worldObj, this, var3, var5, var7);
            this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;

            float i = rand.nextFloat();
            if(i<0.2) {
                EntitySkeleton minion = new EntitySkeleton(this.worldObj);
                minion.setSkeletonType(1);
                minion.entityToAttack = player;
                if (this.rand.nextBoolean()) {
                    minion.setCurrentItemOrArmor(0,new ItemStack(Item.swordStone));
                    // 25% chance to have a sword
                }
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i<0.4){
                EntityCreeper minion = new EntityCreeper(this.worldObj);
                minion.addPotionEffect(new PotionEffect(Potion.invisibility.id, 100000,0));
                minion.entityToAttack = player;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i < 0.5){
                DireWolfEntity minion = new DireWolfEntity(this.worldObj);
                minion.entityToAttack = player;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i < 0.6){
                EntityTNTPrimed minion = new EntityTNTPrimed(this.worldObj);
                minion.fuse = 60;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i < 0.63){
                EntityWitch minion = new EntityWitch(this.worldObj);
                minion.entityToAttack = player;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            }
            this.worldObj.spawnEntityInWorld(var11);

            this.attackTimer = 0;
        }
    }

    @Override
    public float knockbackMagnitude() {
        return this.isCharging ? 0.15f : 0f;
    }

    @Redirect(method = "destroyBlocksInAABB", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockToAir(III)Z"))
    private boolean avoidDestroyingArenaBlocks(World world, int par1, int par2, int par3){
        if(world.getBlockId(par1,par2,par3) == NMBlocks.specialObsidian.blockID || world.getBlockId(par1,par2,par3) == NMBlocks.cryingObsidian.blockID){
            return false;
        }
        return world.setBlockToAir(par1,par2,par3);
    }

    @Redirect(method = "destroyBlocksInAABB", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Block;obsidian:Lnet/minecraft/src/Block;"))
    private Block allowBreakingObsidianWhileCharging(){
        if(this.isCharging) return Block.glass;
        return Block.obsidian;
    }
}
