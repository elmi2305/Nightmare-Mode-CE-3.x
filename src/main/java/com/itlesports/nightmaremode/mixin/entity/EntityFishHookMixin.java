package com.itlesports.nightmaremode.mixin.entity;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.entity.EntityGiantSquid;
import com.itlesports.nightmaremode.fishing.*;
import com.itlesports.nightmaremode.network.FishingNet;
import api.world.WorldUtils;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemUpgradeableFishingRod;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.objectweb.asm.Opcodes;

@Mixin(EntityFishHook.class)
public abstract class EntityFishHookMixin extends Entity implements EntityFishHookAccessor, FishingHookControl {
    @Shadow public EntityPlayer angler;
    @Shadow public Entity bobber;
    @Shadow private boolean isBaited;
    @Shadow private boolean inGround;
    @Shadow private int ticksCatchable;
    @Shadow protected abstract void loseBait();
    @Shadow protected abstract boolean isBodyOfWaterLargeEnoughForFishing();

    @Unique private FishingGame fishingGame;
    @Unique private FishingPool fishingPool;
    @Unique private int submergedTicks;
    @Unique private int lastCatch;
    @Unique private int fishingRodSlot;
    @Unique private ItemStack fishingRod;
    @Unique private float fishingHealth;
    @Unique private volatile boolean cancelFishing;
    @Unique private volatile float fishingInputX = 400, fishingInputY = 42;
    @Unique private int fishingInputAge;
    @Unique private volatile boolean receivedFishingInput;
    @Unique private volatile boolean relativeFishingInput;
    @Unique private float fishingScroll;

    public EntityFishHookMixin(World world) { super(world); }

    @Inject(method = "checkForBite", at = @At("HEAD"), cancellable = true)
    private void replaceRandomBites(CallbackInfoReturnable<Boolean> cir) { cir.setReturnValue(false); }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void tickFishingMinigame(CallbackInfo ci) {
        if (worldObj.isRemote || isDead || !(angler instanceof EntityPlayerMP)) return;
        boolean submerged = !inGround && bobber == null && worldObj.isAABBInMaterial(boundingBox,
                isNetherFishing() ? Material.lava : Material.water) && isBodyOfWaterLargeEnoughForFishing();
        if (fishingGame == null) {
            if (!submerged) { submergedTicks = 0; fishingPool = null; return; }
            if (fishingPool == null) fishingPool = new FishingPool((EntityFishHook)(Object)this, isBaited);
            if (++submergedTicks < fishingPool.activationTicks()) return;
            fishingGame = new FishingGame(rand, fishingPool::next, fishingPool.stock(), fishingPool.arrivals(),
                    fishingPool.habitat.maxDepth, fishingPool.abundance(),
                    com.itlesports.nightmaremode.world.BalanceProfile.isCombatEasy(),
                    com.itlesports.nightmaremode.world.BalanceProfile.isLowProgression());
            fishingGame.fearMultiplier = fishingPool.has("IfhyFishingBell") ? 0.5F : 1;
            fishingGame.captureMultiplier = fishingPool.captureMultiplier();
            fishingGame.rodResistance = fishingPool.resistance();
            fishingGame.catchLimit = fishingPool.has("IfhyTwinHook") ? 2 : 1;
            fishingGame.twinHook = fishingPool.has("IfhyTwinHook");
            fishingGame.depthWinch = fishingPool.has("IfhyDepthWinch");
            fishingGame.lureRadius = FishingGame.LURE_RADIUS
                    * (fishingPool.has("IfhyWideHook") ? FishingTuning.WIDE_HOOK_RADIUS_MULTIPLIER : 1);
            fishingGame.descentMultiplier = fishingPool.habitat.descentMultiplier;
            fishingRodSlot = angler.inventory.currentItem;
            fishingRod = angler.getCurrentEquippedItem().copy();
            fishingHealth = angler.getHealth();
            if (btw.community.nightmaremode.NightmareMode.devMode) reportFishingHabitat();
        }
        if (receivedFishingInput) { fishingInputAge = 0; receivedFishingInput = false; }
        else fishingInputAge++;
        ItemStack held = angler.getCurrentEquippedItem();
        if (!submerged || cancelFishing || angler.isDead || angler.getHealth() < fishingHealth
                || angler.inventory.currentItem != fishingRodSlot || !ItemStack.areItemStacksEqual(held, fishingRod)
                || fishingInputAge > 200) {
            setDead();
            return;
        }
        fishingGame.stock = fishingPool.stock();
        fishingGame.arrivalTicks = fishingPool.arrivals();
        fishingGame.abundance = fishingPool.abundance();
        if (fishingGame.stock <= 0 && fishingGame.fighting == null) fishingGame.targets.removeIf(t -> t.spec.fish);
        if ((fishingGame.fighting != null) == relativeFishingInput) fishingGame.input(fishingInputX, fishingInputY);
        else if (fishingGame.fighting != null) fishingGame.input(0, 0);
        if (fishingGame.fighting != null) fishingInputX = fishingInputY = 0;
        fishingGame.scroll(consumeFishingScroll());
        FishingGame.Target caught = fishingGame.tick();
        if (fishingGame.failed) { loseFishingRod(); setDead(); return; }
        if (caught != null) {
            // reserve supply on the server before awarding anything, including simultaneous catches.
            if (caught.spec.fish && !ChunkAttributeManager.takeFish(worldObj,
                    MathHelper.floor_double(posX), MathHelper.floor_double(posZ))) {
                setDead();
                return;
            }
            awardFishingCatch(caught);
            lastCatch = caught.spec.itemId;
            loseBait();
            held = angler.getCurrentEquippedItem();
            if (held != null) held.damageItem(1, angler);
            fishingPool.setBaited(isBaited);
            fishingGame.stock = fishingPool.stock();
            if (held == null || held.stackSize <= 0 || !fishingGame.keepFishing(caught)) { setDead(); return; }
            fishingRod = held.copy();
            fishingInputX = fishingGame.lureX; fishingInputY = fishingGame.lureY;
            relativeFishingInput = false;
            consumeFishingScroll();
            FishingNet.sendCatch((EntityPlayerMP)angler, entityId, lastCatch);
            lastCatch = 0;
            FishingNet.sendState((EntityPlayerMP)angler, entityId, fishingGame, fishingPool.lava);
            return;
        }
        if (fishingGame.age % 2 == 0 || fishingGame.age == 1)
            FishingNet.sendState((EntityPlayerMP)angler, entityId, fishingGame, fishingPool.lava);
    }

