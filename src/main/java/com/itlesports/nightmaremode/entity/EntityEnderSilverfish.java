package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.world.BalanceProfile;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityEnderSilverfish extends EntitySilverfish {
    public EntityEnderSilverfish(World world) { super(world); }

    @Override protected void dropFewItems(boolean killedByPlayer, int looting) {
        this.dropItem(NMItems.enderShell.itemID, 1 + this.rand.nextInt(2) + (looting > 0 ? this.rand.nextInt(looting + 1) : 0));
        super.dropFewItems(killedByPlayer, looting);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(BalanceProfile.isCombatEasy() ? 20.0 : 42.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(2);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.7f);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(18.0);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.src.NBTTagCompound tag) {
        com.itlesports.nightmaremode.util.EasyCombatStats defaults = com.itlesports.nightmaremode.util.EasyCombatStats.beforeLoad(this);
        super.readEntityFromNBT(tag);
        com.itlesports.nightmaremode.util.EasyCombatStats.afterLoad(this, defaults);
    }
}
