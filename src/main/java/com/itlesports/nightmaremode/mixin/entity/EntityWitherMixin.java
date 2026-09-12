package com.itlesports.nightmaremode.mixin.entity;

import api.world.WorldUtils;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.attribute.BTWAttributes;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.entity.EntityNightmareGolem;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.itlesports.nightmaremode.util.NMFields.PREHARDMODE;

@Mixin(EntityWither.class)
public abstract class EntityWitherMixin extends EntityMob {

    @Shadow public abstract void addPotionEffect(PotionEffect par1PotionEffect);

    @Shadow @Final private static IEntitySelector attackEntitySelector;
    @Unique int witherAttackTimer = 0;
    @Unique int witherSummonTimer = 0;
    @Unique private static final int MINION_INTERVAL = 6000;

    public EntityWitherMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void increaseXPYield(World w, CallbackInfo ci){
        if (NMUtils.getWorldProgress() < NMFields.POSTWITHER) {
            this.experienceValue = 15000;
        }
        this.setSize(1.2f, 4.3f);
        this.isImmuneToFire = true;
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.witherAttackTimer = 200;
        this.targetTasks.addTask(6, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, false, attackEntitySelector));
    }

    @Override
    public void travelToDimension(int par1) {} // prevents sending the wither to the nether/end for cheese

    @Inject(method = "applyEntityAttributes", at = @At(value = "TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(8.0F);
    }

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;II)V"),index = 2)
    private int makeWitherDropSmiteScroll(int par2){
        return Enchantment.smite.effectId;
    }
    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void dropWitherSoul(boolean par1, int par2, CallbackInfo ci){
//        if (NightmareMode.devMode) {
            this.dropItem(NMItems.witherSoul.itemID, 1);
//        }
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(intValue = 20))
    private int reduceWitherBreakBlockInterval(int constant){
        return 1;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void destroyBlocksAbove(CallbackInfo ci){
        EntityLivingBase target = this.getAttackTarget();
        if(target != null && this.posY - target.posY < 4) {
            if (this.ticksExisted % 4 != 0) return; // throttle for performance
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

    @Inject(method = "updateAITasks", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityWither;field_82222_j:I", ordinal = 0, opcode = Opcodes.GETFIELD))
    private void manageWitherBlockBreaking(CallbackInfo ci){
        this.destroyBlocksInAABB(this.boundingBox.expand(0.5d,0,0.5d));
    }
    @Inject(method = "isAIEnabled", at = @At("HEAD"),cancellable = true)
    private void aprilFoolsAiStuff(CallbackInfoReturnable<Boolean> cir){
        if(NightmareMode.isAprilFools){
            cir.setReturnValue(this.worldObj.getClosestVulnerablePlayerToEntity(this,6f) != null);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageDespawnOnAprilFools(CallbackInfo ci){
        if(NightmareMode.isAprilFools && !this.hasAttackTarget() && (this.posY <= 63 || this.ticksExisted >= 5000)){
            this.setDead();
        }
    }
    @Inject(method = "modSpecificOnLivingUpdate", at = @At(value = "INVOKE", target = "Lapi/world/WorldUtils;gameProgressSetWitherHasBeenSummonedServerOnly()V"),cancellable = true, remap = false)
    private void manageAprilFoolsWitherSetter(CallbackInfo ci){
        if(NMUtils.getWorldProgress() == PREHARDMODE && NightmareMode.isAprilFools){
            ci.cancel();
        }
    }

    @Unique
    private void destroyBlocksInAABB(AxisAlignedBB aabb) {
        int minX = MathHelper.floor_double(aabb.minX);
        int minY = MathHelper.floor_double(aabb.minY);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxX = MathHelper.floor_double(aabb.maxX);
        int maxY = MathHelper.floor_double(aabb.maxY);
        int maxZ = MathHelper.floor_double(aabb.maxZ);
        boolean setToAir = false;
        for (int dx = minX; dx <= maxX; ++dx) {
            for (int dy = minY; dy <= maxY; ++dy) {
                for (int dz = minZ; dz <= maxZ; ++dz) {
                    int blockID = this.worldObj.getBlockId(dx, dy, dz);
                    if (blockID == 0) continue;
                    if (
                            blockID != Block.obsidian.blockID
                            && blockID != Block.bedrock.blockID
                            && blockID != NMBlocks.cryingObsidian.blockID
                            && blockID != NMBlocks.specialObsidian.blockID
                            && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                    {
                        setToAir = this.worldObj.setBlockToAir(dx, dy, dz);
                    }
                }
            }
        }
        if (setToAir) {
            double fxX = aabb.minX + (aabb.maxX - aabb.minX) * (double)this.rand.nextFloat();
            double fxY = aabb.minY + (aabb.maxY - aabb.minY) * (double)this.rand.nextFloat();
            double fxZ = aabb.minZ + (aabb.maxZ - aabb.minZ) * (double)this.rand.nextFloat();
            this.worldObj.spawnParticle("largeexplode", fxX, fxY, fxZ, 0.0, 0.0, 0.0);
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

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void manageAnger(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (!(((EntityWither)(Object)this) instanceof EntityBloodWither)) {
            this.witherAttackTimer = (int) Math.min(this.witherAttackTimer + par2 * 2, getMinionInterval());
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

            if (this.witherAttackTimer < getMinionInterval()) {
                this.witherAttackTimer += this.rand.nextInt(5)+1;
            }
            if(this.entityToAttack instanceof EntityPlayer player){
                if(this.witherAttackTimer % 160 == 10){
                    int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-5,5);
                    int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-5,5);
                    int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));
                    player.setPositionAndUpdate(xValue,yValue,zValue);
                    this.entityToAttack = player; // reassures the wither aggro in case it is lost
                    player.worldObj.playSoundAtEntity(player,"mob.endermen.portal",2.0F,1.0F);
                }

                if(player instanceof EntityPlayerExt){
                    ((EntityPlayerExt) player).nightmareMode$setFear(Math.max(((EntityPlayerExt) player).nightmareMode$getFear(), 0.3f));
                }
            }
        } else{
            if(this.entityToAttack instanceof EntityPlayerExt player){
                player.nightmareMode$setFear(Math.max(player.nightmareMode$getFear(), 0.15f));
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
        return scaleDamage(par2);
    }
    @Unique private float scaleDamage(float original){
        if(original > 200){return 400;} // if you want to instakill it with creative
        if(original > 20 && (!WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly())){return 20;}
        return original;
    }

    @ModifyArg(method = "func_82216_a",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWither;func_82209_a(IDDDZ)V"), index = 4)
    private boolean modifyChanceForBlueSkulls(boolean par8){
        return this.rand.nextFloat()<0.01;
    }

    @Inject(method = "updateAITasks", at = @At("HEAD"))
    private void manageMinionSpawning(CallbackInfo ci) {
        if (!(((EntityWither)(Object)this) instanceof EntityBloodWither)) {
            int spawnDelay = getMinionInterval();
            int summonStart = 0;
            int summonEnd = 40;
            int summonComplete = 40;

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
    private int getMinionInterval() {
        return MINION_INTERVAL;
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

            spawnSkeleton(xValue, yValue, zValue);
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
    private void setMinionTarget(EntityCreature minion) {
        if (this.getAttackTarget() != null) {
            minion.entityToAttack = this.getAttackTarget();
        }
    }
}
