package com.itlesports.nightmaremode.util;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.ItemKnife;
import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import net.minecraft.src.BlockColored;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityCow;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPig;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public final class CarcassHarvesting {
    public static final int FIST_PROCESSING_TICKS = 600;

    private CarcassHarvesting() {
    }

    public static int getProcessingTicks(ItemStack stack) {
        ItemKnife knife = ItemKnife.fromStack(stack);
        return knife == null ? FIST_PROCESSING_TICKS : knife.getProcessingTicks();
    }

    public static int getHarvestTier(ItemStack stack) {
        ItemKnife knife = ItemKnife.fromStack(stack);
        return knife == null ? ItemKnife.TIER_FISTS : knife.getHarvestTier();
    }

    public static boolean isValidTool(ItemStack stack) {
        return stack == null || ItemKnife.fromStack(stack) != null;
    }

    public static void completeHarvest(EntityLivingBase entity, EntityPlayer player, int harvestTier, DamageSource source) {
        if (!(entity instanceof EntityAnimal animal)) {
            entity.entityLivingOnDeath(source);
            dropCreeperRecord(entity, source);
            damageKnife(player, harvestTier);
            return;
        }

        ItemStack meat = getMeat(animal);
        ItemStack secondary = getSecondary(animal);
        int randomPrimaryDrops = 1 + animal.getRNG().nextInt(2);

        for (int i = 0; i < randomPrimaryDrops; ++i) {
            if (meat != null && animal.getRNG().nextBoolean()) {
                drop(animal, meat.copy());
            } else {
                drop(animal, new ItemStack(NMItems.boneShard));
            }
        }

        if (secondary != null && animal.getRNG().nextBoolean()) {
            drop(animal, secondary.copy());
        }

        if (harvestTier == ItemKnife.TIER_STONE) {
            dropIfPresent(animal, meat, 1);
        } else if (harvestTier == ItemKnife.TIER_IRON) {
            dropIfPresent(animal, meat, 1);
            dropIfPresent(animal, secondary, 1);
        } else if (harvestTier == ItemKnife.TIER_DIAMOND) {
            dropIfPresent(animal, meat, 1 + animal.getRNG().nextInt(2));
            dropIfPresent(animal, secondary, 1 + animal.getRNG().nextInt(2));
        }

        if (animal instanceof EntityHorse horse) {
            horse.dropChestItems();
        }

        damageKnife(player, harvestTier);
    }

    private static ItemStack getMeat(EntityAnimal animal) {
        if (animal instanceof EntityCow) {
            return new ItemStack(Item.beefRaw);
        }
        if (animal instanceof EntityPig) {
            return new ItemStack(Item.porkRaw);
        }
        if (animal instanceof EntityChicken) {
            return new ItemStack(Item.chickenRaw);
        }
        if (animal instanceof EntitySheep) {
            return new ItemStack(BTWItems.rawMutton);
        }
        if (animal instanceof EntityHorse horse && horse.getHorseType() <= 2) {
            return new ItemStack(BTWItems.rawCheval);
        }
        if (animal instanceof EntityWolf) {
            return new ItemStack(BTWItems.rawWolfChop);
        }
        return null;
    }

    private static ItemStack getSecondary(EntityAnimal animal) {
        if (animal instanceof EntityCow) {
            return new ItemStack(Item.leather);
        }
        if (animal instanceof EntityHorse horse && horse.getHorseType() <= 2) {
            return new ItemStack(Item.leather);
        }
        if (animal instanceof EntityChicken) {
            return new ItemStack(Item.feather);
        }
        if (animal instanceof EntitySheep sheep && !sheep.getSheared()) {
            return new ItemStack(BTWItems.wool, 1, BlockColored.getDyeFromBlock(sheep.getFleeceColor()));
        }
        return null;
    }

    private static void dropIfPresent(EntityAnimal animal, ItemStack stack, int count) {
        if (stack == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            drop(animal, stack.copy());
        }
    }

    private static void drop(EntityAnimal animal, ItemStack stack) {
        animal.entityDropItem(stack, 0.3F);
    }

    private static void damageKnife(EntityPlayer player, int harvestTier) {
        ItemStack held = player.getHeldItem();
        if (harvestTier > ItemKnife.TIER_FISTS && ItemKnife.fromStack(held) != null) {
            held.damageItem(1, player);
        }
    }

    private static void dropCreeperRecord(EntityLivingBase entity, DamageSource source) {
        if (!(source.getEntity() instanceof EntitySkeleton)) {
            return;
        }

        boolean eligibleCreeper = entity instanceof EntityCreeper creeper && creeper.getNeuteredState() == 0;
        eligibleCreeper |= entity instanceof EntityCreeperVariant creeper && creeper.getNeuteredState() == 0;
        if (eligibleCreeper) {
            int recordId = Item.record13.itemID + entity.getRNG().nextInt(Item.recordWait.itemID - Item.record13.itemID + 1);
            entity.dropItem(recordId, 1);
        }
    }
}
