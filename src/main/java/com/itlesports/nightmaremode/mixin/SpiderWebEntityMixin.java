package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.entity.SpiderWebEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SpiderWebEntity.class)
public class SpiderWebEntityMixin {
    @Unique boolean slimeShooter = false;
    @Unique int amplifier = 0;
    @Inject(method = "<init>(Lnet/minecraft/src/World;Lnet/minecraft/src/EntityLiving;Lnet/minecraft/src/Entity;)V", at =@At("TAIL"))
    private void checkIfSlimeShooter(World world, EntityLiving throwingEntity, Entity targetEntity, CallbackInfo ci){
        slimeShooter = throwingEntity instanceof EntitySlime;
    }

    @Inject(method = "onImpact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/Entity;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void applySlownessOnHit(MovingObjectPosition impactPos, CallbackInfo ci, Entity entityHit){
        if(entityHit instanceof EntityPlayer player){
            if(slimeShooter){amplifier=1;}
            player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,(amplifier+1)*(20 + NightmareUtils.getWorldProgress(player.worldObj)*20),amplifier));
        }
    }

    @Redirect(method = "onImpact", at = @At(value = "INVOKE", target = "Lbtw/entity/SpiderWebEntity;attemptToPlaceWebInBlock(III)Z"))
    private boolean doNothingIfSlimeShooter(SpiderWebEntity instance, int x, int y, int z){
        SpiderWebEntity thisObj = (SpiderWebEntity)(Object)this;
        if (this.canWebReplaceBlock(x, y, z) && !slimeShooter) {
            thisObj.worldObj.setBlockWithNotify(x, y, z, BTWBlocks.web.blockID);
            return true;
        }
        return false;
    }
//    @Inject(method = "onImpact", at = @At(value = "TAIL"),locals = LocalCapture.CAPTURE_FAILHARD)
//    private void doBadThingsIfEclipse(MovingObjectPosition impactPos, CallbackInfo ci, Entity entityHit, BlockPos targetPos, int var4, int var5){
//        if(NightmareUtils.getIsEclipse() && entityHit != null){
//            entityHit.worldObj.setBlockMetadataWithNotify((int) entityHit.posX, (int) entityHit.posY, (int) entityHit.posZ, Block.lavaMoving.blockID,2);
//        }
//    }
//    @Redirect(method = "attemptToPlaceWebInBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockWithNotify(IIII)Z"))
//    private boolean placeLavaOnEclipse(World world, int x, int y, int z, int iBlockID){
//        System.out.println(x);
//        System.out.println(y);
//        System.out.println(z);
//        if(NightmareUtils.getIsEclipse()){
//            System.out.println("hi");
//            return world.setBlockAndMetadataWithNotify(x,y,z, Block.lavaMoving.blockID,2);
//        } else{
//            return world.setBlock(x, y, z, iBlockID, 0, 3);
//        }
//    }

    @Unique
    private boolean canWebReplaceBlock(int i, int j, int k) {
        SpiderWebEntity thisObj = (SpiderWebEntity)(Object)this;
        int iBlockID = thisObj.worldObj.getBlockId(i, j, k);
        Block block = Block.blocksList[iBlockID];
        return block == null || block.canSpitWebReplaceBlock(thisObj.worldObj, i, j, k);
    }



    @Redirect(method = "onImpact", at = @At(value = "INVOKE", target = "Lbtw/entity/SpiderWebEntity;spawnTangledWebItem(III)V"))
    private void notSpawnWebItemIfSlime(SpiderWebEntity instance, int x, int y, int z){
        SpiderWebEntity thisObj = (SpiderWebEntity)(Object)this;
        if(!slimeShooter){
            float f1 = 0.7f;
            double d = (double)(thisObj.worldObj.rand.nextFloat() * f1) + (double)(1.0f - f1) * 0.5;
            double d1 = (double)(thisObj.worldObj.rand.nextFloat() * f1) + (double)(1.0f - f1) * 0.5;
            double d2 = (double)(thisObj.worldObj.rand.nextFloat() * f1) + (double)(1.0f - f1) * 0.5;
            EntityItem entityitem = new EntityItem(thisObj.worldObj, (double)x + d, (double)y + d1, (double)z + d2, new ItemStack(BTWItems.tangledWeb));
            entityitem.delayBeforeCanPickup = 10;
            thisObj.worldObj.spawnEntityInWorld(entityitem);
        }
    }
}
