package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.DireWolfEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.EntityShadowZombie;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityDragon.class)
public abstract class EntityDragonMixin extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
    @Shadow
    private void createEnderPortal(int par1, int par2) {
    }

    @Shadow private Entity target;

    @Unique long attackTimer;

    public EntityDragonMixin(World par1World) {
        super(par1World);
    }

//    @Inject(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityDragon;setDead()V"))
//    private void convertAllNearbyEndermenToShadowZombies(CallbackInfo ci){
//        List list = this.worldObj.getEntitiesWithinAABB(EntityEnderman.class, this.boundingBox.expand(200.0, 150.0, 200.0));
//        for(Object tempEntity: list){
//            EntityShadowZombie zombie = new EntityShadowZombie(((EntityEnderman)tempEntity).worldObj);
//            zombie.copyLocationAndAnglesFrom((EntityEnderman)tempEntity);
//            ((EntityEnderman)tempEntity).worldObj.spawnEntityInWorld(zombie);
//            ((EntityEnderman)tempEntity).setDead();
//        }
//    }
    // converts all endermen to shadow zombies

    @Redirect(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityDragon;createEnderPortal(II)V"))
    private void onlySpawnOnSecondDragonKill(EntityDragon instance, int var10, int var12) {
        if (BlockEndPortal.bossDefeated || this.worldObj.getDifficulty() != Difficulties.HOSTILE) {
            createEnderPortal(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
        } else {
//            ChatMessageComponent text2 = new ChatMessageComponent();
//            text2.addText("<Twin " + (rand.nextInt(2)+1) + "> Ugh... You monster...");
//            if (rand.nextInt(2)==0) {
//                text2.setColor(EnumChatFormatting.RED);
//            } else {
//                text2.setColor(EnumChatFormatting.BLUE);
//            }
//            if (this.worldObj.getClosestVulnerablePlayerToEntity(this, -1) != null) {
//                this.worldObj.getClosestVulnerablePlayerToEntity(this, -1).sendChatToPlayer(text2);
//            }
            BlockEndPortal.bossDefeated = true;
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void attackPlayer(CallbackInfo ci){
        attackTimer++;
        if(this.target instanceof EntityPlayer && attackTimer > (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 25 : 40) + rand.nextInt(20)){
            double var3 = target.posX - this.posX;
            double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
            double var7 = target.posZ - this.posZ;

            EntityLargeFireball var11 = new EntityLargeFireball(this.worldObj, this, var3, var5, var7);
            this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;

            float i = rand.nextFloat();
            if (this.rand.nextInt(5)==0) {
                if(i<0.2) {
                    EntitySkeleton minion = new EntitySkeleton(this.worldObj);
                    minion.setSkeletonType(1);
                    minion.entityToAttack = target;
                    minion.mountEntity(var11);
                    this.worldObj.spawnEntityInWorld(minion);
                } else if (i<0.25){
                    EntityCreeper minion = new EntityCreeper(this.worldObj);
                    minion.addPotionEffect(new PotionEffect(Potion.invisibility.id, 100000,0));
                    minion.entityToAttack = target;
                    minion.mountEntity(var11);
                    this.worldObj.spawnEntityInWorld(minion);
                } else if (i < 0.3){
                    DireWolfEntity minion = new DireWolfEntity(this.worldObj);
                    minion.entityToAttack = target;
                    minion.mountEntity(var11);
                    this.worldObj.spawnEntityInWorld(minion);
                } else if (i < 0.5){
                    EntityTNTPrimed minion = new EntityTNTPrimed(this.worldObj);
                    minion.fuse = 60;
                    minion.mountEntity(var11);
                    this.worldObj.spawnEntityInWorld(minion);
                } else if (i < 0.51){
                    EntityWitch minion = new EntityWitch(this.worldObj);
                    minion.entityToAttack = target;
                    minion.mountEntity(var11);
                    this.worldObj.spawnEntityInWorld(minion);
                }
                this.worldObj.spawnEntityInWorld(var11);
            }
            attackTimer = 0;
        }
    }
}
