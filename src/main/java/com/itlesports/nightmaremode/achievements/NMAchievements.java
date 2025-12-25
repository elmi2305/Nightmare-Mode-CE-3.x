package com.itlesports.nightmaremode.achievements;

import api.achievement.AchievementEvents;
import api.achievement.AchievementHandler;
import api.achievement.AchievementProvider;
import api.world.WorldUtils;
import btw.block.BTWBlocks;
import btw.block.tileentity.beacon.BTWBeaconEffects;
import btw.item.BTWItems;
import btw.item.items.ClubItem;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.*;
import java.util.function.Predicate;

import static btw.achievement.BTWAchievements.*;

public class NMAchievements {

    public static final Achievement<Long> MORNING_SECOND_DAY =
            AchievementProvider.getBuilder(NMAchievementEvents.TimeEvent.class)
                    .name(loc("morningSecondDay"))
                    .icon(Item.bed)
                    .displayLocation(-2,0)
                    .triggerCondition(time -> {
                        long day = time / 24000L;
                        int timeOfDay = (int) (time % 24000L);
                        return day >= 1 && timeOfDay < 1000;
                    })
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<NMAchievementEvents.TimeItemEvent.Context> CRAFT_OVEN_FAST =
            AchievementProvider.getBuilder(NMAchievementEvents.TimeItemEvent.class)
                    .name(loc("sludge"))
                    .icon(Item.clay)
                    .displayLocation(-1, -1)
                    .triggerCondition(ctx ->
                            ctx.player().inventory.hasItem(BTWBlocks.idleOven.blockID) && ctx.worldTime() < 24000)
                    .parents(MORNING_SECOND_DAY)
                    .build()
                    .setSecret()
                    .registerAchievement(TAB_GETTING_STARTED);

