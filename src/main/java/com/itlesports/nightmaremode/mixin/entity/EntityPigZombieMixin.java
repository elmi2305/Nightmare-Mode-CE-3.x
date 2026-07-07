package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;

import static com.itlesports.nightmaremode.util.NMFields.POSTWITHER;

@Mixin(EntityPigZombie.class)
public class EntityPigZombieMixin extends EntityZombie {
    @Shadow private int angerLevel;

    public EntityPigZombieMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        if (NMUtils.getIsBloodMoon()) {
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30);
        }
        if(NMEvents.SimpleEvent.HELL.isActive()){
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(50);
        }
    }
    @Override
    public float getAIMoveSpeed() {
        return NMEvents.SimpleEvent.HELL.isActive() ? 0.22f + this.rand.nextFloat() * 0.1f : super.getAIMoveSpeed();
    }

    @Override
    public Entity findPlayerToAttack() {
        if(NMEvents.SimpleEvent.HELL.isActive()){
            this.angerLevel = 1200;
            EntityPlayer p = this.worldObj.getClosestVulnerablePlayerToEntity(this, 50);
            if (p != null) {
                this.entityToAttack = p;
                return p;
            }
            return null;
        }
        return this.angerLevel == 0 ? null : this.worldObj.getClosestVulnerablePlayerToEntity(this, 30);
    }

    @Inject(method = "getCanSpawnHere", at = @At("RETURN"), cancellable = true)
    private void spawnInOverworld(CallbackInfoReturnable<Boolean> cir)
    {
        if(this.dimension == 0 && !NMEvents.SimpleEvent.HELL.isActive()){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void attackNearestPlayer(CallbackInfo ci){
        if (this.entityToAttack == null && (this.ticksExisted + this.entityId) % 20 == 0) { // using entityID to throttle the checks so all pigmen don't check on the same tick
            if(NMUtils.getIsBloodMoon()){
                EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, 30);
                if (player != null) {
                    this.entityToAttack = player;
                }
            } else {
                double baseRange = (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 8.0 : 5.0) + (NMUtils.getIsMobEclipsed(this) ? 3 : 0);
                if(NMEvents.SimpleEvent.HELL.isActive()){
                    baseRange = 50;
                }
                EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, baseRange);
                if(player != null){
                    int goldArmorCount = this.countGoldArmor(player);
                    // range values for gold armor: 0 = 8, 1 = 4, 2 = 2, 3 = 1, 4 = 1/2
                    double actualRange = baseRange / (1 << goldArmorCount);
                    if(player.getDistanceSqToEntity(this) <= actualRange * actualRange){
                        this.entityToAttack = player;
                    }
                }
            }
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        boolean isEclipse = NMUtils.getIsMobEclipsed(this);

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((16 + 4 * NMUtils.getWorldProgress() + (isEclipse ? 10 : 0)) * NMUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((3 + 2 * NMUtils.getWorldProgress()) * NMUtils.getNiteMultiplier());
        if(NMEvents.SimpleEvent.HELL.isActive()){
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.1d + this.rand.nextDouble() * 0.1d);
        }
    }

    @Unique private boolean isValidForEventLoot = false;
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void storeLastHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
    }


    @Inject(method = "dropFewItems", at = @At("HEAD"), cancellable = true)
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if(NMEvents.SimpleEvent.HELL.isActive()) {
            if (!this.isValidForEventLoot) {
                ci.cancel();
                return;
            }

            if(this.rand.nextInt(32) == 0){
                this.dropItem(NMItems.hellGem.itemID, 1);
                ci.cancel();
                return;
            }
        }
        if (bKilledByPlayer && this.isValidForEventLoot) {
            int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
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
            if (NMUtils.getIsMobEclipsed(this) && (NightmareMode.totalEclipse || NMUtils.getWorldProgress() > POSTWITHER)) {
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
        if(this.rand.nextInt(8) == 0 || NMUtils.getIsMobEclipsed(this)) {
            if(!this.isValidForEventLoot && NMEvents.SimpleEvent.HELL.isActive()) return;
            super.dropEquipment(par1, par2);
        }
    }

    @Override
    public boolean isAIEnabled() {
        return NMEvents.SimpleEvent.HELL.isActive();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,6);
    }

    @Override
    public boolean attackEntityAsMob(Entity attackedEntity) {
        if(NMUtils.getIsMobEclipsed(this) && attackedEntity instanceof EntityPlayer){

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

    @Unique private static HashSet<Integer> illegalItemList = new HashSet<>(16);
    @Unique private static @NotNull HashSet<Integer> getIllegalItems() {
        if (illegalItemList.isEmpty()) {
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
        }

        return illegalItemList;
    }
    @Unique private static boolean isHoldingWeapon(EntityZombie mob){
        return mob.getHeldItem() != null && getIllegalItems().contains(mob.getHeldItem().itemID);
    }

    @Unique private int countGoldArmor(EntityPlayer p){
        int count = 0;
        for(int i = 1; i <= 4; i++){
            if(this.isGold(p.getCurrentItemOrArmor(i))){
                count++;
            }
        }
        return count;
    }

    @Unique private boolean isGold(ItemStack stack){
        if(stack == null) return false;
        return stack.getItem() instanceof ItemArmor armor && armor.getArmorMaterial() == EnumArmorMaterial.GOLD;
    }
}
