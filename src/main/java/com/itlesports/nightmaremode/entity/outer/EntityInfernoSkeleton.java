package com.itlesports.nightmaremode.entity.outer;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntityLivingData;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityInfernoSkeleton extends EntitySkeleton {
    public EntityInfernoSkeleton(World world) {
        super(world);
        this.setSkeletonType(NMFields.SKELETON_FIRE);
        this.setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
        this.setEquipmentDropChance(0, 0.0F);
        this.isImmuneToFire = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(32.0D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(8.0D);
    }

    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setSkeletonType(NMFields.SKELETON_FIRE);
        this.setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
        this.setEquipmentDropChance(0, 0.0F);
        return data;
    }
}