    public static final Achievement<ItemStack> CRAFT_BASKET_NM =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(btwLoc("craft_basket"))
                    .icon(BTWBlocks.wickerBasket)
                    .displayLocation(1, -5)
                    .triggerCondition(itemStack -> itemStack.itemID == NMBlocks.customWickerBasket.blockID)
                    .parents(CRAFT_WICKER)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);

    public static final Achievement<DamageSource> HIT_BURNING_SKELETON =
            AchievementProvider.getBuilder(NMAchievementEvents.DamageSourceEvent.class)
                    .name(loc("hitBurningSkeleton"))
                    .icon(Item.arrow)
                    .displayLocation(-1, 1)
                    .triggerCondition(src -> src.getSourceOfDamage() instanceof EntityBurningArrow)
                    .parents(MORNING_SECOND_DAY)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> KILL_ANIMAL =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("killRunningAnimal"))
                    .icon(Item.porkRaw)
                    .displayLocation(2, -1)
                    .triggerCondition(
                            data -> data.heldItem().isPresent() && (data.entity() instanceof EntityAnimal
                                    && data.heldItem().get().getItem() instanceof ClubItem)
                    )
                    .parents(CRAFT_WOODEN_CLUB)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<EntityPlayer> GLUTTONY =
            AchievementProvider.getBuilder(NMAchievementEvents.MiscPlayerEvent.class)
                    .name(loc("gluttony"))
                    .icon(BTWBlocks.rottenFleshBlock)
                    .displayLocation(11, -6)
                    .triggerCondition(p -> p.getFoodStats().getSaturationLevel() >= 20f)
                    .build()
                    .setSecret()
                    .setSpecial()
                    .registerAchievement(TAB_GETTING_STARTED);

    public static final Achievement<ItemStack> CRAFT_IRON_NEEDLES =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("ironNeedles"))
                    .icon(NMItems.ironKnittingNeedles)
                    .displayLocation(12, 1)
                    .triggerCondition(itemStack -> itemStack.itemID == NMItems.ironKnittingNeedles.itemID)
                    .parents(SMELT_IRON)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<ItemStack> CRAFT_BANDAGE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bandage"))
                    .icon(NMItems.bandage)
                    .displayLocation(7, -2)
                    .triggerCondition(itemStack -> itemStack.itemID == NMItems.bandage.itemID)
                    .parents(FIND_STRING)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<AchievementEvents.None> MAKE_SKY_BASE =
            AchievementProvider.getBuilder(NMAchievementEvents.SkybaseScoreEvent.class)
                    .name(loc("skyBase"))
                    .icon(Block.planks)
                    .displayLocation(9, 1)
                    .alwaysTrigger()
                    .parents(FIND_LOGS)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<Boolean> EAT_RAW_FOOD =
            AchievementProvider.getBuilder(NMAchievementEvents.PlayerPoisonedEvent.class)
                    .name(loc("eatRawFood"))
                    .icon(Item.rottenFlesh)
                    .displayLocation(2, -2)
                    .triggerCondition(b -> !b)
                    .parents(KILL_ANIMAL)
                    .build()
                    .setHidden()
                    .registerAchievement(TAB_GETTING_STARTED);

    public static final Achievement<Boolean> SNOWBALL_MOB =
            AchievementProvider.getBuilder(NMAchievementEvents.MobSnowballedByPlayerEvent.class)
                    .name(loc("snowballMob"))
                    .icon(Item.snowball)
                    .displayLocation(-1, -2)
                    .triggerCondition(b -> !b)
                    .parents(MORNING_SECOND_DAY)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<Boolean> SNOWBALL_MOB_KILL =
            AchievementProvider.getBuilder(NMAchievementEvents.MobSnowballedByPlayerEvent.class)
                    .name(loc("snowballMobKill"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_SNOWBALL)
                    .displayLocation(0, -2)
                    .triggerCondition(Boolean::booleanValue)
                    .parents(SNOWBALL_MOB)
                    .build()
                    .setHidden()
                    .setSpecial()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<ItemStack> KILL_SQUID =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("calamariGet"))
                    .icon(NMItems.calamari)
                    .displayLocation(9, -1)
                    .triggerCondition(stack -> stack.itemID == NMItems.calamari.itemID)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<ItemStack> COOK_SQUID =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("calamariCook"))
                    .icon(NMItems.calamariRoast)
                    .displayLocation(11, -1)
                    .triggerCondition(stack -> stack.itemID == NMItems.calamariRoast.itemID)
                    .parents(CRAFT_OVEN, KILL_SQUID)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> KILL_BLOODZOMBIE =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("bloodZombieKill"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE)
                    .displayLocation(1, -2)
                    .triggerCondition(data -> data.entity() instanceof EntityBloodZombie)
                    .parents(CRAFT_WOODEN_CLUB)
                    .build()
                    .setSecret()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> KILL_WITHER_STRATA_3 =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("witherSkeletonKillOverworld"))
                    .icon(Item.coal)
                    .displayLocation(16, -1)
                    .triggerCondition(data -> data.entity() instanceof EntitySkeleton skeleton
                            && skeleton.getSkeletonType().id() == 1
                            && skeleton.dimension == 0)
                    .parents(CRAFT_STONE_PICKAXE)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);

    public static final Achievement<NMAchievementEvents.BlockBrokenEvent.BlockBrokenData> MINE_LAVA_PILLOW =
            AchievementProvider.getBuilder(NMAchievementEvents.BlockBrokenEvent.class)
                    .name(loc("breakLavaPillow"))
                    .icon(BTWBlocks.lavaPillow)
                    .displayLocation(14, -1)
                    .triggerCondition(data -> data.blockID() == BTWBlocks.lavaPillow.blockID)
                    .parents(CRAFT_STONE_PICKAXE)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<EntityPlayer> GREED =
            AchievementProvider.getBuilder(NMAchievementEvents.MiscPlayerEvent.class)
                    .name(loc("greed"))
                    .icon(Block.blockGold)
                    .displayLocation(8, 5)
                    .triggerCondition(NMAchievements::isPlayerGreeding)
                    .build()
                    .setSpecial()
                    .setSecret()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> IRON_ZOMBIE =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("ironZombie"))
                    .icon(Item.legsIron)
                    .displayLocation(9, -3)
                    .triggerCondition(data -> hasIronItem(data.entity()))
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<ItemStack> CRAFT_LADDER =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("ladderWood"))
                    .icon(Block.ladder)
                    .displayLocation(5, -1)
                    .triggerCondition(data -> data.itemID == Block.ladder.blockID)
                    .parents(FIND_STRING)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<ItemStack> CRAFT_ROAD =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("roadStandard"))
                    .icon(NMBlocks.blockRoad)
                    .displayLocation(15, -2)
                    .triggerCondition(data -> data.itemID == NMBlocks.blockRoad.blockID)
                    .parents(CRAFT_STONE_PICKAXE)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<ItemStack> CRAFT_STONE_LADDER =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("ladderStone"))
                    .icon(NMBlocks.stoneLadder)
                    .displayLocation(16, 2)
                    .triggerCondition(data -> data.itemID == NMBlocks.stoneLadder.blockID)
                    .parents(CRAFT_STONE_PICKAXE)
                    .build()
                    .registerAchievement(TAB_GETTING_STARTED);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> KILL_WANDERING_TRADER =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("wanderingTraderKill"))
                    .icon(BTWItems.tannedLeather)
                    .displayLocation(7,-5)
                    .triggerCondition(data -> data.entity() instanceof EntityZombieImposter)
                    .build()
                    .setHidden()
                    .registerAchievement(TAB_GETTING_STARTED);






    // IRON AGE
    public static final Achievement<ItemStack> CRAFT_PLANTER =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("craftPlanter"))
                    .icon(BTWBlocks.planter)
                    .displayLocation(3, 0)
                    .triggerCondition(data -> data.itemID == BTWBlocks.planter.blockID)
                    .parents(FIND_HEMP_SEEDS)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> GET_FRIED_CALAMARI =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("calamariFried"))
                    .icon(NMItems.friedCalamari)
                    .displayLocation(4, 3)
                    .triggerCondition(data -> data.itemID == NMItems.friedCalamari.itemID)
                    .parents(CRAFT_CAULDRON)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> GET_IRON_SWORD =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("ironSword"))
                    .icon(Item.swordIron)
                    .displayLocation(1, -4)
                    .triggerCondition(data -> data.itemID == Item.swordIron.itemID)
                    .parents(IRON_AGE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_IRON_LADDER =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("ladderIron"))
                    .icon(NMBlocks.ironLadder)
                    .displayLocation(1, 3)
                    .triggerCondition(data -> data.itemID == NMBlocks.ironLadder.blockID)
                    .parents(IRON_AGE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<EntityPlayer> HARDMODE =
            AchievementProvider.getBuilder(NMAchievementEvents.MiscPlayerEvent.class)
                    .name(loc("hardmode"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_HARDMODE)
                    .displayLocation(10, 7)
                    .triggerCondition(p -> WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() || NMUtils.getWorldProgress() > 0)
                    .parents(ENTER_NETHER)
                    .build()
                    .setSpecial()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<Boolean> FIRST_BLOODMOON =
            AchievementProvider.getBuilder(NMAchievementEvents.BloodMoonEvent.class)
                    .name(loc("bloodMoon"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_BLOODMOON)
                    .displayLocation(11, 6)
                    .triggerCondition(started -> !started)
                    .parents(HARDMODE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_ORB =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodOrb"))
                    .icon(NMItems.bloodOrb)
                    .displayLocation(13, 6)
                    .triggerCondition(item -> item.itemID == NMItems.bloodOrb.itemID)
                    .parents(FIRST_BLOODMOON)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_INGOT =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodIngot"))
                    .icon(NMItems.bloodIngot)
                    .displayLocation(15, 6)
                    .triggerCondition(item -> item.itemID == NMItems.bloodIngot.itemID)
                    .parents(BLOOD_ORB)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_SWORD =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodSword"))
                    .icon(NMItems.bloodSword)
                    .displayLocation(13, 7)
                    .triggerCondition(item -> item.itemID == NMItems.bloodSword.itemID)
                    .parents(BLOOD_INGOT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_PICK =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodPickaxe"))
                    .icon(NMItems.bloodPickaxe)
                    .displayLocation(14, 7)
                    .triggerCondition(item -> item.itemID == NMItems.bloodPickaxe.itemID)
                    .parents(BLOOD_INGOT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_AXE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodAxe"))
                    .icon(NMItems.bloodAxe)
                    .displayLocation(15, 7)
                    .triggerCondition(item -> item.itemID == NMItems.bloodAxe.itemID)
                    .parents(BLOOD_INGOT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_SHOVEL =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodShovel"))
                    .icon(NMItems.bloodShovel)
                    .displayLocation(16, 7)
                    .triggerCondition(item -> item.itemID == NMItems.bloodShovel.itemID)
                    .parents(BLOOD_INGOT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_HOE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodHoe"))
                    .icon(NMItems.bloodHoe)
                    .displayLocation(17, 7)
                    .triggerCondition(item -> item.itemID == NMItems.bloodHoe.itemID)
                    .parents(BLOOD_INGOT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<NMAchievementEvents.DamageSourcePlayerEvent.DamageSourceData> SHADOW_JUMP_SCARE =
            AchievementProvider.getBuilder(NMAchievementEvents.DamageSourcePlayerEvent.class)
                    .name(loc("shadowJumpScare"))
                    .icon(Item.enderPearl)
                    .displayLocation(11, 7)
                    .triggerCondition(src -> src.src().getSourceOfDamage() instanceof EntityShadowZombie z && !canSeeEnemy(src.player(), z))
                    .parents(HARDMODE)
                    .build()
                    .setSecret()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> BLOOD_CHEST =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bloodChest"))
                    .icon(NMBlocks.bloodChest)
                    .displayLocation(8, -1)
                    .triggerCondition(item -> item.itemID == NMBlocks.bloodChest.blockID)
                    .parents(CRAFT_CHEST)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> KILL_TERRENCE =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("killTerrence"))
                    .icon(BTWItems.creeperOysters)
                    .displayLocation(9, 7)
                    .triggerCondition(data -> data.entity() instanceof EntityCreeper c && Objects.equals(c.getCustomNameTag(), "Terrence"))
                    .parents(HARDMODE)
                    .build()
                    .setSecret()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<EntityPlayer> SLEEP_NETHER =
            AchievementProvider.getBuilder(NMAchievementEvents.MiscPlayerEvent.class)
                    .name(loc("netherSleep"))
                    .icon(Item.bed)
                    .displayLocation(9, 6)
                    .triggerCondition(p -> p.isPlayerSleeping() && p.dimension == -1)
                    .parents(ENTER_NETHER)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<EntityPlayer> EQUIP_BLOOD_ARMOR =
            AchievementProvider.getBuilder(NMAchievementEvents.MiscPlayerEvent.class)
                    .name(loc("equipBloodArmor"))
                    .icon(NMItems.bloodChestplate)
                    .displayLocation(15, 8)
                    .triggerCondition(NMUtils::isWearingFullBloodArmor)
                    .parents(BLOOD_INGOT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> ENVY =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("envy"))
                    .icon(Block.blockEmerald)
                    .displayLocation(8, 8)
                    .triggerCondition(data -> data.entity() instanceof EntityFauxVillager)
                    .build()
                    .setSecret()
                    .setSpecial()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_FABRIC =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("fabric"))
                    .icon(BTWItems.fabric)
                    .displayLocation(6, -1)
                    .triggerCondition(item -> item.itemID == BTWItems.fabric.itemID)
                    .parents(GRIND_HEMP_FIBERS)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_BED =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("bed"))
                    .icon(Item.bed)
                    .displayLocation(6, -2)
                    .triggerCondition(item -> item.itemID == Item.bed.itemID)
                    .parents(CRAFT_FABRIC)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<Integer> SLOTH =
            AchievementProvider.getBuilder(NMAchievementEvents.PlayerSleepEvent.class)
                    .name(loc("sloth"))
                    .icon(Block.coalBlock)
                    .displayLocation(9, -4)
                    .triggerCondition(i -> i >= 48000)
                    .build()
                    .setSecret()
                    .setSpecial()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> FILTER_BRIMSTONE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("brimstone"))
                    .icon(BTWItems.brimstone)
                    .displayLocation(8, 5)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.brimstone.itemID)
                    .parents(ENTER_NETHER)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_GUNPOWDER =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("gunpowder"))
                    .icon(Item.gunpowder)
                    .displayLocation(9, 5)
                    .triggerCondition(itemStack -> itemStack.itemID == Item.gunpowder.itemID)
                    .parents(FILTER_BRIMSTONE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_TNT =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("tnt"))
                    .icon(Block.tnt)
                    .displayLocation(10, 5)
                    .triggerCondition(itemStack -> itemStack.itemID == Block.tnt.blockID)
                    .parents(CRAFT_GUNPOWDER)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> GET_GROUND_NETHERRACK =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("groundNetherrack"))
                    .icon(BTWItems.groundNetherrack)
                    .displayLocation(11, 5)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.groundNetherrack.itemID)
                    .parents(CRAFT_TNT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_HELLFORGE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("craftHellforge"))
                    .icon(NMBlocks.hellforge)
                    .displayLocation(9, 2)
                    .triggerCondition(itemStack -> itemStack.itemID == NMBlocks.hellforge.blockID)
                    .parents(CRAFT_NETHER_COAL)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_REFINED_DIAMOND =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("craftRefinedDiamond"))
                    .icon(NMItems.refinedDiamondIngot)
                    .displayLocation(4, 5)
                    .triggerCondition(itemStack -> itemStack.itemID == NMItems.refinedDiamondIngot.itemID)
                    .parents(CRAFT_DIAMOND_INGOT)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_IRON_ROD =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("craftIronFishingRod"))
                    .icon(NMItems.ironFishingPole)
                    .displayLocation(-1, -4)
                    .triggerCondition(data -> data.itemID == NMItems.ironFishingPole.itemID)
                    .parents(IRON_AGE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);
    public static final Achievement<ItemStack> CRAFT_CHAIN_ARMOR =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("chainArmor"))
                    .icon(Item.plateChain)
                    .displayLocation(1, 4)
                    .triggerCondition(item -> item.getItem() instanceof ItemArmor ar && ar.getArmorMaterial() == EnumArmorMaterial.CHAIN)
                    .parents(IRON_AGE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);

    public static final Achievement<ItemStack> ALCHEMY_DUST =
            AchievementProvider.getBuilder(AchievementEvents.ItemUsedEvent.class)
                    .name(loc("alchemyDust"))
                    .icon(NMItems.witchLocator)
                    .displayLocation(5, -3)
                    .triggerCondition(item -> item.itemID == NMItems.witchLocator.itemID)
                    .parents(CRAFT_MILLSTONE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);

    public static final Achievement<ItemStack> ANCIENT_SAND =
            AchievementProvider.getBuilder(AchievementEvents.ItemUsedEvent.class)
                    .name(loc("ancientSand"))
                    .icon(NMItems.templeLocator)
                    .displayLocation(15, -2)
                    .triggerCondition(item -> item.itemID == NMItems.templeLocator.itemID)
                    .parents(CRAFT_CRUCIBLE)
                    .build()
                    .registerAchievement(TAB_IRON_AGE);






    // AUTOMATION

    public static final Achievement<NMAchievementEvents.BlockBrokenEvent.BlockBrokenData> MINE_STEEL =
            AchievementProvider.getBuilder(NMAchievementEvents.BlockBrokenEvent.class)
                    .name(loc("mineSteelOre"))
                    .icon(BTWItems.steelNugget)
                    .displayLocation(10, -2)
                    .triggerCondition(data -> data.blockID() == NMBlocks.steelOre.blockID)
                    .parents(KILLED_WITHER)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);
    public static final Achievement<Boolean> WITHER_BLOODMOON =
            AchievementProvider.getBuilder(NMAchievementEvents.BloodMoonEvent.class)
                    .name(loc("bloodMoonWither"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER)
                    .displayLocation(10, 0)
                    .triggerCondition(started -> !started && NMUtils.getWorldProgress() > 2)
                    .parents(KILLED_WITHER)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);
    public static final Achievement<ItemStack> CRAFT_CORPSE_EYE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("corpseEye"))
                    .icon(BTWItems.corpseEye)
                    .displayLocation(12, 1)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.corpseEye.itemID)
                    .parents(WITHER_BLOODMOON)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);
    public static final Achievement<ItemStack> CRAFT_ASPHALT =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("roadAsphalt"))
                    .icon(NMBlocks.blockAsphalt)
                    .displayLocation(1, 3)
                    .triggerCondition(itemStack -> itemStack.itemID == NMBlocks.blockAsphalt.blockID)
                    .parents(CRAFT_SOUL_URN_ROOT)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);
    public static final Achievement<ItemStack> CRAFT_STEEL_LOCKER =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("steelLocker"))
                    .icon(NMBlocks.steelLocker)
                    .displayLocation(11, -2)
                    .triggerCondition(itemStack -> itemStack.itemID == NMBlocks.steelLocker.blockID)
                    .parents(MINE_STEEL)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);
    public static final Achievement<NMAchievementEvents.PlayerAttackEvent.PlayerAttackEventData> WRATH =
            AchievementProvider.getBuilder(NMAchievementEvents.PlayerAttackEvent.class)
                    .name(loc("wrath"))
                    .icon(BTWBlocks.nethercoalBlock)
                    .displayLocation(9, 4)
                    .triggerCondition(data -> data.damage() >= 40f && data.entityHit() instanceof EntityLivingBase e && e.getHealth() < 30)
                    .build()
                    .setSecret()
                    .setSpecial()
                    .registerAchievement(TAB_AUTOMATION);

    public static final Achievement<ItemStack> CRAFT_STEEL_INGOT =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("steelIngot"))
                    .icon(BTWItems.soulforgedSteelIngot)
                    .displayLocation(12, -4)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.soulforgedSteelIngot.itemID)
                    .parents(MINE_STEEL)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);

    public static final Achievement<ItemStack> STEEL_SWORD =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("steelSword"))
                    .icon(BTWItems.steelSword)
                    .displayLocation(13, -6)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.steelSword.itemID)
                    .parents(CRAFT_STEEL_INGOT)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);


    public static final Achievement<ItemStack> STEEL_AXE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("steelAxe"))
                    .icon(BTWItems.steelAxe)
                    .displayLocation(13, -5)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.steelAxe.itemID)
                    .parents(CRAFT_STEEL_INGOT)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);

        public static final Achievement<ItemStack> STEEL_BATTLEAXE =
                AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                        .name(loc("steelBattleAxe"))
                        .icon(BTWItems.battleaxe)
                        .displayLocation(14, -5)
                        .triggerCondition(itemStack -> itemStack.itemID == BTWItems.battleaxe.itemID)
                        .parents(STEEL_AXE, STEEL_SWORD)
                        .build()
                        .registerAchievement(TAB_AUTOMATION);


    public static final Achievement<ItemStack> STEEL_PICKAXE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("steelPickaxe"))
                    .icon(BTWItems.steelPickaxe)
                    .displayLocation(13, -4)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.steelPickaxe.itemID)
                    .parents(CRAFT_STEEL_INGOT)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);

    public static final Achievement<ItemStack> STEEL_SHOVEL =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("steelShovel"))
                    .icon(BTWItems.steelShovel)
                    .displayLocation(13, -3)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.steelShovel.itemID)
                    .parents(CRAFT_STEEL_INGOT)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);

        public static final Achievement<ItemStack> STEEL_MATTOCK =
                AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                        .name(loc("steelMattock"))
                        .icon(BTWItems.mattock)
                        .displayLocation(14, -3)
                        .triggerCondition(itemStack -> itemStack.itemID == BTWItems.mattock.itemID)
                        .parents(STEEL_SHOVEL, STEEL_PICKAXE)
                        .build()
                        .registerAchievement(TAB_AUTOMATION);

    public static final Achievement<ItemStack> STEEL_HOE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("steelHoe"))
                    .icon(BTWItems.steelHoe)
                    .displayLocation(13, -2)
                    .triggerCondition(itemStack -> itemStack.itemID == BTWItems.steelHoe.itemID)
                    .parents(CRAFT_STEEL_INGOT)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);
    public static final Achievement<AchievementEvents.None> LUST =
            AchievementProvider.getBuilder(NMAchievementEvents.LustEvent.class)
                    .name(loc("lust"))
                    .icon(new ItemStack(BTWBlocks.aestheticOpaque, 1, 5)) // soap block
                    .displayLocation(8, -5)
                    .alwaysTrigger()
                    .build()
                    .setSpecial()
                    .setSecret()
                    .registerAchievement(TAB_AUTOMATION);

    public static final Achievement<ItemStack> DUNG_APPLE =
            AchievementProvider.getBuilder(AchievementEvents.EatenEvent.class)
                    .name(loc("dungApple"))
                    .icon(NMItems.dungApple)
                    .displayLocation(3, 6)
                    .triggerCondition(item -> item.itemID == NMItems.dungApple.itemID)
                    .parents(TRADED_WITH_FARMER)
                    .build()
                    .setHidden()
                    .registerAchievement(TAB_AUTOMATION);

    public static final Achievement<Entity> VILLAGER_VESSEL =
            AchievementProvider.getBuilder(AchievementEvents.EatEntityWithBlockDispenserEvent.class)
                    .name(loc("makeVillagerVessel"))
                    .icon(NMBlocks.villagerBlock)
                    .displayLocation(6, 1)
                    .triggerCondition(e -> e instanceof EntityVillager)
                    .parents(MAKE_COMPANION_CUBE)
                    .build()
                    .registerAchievement(TAB_AUTOMATION);


    // TAB_END_GAME


    public static final Achievement<Integer> PRIDE =
            AchievementProvider.getBuilder(NMAchievementEvents.ArmorLessEvent.class)
                    .name(loc("pride"))
                    .icon(BTWBlocks.spiderEyeBlock)
                    .displayLocation(-2, -3)
                    .triggerCondition( p -> p > 600)
                    .build()
                    .setSpecial()
                    .setSecret()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<Boolean> FIRST_ECLIPSE =
            AchievementProvider.getBuilder(NMAchievementEvents.EclipseEvent.class)
                    .name(loc("eclipse"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_ECLIPSE)
                    .displayLocation(3, 0)
                    .triggerCondition(started -> !started)
                    .parents(KILLED_DRAGON)
                    .build()
                    .setSpecial()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<ItemStack> GET_ECLIPSE_SHARD =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("eclipseShard"))
                    .icon(NMItems.darksunFragment)
                    .displayLocation(5, 0)
                    .triggerCondition(itemStack -> itemStack.itemID == NMItems.darksunFragment.itemID)
                    .parents(FIRST_ECLIPSE)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.None> NIGHTMARE_MERCHANT =
            AchievementProvider.getBuilder(NMAchievementEvents.NightmareMerchantEvent.class)
                    .name(loc("nightmareMerchant"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_MERCHANT)
                    .displayLocation(3, -2)
                    .alwaysTrigger()
                    .parents(FIRST_ECLIPSE,KILLED_DRAGON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.EntityInteractedEventData> KILL_ECLIPSE_ANIMAL =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("killEclipseAnimal"))
                    .icon(NMItems.creeperChop)
                    .displayLocation(4, 2)
                    .triggerCondition(data -> data.entity() instanceof EntityAnimal a && NMUtils.getIsMobEclipsed(a))
                    .parents(FIRST_ECLIPSE)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<ItemStack> GET_MAGIC_FEATHER =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("magicFeather"))
                    .icon(NMItems.magicFeather)
                    .displayLocation(5, 2)
                    .triggerCondition(stk -> stk.itemID == NMItems.magicFeather.itemID)
                    .parents(KILL_ECLIPSE_ANIMAL)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.EntityInteractedEventData> RIDE_CHICKEN =
            AchievementProvider.getBuilder(AchievementEvents.EntityInteractedEvent.class)
                    .name(loc("rideEclipseChicken"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_CHICKEN)
                    .displayLocation(5, 1)
                    .triggerCondition(data -> data.entity() instanceof EntityChicken c && NMUtils.getIsMobEclipsed(c))
                    .parents(GET_MAGIC_FEATHER)
                    .build()
                    .setHidden()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<ItemStack> ECLIPSE_COW_MILK =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("eclipseCowMilk"))
                    .icon(NMItems.bloodMilk)
                    .displayLocation(5, 3)
                    .triggerCondition(stack -> stack.itemID == NMItems.bloodMilk.itemID)
                    .parents(KILL_ECLIPSE_ANIMAL)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<DamageSource> ECLIPSE_COW_KNOCKBACK =
            AchievementProvider.getBuilder(NMAchievementEvents.DamageSourceEvent.class)
                    .name(loc("eclipseCowKick"))
                    .icon(BTWItems.breedingHarness)
                    .displayLocation(6, 3)
                    .triggerCondition(src -> src.getSourceOfDamage() instanceof EntityCow c && NMUtils.getIsMobEclipsed(c))
                    .parents(ECLIPSE_COW_MILK)
                    .build()
                    .setHidden()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<ItemStack> MAGIC_ARROW =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("magicArrow"))
                    .icon(NMItems.magicArrow)
                    .displayLocation(6, 2)
                    .triggerCondition(itemStack -> itemStack.itemID == NMItems.magicArrow.itemID)
                    .parents(GET_MAGIC_FEATHER)
                    .build()
                    .registerAchievement(TAB_END_GAME);


    public static final Achievement<ItemStack> ECLIPSE_BOW =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("eclipseBow"))
                    .icon(NMItems.eclipseBow)
                    .displayLocation(7, 0)
                    .triggerCondition(itemStack -> itemStack.itemID == NMItems.eclipseBow.itemID)
                    .parents(GET_ECLIPSE_SHARD, MAGIC_ARROW)
                    .build()
                    .setSpecial()
                    .registerAchievement(TAB_END_GAME);





    public static final Achievement<AchievementEvents.EntityInteractedEventData> NIGHTMARE_MERCHANT_LEVEL_2 =
            AchievementProvider.getBuilder(AchievementEvents.EntityInteractedEvent.class)
                    .name(loc("nightmareMerchantLevel2"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_DIAMOND)
                    .displayLocation(5, -2)
                    .triggerCondition(data -> data.entity() instanceof NightmareVillager nmv && nmv.getCurrentTradeLevel() >= 2)
                    .parents(NIGHTMARE_MERCHANT)
                    .build()
                    .registerAchievement(TAB_END_GAME);





    public static final Achievement<ItemStack> ECLIPSE_SHADOW_ZOMBIE =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("eclipseShadowZombie"))
                    .icon(NMItems.charredFlesh)
                    .displayLocation(8, -5)
                    .triggerCondition(item -> item.itemID == NMItems.charredFlesh.itemID)
                    .parents(ENTER_END)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<ItemStack> ECLIPSE_SQUID =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("eclipseSquid"))
                    .icon(NMItems.voidSack)
                    .displayLocation(6, -5)
                    .triggerCondition(item -> item.itemID == NMItems.voidSack.itemID)
                    .parents(ENTER_END)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<ItemStack> ECLIPSE_GHAST =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("eclipseGhast"))
                    .icon(NMItems.ghastTentacle)
                    .displayLocation(6, -3)
                    .triggerCondition(
                            item -> item.itemID == NMItems.ghastTentacle.itemID
                            || item.itemID == NMItems.creeperTear.itemID)
                    .parents(ENTER_END)
                    .build()
                    .registerAchievement(TAB_END_GAME);
    public static final Achievement<AchievementEvents.EntityInteractedEventData> ECLIPSE_ENDERMAN =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("eclipseEnderman"))
                    .icon(Item.eyeOfEnder)
                    .displayLocation(8, -3)
                    .triggerCondition(data -> data.entity() instanceof EntityEnderman em && NMUtils.getIsMobEclipsed(em))
                    .parents(ENTER_END)
                    .build()
                    .registerAchievement(TAB_END_GAME);


    public static final Achievement<ItemStack> ECLIPSE_WITCH  =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("eclipseWitch"))
                    .icon(NMItems.voidMembrane)
                    .displayLocation(7, -4)
                    .triggerCondition(item -> item.itemID == NMItems.voidMembrane.itemID)
                    .parents(ECLIPSE_SQUID, ECLIPSE_SHADOW_ZOMBIE, ECLIPSE_GHAST, ECLIPSE_ENDERMAN)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.EntityInteractedEventData> NIGHTMARE_MERCHANT_LEVEL_3 =
            AchievementProvider.getBuilder(AchievementEvents.EntityInteractedEvent.class)
                    .name(loc("nightmareMerchantLevel3"))
                    .icon(NMBlocks.bloodBones)
                    .displayLocation(7, -2)
                    .triggerCondition(data -> data.entity() instanceof NightmareVillager nmv && nmv.getCurrentTradeLevel() >= 3)
                    .parents(NIGHTMARE_MERCHANT_LEVEL_2, ECLIPSE_WITCH)
                    .build()
                    .registerAchievement(TAB_END_GAME);



    public static final Achievement<AchievementEvents.EntityInteractedEventData> NIGHTMARE_MERCHANT_LEVEL_4 =
            AchievementProvider.getBuilder(AchievementEvents.EntityInteractedEvent.class)
                    .name(loc("nightmareMerchantLevel4"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_SKULL)
                    .displayLocation(9, -2)
                    .triggerCondition(data -> data.entity() instanceof NightmareVillager nmv && nmv.getCurrentTradeLevel() >= 4)
                    .parents(NIGHTMARE_MERCHANT_LEVEL_3)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.EntityInteractedEventData> KILL_BLOODWITHER =
            AchievementProvider.getBuilder(AchievementEvents.EntityKilledEvent.class)
                    .name(loc("killBloodWither"))
                    .icon(NMItems.starOfTheBloodGod)
                    .displayLocation(11, 0)
                    .triggerCondition(data -> data.entity() instanceof EntityBloodWither)
                    .parents(ECLIPSE_BOW, NIGHTMARE_MERCHANT_LEVEL_4)
                    .build()
                    .setSpecial()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.EntityInteractedEventData> NIGHTMARE_MERCHANT_LEVEL_5 =
            AchievementProvider.getBuilder(AchievementEvents.EntityInteractedEvent.class)
                    .name(loc("nightmareMerchantLevel5"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_TRIPLE_TEAR)
                    .displayLocation(11, -2)
                    .triggerCondition(data -> data.entity() instanceof NightmareVillager nmv && nmv.getCurrentTradeLevel() == 5)
                    .parents(NIGHTMARE_MERCHANT_LEVEL_4, KILL_BLOODWITHER)
                    .build()
                    .setSpecial()
                    .registerAchievement(TAB_END_GAME);




    public static final Achievement<Float> HIGH_DAMAGE_ARROW =
            AchievementProvider.getBuilder(NMAchievementEvents.ArrowDamageEvent.class)
                    .name(loc("arrowDamage"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_ARROW_RED)
                    .displayLocation(8, 1)
                    .triggerCondition(dmg -> dmg > 50)
                    .parents(ECLIPSE_BOW)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<Integer> HIGH_PIERCE_ARROW =
            AchievementProvider.getBuilder(NMAchievementEvents.ArrowEnemyHitEvent.class)
                    .name(loc("arrowPierce"))
                    .icon(NMItems.ACHIEVEMENT_SPECIAL_ARROW_TRIPLE)
                    .displayLocation(8, -1)
                    .triggerCondition(dmg -> dmg > 5)
                    .parents(ECLIPSE_BOW)
                    .build()
                    .registerAchievement(TAB_END_GAME);






    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_HELLFIRE =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconHellfire"))
                    .icon(new ItemStack(BTWBlocks.aestheticOpaque.blockID, 1, 3))
                    .displayLocation(9, 4)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.FIRE_RESIST_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);
    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_DIAMOND =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconDiamond"))
                    .icon(BTWBlocks.diamondIngot) // you can try guessing the right icon, but you don't have to
                    .displayLocation(10, 4)// keep the same
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.FORTUNE_EFFECT && data.level() == 4)// keep this constant
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);
    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_DUNG =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconDung"))
                    .icon(new ItemStack(BTWBlocks.aestheticEarth.blockID, 1, 7))
                    .displayLocation(11, 5)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.NAUSEA_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_EMERALD =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconEmerald"))
                    .icon(Block.blockEmerald)
                    .displayLocation(12, 6)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.LOOTING_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_GLASS =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconGlass"))
                    .icon(Block.glass)
                    .displayLocation(13, 5)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.DECORATIVE_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_GLOWSTONE =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconGlowstone"))
                    .icon(Block.glowStone)
                    .displayLocation(14, 4)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.NIGHT_VISION_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_GOLD =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconGold"))
                    .icon(Block.blockGold)
                    .displayLocation(15, 4)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.HASTE_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_IRON =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconIron"))
                    .icon(Block.blockIron)
                    .displayLocation(13, 3)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.MAGNETIC_POLE_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);


    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_LAPIS =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconLapis"))
                    .icon(Block.blockLapis)
                    .displayLocation(12, 2)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.TRUE_SIGHT_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_ENDER_BLOCK =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconEnderBlock"))
                    .icon(new ItemStack(BTWBlocks.aestheticOpaque, 1, 14)) // ender block
                    .displayLocation(11, 3)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.ENDER_ANTENNA_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<AchievementEvents.BeaconEventData> BEACON_SPIDER =
            AchievementProvider.getBuilder(AchievementEvents.BeaconEvent.class)
                    .name(loc("beaconSpider"))
                    .icon(BTWBlocks.spiderEyeBlock)
                    .displayLocation(12, 5)
                    .triggerCondition(data -> data.effect() == BTWBeaconEffects.FIRE_RESIST_EFFECT && data.level() == 4)
                    .parents(CRAFTED_BEACON)
                    .build()
                    .registerAchievement(TAB_END_GAME);

    public static final Achievement<EntityPlayer> BEACON_ALL =
            AchievementProvider.getBuilder(NMAchievementEvents.MiscPlayerEvent.class)
                    .name(loc("beaconAll"))
                    .icon(Block.beacon)
                    .displayLocation(12, 7)
                    .triggerCondition(NMAchievements::hasBeaconAchievements)
                    .parents(CRAFTED_BEACON,
                            BEACON_GOLD,
                            BEACON_HELLFIRE,
                            BEACON_DIAMOND,
                            BEACON_DUNG,
                            BEACON_EMERALD,
                            BEACON_ENDER_BLOCK,
                            BEACON_GLASS,
                            BEACON_GLOWSTONE,
                            BEACON_LAPIS,
                            BEACON_IRON,
                            BEACON_SPIDER,
                            MAX_STEEL_BEACON)
                    .build()
                    .setSpecial()
                    .registerAchievement(TAB_END_GAME);


    public static final Achievement<ItemStack> RIFLE_RPG  =
            AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                    .name(loc("rifleRpg"))
                    .icon(NMItems.rpg)
                    .displayLocation(12,0)
                    .triggerCondition(item -> item.itemID == NMItems.rifle.itemID || item.itemID == NMItems.rpg.itemID)
                    .parents(KILL_BLOODWITHER,NIGHTMARE_MERCHANT_LEVEL_5)
                    .build()
                    .registerAchievement(TAB_END_GAME);
    public static final Achievement<EntityPlayer> SINNER  =
            AchievementProvider.getBuilder(NMAchievementEvents.MiscPlayerEvent.class)
                    .name(loc("sinner"))
                    .icon(NMBlocks.cryingObsidian)
                    .displayLocation(-1,4)
                    .triggerCondition(NMAchievements::hasAllSinAchievements)
                    .parents()
                    .build()
                    .setSecret()
                    .setSpecial()
                    .registerAchievement(TAB_END_GAME);














    public static void initialize(){}

    public static ResourceLocation loc(String name) {
        return new ResourceLocation("nm", name);
    }

    private static ResourceLocation btwLoc(String name) {
        return new ResourceLocation("btw", name);
    }


    // HELPER METHODS


    private static boolean isPlayerGreeding(EntityPlayer p){
        World w = p.worldObj;
        long t = w.getTotalWorldTime();
        if(t % 20 != 0) return false;

        // case 0: player is deliberately going for the achievement, or spawned in a ravine
        if(t < 1000){
            return false;
        }
        int day = 24000;

        // case 1: early greed
        if(t < (day * 7) && p.posY < 50){
            return true;
        }

        // case 2: middle game greed
        return t < (24000 * 14) && p.posY < 24;
    }

    private static final Set<Integer> IRON_ITEM_IDS = new HashSet<Integer>(Arrays.asList(
            Item.swordIron.itemID,
            Item.shovelIron.itemID,
            Item.helmetIron.itemID,
            Item.plateIron.itemID,
            Item.legsIron.itemID,
            Item.bootsIron.itemID
    ));


    private static boolean hasIronItem(Entity e) {
        if(!(e instanceof EntityLivingBase entityLivingBase)) return false;

        for (int i = 0; i <= 4; i++) {
            ItemStack armor = entityLivingBase.getCurrentItemOrArmor(i);
            if (armor != null && IRON_ITEM_IDS.contains(armor.itemID)) {
                return true;
            }
        }

        return false;
    }
    private static boolean canSeeEnemy(EntityPlayer p, EntityLivingBase z) {
        Vec3 lookVec = p.getLookVec();
        Vec3 toEnemy = Vec3.createVectorHelper(
                z.posX - p.posX,
                (z.boundingBox.minY + z.boundingBox.maxY) * 0.5 - (p.posY + p.getEyeHeight()),
                z.posZ - p.posZ
        );
        lookVec = lookVec.normalize();
        toEnemy = toEnemy.normalize();
        double dot = lookVec.dotProduct(toEnemy);
        return dot > 0.1d;
    }
    private static Predicate<ItemStack> itemID(int... itemIDs) {
        return (itemStack) -> Arrays.stream(itemIDs).anyMatch((id) -> id == itemStack.itemID);
    }
    private static final List<Integer> steelArmor = new ArrayList<>(Arrays.asList(
            BTWItems.plateBoots.itemID,
            BTWItems.plateLeggings.itemID,
            BTWItems.plateBreastplate.itemID,
            BTWItems.plateHelmet.itemID
    ));

    public static boolean isWearingNoArmor(EntityLivingBase entity){
        for(int i = 1; i < 5; i++){
            if(entity.getCurrentItemOrArmor(i) == null) continue;
            return false;
        }
        return true;
    }

    private static boolean hasBeaconAchievements(EntityPlayer player) {
        Achievement<?>[] beaconAchievements = new Achievement<?>[] {
                BEACON_GOLD,
                BEACON_HELLFIRE,
                BEACON_DIAMOND,
                BEACON_DUNG,
                BEACON_EMERALD,
                BEACON_ENDER_BLOCK,
                BEACON_GLASS,
                BEACON_GLOWSTONE,
                BEACON_LAPIS,
                BEACON_IRON,
                BEACON_SPIDER,
                MAX_STEEL_BEACON
        };

        for (Achievement<?> achievement : beaconAchievements) {
            if (!AchievementHandler.hasUnlocked(player, achievement)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasAllSinAchievements(EntityPlayer player) {
        Achievement<?>[] sinAchievements = new Achievement<?>[] {
                GREED,
                PRIDE,
                LUST,
                SLOTH,
                GLUTTONY,
                WRATH,
                ENVY
        };

        for (Achievement<?> achievement : sinAchievements) {
            if (!AchievementHandler.hasUnlocked(player, achievement)) {
                return false;
            }
        }
        return true;
    }














}