    @Unique
    private void reportFishingHabitat() {
        FishingHabitat habitat = fishingPool.habitat;
        String offshore = habitat.offshore >= 1 ? "256+" : String.format(java.util.Locale.ROOT, "%.0f", habitat.offshore * 256);
        angler.sendChatToPlayer(ChatMessageComponent.createFromText(String.format(java.util.Locale.ROOT,
                "[Fishing] Chunk %d, %d (%s). Source depth: hook %.1f, nearby %.1f, effective %.1f blocks. "
                        + "Offshore: about %s blocks. Depth cap: %.0f m. Giant squid habitat: %s (needs %.0f blocks offshore and 175 m). Stock: %.0f%%.",
                MathHelper.floor_double(posX) >> 4, MathHelper.floor_double(posZ) >> 4, habitat.biome,
                habitat.hookSourceDepth, habitat.averageSourceDepth, habitat.sourceDepth, offshore, habitat.maxDepth,
                habitat.supportsGiantSquid() ? "yes" : "no", FishingTuning.GIANT_SQUID_MIN_OFFSHORE_BLOCKS, fishingGame.stock * 100)));
    }

    @Override public synchronized void nm$fishingInput(float x, float y, boolean cancel, boolean relative, float scroll) {
        if (fishingGame == null || !Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(scroll)) return;
        fishingInputX = x;
        fishingInputY = y;
        cancelFishing |= cancel;
        relativeFishingInput = relative;
        fishingScroll = relative || fishingGame.winchLocked() ? 0 : FishingGame.clamp(fishingScroll + scroll, -6, 6);
        receivedFishingInput = true;
    }

