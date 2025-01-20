package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.DireWolfEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.EntityShadowZombie;
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

import java.util.List;

@Mixin(EntityDragon.class)
public abstract class EntityDragonMixin extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
    @Shadow private void createEnderPortal(int par1, int par2) {}
    @Shadow private Entity target;
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
            ((EntityEnderman)tempEntity).setDead();
        }
    }
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

    @ModifyArg(method = "func_82195_e", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLiving;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"),index = 1)
    private float manageDamageCap(float par2){
        if(par2 > 20){
            return 20;
        }
        return par2;
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void attackPlayer(CallbackInfo ci){
        this.attackTimer++;
        if(this.attackTimer % 4 == 0){
            EntityPlayer target = this.worldObj.getClosestVulnerablePlayer(this.posX,this.posY,this.posZ,50);
            if (target != null) {
                this.setAttackTarget(target);
            }
        }
        if(this.target instanceof EntityPlayer && this.attackTimer > (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 15 : 25)){
            double var3 = this.target.posX - this.posX;
            double var5 = this.target.boundingBox.minY + (double) (this.target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
            double var7 = this.target.posZ - this.posZ;

            EntityLargeFireball var11 = new EntityLargeFireball(this.worldObj, this, var3, var5, var7);
            this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;

            float i = rand.nextFloat();
            if(i<0.3) {
                EntitySkeleton minion = new EntitySkeleton(this.worldObj);
                minion.setSkeletonType(1);
                minion.entityToAttack = this.target;
                minion.setCurrentItemOrArmor(0,new ItemStack(Item.swordStone));
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i<0.45){
                EntityCreeper minion = new EntityCreeper(this.worldObj);
                minion.addPotionEffect(new PotionEffect(Potion.invisibility.id, 100000,0));
                minion.entityToAttack = this.target;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i < 0.55){
                DireWolfEntity minion = new DireWolfEntity(this.worldObj);
                minion.entityToAttack = this.target;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i < 0.65){
                EntityTNTPrimed minion = new EntityTNTPrimed(this.worldObj);
                minion.fuse = 60;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            } else if (i < 0.68){
                EntityWitch minion = new EntityWitch(this.worldObj);
                minion.entityToAttack = this.target;
                minion.mountEntity(var11);
                this.worldObj.spawnEntityInWorld(minion);
            }
            this.worldObj.spawnEntityInWorld(var11);

            this.attackTimer = 0;
        }
    }

    @Redirect(method = "destroyBlocksInAABB", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockToAir(III)Z"))
    private boolean avoidDestroyingArenaBlocks(World world, int par1, int par2, int par3){
        if(world.getBlockId(par1,par2,par3) == NMBlocks.specialObsidian.blockID || world.getBlockId(par1,par2,par3) == NMBlocks.cryingObsidian.blockID){
            return false;
        }
        return world.setBlockToAir(par1,par2,par3);
    }
}
