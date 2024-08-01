package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulty;
import com.itlesports.nightmaremode.EntityFireCreeper;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(EntityCreeper.class)
public class EntityCreeperMixin {
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void chanceToSpawnWithSpeed(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if (new Random().nextFloat() < 0.05 + (NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)*0.02)) {
            thisObj.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
        thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20+NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)*6);
        // 20 -> 26 -> 32 -> 38
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropGhastTearsIfCharged(boolean bKilledByPlayer, int iFortuneModifier, CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        if(thisObj.getDataWatcher().getWatchableObjectByte(17) == 1) {
            thisObj.dropItem(Item.ghastTear.itemID, 1);
        }
    }

    @Inject(method = "interact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/InventoryPlayer;getCurrentItem()Lnet/minecraft/src/ItemStack;",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void explodeIfShorn(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears && thisObj.getNeuteredState() == 0) {
            if (!thisObj.worldObj.isRemote) {
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
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void detonateIfFireDamage(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if(par1DamageSource == DamageSource.lava){
            boolean var2 = thisObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
            if (thisObj.getPowered()) {
                thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 6, var2);
            } else {
                thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 3, var2);
            }
            thisObj.setDead();
        } else if (par1DamageSource == DamageSource.inFire || par1DamageSource == DamageSource.onFire){
            thisObj.onKickedByAnimal(null); // primes the creeper instantly
        }
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 2.0f))
    private float creeperImmunityToExplosionDamage(float constant){
        return 5.0f; // explosions deal 1/5 damage to creepers
    }
    // redirecting all hostile calls
    @Redirect(method = "attackEntityFrom", at  = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"),remap = false)
    private boolean returnTrue(Difficulty instance){return true;}
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"),remap = false)
    private boolean returnTrue1(Difficulty instance){return true;}
    @Redirect(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"),remap = false)
    private boolean returnTrue2(Difficulty instance){return true;}
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"),remap = false)
    private boolean returnTrue3(Difficulty instance){return true;}
    // done redirecting

    @ModifyConstant(method = "entityInit", constant = @Constant(intValue = 0,ordinal = 0))
    private int chanceToSpawnCharged(int constant){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        int progress = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);
        if((progress>0 || (thisObj.dimension==-1 && progress > 0)) && thisObj.rand.nextFloat() < 0.1 + (progress)*0.02){
            return 1;       // set to charged if conditions met
        } else if((thisObj.dimension == -1 && !(thisObj instanceof EntityFireCreeper)) && progress > 0){
            return 1;
        } else if(thisObj.dimension == 1){
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
        if (creeperTimeSinceIgnited == 24 && thisObj.getCreeperState()==1) {
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
    private float injected(float par8) {
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        if(NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)>=2){
            return 4;
        } else if(NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)==1){
            return 3.5f;
        }
        return 3.375f;
    }
}
