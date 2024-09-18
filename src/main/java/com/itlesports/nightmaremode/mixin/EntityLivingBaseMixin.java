package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements EntityAccess{
    @Shadow public abstract boolean isEntityAlive();

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }
    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.85f))
    private float modifyWitherSkeletonSight(float constant){
        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
        if(thisObj instanceof EntitySkeleton skeleton && skeleton.getSkeletonType()==1){
            return 0.6f;
        } else{return 0.85f;}
    }
//      code is completely irrelevant since leaves are transparent
//    @Inject(method = "jump", at = @At("TAIL"))
//    private void breakLeafBlockBelowEntity(CallbackInfo ci){
//        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
//        if (!(thisObj instanceof EntityMob && this.worldObj != null) && !(thisObj instanceof EntityOcelot || thisObj instanceof EntityChicken)) {
//            MinecraftServer server = MinecraftServer.getServer();
//            for(double i = -0.3d; i <= 0.3d; i += 0.3d){
//                for(double j = -0.3d;j <= 0.3d;j += 0.3d) {
//                    assert this.worldObj != null;
//                    if (this.worldObj.getBlockId((int) Math.floor(this.posX + i), (int) Math.floor(this.posY - 2.5), (int) Math.floor(this.posZ + j)) == Block.leaves.blockID) {
//                        server.getEntityWorld().destroyBlock((int) Math.floor(this.posX + i), (int) Math.floor(this.posY - 2.5), (int) Math.floor(this.posZ + j), false);
//                    }
//                }
//            }
//            if (this.worldObj.getBlockId((int) Math.floor(this.posX), (int) Math.floor(this.posY - 3), (int) Math.floor(this.posZ)) == Block.leaves.blockID) {
//                server.getEntityWorld().destroyBlock((int) Math.floor(this.posX), (int) Math.floor(this.posY - 3), (int) Math.floor(this.posZ), false);
//            }
//        }
//    }
//
//
//    @Inject(method = "onUpdate", at = @At("HEAD"))
//    private void checkIfShouldBreakLeaves(CallbackInfo ci){
//        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
//        if (!(thisObj instanceof EntityMob)) {
//            if(this.isEntityAlive()){
//                if(this.worldObj.getBlockId((int) Math.floor(this.posX), (int) Math.ceil(this.posY - 1), (int) Math.floor(this.posZ)) == Block.leaves.blockID){
//
//                    int sprintModifier = 0;
//                    if(this.isSprinting()){sprintModifier = 14;}
//
//                    if (rand.nextInt((15-sprintModifier))==0) {
//                        MinecraftServer server = MinecraftServer.getServer();
//                        for(double i = -0.3d; i <= 0.3d; i += 0.3d){
//                            for(double j = -0.3d;j <= 0.3d;j += 0.3d) {
//                                if (this.worldObj.getBlockId((int) Math.floor(this.posX + i), (int) Math.floor(this.posY - 1), (int) Math.floor(this.posZ + j)) == Block.leaves.blockID) {
//                                    server.getEntityWorld().destroyBlock((int) Math.floor(this.posX + i), (int) Math.floor(this.posY - 1), (int) Math.floor(this.posZ + j), false);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}