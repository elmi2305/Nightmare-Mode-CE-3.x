package com.itlesports.nightmaremode.mixin.entity;

import btw.item.BTWItems;
import btw.util.BTWSounds;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(EntityFishHook.class)
public abstract class EntityFishHookMixin extends Entity implements EntityFishHookAccessor {
    @Unique private static final FishingCatch[] JUNK_CATCHES = {
            catchOf(NMItems.bonusChestLoot, 12, false),
            catchOf(NMItems.scrapedBark, 5, false),
            catchOf(BTWItems.pointyStick, 3, false),
            catchOf(Item.stick, 4, false),
            catchOf(Item.itemsList[Block.dirt.blockID], 5, false),
            catchOf(Item.itemsList[Block.sand.blockID], 5, false),
            catchOf(Item.itemsList[Block.gravel.blockID], 5, false)
    };

    // Each biome table contains three regular fish and one deliberately scarce trophy fish.
    @Unique private static final FishingCatch[] OCEAN_CATCHES = {
            catchOf(NMItems.mackerel, 26, false), catchOf(NMItems.cod, 22, false),
            catchOf(NMItems.tuna, 14, false), catchOf(NMItems.swordfish, 2, true)
    };
    @Unique private static final FishingCatch[] RIVER_CATCHES = {
            catchOf(NMItems.bass, 26, false), catchOf(NMItems.trout, 22, false),
            catchOf(NMItems.carp, 14, false), catchOf(NMItems.goldenCarp, 2, true)
    };
    @Unique private static final FishingCatch[] SWAMP_CATCHES = {
            catchOf(NMItems.mudfish, 26, false), catchOf(NMItems.catfish, 22, false),
            catchOf(NMItems.swampEel, 14, false), catchOf(NMItems.alligatorGar, 2, true)
    };
    @Unique private static final FishingCatch[] JUNGLE_CATCHES = {
            catchOf(NMItems.piranha, 26, false), catchOf(NMItems.neonTetra, 22, false),
            catchOf(NMItems.jungleCatfish, 14, false), catchOf(NMItems.arapaima, 2, true)
    };
    @Unique private static final FishingCatch[] COLD_CATCHES = {
            catchOf(NMItems.salmon, 26, false), catchOf(NMItems.perch, 22, false),
            catchOf(NMItems.icefish, 14, false), catchOf(NMItems.frostfish, 2, true)
    };
    @Unique private static final FishingCatch[] DESERT_CATCHES = {
            catchOf(NMItems.desertMinnow, 26, false), catchOf(NMItems.sandfish, 22, false),
            catchOf(NMItems.tilapia, 14, false), catchOf(NMItems.duneKoi, 2, true)
    };

    @Shadow public EntityPlayer angler;
    @Shadow public Entity bobber;

    @Unique private FishingCatch selectedCatch = catchOf(Item.fishRaw, 1, false);

    public EntityFishHookMixin(World world) {
        super(world);
    }

    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 8))
    private int increaseBiteOdds(int constant) {
        return 2;
    }

    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 4))
    private int biteChanceMultiplierDay(int constant) {
        return 20;
    }

    @Redirect(method = "catchFish", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityFishHook;bobber:Lnet/minecraft/src/Entity;", ordinal = 0))
    private Entity cannotHookEnemies(EntityFishHook instance) {
        return null;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageFishingEnemies(CallbackInfo ci) {
        if (this.bobber instanceof EntityMob && this.angler != null) {
            this.angler.getHeldItem().attemptDamageItem(4, this.rand);
            this.angler.playSound("random.splash", 0.5f, 2.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
            this.angler.dropOneItem(false);
        }
    }

    @ModifyArg(method = "catchFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;)V", ordinal = 1))
    private Item useSelectedCatch(Item item) {
        return this.selectedCatch.item;
    }

    @Inject(method = "catchFish", at = @At("TAIL"))
    private void trackSkillFishing(CallbackInfoReturnable<Integer> cir) {
        if (this.angler != null && cir.getReturnValueI() > 0) {
            SkillHandler.incrementFishCaught(this.angler, this.selectedCatch.rare);
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityFishHook;playSound(Ljava/lang/String;FF)V"))
    private void playCatchSoundAtPlayer(EntityFishHook instance, String sound, float volume, float pitch) {
        this.selectedCatch = this.selectCatch();
        if (this.selectedCatch.rare) {
            instance.worldObj.playSoundAtEntity(instance.angler, BTWSounds.GEM_STEP.sound(), 2f, 1f + (float)this.rand.nextGaussian());
        } else {
            instance.worldObj.playSoundAtEntity(instance.angler, sound, volume, pitch);
        }
    }

    @Unique
    private FishingCatch selectCatch() {
        FishingCatch[] biomeCatches = this.getBiomeCatches();
        int totalWeight = totalWeight(JUNK_CATCHES) + totalWeight(biomeCatches);
        int roll = this.rand.nextInt(totalWeight);

        for (FishingCatch catchEntry : JUNK_CATCHES) {
            if ((roll -= catchEntry.weight) < 0) {
                return catchEntry;
            }
        }
        for (FishingCatch catchEntry : biomeCatches) {
            if ((roll -= catchEntry.weight) < 0) {
                return catchEntry;
            }
        }
        return biomeCatches[0];
    }

    @Unique
    private FishingCatch[] getBiomeCatches() {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
        String biomeName = biome.biomeName.toLowerCase(Locale.ROOT);
        if (biomeName.contains("ocean") || biomeName.contains("beach")) return OCEAN_CATCHES;
        if (biomeName.contains("swamp")) return SWAMP_CATCHES;
        if (biomeName.contains("jungle")) return JUNGLE_CATCHES;
        if (biomeName.contains("taiga") || biomeName.contains("ice") || biomeName.contains("frozen")) return COLD_CATCHES;
        if (biomeName.contains("desert")) return DESERT_CATCHES;
        return RIVER_CATCHES;
    }

    @Unique
    private static int totalWeight(FishingCatch[] catches) {
        int total = 0;
        for (FishingCatch catchEntry : catches) total += catchEntry.weight;
        return total;
    }

    @Unique
    private static FishingCatch catchOf(Item item, int weight, boolean rare) {
        return new FishingCatch(item, weight, rare);
    }

    private static final class FishingCatch {
        private final Item item;
        private final int weight;
        private final boolean rare;

        private FishingCatch(Item item, int weight, boolean rare) {
            this.item = item;
            this.weight = weight;
            this.rare = rare;
        }
    }
}
