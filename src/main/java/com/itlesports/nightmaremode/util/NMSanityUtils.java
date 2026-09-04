package com.itlesports.nightmaremode.util;

import btw.entity.mob.BTWSquidEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.underworld.EntityRitualPortal;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.IUnderworldSanityArmor;
import com.itlesports.nightmaremode.underworld.biomes.*;
import net.minecraft.src.*;

import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.WeakHashMap;

import static btw.community.nightmaremode.NightmareMode.SANITY;
import static btw.community.nightmaremode.NightmareMode.SANITY_CAPACITY_LEVEL;

/** server-authoritative sanity calculations; values are remaining sanity points */
public final class NMSanityUtils {
    private NMSanityUtils() {}

    public static final double HEIGHT_REFERENCE_Y = 100.0;
    private static final double ENEMY_DETECTION_RADIUS = 16.0;
    private static final Map<EntityPlayer, LightCache> LIGHT_CACHE = new WeakHashMap<>();
    private static final Map<EntityPlayer, RecoveryWindow> RECOVERY_WINDOWS = new WeakHashMap<>();

    public static double getCapacity(EntityPlayer player) {
        int level = MathHelper.clamp_int(player.getData(SANITY_CAPACITY_LEVEL), 0, NMFields.MAX_SANITY_CAPACITY_LEVEL);
        return NMFields.MAX_SANITY + level * NMFields.SANITY_PER_CAPACITY_LEVEL;
    }

    public static double getPercent(EntityPlayer player) {
        return clamp(player.getData(SANITY), 0.0, getCapacity(player)) / getCapacity(player);
    }

    public static void set(EntityPlayer player, double value) {
        player.setData(SANITY, clamp(value, 0.0, getCapacity(player)));
    }

    public static void restore(EntityPlayer player, double points) {
        if (points > 0.0) set(player, player.getData(SANITY) + points);
    }

    public static void drain(EntityPlayer player, double points) {
        if (points > 0.0) set(player, player.getData(SANITY) - points);
    }

    /** Returns points drained per tick. */
    public static double getSanityDrainPerTick(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.dimension != NMFields.UNDERWORLD_DIMENSION) return 0.0;

        Protection protection = getProtection(player);
        double darkness = getLightPressure(player);
        if (protection == Protection.SOUL_LANTERN) darkness = 0.0;
        else if (protection == Protection.SOUL_TORCH) darkness *= 0.25;
        else if (isSaferBiome(player)) darkness *= 0.75;
        else darkness = 2.0;

        double environment = getHeightPressure(player) + getBiomePressure(player);
        if (protection == Protection.SOUL_LANTERN) environment *= 0.5;

