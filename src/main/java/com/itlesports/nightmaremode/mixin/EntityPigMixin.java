package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.AITasks.EntityAIChasePlayer;
import com.itlesports.nightmaremode.AITasks.EntityAIPursuePlayer;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPig.class)
public abstract class EntityPigMixin extends EntityAnimal {
    @Unique private boolean hasMadeSound = false;
    @Unique private int jumpCounter;

    public EntityPigMixin(World par1World) {
        super(par1World);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,8);
        this.targetTasks.addTask(12, new EntityAIChasePlayer(this, 1.25f));
    }
    @Inject(method = "isSubjectToHunger", at = @At("HEAD"),cancellable = true)
    private void manageEclipseHunger(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsMobEclipsed(this)){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updateAITasks", at = @At("TAIL"))
    private void manageJumpAttackAtPlayer(CallbackInfo ci){
        if(this.getAttackTarget() instanceof EntityPlayer player && NightmareUtils.getIsMobEclipsed(this)){
            double dist = Math.sqrt(this.getDistanceSqToEntity(player));
            if(dist > 1.5 && dist < 7){
                if (!this.hasMadeSound) {
                    this.playSound("random.fuse", 1.0F, 0.5F);
                    this.hasMadeSound = true;
                }

                if(dist < 3 && this.onGround){
                    this.jumpCounter = Math.min(this.jumpCounter + 1, 10);
                    double var1 = player.posX - this.posX;
                    double var2 = player.posZ - this.posZ;
                    Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
                    vector.normalize();
                    this.motionX = vector.xCoord * 0.2;
                    this.motionY = 0.4f;
                    this.motionZ = vector.zCoord * 0.2;
                }
            } else if (dist > 6){
                this.hasMadeSound = false;
            } else if (dist < 1.5 || this.jumpCounter == 10){
                this.worldObj.newExplosion(this,this.posX,this.posY,this.posZ,5f,false,true);
                this.setDead();
            }
        }
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {
        if(!NightmareUtils.getIsMobEclipsed(this)) {
            super.knockBack(par1Entity, par2, par3, par5);
        }
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (lootingLevel * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.creeperChop.itemID;

            int var4 = this.rand.nextInt(3);
            if (lootingLevel > 0) {
                var4 += this.rand.nextInt(lootingLevel + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10d * NightmareUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25F * (1 + (NightmareUtils.getNiteMultiplier() - 1) / 20));
    }
}
