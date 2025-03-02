package com.itlesports.nightmaremode.mixin;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.DireWolfEntity;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.entity.EntityFireCreeper;
import com.itlesports.nightmaremode.entity.EntityNightmareGolem;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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
        this.isImmuneToFire = true;
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.witherAttackTimer = 200;
        this.targetTasks.addTask(6, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, false, attackEntitySelector));
    }
    @Inject(method = "applyEntityAttributes", at = @At(value = "TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(8.0F);
    }

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;II)V"),index = 2)
    private int makeWitherDropSmiteScroll(int par2){
        return Enchantment.smite.effectId;
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(intValue = 20))
    private int reduceWitherBreakBlockInterval(int constant){
        return 1;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void destroyBlocksAbove(CallbackInfo ci){
        EntityLivingBase target = this.getAttackTarget();
        if(target != null && this.posY - target.posY < 4) {
            for (int i = -1; i < 1; i++) {
                for (int j = -1; j < 1; j++) {
                    this.destroyBlock(this.worldObj, (int) this.posX + i, (int) this.posY + 3, (int) this.posZ + j);
                    this.destroyBlock(this.worldObj, (int) this.posX + i, (int) this.posY + 4, (int) this.posZ + j);
                    this.destroyBlock(this.worldObj, (int) this.posX + i, (int) this.posY + 5, (int) this.posZ + j);
                }
            }
        }

        if(this.ticksExisted % 40 == 0){
            onGolemNearby(this);
            ensureTargettingOnPlayer();
        }
    }


    @Unique private void ensureTargettingOnPlayer(){
        EntityPlayer player = this.worldObj.getClosestVulnerablePlayer(this.posX,this.posY,this.posZ,20);
        if(player != null){
            this.entityLivingToAttack = player;
            this.setAttackTarget(player);
        }
    }

    @Inject(method = "updateAITasks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityWither;field_82222_j:I",ordinal = 0))
    private void manageWitherBlockBreaking(CallbackInfo ci){
        this.destroyBlocksInAABB(this.boundingBox.expand(0.5d,0,0.5d));
    }

    @Unique
    private void destroyBlocksInAABB(AxisAlignedBB par1AxisAlignedBB) {
        int var2 = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int var3 = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int var4 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int var5 = MathHelper.floor_double(par1AxisAlignedBB.maxX);
        int var6 = MathHelper.floor_double(par1AxisAlignedBB.maxY);
        int var7 = MathHelper.floor_double(par1AxisAlignedBB.maxZ);
        boolean var9 = false;
        for (int var10 = var2; var10 <= var5; ++var10) {
            for (int var11 = var3; var11 <= var6; ++var11) {
                for (int var12 = var4; var12 <= var7; ++var12) {
                    int var13 = this.worldObj.getBlockId(var10, var11, var12);
                    if (var13 == 0) continue;
                    if (var13 != Block.obsidian.blockID && var13 != Block.whiteStone.blockID && var13 != Block.bedrock.blockID  && var13 != NMBlocks.cryingObsidian.blockID  && var13 != NMBlocks.specialObsidian.blockID && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                        var9 = this.worldObj.setBlockToAir(var10, var11, var12) || var9;
                    }
                }
            }
        }
        if (var9) {
            double var16 = par1AxisAlignedBB.minX + (par1AxisAlignedBB.maxX - par1AxisAlignedBB.minX) * (double)this.rand.nextFloat();
            double var17 = par1AxisAlignedBB.minY + (par1AxisAlignedBB.maxY - par1AxisAlignedBB.minY) * (double)this.rand.nextFloat();
            double var14 = par1AxisAlignedBB.minZ + (par1AxisAlignedBB.maxZ - par1AxisAlignedBB.minZ) * (double)this.rand.nextFloat();
            this.worldObj.spawnParticle("largeexplode", var16, var17, var14, 0.0, 0.0, 0.0);
        }
    }

    @Redirect(method = "updateAITasks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;destroyBlock(IIIZ)Z"))
    private boolean avoidDestroyingSpecialNightmareBlocks(World world, int x, int y, int z, boolean par3){
        if(world.getBlockId(x,y,z) == NMBlocks.specialObsidian.blockID || world.getBlockId(x,y,z) == NMBlocks.cryingObsidian.blockID){
            return false;
        }
        return world.destroyBlock(x,y,z,par3);
    }


    @Unique private static void onGolemNearby(EntityMob wither){
        List list = wither.worldObj.getEntitiesWithinAABBExcludingEntity(wither, wither.boundingBox.expand(6, 6, 6));
        for (Object tempEntity : list) {
            if (!(tempEntity instanceof EntityIronGolem golem)) continue;
            if (golem instanceof EntityNightmareGolem) continue;
            if (!(golem.getAttackTarget() instanceof EntityPlayer)) continue;
            golem.attackEntityFrom(DamageSource.magic,100f);
            wither.worldObj.newExplosion(wither,golem.posX,golem.posY,golem.posZ,2f,false,false);
            wither.heal(1f);
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
                this.getEntityAttribute(BTWAttributes.armor).setAttribute(10.0F);
                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 5f + this.rand.nextFloat()*2,true, mobGriefing);
            }
            this.worldObj.playSoundAtEntity(this,"mob.endermen.portal",2.0F,1.0F);
        }
    }
    @ModifyConstant(method = "updateAITasks", constant = @Constant(intValue = 20,ordinal = 1))
    private int increaseHealingRate2ndPhase(int constant){
        return this.hasRevived ? 12 : constant;
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void manageAnger(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (!(((EntityWither)(Object)this) instanceof EntityBloodWither)) {
            this.witherAttackTimer = (int) Math.min(this.witherAttackTimer + par2 * 2, this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 1500 : 4000);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void attackTimer(CallbackInfo ci){
        if (!(((EntityWither)(Object)this) instanceof EntityBloodWither)) {
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
        if(!(((EntityWither)(Object)this) instanceof EntityBloodWither) && this.getHealth() < 21 && !this.hasRevived && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            this.setHealth(300);
            if (this.worldObj.getClosestPlayer(this.posX,this.posY,this.posZ,20) != null) {
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("A God does not fear death.");
                text2.setColor(EnumChatFormatting.BLACK);
                this.worldObj.getClosestPlayer(this.posX,this.posY,this.posZ,20).sendChatToPlayer(text2);
            }
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
    private void manageMinionSpawning(CallbackInfo ci) {
        if (!(((EntityWither)(Object)this) instanceof EntityBloodWither)) {
            boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
            int spawnDelay = isHostile ? 1500 : 4000;
            int summonStart = isHostile ? 0 : 100;
            int summonEnd = isHostile ? 40 : 140;
            int summonComplete = isHostile ? 40 : 100;

            if (this.witherAttackTimer >= spawnDelay) {
                if (this.witherSummonTimer == 0) {
                    this.worldObj.playAuxSFX(2279, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
                    this.playSound("mob.ghast.scream", 0.6F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                }

                this.witherSummonTimer++;

                if (this.witherSummonTimer > summonStart && this.witherSummonTimer < summonEnd) {
                    this.motionX = this.motionZ = 0;
                }

                if (this.witherSummonTimer == summonComplete) {
                    spawnMinions();
                    this.witherSummonTimer = 0;
                    this.witherAttackTimer = 0;
                }
            }
        }
    }


    @Unique
    private void spawnMinions() {
        for (int i = 0; i < 3; i++) {
            int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
            int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
            int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

            if (this.posY + 5 < yValue) {
                yValue = (int) (this.posY + 2);
                xValue = (int) this.posX;
                zValue = (int) this.posZ;
            }

            if (!this.hasRevived) {
                spawnSkeleton(xValue, yValue, zValue);
            } else if (this.rand.nextFloat() < 0.7f) {
                spawnFireCreeper(xValue, yValue, zValue);
            } else if (this.getHealth() < 100 && this.rand.nextBoolean()) {
                spawnBlaze(xValue, yValue, zValue);
            } else {
                spawnDireWolf(xValue, yValue, zValue);
            }
        }
    }

    @Unique
    private void spawnSkeleton(int x, int y, int z) {
        EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
        skeleton.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        skeleton.setSkeletonType(1);
        if (this.rand.nextFloat() < 0.3) {
            skeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
        }
        setMinionTarget(skeleton);
        this.worldObj.spawnEntityInWorld(skeleton);
    }

    @Unique
    private void spawnFireCreeper(int x, int y, int z) {
        EntityFireCreeper fireCreeper = new EntityFireCreeper(this.worldObj);
        fireCreeper.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        setMinionTarget(fireCreeper);
        this.worldObj.spawnEntityInWorld(fireCreeper);
    }

    @Unique
    private void spawnBlaze(int x, int y, int z) {
        EntityBlaze blaze = new EntityBlaze(this.worldObj);
        blaze.setLocationAndAngles(x, y + this.rand.nextInt(5), z, this.rotationYaw, this.rotationPitch);
        setMinionTarget(blaze);
        this.worldObj.spawnEntityInWorld(blaze);
    }

    @Unique
    private void spawnDireWolf(int x, int y, int z) {
        DireWolfEntity direWolf = new DireWolfEntity(this.worldObj);
        direWolf.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        setMinionTarget(direWolf);
        this.worldObj.spawnEntityInWorld(direWolf);
    }

    @Unique
    private void setMinionTarget(EntityCreature minion) {
        if (this.getAttackTarget() != null) {
            minion.entityToAttack = this.getAttackTarget();
        }
    }
}
