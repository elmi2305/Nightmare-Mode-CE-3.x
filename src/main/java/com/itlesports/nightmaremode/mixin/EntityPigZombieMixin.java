package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.behavior.ZombieBreakBarricadeBehaviorHostile;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityPigZombie.class)
public class EntityPigZombieMixin extends EntityZombie {
    public EntityPigZombieMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addBlockBreakingTask(World par1World, CallbackInfo ci){
        this.tasks.addTask(1, new ZombieBreakBarricadeBehaviorHostile(this));
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        if (NightmareUtils.getIsBloodMoon()) {
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30);
        }
    }

    @Override
    public Entity findPlayerToAttack() {
        return this.worldObj.getClosestVulnerablePlayerToEntity(this, 30);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void attackNearestPlayer(CallbackInfo ci){
        if (this.entityToAttack == null) {
            if(NightmareUtils.getIsBloodMoon()){
                EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, 30);
                if (player != null) {
                    this.entityToAttack = player;
                }
            } else {
                double range = (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 3.0 : 2.0) + (NightmareUtils.getIsMobEclipsed(this) ? 3 : 0);
                EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, range);
                if(player != null && this.isPlayerWearingGoldArmor(player)){
                    this.entityToAttack = player;
                }
            }
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((16 + 4 * NightmareUtils.getWorldProgress() + (isEclipse ? 10 : 0)) * NightmareUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((3 + 2 * NightmareUtils.getWorldProgress()) * NightmareUtils.getNiteMultiplier());
    }


    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if (bKilledByPlayer) {
            int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
            if (bloodOrbID > 0) {
                int var4 = this.rand.nextInt(2);
                // 0 - 1
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    this.dropItem(bloodOrbID, 1);
                }
            }
            if (NightmareUtils.getIsMobEclipsed(this)) {
                for(int i = 0; i < (iLootingModifier * 2) + 1; i++) {
                    if (this.rand.nextInt(8) == 0) {
                        this.dropItem(NMItems.darksunFragment.itemID, 1);
                        if (this.rand.nextBoolean()) {
                            break;
                        }
                    }
                }

                int itemID = NMItems.decayedFlesh.itemID;

                int var4 = this.rand.nextInt(3);
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    if(this.rand.nextInt(3) == 0) continue;
                    this.dropItem(itemID, 1);
                }
            }
        }
    }

    @Override
    protected void dropEquipment(boolean par1, int par2) {
        if(this.rand.nextInt(8) == 0) {
            super.dropEquipment(par1, par2);
        }
    }

    @ModifyArg(method = "dropFewItems", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int reduceDropRatesFromPigMen(int bound){
        return bound - 1;
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,6);
    }

    @Override
    public boolean attackEntityAsMob(Entity attackedEntity) {
        if(NightmareUtils.getIsMobEclipsed(this) && attackedEntity instanceof EntityPlayer){

            if(this.rand.nextInt(12) == 0){
                ((EntityPlayer) attackedEntity).addPotionEffect(new PotionEffect(Potion.poison.id, 60,0));
            }
            if(this.rand.nextInt(16) == 0 && ((EntityPlayer) attackedEntity).inventory.getCurrentItem() != null){
                if (isHoldingWeapon(this)) {
                    ((EntityPlayer) attackedEntity).dropOneItem(true);
                    this.worldObj.playSoundAtEntity(attackedEntity, "random.break", 0.5f, 2f);
                } else if (this.getHeldItem() == null){
                    this.setCurrentItemOrArmor(0,((EntityPlayer) attackedEntity).getHeldItem());
                    this.equipmentDropChances[0] = 1f;
                    ((EntityPlayer) attackedEntity).destroyCurrentEquippedItem();
                }
            }
        }
        if(this.getHeldItem() != null){
            this.swingItem();
        }
        return super.attackEntityAsMob(attackedEntity);
    }

    @Unique
    private static @NotNull List<Integer> getIllegalItems() {
        List<Integer> illegalItemList = new ArrayList<>(16);
        illegalItemList.add(Item.swordStone.itemID);
        illegalItemList.add(Item.swordIron.itemID);
        illegalItemList.add(Item.swordGold.itemID);
        illegalItemList.add(BTWItems.steelSword.itemID);
        illegalItemList.add(Item.axeStone.itemID);
        illegalItemList.add(Item.axeDiamond.itemID);
        illegalItemList.add(Item.axeIron.itemID);
        illegalItemList.add(Item.shovelIron.itemID);
        illegalItemList.add(Item.shovelStone.itemID);
        illegalItemList.add(Item.shovelGold.itemID);
        illegalItemList.add(Item.shovelDiamond.itemID);

        illegalItemList.add(BTWItems.boneClub.itemID);
        illegalItemList.add(Item.swordWood.itemID);
        illegalItemList.add(Item.swordDiamond.itemID);
        illegalItemList.add(Item.axeGold.itemID);
        illegalItemList.add(Item.pickaxeStone.itemID);

        return illegalItemList;
    }
    @Unique private static boolean isHoldingWeapon(EntityZombie mob){
        return mob.getHeldItem() != null && getIllegalItems().contains(mob.getHeldItem().itemID);
    }

    @Unique boolean isPlayerWearingGoldArmor(EntityPlayer player){
        return (player.getCurrentItemOrArmor(1) != null && player.getCurrentItemOrArmor(1).itemID == Item.bootsGold.itemID)
                || (player.getCurrentItemOrArmor(2) != null && player.getCurrentItemOrArmor(2).itemID == Item.legsGold.itemID)
                || (player.getCurrentItemOrArmor(3) != null && player.getCurrentItemOrArmor(3).itemID == Item.plateGold.itemID)
                || (player.getCurrentItemOrArmor(4) != null && player.getCurrentItemOrArmor(4).itemID == Item.helmetGold.itemID);
    }
}
