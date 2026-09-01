package com.itlesports.nightmaremode.item.items.template;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.AttributeModifier;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SharedMonsterAttributes;
import com.itlesports.nightmaremode.util.interfaces.NoMeleeKnockback;

public class ItemKnife extends NMItem implements NoMeleeKnockback {
    public static final int TIER_FISTS = 0;
    public static final int TIER_STONE = 1;
    public static final int TIER_IRON = 2;
    public static final int TIER_DIAMOND = 3;

    private final int processingTicks;
    private final int harvestTier;
    private float damageVsEntity;

    public ItemKnife(int id, int processingTicks, int harvestTier, int durability) {
        super(id);
        this.processingTicks = processingTicks;
        this.harvestTier = harvestTier;
        this.setMaxDamage(durability);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public int getProcessingTicks() {
        return this.processingTicks;
    }

    public int getHarvestTier() {
        return this.harvestTier;
    }

    public ItemKnife setDamageVsEntity(float damageVsEntity) {
        this.damageVsEntity = damageVsEntity - 1;
        return this;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap modifiers = HashMultimap.create();
        modifiers.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(Item.field_111210_e, "Weapon modifier", this.damageVsEntity, 0));
        return modifiers;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase defendingEntity, EntityLivingBase attackingEntity) {
        stack.damageItem(2, attackingEntity);
        return true;
    }

    public static ItemKnife fromStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemKnife knife ? knife : null;
    }
}
