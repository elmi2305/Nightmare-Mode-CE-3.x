package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.stream.Stream;

@Mixin(EntityDragon.class)
public abstract class EntityDragonMixin extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob {
    @Shadow
    public int deathTicks;

    public EntityDragonMixin(World par1World) {
        super(par1World);
    }
    /**
     * @author elmi
     * @reason yeah, so I just need to change this code to add logic that detects whether both the dragons were killed
     * or just one. since I don't want two portals to spawn. instead I want the portal to spawn after both dragons are killed
     */
    @Overwrite
    public void onDeathUpdate() {
        int dragonKills = 0;
        ++this.deathTicks;
        if (this.deathTicks >= 180 && this.deathTicks <= 200) {
            float var1 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float var2 = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float var3 = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle("hugeexplosion", this.posX + (double)var1, this.posY + 2.0 + (double)var2, this.posZ + (double)var3, 0.0, 0.0, 0.0);
        }

        int var4;
        int var5;
        if (!this.worldObj.isRemote) {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0) {
                var4 = 1000;

                while(var4 > 0) {
                    var5 = EntityXPOrb.getXPSplit(var4);
                    var4 -= var5;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var5));
                }
            }

            if (this.deathTicks == 1) {
                this.worldObj.func_82739_e(1018, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            }
        }

        this.moveEntity(0.0, 0.10000000149011612, 0.0);
        this.renderYawOffset = this.rotationYaw += 20.0F;
        if (this.deathTicks == 200 && !this.worldObj.isRemote) {
            var4 = 2000;

            while(var4 > 0) {
                var5 = EntityXPOrb.getXPSplit(var4);
                var4 -= var5;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var5));
            }
            int count = 0;
            for(Object entity : this.worldObj.loadedEntityList) {
                if (entity instanceof EntityDragon) {
                    count++;
                }
            }
//            for(Object entity : this.worldObj.unloadedEntityList) {
//                if (entity instanceof EntityDragon) {
//                    count++;
//                }
//            }
//            if(this.worldObj.loadedEntityList.contains()){
//                System.out.println("it contains a dragon");
//            } else{
//                System.out.println("apparently not");
//            }
            createEnderPortal(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
            this.setDead();
        }
    }


    @Unique
    private void createEnderPortal(int par1, int par2) {
        byte var3 = 64;
        BlockEndPortal.bossDefeated = true;
        byte var4 = 4;

        for(int var5 = var3 - 1; var5 <= var3 + 32; ++var5) {
            for(int var6 = par1 - var4; var6 <= par1 + var4; ++var6) {
                for(int var7 = par2 - var4; var7 <= par2 + var4; ++var7) {
                    double var8 = var6 - par1;
                    double var10 = var7 - par2;
                    double var12 = var8 * var8 + var10 * var10;
                    if (var12 <= ((double)var4 - 0.5) * ((double)var4 - 0.5)) {
                        if (var5 < var3) {
                            if (var12 <= ((double)(var4 - 1) - 0.5) * ((double)(var4 - 1) - 0.5)) {
                                this.worldObj.setBlock(var6, var5, var7, Block.bedrock.blockID);
                            }
                        } else if (var5 > var3) {
                            this.worldObj.setBlock(var6, var5, var7, 0);
                        } else if (var12 > ((double)(var4 - 1) - 0.5) * ((double)(var4 - 1) - 0.5)) {
                            this.worldObj.setBlock(var6, var5, var7, Block.bedrock.blockID);
                        } else {
                            this.worldObj.setBlock(var6, var5, var7, Block.endPortal.blockID);
                        }
                    }
                }
            }
        }
        this.worldObj.setBlock(par1, var3 + 0, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1, var3 + 1, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1, var3 + 2, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1 - 1, var3 + 2, par2, Block.torchWood.blockID);
        this.worldObj.setBlock(par1 + 1, var3 + 2, par2, Block.torchWood.blockID);
        this.worldObj.setBlock(par1, var3 + 2, par2 - 1, Block.torchWood.blockID);
        this.worldObj.setBlock(par1, var3 + 2, par2 + 1, Block.torchWood.blockID);
        this.worldObj.setBlock(par1, var3 + 3, par2, Block.bedrock.blockID);
        this.worldObj.setBlock(par1, var3 + 4, par2, Block.dragonEgg.blockID);
        BlockEndPortal.bossDefeated = false;
    }
}