    @Inject(method = "isBodyOfWaterLargeEnoughForFishing", at = @At("HEAD"), cancellable = true)
    private void allowSmallerPoolsWithBetterRods(CallbackInfoReturnable<Boolean> cir) {
        if (angler == null) return;
        float rod = FishingPool.rodMultiplier(angler.getCurrentEquippedItem());
        if (rod > 0.8F) return;
        Material fluid = isNetherFishing() ? Material.lava : Material.water;
        cir.setReturnValue(FishingWaterRequirements.smallPool((x, y, z) -> worldObj.getBlockMaterial(x, y, z) == fluid
                        && worldObj.getBlockMetadata(x, y, z) == 0, rod,
                MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)));
    }

    @Unique
    private synchronized float consumeFishingScroll() {
        float scroll = fishingScroll;
        fishingScroll = 0;
        return scroll;
    }

    @Inject(method = "setDead", at = @At("HEAD"))
    private void closeFishingMinigame(CallbackInfo ci) {
        if (!worldObj.isRemote && fishingGame != null && angler instanceof EntityPlayerMP player) {
            FishingNet.sendEnd(player, entityId, lastCatch);
            fishingGame = null;
        }
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

    @Inject(method = "catchFish", at = @At("HEAD"))
    private void preventVanillaCatchBypass(CallbackInfoReturnable<Integer> cir) { ticksCatchable = 0; }

    @Unique
    private void awardFishingCatch(FishingGame.Target caught) {
        if (caught.spec.itemId < 0) {
            EntityLiving mob = caught.spec.itemId == FishingCreatures.GIANT_SQUID
                    ? new EntityGiantSquid(worldObj)
                    : new btw.entity.mob.BTWSquidEntity(worldObj);
            mob.setPosition(posX, posY + 1, posZ);
            for (int i = 0; i < 12; i++) {
                double angle = i * Math.PI / 6;
                mob.setPosition(angler.posX + Math.cos(angle) * 7, angler.posY + 2, angler.posZ + Math.sin(angle) * 7);
                if (worldObj.getCollidingBoundingBoxes(mob, mob.boundingBox).isEmpty()) break;
            }
            if (caught.spec.itemId == FishingCreatures.BLUE_SQUID) {
                double dx = angler.posX - mob.posX, dz = angler.posZ - mob.posZ;
                double length = Math.max(1, Math.hypot(dx, dz));
                mob.motionX = dx / length * 0.8; mob.motionZ = dz / length * 0.8; mob.motionY = 0.3;
                com.itlesports.nightmaremode.skill.SkillTreeData data = SkillHandler.getPlayerData(angler);
                data.blueSquidCaught++;
                angler.setData(btw.community.nightmaremode.NightmareMode.SKILL_TREE, data);
            }
            worldObj.spawnEntityInWorld(mob);
        } else {
            ItemStack stack = new ItemStack(Item.itemsList[caught.spec.itemId]);
            spawnReeledItem(stack);
            if (com.itlesports.nightmaremode.util.ArmorSetHelper.isWearingCompleteAnglerSet(angler)) spawnReeledItem(stack.copy());
        }
        if (caught.spec.fish) SkillHandler.incrementFishCaught(angler, caught.spec.rare);
        else if (caught.spec.rare) {
            com.itlesports.nightmaremode.skill.SkillTreeData data = SkillHandler.getPlayerData(angler);
            data.rareItemsCaught++;
            angler.setData(btw.community.nightmaremode.NightmareMode.SKILL_TREE, data);
        }
        angler.addStat(StatList.fishCaughtStat, 1);
        worldObj.playSoundAtEntity(angler, "random.splash", 0.7F, 1.1F);
    }

    @Unique
    private void spawnReeledItem(ItemStack stack) {
        EntityItem item = new EntityItem(worldObj, posX, posY, posZ, stack);
        ((com.itlesports.nightmaremode.util.interfaces.FishingItemProtection)item).nm$protectFishingCatch(200);
        double dx = angler.posX - posX, dy = angler.posY - posY, dz = angler.posZ - posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        item.motionX = dx * 0.1; item.motionY = dy * 0.1 + Math.sqrt(distance) * 0.08; item.motionZ = dz * 0.1;
        worldObj.spawnEntityInWorld(item);
    }

    @Unique
    private void loseFishingRod() {
        EntityItem rod = angler.dropOneItem(false);
        if (rod == null) return;
        double angle = angler.rotationYaw * Math.PI / 180;
        rod.motionX = -Math.sin(angle) * 0.8; rod.motionZ = Math.cos(angle) * 0.8; rod.motionY = 0.2;
        ((com.itlesports.nightmaremode.util.interfaces.FishingItemProtection)rod).nm$protectFishingCatch(200);
    }

    @Unique
    private boolean isNetherFishing() {
        if (this.angler == null) {
            return false;
        }
        ItemStack held = this.angler.getCurrentEquippedItem();
        return held != null && held.getItem() instanceof com.itlesports.nightmaremode.item.items.ItemNetherFishingRod;
    }

}