        double pressure = darkness + environment + getNearbyEnemyPressure(player);
        return pressure * (1.0 - getArmorPressureReduction(player)) / 20.0;
    }

    public static double getLightPressure(EntityPlayer player) {
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY + player.getEyeHeight());
        int z = MathHelper.floor_double(player.posZ);
        int light = player.worldObj.getBlockLightValue(x, y, z);
        return clamp((7.0 - light) / 7.0, 0.0, 1.0) * 2.0;
    }

    public static double getHeightPressure(EntityPlayer player) {
        return clamp((HEIGHT_REFERENCE_Y - player.posY) / 52.0, 0.0, 1.0) * 2.0;
    }

    public static double getBiomePressure(EntityPlayer player) {
        BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));
        if (biome instanceof BiomeGenFlowerFields) return 3.5;
        if (biome instanceof BiomeGenBlightlands) return 0.5;
        if (biome instanceof BiomeGenHighlands) return 1.0;
        if (biome instanceof BiomeGenUnderHell) return 2.0;
        if (biome instanceof BiomeGenShadowRealm) return 3.0;
        return 1.0;
    }

    public static double getNearbyEnemyPressure(EntityPlayer player) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                player.posX - ENEMY_DETECTION_RADIUS, player.posY - ENEMY_DETECTION_RADIUS, player.posZ - ENEMY_DETECTION_RADIUS,
                player.posX + ENEMY_DETECTION_RADIUS, player.posY + ENEMY_DETECTION_RADIUS, player.posZ + ENEMY_DETECTION_RADIUS);
        List<?> entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
        double pressure = 0.0;
        for (Object value : entities) {
            if (!(value instanceof EntityLivingBase mob) || mob == player || mob.isDead || !isHostileMob(mob)) continue;
            pressure += clamp(1.0 - player.getDistanceToEntity(mob) / ENEMY_DETECTION_RADIUS, 0.15, 1.0);
        }
        return Math.min(3.0, pressure);
    }

    public static String getDebugSummary(EntityPlayer player) {
        return String.format(Locale.ROOT,
                "[Underworld/Sanity] %s remaining=%.1f/%.1f drain=%.3f/tick light=%.2f depth=%.2f biome=%.2f hostiles=%.2f protection=%s armor=%.0f%%",
                player.username, player.getData(SANITY), getCapacity(player), getSanityDrainPerTick(player),
                getLightPressure(player), getHeightPressure(player), getBiomePressure(player),
                getNearbyEnemyPressure(player), getProtection(player).name(), getArmorPressureReduction(player) * 100.0D);
    }

    public static void restoreForKill(EntityPlayer player, EntityLivingBase victim) {
        if (player.dimension != NMFields.UNDERWORLD_DIMENSION || player.worldObj.isRemote) return;
        boolean boss = victim instanceof IBossDisplayData;
        double amount = boss ? 100.0 : (isElite(victim) ? 25.0 : 10.0);
        if (!boss) {
            long now = player.worldObj.getTotalWorldTime();
            RecoveryWindow window = RECOVERY_WINDOWS.computeIfAbsent(player, ignored -> new RecoveryWindow(now));
            if (now - window.startedAt >= 1200L) {
                window.startedAt = now;
                window.points = 0.0;
            }
            amount = Math.min(amount, 100.0 - window.points);
            if (amount <= 0.0) return;
            window.points += amount;
        }
        restore(player, amount);
    }

    private static boolean isElite(EntityLivingBase victim) {
        String name = victim.getClass().getName();
        return name.startsWith("com.itlesports.nightmaremode.entity") || name.contains("Wither") || name.contains("Enderman");
    }

    public static double getArmorPressureReduction(EntityPlayer player) {
        double reduction = 0.0;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof IUnderworldSanityArmor armor) {
                reduction += armor.getSanityPressureReduction();
            }
        }
        return Math.min(0.40D, reduction);
    }

    private static boolean isSaferBiome(EntityPlayer player) {
        BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));
        return biome instanceof BiomeGenBlightlands || biome instanceof BiomeGenHighlands;
    }

    private static Protection getProtection(EntityPlayer player) {
        LightCache cache = LIGHT_CACHE.get(player);
        if (cache != null && player.ticksExisted - cache.tick < 20) return cache.protection;
        Protection result = scanFor(player, NMBlocks.soulLantern, 12) ? Protection.SOUL_LANTERN
                : scanFor(player, NMBlocks.soulTorch, 8) ? Protection.SOUL_TORCH : Protection.NONE;
        LIGHT_CACHE.put(player, new LightCache(player.ticksExisted, result));
        return result;
    }

    private static boolean scanFor(EntityPlayer player, Block target, int radius) {
        if (target == null) return false;
        int px = MathHelper.floor_double(player.posX);
        int py = MathHelper.floor_double(player.posY);
        int pz = MathHelper.floor_double(player.posZ);
        int vertical = Math.min(radius, 6);
        for (int x = px - radius; x <= px + radius; x++) {
            for (int y = py - vertical; y <= py + vertical; y++) {
                for (int z = pz - radius; z <= pz + radius; z++) {
                    if ((x - px) * (x - px) + (y - py) * (y - py) + (z - pz) * (z - pz) <= radius * radius
                            && player.worldObj.getBlockId(x, y, z) == target.blockID) return true;
                }
            }
        }
        return false;
    }

    public static boolean isHostileMob(EntityLivingBase mob) {
        if (mob instanceof EntityRitualPortal) return false;
        if (mob instanceof IMob || mob instanceof EntityMob || mob instanceof BTWSquidEntity) return true;
        return mob instanceof EntityWolf wolf && wolf.isAngry();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private enum Protection { NONE, SOUL_TORCH, SOUL_LANTERN }
    private record LightCache(int tick, Protection protection) {}
    private static final class RecoveryWindow {
        private long startedAt;
        private double points;
        private RecoveryWindow(long startedAt) { this.startedAt = startedAt; }
    }
}
