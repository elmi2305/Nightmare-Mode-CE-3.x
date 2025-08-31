package com.itlesports.nightmaremode.achievements;

import btw.achievement.event.AchievementEventDispatcher;
import btw.achievement.event.BTWAchievementEvents;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public class NMAchievementEvents {
    public static class TimeEvent extends AchievementEventDispatcher.AchievementEvent<Long> {
        public TimeEvent() {
            super();
        }
    }

    public static class TimeItemEvent extends AchievementEventDispatcher.AchievementEvent<TimeItemEvent.Context> {
        public record Context(EntityPlayer player, long worldTime) {
        }

        public TimeItemEvent() {
            super();
        }
    }

    public static class MiscPlayerEvent extends AchievementEventDispatcher.AchievementEvent<EntityPlayer> {
        public MiscPlayerEvent() {
            super();
        }
    }

    public static class DamageSourceEvent extends AchievementEventDispatcher.AchievementEvent<DamageSource> {
        public DamageSourceEvent() {
            super();
        }
    }
    public static class DamageSourcePlayerEvent extends AchievementEventDispatcher.AchievementEvent<DamageSourcePlayerEvent.DamageSourceData> {
        public record DamageSourceData(EntityPlayer player, DamageSource src) {
        }
        public DamageSourcePlayerEvent() {
            super();
        }
    }
    public static class PlayerAttackEvent extends AchievementEventDispatcher.AchievementEvent<PlayerAttackEvent.PlayerAttackEventData> {
        public record PlayerAttackEventData(EntityPlayer player, Entity entityHit, float damage) {
        }
        public PlayerAttackEvent() {
            super();
        }
    }


    public static class PlayerEatenEvent extends AchievementEventDispatcher.AchievementEvent<PlayerEatenEvent.Context> {
        public record Context(EntityPlayer player, ItemStack stack) {
        }

        public PlayerEatenEvent() {
            super();
        }
    }
    public static class PlayerPoisonedEvent extends AchievementEventDispatcher.AchievementEvent<Boolean> {
        public PlayerPoisonedEvent() {
            super();
        }
    }
    public static class MobSnowballedByPlayerEvent extends AchievementEventDispatcher.AchievementEvent<Boolean> {
        public MobSnowballedByPlayerEvent() {
            super();
        }
    }
    public static class BlockBrokenEvent extends AchievementEventDispatcher.AchievementEvent<BlockBrokenEvent.BlockBrokenData> {
        public record BlockBrokenData(int blockID, int metadata) {
        }
        public BlockBrokenEvent() {
            super();
        }
    }
    public static class BloodMoonEvent extends AchievementEventDispatcher.AchievementEvent<Boolean> {

        public BloodMoonEvent() {
            super();
        }
    }

    public static class EclipseEvent extends AchievementEventDispatcher.AchievementEvent<Boolean> {

        public EclipseEvent() {
            super();
        }
    }
    public static class NightmareMerchantEvent extends AchievementEventDispatcher.AchievementEvent<BTWAchievementEvents.None> {

        public NightmareMerchantEvent() {
            super();
        }
    }
    public static class ArrowEnemyHitEvent extends AchievementEventDispatcher.AchievementEvent<Integer> {

        public ArrowEnemyHitEvent() {
            super();
        }
    }

    public static class ArrowDamageEvent extends AchievementEventDispatcher.AchievementEvent<Float> {

        public ArrowDamageEvent() {
            super();
        }
    }

    public static class ItemTradedEvent extends AchievementEventDispatcher.AchievementEvent<ItemTradedEvent.ItemTradedData> {
        public record ItemTradedData(int givenItem, int takenItem) {
        }
        public ItemTradedEvent() {
            super();
        }
        // unused
    }

    public static class LustEvent extends AchievementEventDispatcher.AchievementEvent<BTWAchievementEvents.None> {
        public LustEvent() {
            super();
        }
    }

    public static class PlayerSleepEvent extends AchievementEventDispatcher.AchievementEvent<Integer> {
        public PlayerSleepEvent() {
            super();
        }
    }


}