package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import com.itlesports.nightmaremode.util.interfaces.IArmorStatus;
import net.minecraft.src.*;

import java.util.List;

public class ItemDivingGear extends ItemOxygenGear implements INetherItem, IArmorStatus {
    public static final String AIR_TAG = "ifhyAir";

    private final int airCapacity;
    private final String wornTexturePrefix;
    private final String setBonusKey;
    private final int enchantability;
    private final int repairItemID;

    public ItemDivingGear(int id, int armorType, int weight, int maxUses, float oxygenDrainReduction,
                          int airCapacity, String wornTexturePrefix, String setBonusKey) {
        this(id, armorType, armorType == 0 ? 2 : 6, weight, maxUses, 8, 0.0D, -1,
                oxygenDrainReduction, airCapacity, wornTexturePrefix, setBonusKey);
    }

    public ItemDivingGear(int id, int armorType, int protection, int weight, int maxUses,
                          int enchantability, double knockbackResistance, int repairItemID,
                          float oxygenDrainReduction, int airCapacity, String wornTexturePrefix,
                          String setBonusKey) {
        super(id, armorType, weight, maxUses, oxygenDrainReduction, knockbackResistance);
        this.damageReduceAmount = protection;
        this.airCapacity = Math.max(0, airCapacity);
        this.wornTexturePrefix = wornTexturePrefix;
        this.setBonusKey = setBonusKey;
        this.enchantability = enchantability;
        this.repairItemID = repairItemID;
    }

    public int getAirCapacity() {
        return this.airCapacity;
    }

    /** Only tanks and reservoirs are compressed-air containers; masks merely seal their breathing loop. */
    public boolean storesAir() {
        return this.airCapacity > 0;
    }

    public int getStoredAir(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
                ? Math.max(0, Math.min(this.airCapacity, stack.getTagCompound().getInteger(AIR_TAG)))
                : 0;
    }

    public void setStoredAir(ItemStack stack, int air) {
        if (stack == null || this.airCapacity <= 0) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger(AIR_TAG, Math.max(0, Math.min(this.airCapacity, air)));
    }

    public boolean consumeAir(ItemStack stack, int amount) {
        int stored = this.getStoredAir(stack);
        if (stored <= 0 || amount <= 0) return false;
        this.setStoredAir(stack, Math.max(0, stored - amount));
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!this.storesAir() || this.getStoredAir(stack) >= this.airCapacity) return stack;

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY + player.getEyeHeight());
        int z = MathHelper.floor_double(player.posZ);
        boolean surface = player.dimension == 0 && y >= 43 && y <= 120
                && !player.isInsideOfMaterial(Material.water) && world.canBlockSeeTheSky(x, y, z);
        if (!surface) {
            if (!world.isRemote) player.addChatMessage(I18n.getString("item.ifhyDivingFill.invalid"));
            return stack;
        }

        float efficiency = y <= 63 ? (y - 43) / 20.0F : (120 - y) / 57.0F;
        int amount = Math.max(0, Math.round(200.0F * efficiency));
        if (amount <= 0) {
            if (!world.isRemote) player.addChatMessage(I18n.getString("item.ifhyDivingFill.noPressure"));
            return stack;
        }

        if (!world.isRemote) {
            this.setStoredAir(stack, this.getStoredAir(stack) + amount);
            world.playSoundAtEntity(player, "random.breath", 0.6F, 1.1F);
        }
        return stack;
    }

    @Override
    public long getItemRightClickCooldown() {
        return this.storesAir() ? 20L : 0L;
    }

    @Override
    public int getItemEnchantability() {
        return this.enchantability;
    }

    @Override
    public boolean getIsRepairable(ItemStack armor, ItemStack material) {
        return this.repairItemID >= 0 && material != null && material.itemID == this.repairItemID;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (this.airCapacity > 0) {
            tooltip.add(EnumChatFormatting.AQUA + I18n.getStringParams(
                    "item.ifhyDivingAir", this.getStoredAir(stack) / 20, this.airCapacity / 20));
        }
        if (this.setBonusKey != null && !this.setBonusKey.isEmpty()) {
            tooltip.add(I18n.getString(this.setBonusKey));
        }
    }

    @Override
    public String getWornTexturePrefix() {
        return this.wornTexturePrefix;
    }

    @Override
    public float getStatusFraction(ItemStack stack) {
        return !this.storesAir() ? 0.0F : (float)this.getStoredAir(stack) / (float)this.airCapacity;
    }

    @Override
    public int getStatusColor(ItemStack stack) {
        return 0x32D7E6;
    }

    @Override
    public int getStatusBackgroundColor() {
        return 0x12363B;
    }
}
