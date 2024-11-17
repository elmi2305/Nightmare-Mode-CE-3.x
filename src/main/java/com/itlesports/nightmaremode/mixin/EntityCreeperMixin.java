package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.world.util.WorldUtils;
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

import java.util.Random;

@Mixin(EntityCreeper.class)
public class EntityCreeperMixin {
    @Shadow private int fuseTime;

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int reduceScrollDrops(int bound){
        return 250;
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void chanceToSpawnWithSpeed(CallbackInfo ci){
        EntityCreeper thisObj = ((EntityCreeper)(Object)this);
        int progress = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);

        if (thisObj.rand.nextFloat() < 0.08 + (progress * 0.02) && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            thisObj.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
        thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20 + progress*6);
        // 20 -> 26 -> 32 -> 38
        thisObj.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.28);
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 36.0))
    private double increaseCreeperBreachRange(double constant){
        EntityCreeper thisObj = ((EntityCreeper)(Object)this);

        if (thisObj.worldObj != null && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            int i = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);
            return switch (i) {
                case 0 -> 36; // 6b
                case 1 -> 64; // 8b
                case 2 -> 100; // 10b
                case 3 -> 196; // 14b
                default -> constant;
            };
        }
        return constant;
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropGhastTearsIfCharged(boolean bKilledByPlayer, int iFortuneModifier, CallbackInfo ci){
        EntityCreeper thisObj = ((EntityCreeper)(Object)this);

        if(thisObj.getDataWatcher().getWatchableObjectByte(17) == 1) {
            thisObj.dropItem(Item.ghastTear.itemID, 1);
            thisObj.dropItem(BTWItems.creeperOysters.itemID, 1);
        }
    }

    @Inject(method = "interact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/InventoryPlayer;getCurrentItem()Lnet/minecraft/src/ItemStack;",
                    shift = At.Shift.AFTER))
    private void explodeIfShorn(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears && thisObj.getNeuteredState() == 0) {
            if (!thisObj.worldObj.isRemote) {
                if (thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE || (thisObj.worldObj.getDifficulty() == Difficulties.STANDARD && playersCurrentItem.getItem().itemID == Item.shears.itemID)) {
                    boolean var2 = thisObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if (thisObj.getPowered()) {
                        thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 8, var2);
                    } else {
                        thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 3, var2);
                    }
                    thisObj.setDead();
                }
            }
        }
    }
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void detonateIfFireDamage(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        if ((par1DamageSource == DamageSource.inFire || par1DamageSource == DamageSource.onFire || par1DamageSource == DamageSource.lava) && thisObj.dimension != -1 && !thisObj.isPotionActive(Potion.fireResistance.id)){
            thisObj.onKickedByAnimal(null); // primes the creeper instantly
        }
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 2.0f))
    private float creeperImmunityToExplosionDamage(float constant){
        return 5.0f; // explosions deal 1/5 damage to creepers
    }

    @ModifyConstant(method = "entityInit", constant = @Constant(intValue = 0,ordinal = 0))
    private int chanceToSpawnCharged(int constant){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        int progress = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);

        if(progress>0 && thisObj.rand.nextFloat() < 0.1 + (progress)*0.02){
            if(thisObj.rand.nextInt(10)==0) {
                thisObj.setCustomNameTag("Terrence");
            }
            return 1;   // set to charged if conditions met
        } else if((thisObj.dimension == -1 && !(thisObj instanceof EntityFireCreeper)) && progress > 0){
            return 1;
        } else if(thisObj.dimension == 1 && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE){
            return 1;
        }
        return 0;
    }
    @Unique private int creeperTimeSinceIgnited = 0;

    @Inject(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityCreeper;timeSinceIgnited:I",ordinal = 3, shift = At.Shift.AFTER))
    private void jumpBeforeExploding(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper) (Object)this;

        if (thisObj.getCreeperState()==1) {
            creeperTimeSinceIgnited++;
        } else {creeperTimeSinceIgnited = 0;}
        // 8 ticks before it explodes
        if (creeperTimeSinceIgnited == (this.fuseTime - 8) && thisObj.getCreeperState()==1 && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            thisObj.motionY = 0.38F;
            EntityPlayer target = thisObj.worldObj.getClosestVulnerablePlayerToEntity(thisObj,6);
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

    @ModifyArg(method = "onUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;",
                    ordinal = 1), index = 4)
    private float modifyExplosionSize(float par8) {
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if(thisObj.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return 3f;
        }
        if(NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)>=2){
            return 4.2f;
        } else if(NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)==1){
            return 3.5f;
        }
        return 3.375f;
    }
}
