package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;

import java.util.List;

import static btw.community.nightmaremode.NightmareMode.SANITY;

public final class NMSanityUtils {
    private NMSanityUtils() {}

    public static final double LIGHT_DRAIN_MULTIPLIER   = 0.02;
    public static final double HEIGHT_DRAIN_MULTIPLIER  = 0.012;
    public static final double BIOME_DRAIN_MULTIPLIER   = 0.02;
    public static final double ENEMY_DRAIN_MULTIPLIER   = 0.015;

    public static final double HEIGHT_REFERENCE_Y       = 100;

    public static final double ENEMY_DETECTION_RADIUS   = 16.0;
    public static final double ENEMY_HEALTH_WEIGHT      = 0.20;



    public static final double MAX_SANITY = 2000.0;
    public static final double CRITICAL_SANITY = 1500.0;

    public static double getSanityDrainPerTick(EntityPlayer player) {
        if (player == null) return 0.0;
        if (player.worldObj == null) return 0.0;

        double drain = 0.0;

        drain += getLightDrain(player);
        drain += getHeightDrain(player);
        drain += getBiomeDrain(player);
        drain += getNearbyEnemyDrain(player);
        if(Keyboard.isKeyDown(Keyboard.KEY_O)){
            drain += 10;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_P)){
            return -10;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_I)){
            player.setData(SANITY, 0d);
            return 0;
        }


        if (player.ticksExisted % 40 == 0 && player.isSneaking()) {
            System.out.println("sanity drain from LIGHT: " + getLightDrain(player));
            System.out.println("sanity drain from HEIGHT: " + getHeightDrain(player));
            System.out.println("sanity drain from BLIGHT: " + getBiomeDrain(player));
            System.out.println("sanity drain from FRIGHT: " + getNearbyEnemyDrain(player));
            System.out.println(" ");
        }

        return Math.max(0.0, drain);
    }



    public static double getLightDrain(EntityPlayer player) {
        World world = player.worldObj;

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY);
        int z = MathHelper.floor_double(player.posZ);

        float brightness = world.getLightBrightness(x, y, z); // think this is only skylight
        double darkness = 1.0 - brightness; // 0.0 to 1.0

        return darkness * LIGHT_DRAIN_MULTIPLIER;
    }

    public static double getHeightDrain(EntityPlayer player) {
        double below = Math.max(0.0, (HEIGHT_REFERENCE_Y - player.posY) / HEIGHT_REFERENCE_Y); // 0..1
        return below * HEIGHT_DRAIN_MULTIPLIER;
    }

    public static double getBiomeDrain(EntityPlayer player) {
        World world = player.worldObj;

        int bx = MathHelper.floor_double(player.posX);
        int bz = MathHelper.floor_double(player.posZ);

        if(player.dimension == NightmareMode.UNDERWORLD_DIMENSION) {
            BiomeGenBase tempBiome = world.getBiomeGenForCoords(bx, bz);
            if (tempBiome == null) return 0.0;

            if(!(tempBiome instanceof BiomeGenUnderworld biome)) return 0.0;

            double factor = biome.getDrainMultiplier();
            return (factor - 1.0) * BIOME_DRAIN_MULTIPLIER;
        }
        return 0d;
    }

    public static double getNearbyEnemyDrain(EntityPlayer player) {
        World world = player.worldObj;

        double radius = ENEMY_DETECTION_RADIUS;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                player.posX - radius, player.posY - radius, player.posZ - radius,
                player.posX + radius, player.posY + radius, player.posZ + radius);

        List list = world.getEntitiesWithinAABB(Entity.class, box);

        double sum = 0.0;

        for (Object o : list) {
            if (!(o instanceof EntityLivingBase)) continue;

            EntityLivingBase mob = (EntityLivingBase) o;
            if (mob == player) continue;
            if (mob.isDead) continue;

            if (!isHostileMob(mob)) continue;

            double dist = player.getDistanceToEntity(mob);
            if (dist >= radius) continue;

            double mobContribution = getMobContribution(radius, dist, mob);

            sum += mobContribution;
        }

        return sum * ENEMY_DRAIN_MULTIPLIER;
    }

    private static double getMobContribution(double radius, double dist, EntityLivingBase mob) {
        double proximity = (radius - dist) / radius;

        // low impact; mostly just makes big mobs a bit scarier
        double healthFactor = 1.0;
        float maxHp = mob.getMaxHealth();
        if (maxHp > 0.0F) {
            healthFactor = mob.getHealth() / maxHp; // 0 - 1
        }

        // this makes an Enderman (20hp) about ~20% scarier than a Zombie (20hp too)
        double mobContribution = proximity * (1.0 + ENEMY_HEALTH_WEIGHT * healthFactor);

        return mobContribution;
    }

    public static boolean isHostileMob(EntityLivingBase mob) {
        if (mob instanceof IMob) return true;

        if (mob instanceof EntityMob) return true;

        if (mob instanceof BTWSquidEntity) return true;

        // optional: treat angry wolves as hostile
        if (mob instanceof EntityWolf) {
            return ((EntityWolf) mob).isAngry();
        }

        return false;
    }
}
