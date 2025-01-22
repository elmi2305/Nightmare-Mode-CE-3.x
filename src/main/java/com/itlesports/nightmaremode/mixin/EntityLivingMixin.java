package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.EntityShadowZombie;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBase {
    public EntityLivingMixin(World par1World) {
        super(par1World);
    }

    @Shadow public abstract ItemStack getCurrentItemOrArmor(int par1);
    @Shadow public abstract void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack);
    @Shadow public abstract ItemStack getHeldItem();
    @Shadow protected abstract boolean canDespawn();
    @Shadow private boolean persistenceRequired;
    @Shadow protected float[] equipmentDropChances;
    
    @Unique private static Map<Class, Integer> classToDropMap = Map.of(
            EntityZombie.class, NMItems.decayedFlesh.itemID,
            EntityCreeper.class, NMItems.sulfur.itemID,
            EntitySpider.class, NMItems.spiderFangs.itemID,
            EntityChicken.class, NMItems.magicFeather.itemID,
            EntityPig.class, NMItems.creeperChop.itemID,
            EntitySkeleton.class, NMItems.witheredBone.itemID,

            EntityEnderman.class, Item.eyeOfEnder.itemID,
            EntityShadowZombie.class, NMItems.charredFlesh.itemID,
            EntitySilverfish.class, NMItems.silverLump.itemID,
            EntityWitch.class, NMItems.voidMembrane.itemID
    );
    @Unique private static int computeWhichItemToDrop(EntityLivingBase mob){
        if(classToDropMap.containsKey(mob.getClass())){
            return classToDropMap.get(mob.getClass());
        }
        if(mob instanceof EntityBlaze){
            return mob.isPotionActive(Potion.waterBreathing.id) ? NMItems.waterRod.itemID : NMItems.fireRod.itemID;
        } else if(mob instanceof EntityGhast){
            return mob.isPotionActive(Potion.moveSpeed.id) ? NMItems.creeperTear.itemID : NMItems.ghastTentacle.itemID;
        } else if(mob instanceof EntityPigZombie){
            return NMItems.decayedFlesh.itemID;
        } else if(mob instanceof EntityHorse){
            return NMItems.greg.itemID;
        }
        return 0;
    }


    @Redirect(method = "entityLivingAddRandomArmor", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F", ordinal = 0))
    private float returnRandomFloatButLower(Random rand){
        return (rand.nextFloat()-0.008F);
        // rand.nextFloat()   <  0.0033F ( original number: 0.0025F), from 1/400 to 1/303 chance
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (lootingLevel * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = computeWhichItemToDrop(this);

            if (itemID > 0) {
                int var4 = this.rand.nextInt(3);
                if (lootingLevel > 0) {
                    var4 += this.rand.nextInt(lootingLevel + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    if(this.rand.nextInt(3) == 0) continue;
                    this.dropItem(itemID, 1);
                }
            }
        }
    }

    @Inject(method = "entityLivingAddRandomArmor", at = @At("TAIL"))
    private void chanceToSpawnWithLeatherArmor(CallbackInfo ci) {
        if (this.worldObj != null) {
            float streakModifier = 0.0f;
            for (int i = 1; i <= 4; i++) {
                if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                    if(rand.nextFloat() < (0.04f + NightmareUtils.getWorldProgress(this.worldObj)*0.02) + streakModifier){
                        // 0.04f -> 0.06f -> 0.08f -> 0.10f
                        streakModifier += 0.05f;
                        List<ItemStack> leatherArmorList = getItemStacks();
                        this.setCurrentItemOrArmor(i, leatherArmorList.get(i-1));
                        this.equipmentDropChances[i] = 0f;
                    }
                }
            }
        }
    }

    @Inject(method = "despawnEntity", at = @At(value = "TAIL"))
    private void manageDespawnDuringBloodMoon(CallbackInfo ci){
        if (this.canDespawn() && !this.persistenceRequired && this.ticksExisted % 300 == 299 && NightmareUtils.getIsBloodMoon()) {
            EntityPlayer nearestPlayer = this.worldObj.getClosestVulnerablePlayer(this.posX, this.posY, this.posZ, 128);

            if (nearestPlayer != null) {
                double verticalDistance = Math.abs(nearestPlayer.posY - this.posY);

                if (verticalDistance > 20) {
                    boolean isOnNonWoodMaterial = this.worldObj.getBlockMaterial(
                            (int) this.posX,
                            (int) (this.posY - 1),
                            (int) this.posZ
                    ) != Material.wood;

                    if (rand.nextInt(3) == 0 && isOnNonWoodMaterial) {
                        this.setDead();
                    }
                }
            } else {
                this.setDead();
            }
        }
    }

    @Unique
    private static @NotNull List<ItemStack> getItemStacks() {
        ItemStack boots = new ItemStack(Item.bootsLeather);
        ItemStack pants = new ItemStack(Item.legsLeather);
        ItemStack chest = new ItemStack(Item.plateLeather);
        ItemStack helmet = new ItemStack(Item.helmetLeather);

        List<ItemStack> leatherArmorList = new ArrayList<>(4);
        leatherArmorList.add(boots);
        leatherArmorList.add(pants);
        leatherArmorList.add(chest);
        leatherArmorList.add(helmet);
        return leatherArmorList;
    }
}
