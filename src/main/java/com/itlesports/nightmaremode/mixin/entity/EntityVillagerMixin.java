package com.itlesports.nightmaremode.mixin.entity;

import api.inventory.InventoryUtils;
import btw.block.blocks.BlockDispenserBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.entity.NightmareVillager;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.interfaces.FoodItemExt;
import com.itlesports.nightmaremode.util.interfaces.VillagerHunger;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

import static com.itlesports.nightmaremode.util.NMFields.HARDMODE;


@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements IMerchant, INpc, VillagerHunger {

    @Unique private static final int VILLAGER_HUNGER_WATCHER_ID = 30;
    @Unique private static final int MAX_VILLAGER_HUNGER = 60;
    @Unique private static final int HUNGRY_THRESHOLD = 30;
    @Unique private static final float HUNGER_DRAIN_PER_TICK = 1.0F / 1200.0F;

    @Shadow protected MerchantRecipeList buyingList;
    @Shadow public static Map<Integer, Class> professionMap;

    @Shadow public abstract int getCurrentTradeLevel();
    @Shadow public abstract int getProfession();
    @Shadow public abstract int getCurrentTradeMaxXP();
    @Shadow public abstract int getCurrentTradeXP();
    @Shadow public abstract EntityPlayer getCustomer();
    @Shadow public abstract void setProfession(int profession);

    @Unique private int nightmareMode$levelBeforeTrade;
    @Unique private EntityPlayer nightmareMode$tradingPlayer;
    @Unique private float nightmareMode$hungerDrainProgress;


    @Shadow public abstract void setInLove(int iInLove);
    @Shadow public abstract int getInLove();

    public EntityVillagerMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "entityInit", at = @At("TAIL"))
    private void addVillagerHungerWatcher(CallbackInfo ci) {
        this.dataWatcher.addObject(VILLAGER_HUNGER_WATCHER_ID, MAX_VILLAGER_HUNGER);
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeVillagerHunger(NBTTagCompound tag, CallbackInfo ci) {
        tag.setInteger("NmVillagerHunger", this.nightmareMode$getHungerLevel());
        tag.setFloat("NmVillagerHungerProgress", this.nightmareMode$hungerDrainProgress);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readVillagerHunger(NBTTagCompound tag, CallbackInfo ci) {
        int hunger = tag.hasKey("NmVillagerHunger") ? tag.getInteger("NmVillagerHunger") : MAX_VILLAGER_HUNGER;
        this.dataWatcher.updateObject(VILLAGER_HUNGER_WATCHER_ID, Math.max(0, Math.min(MAX_VILLAGER_HUNGER, hunger)));
        this.nightmareMode$hungerDrainProgress = tag.hasKey("NmVillagerHungerProgress") ? tag.getFloat("NmVillagerHungerProgress") : 0.0F;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    private void stopVillagerCarcassUpdate(CallbackInfo ci) {
        if ((Object)this instanceof com.itlesports.nightmaremode.util.interfaces.CarcassAnimal carcass && carcass.nm$isCarcass()) {
            ci.cancel();
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void tickVillagerHunger(CallbackInfo ci) {
        if (this.worldObj.isRemote || !this.isEntityAlive()) {
            return;
        }

        this.nightmareMode$hungerDrainProgress += HUNGER_DRAIN_PER_TICK
                * SkillHandler.getWorldData(this.worldObj).globalVillagerHungerDrainRateMultiplier;
        while (this.nightmareMode$hungerDrainProgress >= 1.0F) {
            this.nightmareMode$hungerDrainProgress -= 1.0F;
            this.nightmareMode$setHungerLevel(this.nightmareMode$getHungerLevel() - 1);

        }

        if (this.ticksExisted % 20 == 0 && this.nightmareMode$isHungry()) {
            this.nightmareMode$checkForLooseFood();
            if(this.nightmareMode$getHungerLevel() <= 0){
                this.attackEntityFrom(DamageSource.starve, 1.0f);
            }
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void feedHungryVillager(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (!(this.nightmareMode$getHungerLevel() < MAX_VILLAGER_HUNGER)) {
            return;
        }

        if(!this.nightmareMode$isSafeFood(stack)){
            if(this.nightmareMode$getHungerLevel() < MAX_VILLAGER_HUNGER / 6){
                cir.setReturnValue(false);
            }
            return;
        }


        if (!this.worldObj.isRemote) {
            this.nightmareMode$eatFood(stack);
            if (!player.capabilities.isCreativeMode && --stack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }

        cir.setReturnValue(true);
    }

    @Inject(method = "getSoundPitch", at = @At("RETURN"), cancellable = true)
    private void makeHungryVillagersSoundHungry(CallbackInfoReturnable<Float> cir) {
        if (this.nightmareMode$isHungry()) {
            cir.setReturnValue(cir.getReturnValue() * 1.35F);
        }
    }

    @Inject(method = "useRecipe", at = @At("HEAD"))
    private void rememberTradeState(MerchantRecipe recipe, CallbackInfo ci) {
        this.nightmareMode$levelBeforeTrade = this.getCurrentTradeLevel();
        this.nightmareMode$tradingPlayer = this.getCustomer();
    }

    @Inject(method = "useRecipe", at = @At("TAIL"))
    private void applySkillTradeProgress(MerchantRecipe recipe, CallbackInfo ci) {
        EntityPlayer player = this.nightmareMode$tradingPlayer;
        SkillHandler.incrementTradesCompleted(player);
        if (player != null && this.getCurrentTradeLevel() > this.nightmareMode$levelBeforeTrade
                && this.rand.nextFloat() < SkillHandler.getPlayerData(player).villagerProfessionChangeChance) {
            int oldProfession = this.getProfession();
            int newProfession = this.rand.nextInt(4);
            if (newProfession >= oldProfession) {
                newProfession++;
            }
            this.setProfession(newProfession);
            this.buyingList = null;
        }
        this.nightmareMode$tradingPlayer = null;
    }

    @Override
    public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntity) {
        int profession = this.getProfession();
        int level = this.getCurrentTradeLevel();
        if(this.getHealth() < 10 || this.isDead) return false; // if the villager is about to die, it cannot be vacuumed up. prevents exploits with grabbing them while they're dying / about to die

        int itemMeta = NMUtils.VillagerMetaCodec.packItemMeta(profession, level);

        ItemStack stack = new ItemStack(NMItems.villagerOrb, 1, itemMeta);

        this.setDead();
        InventoryUtils.addSingleItemToInventory(tileEntity, stack.itemID, stack.getItemDamage());

        return true;
    }


    @Inject(method = "updateAITick", at = @At("HEAD"))
    private void resetVillagerTrades(CallbackInfo ci) {
        if(this.ticksExisted % 20 == 0 && NMUtils.getIsBloodMoon()){
            this.heal(20f);
        }
    }

    @Inject(method = "isTemptingItem", at = @At("HEAD"),cancellable = true)
    private void addTemptingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.itemID == NMItems.refinedDiamondIngot.itemID){
            cir.setReturnValue(true);
        }
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityVillager;customInteract(Lnet/minecraft/src/EntityPlayer;)Z"))
    private boolean breedWithRefinedDiamond(EntityVillager instance, EntityPlayer player){
        ItemStack heldStack = player.inventory.getCurrentItem();
        if (!(heldStack == null || heldStack.getItem().itemID != Item.diamond.itemID && heldStack.getItem().itemID != BTWItems.diamondIngot.itemID || this.getGrowingAge() != 0 || this.getInLove() != 0 || this.isPossessed())) {
            if (!player.capabilities.isCreativeMode) {
                --heldStack.stackSize;
                if (heldStack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            this.worldObj.playSoundAtEntity(this, "mob.villager.hurt", 1.0f, this.getSoundPitch() * 2.0f);
            this.setInLove(1);
            this.entityToAttack = null;
            return true;
        }
        return false;
    }
    @Inject(method = "<clinit>", at = @At("TAIL"),remap = false)
    private static void addNightmareVillagerProfession(CallbackInfo ci){
        professionMap.put(5, NightmareVillager.class);
    }

    @Override
    public boolean isSecondaryTargetForSquid() {
        return false;
    }

    @Override
    public int nightmareMode$getHungerLevel() {
        return this.dataWatcher.getWatchableObjectInt(VILLAGER_HUNGER_WATCHER_ID);
    }

    @Override
    public boolean nightmareMode$isHungry() {
        return this.nightmareMode$getHungerLevel() <= HUNGRY_THRESHOLD;
    }

    @Unique
    private void nightmareMode$setHungerLevel(int hunger) {
        this.dataWatcher.updateObject(VILLAGER_HUNGER_WATCHER_ID, Math.max(0, Math.min(MAX_VILLAGER_HUNGER + 30, hunger)));
    }

    @Unique
    private boolean nightmareMode$isSafeFood(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemFood)) {
            return false;
        }
        return !((FoodItemExt) stack.getItem()).nightmareMode$causesFoodPoisoning();
    }

    @Unique
    private void nightmareMode$eatFood(ItemStack stack) {
        ItemFood food = (ItemFood) stack.getItem();
        this.nightmareMode$setHungerLevel(this.nightmareMode$getHungerLevel() + (food.getHungerRestored() * 2));
        this.worldObj.playSoundAtEntity(this, "random.eat", 0.5F, this.getSoundPitch());
    }

    @Unique
    private void nightmareMode$checkForLooseFood() {
        List<EntityItem> itemList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(2.5D, 1.0D, 2.5D));
        for (EntityItem itemEntity : itemList) {
            ItemStack stack = itemEntity.getEntityItem();
            if (itemEntity.isDead || itemEntity.delayBeforeCanPickup > 0 || !this.nightmareMode$isSafeFood(stack)) {
                continue;
            }

            this.nightmareMode$eatFood(stack);
            if (--stack.stackSize <= 0) {
                itemEntity.setDead();
            } else {
                itemEntity.setEntityItemStack(stack);
            }
            return;
        }
    }
}
