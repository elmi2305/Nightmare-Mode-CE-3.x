package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static com.itlesports.nightmaremode.util.NMFields.POSTWITHER;


@Mixin(EntityWitch.class)
public abstract class EntityWitchMixin extends EntityMob {
    @Unique private int minionCountdown = 0;
    public EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    private void stopWitchCarcassUpdate(CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass && carcass.nm$isCarcass()) {
            ci.cancel();
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        if (NightmareMode.magicMonsters) {
            return super.getCanSpawnHere();
        } else{
            return (int) this.posY >= this.worldObj.provider.getAverageGroundLevel() - 5 && super.getCanSpawnHere();
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,10);
    }

    @Unique private void summonMinion(EntityWitch witch, EntityPlayer player) {
        if (NMUtils.getWorldProgress() < 1 || this.timesSummoned >= 3) return;
        ++this.timesSummoned;
        for (int i = 0; i < 2; ++i) {
            EntitySilverfish minion = new EntitySilverfish(this.worldObj);
            minion.copyLocationAndAnglesFrom(witch);
            minion.entityToAttack = player;
            this.worldObj.spawnEntityInWorld(minion);
        }
    }
    @Unique private int timesSummoned = 0;


    @Unique private boolean isValidForEventLoot = false;

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean killedByPlayer, int looting, CallbackInfo ci) {
        if (!killedByPlayer) return;

        Random rand = this.rand;

        boolean eclipsed = NMUtils.getIsMobEclipsed(this);
        boolean validLoot = isValidForEventLoot;

        if (NightmareMode.magicMonsters) {
            for (int i = 0; i < 3; i++) {
                Item drop;

                if (eclipsed) {
                    drop = getRandomWeighted(rand, ECLIPSE_ITEMS, ECLIPSE_WEIGHTS);
                } else if (this.dimension == -1) {
                    drop = getRandomWeighted(rand, NETHER_ITEMS, NETHER_WEIGHTS);
                } else {
                    drop = getRandomWeighted(rand, OVERWORLD_ITEMS, OVERWORLD_WEIGHTS);
                }

                this.dropItem(drop.itemID, 1);
            }
        }

        // BLOOD MOON ORBS
        if (validLoot && NMUtils.getIsBloodMoon()) {
            int count = 6 + rand.nextInt(9); // 6–14
            if (looting > 0) count += rand.nextInt(looting + 1);

            for (int i = 0; i < count; i++) {
                this.dropItem(NMItems.bloodOrb.itemID, 1);
            }
        }

        // ECLIPSE BONUS DROPS
        if (eclipsed && validLoot && (NightmareMode.totalEclipse || NMUtils.getWorldProgress() > POSTWITHER)) {

            // Darksun fragments (improved chance logic)
            int attempts = looting * 2 + 1;
            for (int i = 0; i < attempts; i++) {
                if (rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (rand.nextBoolean()) break;
                }
            }

            // Void membrane drops
            int count = 1 + rand.nextInt(5);
            if (looting > 0) count += rand.nextInt(looting + 1);

            for (int i = 0; i < count; i++) {
                if (rand.nextInt(3) != 0) {
                    this.dropItem(NMItems.voidMembrane.itemID, 1);
                }
            }
        }
    }


    @Unique
    private static Item getRandomWeighted(Random rand, Item[] items, int[] weights) {
        int total = 0;
        for (int w : weights) total += w;

        int r = rand.nextInt(total);

        for (int i = 0; i < items.length; i++) {
            r -= weights[i];
            if (r < 0) return items[i];
        }

        return items[0]; // fallback
    }
    @Unique
    private static final Item[] OVERWORLD_ITEMS = {
            BTWItems.nitre, Item.rottenFlesh, Item.spiderEye, Item.fireballCharge,
            Item.clay, Item.enderPearl, BTWItems.witchWart, BTWItems.mysteriousGland,
            NMItems.calamari, Item.bone, Item.slimeBall, Item.potion,
            Item.fermentedSpiderEye, Item.dyePowder, Item.skull, Item.arrow,
            Item.bow, Item.plateIron, Item.bootsIron, Item.legsIron,
            Item.helmetIron, BTWItems.creeperOysters, Item.silk
    };

    @Unique
    private static final int[] OVERWORLD_WEIGHTS = {
            18, 20, 12, 4,
            13, 4, 8, 7,
            9, 15, 7, 9,
            6, 9, 3, 12,
            4, 2, 3, 2,
            4, 14, 14
    };

    @Unique
    private static final Item[] NETHER_ITEMS = {
            Item.blazeRod, Item.magmaCream, Item.ghastTear,
            Item.goldNugget, Item.swordGold, Item.plateGold, Item.legsGold
    };

    @Unique
    private static final int[] NETHER_WEIGHTS = {
            12, 12, 5,
            5, 1, 1, 1
    };
    @Unique
    private static final Item[] ECLIPSE_ITEMS = {
            NMItems.magicFeather, NMItems.creeperChop, NMItems.magicArrow,
            NMItems.bloodMilk, NMItems.calamari, NMItems.silverLump,
            BTWItems.soulFlux, NMItems.voidMembrane, NMItems.voidSack,
            NMItems.charredFlesh, NMItems.ghastTentacle, NMItems.creeperTear,
            NMItems.spiderFangs, NMItems.speedCoil, NMItems.waterRod,
            NMItems.elementalRod, NMItems.decayedFlesh
    };

    @Unique
    private static final int[] ECLIPSE_WEIGHTS = {
            5, 5, 5,
            2, 6, 7,
            9, 4, 5,
            3, 4, 3,
            4, 3, 9,
            2, 4
    };

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            int progress = NMUtils.getWorldProgress();
            double bloodMoonModifier = NMUtils.getBloodMoonModifier(1.5);
            int eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 10 : 0;

            boolean isEclipse = NMUtils.getIsMobEclipsed(this);
            boolean isBloodMoon = NMUtils.getIsBloodMoon();
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(NMUtils.getBalancedMobFollowRange(this.worldObj, 16.0d, progress, isBloodMoon, isEclipse));
            double niteMultiplier = NMUtils.getNiteMultiplier();
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((20.0 + progress * 2) * bloodMoonModifier + eclipseModifier) * niteMultiplier);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.3 * (1 + (niteMultiplier - 1) / 20));
        }
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void chanceToDropSpecialItems(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        for(int i = 0; i < 1 + iLootingModifier * 2; i++){
            if(this.rand.nextInt(Math.max(6 - iLootingModifier, 2)) == 0){
                this.dropItem(Item.expBottle.itemID, 1);
            }
        }

        if(this.rand.nextInt(NMUtils.getIsBloodMoon() ? 2 : 6) == 0){
            this.dropItem(BTWItems.witchWart.itemID, this.rand.nextInt(2));
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageMinionSummons(CallbackInfo ci){
        EntityWitch thisObj = (EntityWitch)(Object)this;

        if(NMUtils.getIsBloodMoon()) return;

        this.minionCountdown += thisObj.rand.nextInt(3 + NMUtils.getWorldProgress());
        if(this.minionCountdown > (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 600 : 1600) - (NMUtils.getIsMobEclipsed(this) ? 300 : 0)){
            if(thisObj.getAttackTarget() instanceof EntityPlayer player && !player.capabilities.isCreativeMode){
                this.summonMinion(thisObj, player);
                this.minionCountdown = this.rand.nextInt(15) * (10 - (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 0 : 10));
                // this formula produces 3 silverfish around every 300 ticks spent targeting the player
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(this.rand.nextInt(4) == 0 && NMUtils.getIsMobEclipsed(this)){
            int xOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+3);
            int zOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+3);

            int xValue = MathHelper.floor_double(this.posX) + xOffset;
            int zValue = MathHelper.floor_double(this.posZ) + zOffset;
            int yValue = MathHelper.floor_double(this.posY) + this.rand.nextInt(-2,2);
            if(this.worldObj.getBlockId(xValue,yValue,zValue) != 0){
                yValue = this.worldObj.getPrecipitationHeight(xValue,zValue);
                if (Math.abs(yValue - this.posY) > 5){
                    yValue = (int) this.posY;
                }
            }
            this.setPositionAndUpdate(xValue,yValue,zValue);
        }
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
        return super.attackEntityFrom(par1DamageSource, par2);
    }
}
