package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityStoneZombie extends EntityZombie {
    private double layer = 0;

    public EntityStoneZombie(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        if (this.worldObj != null) {
            boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);
            boolean isBloodMoon = NightmareUtils.getIsBloodMoon();

            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isBloodMoon ? 32 : 28) + progress * (isBloodMoon ? 8 : 6) + (isEclipse ? 20 : 0)) * NightmareUtils.getNiteMultiplier());
        }
    }

    @Override
    public void onUpdate() {
        if(this.layer == 0){
            this.layer = this.posY;
        }
        super.onUpdate();
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(par1DamageSource.getEntity() instanceof EntityPlayer target){
            Item heldItem = null;
            if(target.getHeldItem() != null){
                heldItem = target.getHeldItem().getItem();
            }

            int progress =  NightmareUtils.getWorldProgress(this.worldObj);

            if(heldItem != null){
                if (itemsThatCanAttackTheZombie.contains(heldItem.itemID)) {
                    return super.attackEntityFrom(par1DamageSource, par2);
                } else{
                    this.playSound("random.break",0.5f, 0.8f);
                    target.getHeldItem().attemptDamageItem(this.rand.nextInt(4 + progress * 2) + progress + 1, this.rand);
                }
            }
            return false;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected void addRandomArmor() {}

    private static final List<Integer> itemsThatCanAttackTheZombie = new ArrayList<>(Arrays.asList(
            Item.pickaxeStone.itemID,
            Item.pickaxeIron.itemID,
            Item.pickaxeDiamond.itemID,
            BTWItems.steelPickaxe.itemID,
            BTWItems.mattock.itemID,
            NMItems.bloodPickaxe.itemID
    ));

    @Override
    protected int getDropItemId() {
        return BTWItems.stone.itemID;
    }

    @Override
    public boolean getCanSpawnHere() {
        return (NightmareMode.moreVariants || NightmareMode.isAprilFools) && super.getCanSpawnHere() && this.posY < 63;
    }

    @Override
    protected void entityLivingDropFewItems(boolean par1, int par2) {
        if(isBetween((int) this.layer, 55, 70)){
            if(this.rand.nextInt(6) == 0){
                this.dropItem(BTWItems.ironOreChunk.itemID,1);
            }
        } else if (isBetween((int) this.layer, 40, 55)){
            if(this.rand.nextInt(4) == 0) {
                this.dropItem(BTWItems.ironOreChunk.itemID, 1);
                if (this.rand.nextInt(4) == 0) {
                    this.dropItem(BTWItems.ironOreChunk.itemID, 2);
                }
            }
        } else if (isBetween((int) this.layer, 30, 40)) {
            if(this.rand.nextInt(8) == 0){
                this.dropItem(BTWItems.ironOreChunk.itemID, 1);
            }
            if(this.rand.nextInt(8) == 0){
                this.dropItem(BTWItems.goldOreChunk.itemID, 1);
            }
        } else if (isBetween((int) this.layer, 16, 30)) {
            if(this.rand.nextInt(8) == 0){
                this.dropItem(BTWItems.goldOreChunk.itemID, 1);
            }
            if(this.rand.nextInt(8) == 0){
                this.dropItem(Item.redstone.itemID, 1);
            }
        } else if (isBetween((int) this.layer,0,16)){
            if(this.rand.nextInt(16) == 0){
                this.dropItem(Item.diamond.itemID, 1);
            }
        }
        super.entityLivingDropFewItems(par1, par2);
    }
    private static boolean isBetween(int num, int min, int max){
        return num >= min && num <= max;
    }
}
