package com.itlesports.nightmaremode.item.items;

import btw.entity.SpiderWebEntity;
import com.itlesports.nightmaremode.NightmareFlameEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

public class ItemFlamethrower extends Item {
    public ItemFlamethrower(int par1) {
        super(par1);
        this.maxStackSize = 1;
        this.setMaxDamage(2000);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setBuoyant();
        this.setUnlocalizedName("nmFlamethrower");
    }
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        world.playSoundAtEntity(player, "fire.fire", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        for (int i = -10; i < 10; i += 2) {
            for (int j = -10; j < 10; j += 2) {
//                NightmareFlameEntity var11 = new NightmareFlameEntity(world);
//                var11.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, (player.rotationYaw + i), (player.rotationPitch + j));
//                var11.posX -= (double)(MathHelper.cos((var11.rotationYaw + i + (float)player.rand.nextGaussian()) / 180.0f * (float)Math.PI) * 0.16f);
//                var11.posY -= (double)0.1f;
//                var11.posZ -= (double)(MathHelper.sin((var11.rotationYaw + i + (float)player.rand.nextGaussian()) / 180.0f * (float)Math.PI) * 0.16f);
//                var11.setPosition(var11.posX, var11.posY, var11.posZ);
//                var11.yOffset = 0.0f;
//                float var3 = 0.4f;
//                var11.motionX = -MathHelper.sin((var11.rotationYaw + i + (float)player.rand.nextGaussian()) / 180.0f * (float)Math.PI) * MathHelper.cos((var11.rotationPitch + j + (float)player.rand.nextGaussian()) / 180.0f * (float)Math.PI) * var3;
//                var11.motionZ = MathHelper.cos((var11.rotationYaw + i + (float)player.rand.nextGaussian()) / 180.0f * (float)Math.PI) * MathHelper.cos((var11.rotationPitch + j + (float)player.rand.nextGaussian()) / 180.0f * (float)Math.PI) * var3;
//                var11.motionY = -MathHelper.sin((var11.rotationPitch+j + (float)player.rand.nextGaussian()) / 180.0f * (float)Math.PI) * var3;
//
//                this.setThrowableHeading(var11.motionX, var11.motionY, var11.motionZ, 1.5f, 1.0f,var11.rand,var11);
//
//                if (world.isRemote) {
//                    world.spawnEntityInWorld(var11);
//                }
//                world.spawnEntityInWorld(new SpiderWebEntity(world, (EntityLiving)player));

            }
        }
        return stack;
    }



    @Unique
    private void setThrowableHeading(double xDirection, double yDirection, double zDirection, float velocity, float accuracy, Random rand, EntityThrowable entity) {
        // Calculate initial direction magnitude
        float directionMagnitude = MathHelper.sqrt_double(xDirection * xDirection + yDirection * yDirection + zDirection * zDirection);

        // Normalize direction
        xDirection /= (double) directionMagnitude;
        yDirection /= (double) directionMagnitude;
        zDirection /= (double) directionMagnitude;

        // Apply random inaccuracy based on accuracy parameter
        xDirection += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.0075 * accuracy;
        yDirection += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.0075 * accuracy;
        zDirection += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.0075 * accuracy;

        // Apply velocity to direction components and set entity motion
        entity.motionX = xDirection *= (double) velocity;
        entity.motionY = yDirection *= (double) velocity;
        entity.motionZ = zDirection *= (double) velocity;

        // Calculate rotation for the entity based on its direction
        float horizontalMagnitude = MathHelper.sqrt_double(xDirection * xDirection + zDirection * zDirection);
        entity.prevRotationYaw = entity.rotationYaw = (float) (Math.atan2(xDirection, zDirection) * 180.0 / Math.PI);
        entity.prevRotationPitch = entity.rotationPitch = (float) (Math.atan2(yDirection, horizontalMagnitude) * 180.0 / Math.PI);

        // Reset ticks in ground
        entity.ticksInGround = 0;
    }
}
