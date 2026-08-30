package com.itlesports.nightmaremode.mixin.entity;

import btw.item.BTWItems;
import btw.util.BTWSounds;
import api.world.WorldUtils;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemUpgradeableFishingRod;
import com.itlesports.nightmaremode.crafting.recipe.types.FishingRodUpgradeRecipe;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.elements.FishingCatch;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.objectweb.asm.Opcodes;

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
    @Unique private static final FishingCatch[] NETHER_CATCHES = {
            catchOf(NMItems.obsidianShard, 2, false),
            catchOf(NMItems.ashClump, 8, false),
            catchOf(NMItems.netherrackChunk, 12, false)
    };
    @Unique private static final FishingCatch[] BAITED_NETHER_CATCHES = {
            catchOf(NMItems.obsidianShard, 2, false),
            catchOf(NMItems.ashClump, 8, false),
            catchOf(NMItems.lavafish, 5, false),
            catchOf(NMItems.netherrackChunk, 12, false)
    };

    @Shadow public EntityPlayer angler;
    @Shadow public Entity bobber;
    @Shadow private boolean isBaited;

    @Unique private FishingCatch selectedCatch = catchOf(Item.fishRaw, 1, false);
    @Unique private boolean selectedCatchIsFish;
    @Unique private boolean caughtFishThisCast;

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

    @ModifyArg(method = "checkForBite", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"), index = 0)
    private int increaseLavaFishingBiteChance(int odds) {
        if (this.hasRodUpgrade("IfhyFishingLure")) {
            return Math.max(1, odds / 2);
        }
        if (this.isNetherFishing()) {
            return Math.max(1, odds / 6);
        }
        float availability = ChunkAttributeManager.getFishAvailability(
                this.worldObj,
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posZ)
        );
        return Math.max(1, Math.round(odds * (1.0F + (1.0F - availability) * 7.0F)));
    }

    @Inject(method = "checkForBite", at = @At("HEAD"), cancellable = true)
    private void preventBitesInDepletedChunks(CallbackInfoReturnable<Boolean> cir) {
        if (!this.isNetherFishing() && !ChunkAttributeManager.hasFish(
                this.worldObj,
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posZ)
        )) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "checkForBite", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;canBlockSeeTheSky(III)Z"))
    private boolean allowLavaFishingUnderNetherCeiling(World world, int x, int y, int z) {
        return this.isNetherFishing() || world.canBlockSeeTheSky(x, y, z);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isAABBInMaterial(Lnet/minecraft/src/AxisAlignedBB;Lnet/minecraft/src/Material;)Z"))
    private boolean treatLavaAsFishingFluid(World world, AxisAlignedBB box, Material material) {
        return world.isAABBInMaterial(box, this.isNetherFishing() ? Material.lava : material);
    }

    @Redirect(method = "isBodyOfWaterLargeEnoughForFishing", at = @At(value = "INVOKE", target = "Lapi/world/WorldUtils;isWaterSourceBlock(Lnet/minecraft/src/World;III)Z"))
    private boolean requireLargeLavaPool(World world, int x, int y, int z) {
        if (!this.isNetherFishing()) {
            return WorldUtils.isWaterSourceBlock(world, x, y, z);
        }
        return world.getBlockMaterial(x, y, z) == Material.lava && world.getBlockMetadata(x, y, z) == 0;
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;getItem()Lnet/minecraft/src/Item;", ordinal = 0))
    private Item recognizeUnbaitedNetherRod(ItemStack stack) {
        if (stack.getItem() == NMItems.netherFishingRod) return Item.fishingRod;
        if (stack.getItem() instanceof ItemUpgradeableFishingRod rod) {
            return rod.isBaited() ? BTWItems.baitedFishingRod : Item.fishingRod;
        }
        return stack.getItem();
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;getItem()Lnet/minecraft/src/Item;", ordinal = 1))
    private Item recognizeBaitedNetherRod(ItemStack stack) {
        if (stack.getItem() == NMItems.netherFishingRodBaited) return BTWItems.baitedFishingRod;
        if (stack.getItem() instanceof ItemUpgradeableFishingRod rod) {
            return rod.isBaited() ? BTWItems.baitedFishingRod : Item.fishingRod;
        }
        return stack.getItem();
    }

    @Redirect(method = "loseBait", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;getItem()Lnet/minecraft/src/Item;"))
    private Item recognizeBaitedNetherRodWhenLosingBait(ItemStack stack) {
        if (stack.getItem() == NMItems.netherFishingRodBaited) return BTWItems.baitedFishingRod;
        if (stack.getItem() instanceof ItemUpgradeableFishingRod rod) {
            return rod.isBaited() ? BTWItems.baitedFishingRod : Item.fishingRod;
        }
        return stack.getItem();
    }

    @Redirect(method = "loseBait", at = @At(value = "FIELD", target = "Lnet/minecraft/src/ItemStack;itemID:I", opcode = Opcodes.PUTFIELD))
    private void restoreUnbaitedNetherRod(ItemStack stack, int itemID) {
        if (stack.getItem() == NMItems.netherFishingRodBaited) {
            stack.itemID = NMItems.netherFishingRod.itemID;
        } else if (stack.getItem() instanceof ItemUpgradeableFishingRod rod && rod.isBaited()) {
            stack.itemID = rod.getCounterpartItemId();
        } else {
            stack.itemID = itemID;
        }
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

    @ModifyArg(method = "catchFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;)V", ordinal = 0))
    private Item useSelectedCatch(Item item) {
        return this.selectedCatch.item;
    }

    @Inject(method = "catchFish", at = @At("HEAD"))
    private void resetCaughtFishState(CallbackInfoReturnable<Integer> cir) {
        this.caughtFishThisCast = false;
    }

    @Redirect(method = "catchFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private boolean identifyCaughtFish(World world, Entity entity) {
        if (entity instanceof EntityItem) {
            ItemStack caughtStack = ((EntityItem)entity).getEntityItem();
            this.caughtFishThisCast = this.selectedCatchIsFish
                    && caughtStack != null
                    && caughtStack.itemID == this.selectedCatch.item.itemID;
        }
        return world.spawnEntityInWorld(entity);
    }

    @Inject(method = "catchFish", at = @At("TAIL"))
    private void trackSkillFishing(CallbackInfoReturnable<Integer> cir) {
        if (this.angler != null && cir.getReturnValueI() > 0) {
            SkillHandler.incrementFishCaught(this.angler, this.selectedCatch.rare);
            if (this.caughtFishThisCast) {
                ChunkAttributeManager.takeFish(
                        this.worldObj,
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posZ)
                );
            }
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
        if (this.hasRodUpgrade("IfhyFishingBell")) {
            instance.worldObj.playSoundAtEntity(instance.angler, "random.orb", 0.7F, 1.35F);
        }
        if (!instance.worldObj.isRemote && this.hasRodUpgrade("IfhyFishingAutoReel") && instance.angler != null) {
            int damage = instance.catchFish();
            ItemStack held = instance.angler.getCurrentEquippedItem();
            if (held != null) held.damageItem(damage, instance.angler);
        }
    }

    @Unique
    private FishingCatch selectCatch() {
        if (this.isNetherFishing()) {
            this.selectedCatchIsFish = false;
            FishingCatch[] catches = this.isBaited ? BAITED_NETHER_CATCHES : NETHER_CATCHES;
            int roll = this.rand.nextInt(totalWeight(catches));
            for (FishingCatch catchEntry : catches) {
                if ((roll -= catchEntry.weight) < 0) {
                    return catchEntry;
                }
            }
            return catches[0];
        }
        FishingCatch[] biomeCatches = this.getBiomeCatches();
        int localCapacity = ChunkAttributeManager.getLocalMaxFish(
                this.worldObj,
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posZ)
        );
        if (this.angler != null && this.rand.nextFloat() < SkillHandler.getPlayerData(this.angler).rareFishChanceBonus
                + (this.hasRodUpgrade("IfhyRareFishLure") ? 0.20F : 0.0F)) {
            this.selectedCatchIsFish = true;
            return biomeCatches[biomeCatches.length - 1];
        }
        int totalWeight = totalWeight(JUNK_CATCHES) + totalFishWeight(biomeCatches, localCapacity);
        int roll = this.rand.nextInt(totalWeight);

        for (FishingCatch catchEntry : JUNK_CATCHES) {
            if ((roll -= catchEntry.weight) < 0) {
                this.selectedCatchIsFish = false;
                return catchEntry;
            }
        }
        for (FishingCatch catchEntry : biomeCatches) {
            if ((roll -= adjustedFishWeight(catchEntry, localCapacity)) < 0) {
                this.selectedCatchIsFish = true;
                return catchEntry;
            }
        }
        this.selectedCatchIsFish = true;
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
    private static int totalFishWeight(FishingCatch[] catches, int capacity) {
        int total = 0;
        for (FishingCatch catchEntry : catches) {
            total += adjustedFishWeight(catchEntry, capacity);
        }
        return total;
    }

    @Unique
    private static int adjustedFishWeight(FishingCatch catchEntry, int capacity) {
        if (!catchEntry.rare) {
            return catchEntry.weight;
        }
        return Math.max(1, Math.min(5, Math.round(
                catchEntry.weight * (float)Math.sqrt(30.0D / Math.max(1, capacity))
        )));
    }

    @Unique
    private static FishingCatch catchOf(Item item, int weight, boolean rare) {
        return new FishingCatch(item, weight, rare);
    }

    @Unique
    private boolean isNetherFishing() {
        if (this.angler == null) {
            return false;
        }
        ItemStack held = this.angler.getCurrentEquippedItem();
        return held != null && (held.itemID == NMItems.netherFishingRod.itemID || held.itemID == NMItems.netherFishingRodBaited.itemID);
    }

    @Unique
    private boolean hasRodUpgrade(String key) {
        return this.angler != null && FishingRodUpgradeRecipe.hasUpgrade(this.angler.getCurrentEquippedItem(), key);
    }

}
