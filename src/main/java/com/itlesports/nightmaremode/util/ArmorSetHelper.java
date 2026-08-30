package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemDivingGear;
import com.itlesports.nightmaremode.item.items.ItemCoresteelArmor;
import com.itlesports.nightmaremode.item.items.ItemChargedArmor;
import com.itlesports.nightmaremode.util.interfaces.IHeatResistantArmor;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public final class ArmorSetHelper {
    private ArmorSetHelper() {
    }

    public static boolean isWearingCompleteHeatResistantSet(EntityLivingBase wearer) {
        return isIntact(wearer.getCurrentItemOrArmor(4), NMItems.heatResistantHelmet)
                && isIntact(wearer.getCurrentItemOrArmor(3), NMItems.heatResistantChestplate)
                && isIntact(wearer.getCurrentItemOrArmor(2), NMItems.heatResistantLeggings)
                && isIntact(wearer.getCurrentItemOrArmor(1), NMItems.heatResistantBoots);
    }

    public static int getHeatResistantPieceCount(EntityLivingBase wearer) {
        int count = 0;
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof IHeatResistantArmor) ++count;
        }
        return count;
    }

    public static float getFireTimeReduction(EntityLivingBase wearer) {
        float reduction = 0.0F;
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof IHeatResistantArmor armor) {
                reduction += armor.getFireTimeReduction();
            }
        }
        return Math.min(0.8F, reduction);
    }

    public static int getReinforcedIronPieceCount(EntityLivingBase wearer) {
        int count = 0;
        if (isIntact(wearer.getCurrentItemOrArmor(4), NMItems.reinforcedIronHelmet)) ++count;
        if (isIntact(wearer.getCurrentItemOrArmor(3), NMItems.reinforcedIronChestplate)) ++count;
        if (isIntact(wearer.getCurrentItemOrArmor(2), NMItems.reinforcedIronLeggings)) ++count;
        if (isIntact(wearer.getCurrentItemOrArmor(1), NMItems.reinforcedIronBoots)) ++count;
        return count;
    }

    public static boolean isWearingCompleteNickelWorkSet(EntityLivingBase wearer) {
        return isIntact(wearer.getCurrentItemOrArmor(4), NMItems.oxygenMask)
                && isIntact(wearer.getCurrentItemOrArmor(3), NMItems.oxygenTank)
                && isIntact(wearer.getCurrentItemOrArmor(2), NMItems.nickelWorkLeggings)
                && isIntact(wearer.getCurrentItemOrArmor(1), NMItems.nickelWorkBoots);
    }

    public static ItemStack getDivingTank(EntityLivingBase wearer) {
        ItemStack chest = wearer.getCurrentItemOrArmor(3);
        return isIntact(chest) && chest.getItem() instanceof ItemDivingGear gear
                && gear.getAirCapacity() > 0 ? chest : null;
    }

    public static ItemStack getSealedDivingTank(EntityLivingBase wearer) {
        ItemStack mask = wearer.getCurrentItemOrArmor(4);
        ItemStack tank = getDivingTank(wearer);
        if (tank == null) return null;
        boolean divingPair = isIntact(mask, NMItems.divingMask) && isIntact(tank, NMItems.divingTank);
        boolean sunPair = isIntact(mask, NMItems.sunVisor) && isIntact(tank, NMItems.sunReservoir);
        return divingPair || sunPair ? tank : null;
    }

    public static boolean isWearingCompleteTungstenSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.tungstenHelmet, NMItems.tungstenChestplate,
                NMItems.tungstenLeggings, NMItems.tungstenBoots);
    }

    public static boolean isWearingCompleteCoresteelSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.coresteelHelmet, NMItems.coresteelChestplate,
                NMItems.coresteelLeggings, NMItems.coresteelBoots);
    }

    public static boolean isWearingCompleteDeadzoneSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.deadzoneHelmet, NMItems.deadzoneChestplate,
                NMItems.deadzoneLeggings, NMItems.deadzoneBoots);
    }

    public static boolean isWearingCompleteSunSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.sunVisor, NMItems.sunReservoir,
                NMItems.sunLeggings, NMItems.sunBoots);
    }

    public static boolean isWearingCompleteSignalSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.signalHelmet, NMItems.signalChestplate, NMItems.signalLeggings, NMItems.signalBoots);
    }

    public static boolean isWearingCompleteAzureSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.azureHelmet, NMItems.azureChestplate, NMItems.azureLeggings, NMItems.azureBoots);
    }

    public static boolean isWearingCompletePrismaticSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.prismaticHelmet, NMItems.prismaticChestplate, NMItems.prismaticLeggings, NMItems.prismaticBoots);
    }

    public static boolean isWearingCompleteRefinedPrismaSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.refinedPrismaHelmet, NMItems.refinedPrismaChestplate, NMItems.refinedPrismaLeggings, NMItems.refinedPrismaBoots);
    }

    public static boolean isWearingPrismaticSet(EntityLivingBase wearer) {
        return isWearingCompletePrismaticSet(wearer) || isWearingCompleteRefinedPrismaSet(wearer);
    }

    public static boolean isWearingCompleteVerdantSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.verdantHelmet, NMItems.verdantChestplate, NMItems.verdantLeggings, NMItems.verdantBoots);
    }

    public static boolean isWearingCompleteGlassSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.glassHelmet, NMItems.glassChestplate, NMItems.glassLeggings, NMItems.glassBoots);
    }

    public static boolean isWearingCompleteBlackglassSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.blackglassHelmet, NMItems.blackglassChestplate, NMItems.blackglassLeggings, NMItems.blackglassBoots);
    }

    public static boolean isWearingCompleteQuartzglassSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.quartzglassHelmet, NMItems.quartzglassChestplate, NMItems.quartzglassLeggings, NMItems.quartzglassBoots);
    }

    public static boolean isWearingCompleteDarkSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.darkHelmet, NMItems.darkChestplate, NMItems.darkLeggings, NMItems.darkBoots);
    }

    public static boolean isWearingCompleteQuicksilverSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.quicksilverHelmet, NMItems.quicksilverChestplate, NMItems.quicksilverLeggings, NMItems.quicksilverBoots);
    }

    public static boolean isWearingCompleteAnchorSet(EntityLivingBase wearer) {
        return hasSet(wearer, NMItems.anchorHelmet, NMItems.anchorChestplate, NMItems.anchorLeggings, NMItems.anchorBoots);
    }

    public static int getSignalCharge(EntityLivingBase wearer) {
        int charge = 0;
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof ItemChargedArmor armor) charge += armor.getCharge(stack);
        }
        return charge;
    }

    public static void addSignalCharge(EntityLivingBase wearer, int amountPerPiece) {
        if (amountPerPiece <= 0) return;
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof ItemChargedArmor armor) armor.addCharge(stack, amountPerPiece);
        }
    }

    public static int drainSignalCharge(EntityLivingBase wearer, int requested) {
        int remaining = Math.max(0, requested);
        for (int slot = 1; slot <= 4 && remaining > 0; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof ItemChargedArmor armor) {
                remaining -= armor.removeCharge(stack, remaining);
            }
        }
        return requested - remaining;
    }

    public static int getVerdantPieceCount(EntityLivingBase wearer) {
        int count = 0;
        Item[] pieces = {NMItems.verdantBoots, NMItems.verdantLeggings, NMItems.verdantChestplate, NMItems.verdantHelmet};
        for (int slot = 1; slot <= 4; ++slot) if (isIntact(wearer.getCurrentItemOrArmor(slot), pieces[slot - 1])) ++count;
        return count;
    }

    public static int getPieceCount(EntityLivingBase wearer, Item... pieces) {
        int count = 0;
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (!isIntact(stack)) continue;
            for (Item piece : pieces) {
                if (piece != null && stack.itemID == piece.itemID) {
                    ++count;
                    break;
                }
            }
        }
        return count;
    }

    public static boolean hasSuppliedSunSet(EntityLivingBase wearer) {
        if (!isWearingCompleteSunSet(wearer)) return false;
        ItemStack reservoir = wearer.getCurrentItemOrArmor(3);
        return reservoir != null && reservoir.getItem() instanceof ItemDivingGear gear
                && gear.getStoredAir(reservoir) > 0;
    }

    public static boolean isProtectedFromNetherAmbientHeat(EntityLivingBase wearer) {
        return isWearingCompleteHeatResistantSet(wearer)
                || isWearingCompleteDeadzoneSet(wearer)
                || isWearingCompleteSunSet(wearer)
                || isWearingCompleteCoresteelSet(wearer) && getCoresteelRemainingHeatCapacity(wearer) > 0;
    }

    public static boolean isWearingClearVisionMask(EntityLivingBase wearer) {
        ItemStack helmet = wearer.getCurrentItemOrArmor(4);
        return isIntact(helmet, NMItems.divingMask) || isIntact(helmet, NMItems.sunVisor)
                || isIntact(helmet, NMItems.quartzglassHelmet);
    }

    public static int getCoresteelRemainingHeatCapacity(EntityLivingBase wearer) {
        int remaining = 0;
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof ItemCoresteelArmor armor) {
                remaining += armor.getHeatCapacity() - armor.getStoredHeat(stack);
            }
        }
        return remaining;
    }

    public static boolean addCoresteelHeat(EntityLivingBase wearer, int amount) {
        if (!isWearingCompleteCoresteelSet(wearer) || amount <= 0
                || getCoresteelRemainingHeatCapacity(wearer) < amount) return false;
        int totalCapacity = getCoresteelRemainingHeatCapacity(wearer);
        int remaining = amount;
        for (int slot = 1; slot <= 4 && remaining > 0; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            ItemCoresteelArmor armor = (ItemCoresteelArmor)stack.getItem();
            int available = armor.getHeatCapacity() - armor.getStoredHeat(stack);
            int share = Math.min(available, Math.round((float)amount * available / totalCapacity));
            remaining -= armor.addHeat(stack, Math.min(share, remaining));
        }
        for (int slot = 1; slot <= 4 && remaining > 0; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            ItemCoresteelArmor armor = (ItemCoresteelArmor)stack.getItem();
            remaining -= armor.addHeat(stack, remaining);
        }
        return remaining <= 0;
    }

    /**
     * Removes one shared cooling budget from the suit. This makes a single hot piece cool much
     * faster than a fully heated set, while keeping equally heated pieces at similar percentages.
     */
    public static void coolCoresteel(EntityLivingBase wearer, int amount) {
        if (amount <= 0) return;
        int totalHeat = 0;
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof ItemCoresteelArmor armor) {
                totalHeat += armor.getStoredHeat(stack);
            }
        }

        if (totalHeat <= 0) return;
        int remaining = Math.min(amount, totalHeat);
        for (int slot = 1; slot <= 4; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof ItemCoresteelArmor armor) {
                int stored = armor.getStoredHeat(stack);
                int share = Math.min(stored, Math.round((float)amount * stored / totalHeat));
                int cooled = Math.min(share, remaining);
                armor.setStoredHeat(stack, stored - cooled);
                remaining -= cooled;
            }
        }
        for (int slot = 1; slot <= 4 && remaining > 0; ++slot) {
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if (isIntact(stack) && stack.getItem() instanceof ItemCoresteelArmor armor) {
                int stored = armor.getStoredHeat(stack);
                if (stored > 0) {
                    int cooled = Math.min(stored, remaining);
                    armor.setStoredHeat(stack, stored - cooled);
                    remaining -= cooled;
                }
            }
        }
    }

    public static float getAdditionalFireTimeReduction(EntityLivingBase wearer) {
        if (isWearingCompleteBlackglassSet(wearer)) return 0.45F;
        if (isWearingCompleteQuartzglassSet(wearer)) return 0.35F;
        return 0.0F;
    }

    public static boolean isIntact(ItemStack stack) {
        return stack != null && (!stack.isItemStackDamageable() || stack.getItemDamage() < stack.getMaxDamage());
    }

    private static boolean isIntact(ItemStack stack, Item expected) {
        return expected != null && isIntact(stack) && stack.itemID == expected.itemID;
    }

    private static boolean hasSet(EntityLivingBase wearer, Item helmet, Item chest, Item legs, Item boots) {
        return isIntact(wearer.getCurrentItemOrArmor(4), helmet)
                && isIntact(wearer.getCurrentItemOrArmor(3), chest)
                && isIntact(wearer.getCurrentItemOrArmor(2), legs)
                && isIntact(wearer.getCurrentItemOrArmor(1), boots);
    }
}
