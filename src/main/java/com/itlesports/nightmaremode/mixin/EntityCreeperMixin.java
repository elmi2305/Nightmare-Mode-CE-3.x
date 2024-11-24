package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.EntityFireCreeper;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityCreeper.class)
public abstract class EntityCreeperMixin extends EntityMob implements EntityCreeperAccessor{

    @Shadow public abstract int getCreeperState();

    public EntityCreeperMixin(World par1World) {
        super(par1World);
    }

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int reduceScrollDrops(int bound){
        return 250;
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void chanceToSpawnWithSpeed(CallbackInfo ci){
        int progress = NightmareUtils.getWorldProgress(this.worldObj);
        double bloodMoonModifier = NightmareUtils.getIsBloodMoon(this.worldObj) ? 1.35 : 1;
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;

        if (this.rand.nextInt(8 - progress * 2) == 0 && isHostile) {
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((20 + progress * 6) * bloodMoonModifier);
        // 20 -> 26 -> 32 -> 38
        // 27 -> 35 -> 43 -> 51
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.28 + ((bloodMoonModifier-1) * 0.08));
        // 0.28 or 0.308
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 36.0))
    private double increaseCreeperBreachRange(double constant){
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
        int bloodMoonModifier = NightmareUtils.getIsBloodMoon(this.worldObj) ? 3 : 1;
        if (isHostile) {
            int i = NightmareUtils.getWorldProgress(this.worldObj);
            return switch (i) {
                case 0 -> 36 * bloodMoonModifier;  // 6b   10.4b
                case 1 -> 64 * bloodMoonModifier;  // 8b   13.8b
                case 2 -> 100 * bloodMoonModifier; // 10b  17.3b
                case 3 -> 196 * bloodMoonModifier; // 14b  24.2b
                default -> constant;
            };
        }
        return constant;
    }
    @Inject(method = "onUpdate", at = @At(value = "TAIL"))
    private void manageBloodMoonFuse(CallbackInfo ci){
        if(this.getFuseTime() == 30 && this.getCreeperState() == -1 && NightmareUtils.getIsBloodMoon(this.worldObj)){
            this.setFuseTime(20);
        }
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropGhastTearsIfCharged(boolean bKilledByPlayer, int iFortuneModifier, CallbackInfo ci){
        if(this.getDataWatcher().getWatchableObjectByte(17) == 1) {
            this.dropItem(Item.ghastTear.itemID, 1);
            this.dropItem(BTWItems.creeperOysters.itemID, 1);
        }
    }

    @Inject(method = "interact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/InventoryPlayer;getCurrentItem()Lnet/minecraft/src/ItemStack;",
                    shift = At.Shift.AFTER))
    private void explodeIfShorn(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
        float bloodMoonModifier = NightmareUtils.getIsBloodMoon(this.worldObj) ? 1.25f : 1;
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears && thisObj.getNeuteredState() == 0) {
            if (!thisObj.worldObj.isRemote) {
                if (isHostile || (!isHostile && playersCurrentItem.getItem().itemID == Item.shears.itemID)) {
                    boolean var2 = thisObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if (thisObj.getPowered()) {
                        thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 8 * bloodMoonModifier, var2);
                    } else {
                        thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 3 * bloodMoonModifier, var2);
                    }
                    thisObj.setDead();
                }
            }
        }
    }
    @Redirect(method = "interact", at = @At(value = "FIELD", target = "Lnet/minecraft/src/World;isRemote:Z"))
    private boolean doNotDropCreeperOystersIfShorn(World world){
        if(world.getDifficulty() != Difficulties.HOSTILE){
            return world.isRemote;
        }
        return true;
    }
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void detonateIfFireDamage(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if ((par1DamageSource == DamageSource.inFire || par1DamageSource == DamageSource.onFire || par1DamageSource == DamageSource.lava) && this.dimension != -1 && !this.isPotionActive(Potion.fireResistance.id)){
            this.onKickedByAnimal(null); // primes the creeper instantly
        }
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 2.0f))
    private float creeperImmunityToExplosionDamage(float constant){
        return 5.0f; // explosions deal 1/5 damage to creepers
    }
    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void immuneToDrowningDuringBloodMoon(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsBloodMoon(this.worldObj) && par1DamageSource == DamageSource.drown){
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "entityInit", constant = @Constant(intValue = 0,ordinal = 0))
    private int chanceToSpawnCharged(int constant){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        int progress = NightmareUtils.getWorldProgress(thisObj.worldObj);
        boolean isBloodMoon = NightmareUtils.getIsBloodMoon(thisObj.worldObj);

        if(progress>0 && thisObj.rand.nextFloat() < 0.15 + (progress - 1)*0.03){
            if(thisObj.rand.nextInt(10) == 0 && thisObj.dimension == 0) {
                thisObj.setCustomNameTag("Terrence");
            }
            return 1;   // set to charged if conditions met
        } else if((thisObj.dimension == -1 && !(thisObj instanceof EntityFireCreeper)) && progress > 0){
            return 1;
        } else if(thisObj.dimension == 1 && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE){
            return 1;
        }
        if(isBloodMoon){
            return rand.nextInt(4) == 0 ? 1 : 0;
        }
        return 0;
    }
    @Unique private int creeperTimeSinceIgnited = 0;

    @Inject(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityCreeper;timeSinceIgnited:I",ordinal = 3, shift = At.Shift.AFTER))
    private void jumpBeforeExploding(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper) (Object)this;

        if (thisObj.getCreeperState() == 1) {
            creeperTimeSinceIgnited++;
        } else {creeperTimeSinceIgnited = 0;}

        // 8 ticks before it explodes
        if (creeperTimeSinceIgnited == (this.getFuseTime() - 8) && thisObj.getCreeperState() == 1 && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            EntityPlayer target = thisObj.worldObj.getClosestVulnerablePlayerToEntity(thisObj,6);
            thisObj.motionY = 0.38F;
            if(target != null) {
                double var1 = target.posX - thisObj.posX;
                double var2 = target.posZ - thisObj.posZ;
                Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
                vector.normalize();
                thisObj.motionX = vector.xCoord * 0.18;
                thisObj.motionZ = vector.zCoord * 0.18;
            }
        }
    }

    @Override
    public boolean isImmuneToHeadCrabDamage() {
        return true;
    }

    @Override
    public boolean isSecondaryTargetForSquid() {
        return NightmareUtils.getIsBloodMoon(this.worldObj) && this.getDataWatcher().getWatchableObjectByte(17) == 0;
    }

    @Override
    public Entity getHeadCrabSharedAttackTarget() {
        return this.getAttackTarget();
    }

    @ModifyArg(method = "onUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;",
                    ordinal = 1), index = 4)
    private float modifyExplosionSize(float par8) {
        float bloodMoonModifier = NightmareUtils.getIsBloodMoon(this.worldObj) ? 1.25f : 1;

        if(this.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return 3f;
        }
        if(NightmareUtils.getWorldProgress(this.worldObj)>=2){
            return 4.2f * bloodMoonModifier;
        } else if(NightmareUtils.getWorldProgress(this.worldObj)==1){
            return 3.5f * bloodMoonModifier;
        }
        return 3.375f * bloodMoonModifier;
    }
}
