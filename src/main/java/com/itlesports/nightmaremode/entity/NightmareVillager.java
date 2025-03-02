package com.itlesports.nightmaremode.entity;

import btw.entity.mob.villager.trade.VillagerTrade;
import btw.util.sounds.BTWSoundManager;
import net.minecraft.src.*;

import java.util.HashSet;

public class NightmareVillager extends EntityVillager {
    public NightmareVillager(World par1World) {
        super(par1World, 5);
    }
    @Override
    public int getProfessionFromClass() {
        return 5;
    }
    @Override
    public boolean getCanCreatureTypeBePossessed() {
        return false;
    }
    @Override
    protected void checkForProfessionTrades(MerchantRecipeList recipeList, int availableTrades) {
        HashSet<VillagerTrade> tradeList = new HashSet<VillagerTrade>();
        for (VillagerTrade entry : tradeByProfessionList.get(this.getProfessionFromClass())) {
            if (entry.level > this.getCurrentTradeLevel() || entry.isMandatory() || !entry.canBeAdded(this)) continue;
            tradeList.add(entry);
        }
        int maxAttempts = 50;
        for (int currentAttempts = 0; availableTrades > 0 && currentAttempts < maxAttempts; ++currentAttempts) {
            VillagerTrade trade = this.getRandomTradeFromAdjustedWeight(tradeList);
            if (this.doesRecipeListAlreadyContainRecipe(trade)) continue;
            MerchantRecipe recipe = trade.generateRecipe(this.rand);
            recipeList.add(recipe);
            --availableTrades;
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.27d);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(800.0);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(par1DamageSource.getSourceOfDamage() instanceof EntityLivingBase attacker){
            attacker.attackEntityFrom(DamageSource.generic, 6f);
            if(attacker instanceof EntityLiving){
                ((EntityLiving) attacker).setAttackTarget(null);
                this.setLastAttacker(attacker);
            }
        }
        this.heal(par2 * 3);
        this.playSound(BTWSoundManager.WITCH_IDLE.sound(),2f,0.1f);
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    @Override
    public boolean getCanBeHeadCrabbed(boolean bSquidInWater) {
        return false;
    }
}
