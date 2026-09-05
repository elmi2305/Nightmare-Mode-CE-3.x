package com.itlesports.nightmaremode.item.items;

import net.minecraft.src.*;

import java.util.List;
import java.util.Locale;

public class ItemAlloyHorseArmor extends ItemAdvancedHorseArmor {
    public enum Material {
        NICKEL            (480 , 8 , 192, 0.08f, 1.05f, 6 , 0xCCC578),
        CARBON_IRON       (840 , 12, 192, 0.07f, 1.15f, 8 , 0x5f6169),
        REINFORCED_IRON   (1600, 16, 256, 0.06f, 1.25f, 11, 0xA9B5C1),
        TUNGSTEN          (1600, 24, 320, 0.04f, 1.40f, 16, 0x9AB4B5),
        QUARTZGLASS       (560 , 4 , 128, 0.19f, 0.90f, 5 , 0xFFF7F2),
        VERDANT           (910 , 6 , 384, 0.16f, 0.80f, 7 , 0x80D96C),
        BLACKGLASS        (1100, 18, 256, 0.08f, 1.20f, 14, 0x312236),
        CORESTEEL         (1800, 20, 448, 0.15f, 1.25f, 18, 0x59C4DD),
        DEADZONE_ALLOY    (2200, 22, 512, 0.22f, 1.35f, 20, 0x9C1010),
        SIGNAL_ALLOY      (680 , 8 , 256, 0.28f, 1.10f, 8 , 0xFF004C),
        AZURE_CERAMIC     (560 , 5 , 256, 0.15f, 0.90f, 9 , 0x1409E0),
        PRISMATIC         (1400, 7 , 320, 0.32f, 1.00f, 12, 0xDE9AED),
        PHASE_STEEL       (3200, 10, 640, 0.40f, 1.10f, 19, 0x8B0AA8),
        SEALED_QUICKSILVER(1550, 3 , 384, 0.43f, 1.30f, 10, 0xC9C9C9),
        ENDSTONE          (2200, 30, 768, 0.05f, 1.50f, 22, 0xD5D99B);

        public final int durability, weight, capacity, protection, color;
        public final float mobility, hunger;

        Material(int durability, int weight, int capacity, float mobility, float hunger, int protection, int color) {
            this.durability = durability;
            this.weight = weight;
            this.capacity = capacity;
            this.mobility = mobility;
            this.hunger = hunger;
            this.protection = protection;
            this.color = color;
        }

        public float speedMultiplier() {
            return 1.0f + mobility - weight * 0.01f;
        }

        public int armorIndex() {
            return ordinal() + 4;
        }

        public static Material fromArmorIndex(int index) {
            int ordinal = index - 4;
            return ordinal >= 0 && ordinal < values().length ? values()[ordinal] : null;
        }
    }

    private final Material material;

    public ItemAlloyHorseArmor(int id, Material material) {
        super(id, ArmorTier.IRON);
        this.material = material;
        setMaxDamage(material.durability);
        setTextureName("iron_horse_armor");
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return material.color;
    }

    @Override
    public int getMaxWheat() {
        return material.capacity;
    }

    @Override
    public int getFuelSlotCount() {
        return 1;
    }

    @Override
    public int getWheatCount(ItemStack stack) {
        return stack == null || !stack.hasTagCompound() ? 0
                : Math.max(0, Math.min(getMaxWheat(), stack.getTagCompound().getInteger("nmHorseFood")));
    }

    @Override
    public void setWheatCount(ItemStack stack, int count) {
        if (stack == null) return;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("nmHorseFood", Math.max(0, Math.min(count, getMaxWheat())));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP && getWheatCount(stack) < getMaxWheat()) {
            displayGUIAdvancedHorseArmor((EntityPlayerMP) player, stack);
        }
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
//        list.add(String.format(Locale.ROOT, I18n.getString("nm.horseArmor.durability"), Math.max(0, getMaxDamage() - stack.getItemDamage()), getMaxDamage()));
//        list.add(String.format(Locale.ROOT, I18n.getString("nm.horseArmor.weight"), material.weight));
//        list.add(String.format(Locale.ROOT, I18n.getString("nm.horseArmor.speed"), (material.speedMultiplier() - 1) * 100));
//        list.add(String.format(Locale.ROOT, I18n.getString("nm.horseArmor.hunger"), material.hunger));
//        list.add(String.format(Locale.ROOT, I18n.getString("nm.horseArmor.protection"), material.protection, material.protection * 4));
    }
}
