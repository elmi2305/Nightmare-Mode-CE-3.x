package com.itlesports.nightmaremode.mixin.entity;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.entity.underworld.EntityAwakenedWither;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.EntityWitherSkullExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.itlesports.nightmaremode.entity.underworld.EntityAwakenedWither.ATK_SKULL_RAIN;

@Mixin(EntityWitherSkull.class)
public abstract class EntityWitherSkullMixin extends EntityFireball implements EntityWitherSkullExt {
    @Unique private boolean isLifeStealing;

    public EntityWitherSkullMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "onImpact", constant = @Constant(intValue = 1))
    private int increaseEffectAmplifier(int constant){
        EntityWitherSkull thisObj = (EntityWitherSkull)(Object)this;
        if(thisObj.rand.nextFloat() < 0.15 && thisObj.worldObj != null && thisObj.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) && !(thisObj.shootingEntity instanceof EntityBloodWither)){
            return 2;
        }
        return 1;
    }

    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"))
    private void lowerPlayerLifeOnHit(MovingObjectPosition pos, CallbackInfo ci){
        if(pos.entityHit instanceof EntityPlayer p && (this.nightmareMode$getLifeStealing())){
            p.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(p.getMaxHealth() - 1);
            if(p.getHealth() > p.getMaxHealth()){
                p.setHealth(p.getMaxHealth());
            }
            if(p instanceof EntityPlayerExt && !p.isPotionActive(Potion.resistance)){
                ((EntityPlayerExt) p).nightmareMode$setFear(Math.min(((EntityPlayerExt) p).nightmareMode$getFear() + 0.12f, 0.9f));
                p.playSound("random.drink", 1.0f, 0.7f + (this.rand.nextFloat()  - 0.5f ) * 0.1f);
            }
            p.addPotionEffect(new PotionEffect(Potion.resistance.id, 10, 0));
        }
    }
    @Inject(method = "onImpact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityLivingBase;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V")
    )
    private void applyAdditionalEffectsOnImpact(MovingObjectPosition pos, CallbackInfo ci){
        if (pos.entityHit.rand.nextFloat()<0.06 && !(this.shootingEntity instanceof EntityBloodWither)) {
            ((EntityLivingBase)pos.entityHit).addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
        }
    }
    @ModifyConstant(method = "onImpact", constant = @Constant(floatValue = 8.0f))
    private float increaseDamage(float constant){
        if(this.shootingEntity instanceof EntityAwakenedWither){
            if(((EntityAwakenedWither) this.shootingEntity).getCurrentAttack() == ATK_SKULL_RAIN){
                return 55f;
            }
            return 18f;
        }
        return this.shootingEntity instanceof EntityBloodWither bloodWither ? (bloodWither.isDoingLaserAttack ? 50f : 15f) : 12f;
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void addRedSkullNBT(NBTTagCompound tag, CallbackInfo ci){
        this.isLifeStealing = tag.getByte("lifeStealing") == 1;
    }
    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeRedSkullNBT(NBTTagCompound tag, CallbackInfo ci){
        tag.setByte("lifeStealing", (byte) (this.isLifeStealing ? 1 : 0));
    }

    @Override
    public boolean nightmareMode$getLifeStealing() {
        return this.isLifeStealing || this.dataWatcher.getWatchableObjectByte(11) == 1;
    }
    @Inject(method = "entityInit", at = @At("TAIL"))
    private void initializeNBTValueAsFalse(CallbackInfo ci){
        this.dataWatcher.addObject(11, (byte)0);
    }

    @Override
    public void nightmareMode$setLifeStealing(boolean lifeStealing) {
        this.isLifeStealing = lifeStealing;
        this.dataWatcher.updateObject(11, (byte)(lifeStealing ? 1 : 0));
    }

    @Inject(method = "getBlockExplosionResistance", at = @At("HEAD"),cancellable = true)
    private void manageBloodWitherNotDestroyingArena(Explosion explosion, World par2World, int par3, int par4, int par5, Block par6Block, CallbackInfoReturnable<Float> cir){
        if(par6Block.blockID == NMBlocks.specialObsidian.blockID || par6Block.blockID == NMBlocks.cryingObsidian.blockID){
            cir.setReturnValue(par6Block.blockResistance);
        }
        if(par6Block.blockID == BTWBlocks.soulforgedSteelBlock.blockID){
            cir.setReturnValue(0.8f);
        }
    }
    @Redirect(method = "getBlockExplosionResistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWitherSkull;isHighExplosive()Z"))
    private boolean normalSkullsExplodeOnBloodWither(EntityWitherSkull skull){
        if(skull.shootingEntity instanceof EntityBloodWither){
            return true;
        }
        return skull.isInvulnerable();
    }
}
