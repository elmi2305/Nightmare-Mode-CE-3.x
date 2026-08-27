package com.itlesports.nightmaremode.mixin.entity;

import api.inventory.InventoryUtils;
import btw.block.blocks.BlockDispenserBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemVillagerDebugTool;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.LibrarianStoryBook;
import com.itlesports.nightmaremode.util.ArmorSetHelper;
import com.itlesports.nightmaremode.entity.NightmareVillager;
import com.itlesports.nightmaremode.entity.EntityNetherPostVillager;
import com.itlesports.nightmaremode.entity.EntityTier1NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier2NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier3NetherVillager;
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
    @Unique private int nightmareMode$questDialogueStage;
    @Unique private boolean nightmareMode$questIssued;
    @Unique private boolean nightmareMode$questComplete;
    @Unique private int nightmareMode$questAnswerItem = -1;
    @Unique private int nightmareMode$questAnswerMeta = -1;
    @Unique private int nightmareMode$questAnswerAmount = 1;
    @Unique private int nightmareMode$questStory;
    @Unique private int nightmareMode$questQuestion = -1;
    @Unique private int nightmareMode$questWrongAnswers;
    @Unique private long nightmareMode$questLockoutUntil;
    @Unique private String nightmareMode$questToken = "";


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
        tag.setInteger("NmQuestDialogueStage", this.nightmareMode$questDialogueStage);
        tag.setBoolean("NmQuestIssued", this.nightmareMode$questIssued);
        tag.setBoolean("NmQuestComplete", this.nightmareMode$questComplete);
        tag.setInteger("NmQuestAnswerItem", this.nightmareMode$questAnswerItem);
        tag.setInteger("NmQuestAnswerMeta", this.nightmareMode$questAnswerMeta);
        tag.setInteger("NmQuestAnswerAmount", this.nightmareMode$questAnswerAmount);
        tag.setInteger("NmQuestStory", this.nightmareMode$questStory);
        tag.setInteger("NmQuestQuestion", this.nightmareMode$questQuestion);
        tag.setInteger("NmQuestWrongAnswers", this.nightmareMode$questWrongAnswers);
        tag.setLong("NmQuestLockoutUntil", this.nightmareMode$questLockoutUntil);
        tag.setString("NmQuestToken", this.nightmareMode$questToken);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readVillagerHunger(NBTTagCompound tag, CallbackInfo ci) {
        int hunger = tag.hasKey("NmVillagerHunger") ? tag.getInteger("NmVillagerHunger") : MAX_VILLAGER_HUNGER;
        this.dataWatcher.updateObject(VILLAGER_HUNGER_WATCHER_ID, Math.max(0, Math.min(MAX_VILLAGER_HUNGER, hunger)));
        this.nightmareMode$hungerDrainProgress = tag.hasKey("NmVillagerHungerProgress") ? tag.getFloat("NmVillagerHungerProgress") : 0.0F;
        this.nightmareMode$questDialogueStage = tag.getInteger("NmQuestDialogueStage");
        this.nightmareMode$questIssued = tag.getBoolean("NmQuestIssued");
        this.nightmareMode$questComplete = tag.getBoolean("NmQuestComplete");
        this.nightmareMode$questAnswerItem = tag.hasKey("NmQuestAnswerItem") ? tag.getInteger("NmQuestAnswerItem") : -1;
        this.nightmareMode$questAnswerMeta = tag.hasKey("NmQuestAnswerMeta") ? tag.getInteger("NmQuestAnswerMeta") : -1;
        this.nightmareMode$questAnswerAmount = tag.hasKey("NmQuestAnswerAmount") ? tag.getInteger("NmQuestAnswerAmount") : 1;
        this.nightmareMode$questStory = tag.getInteger("NmQuestStory");
        this.nightmareMode$questQuestion = tag.hasKey("NmQuestQuestion") ? tag.getInteger("NmQuestQuestion") : -1;
        this.nightmareMode$questWrongAnswers = tag.getInteger("NmQuestWrongAnswers");
        this.nightmareMode$questLockoutUntil = tag.getLong("NmQuestLockoutUntil");
        this.nightmareMode$questToken = tag.getString("NmQuestToken");
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    private void stopVillagerCarcassUpdate(CallbackInfo ci) {
        if ((Object)this instanceof com.itlesports.nightmaremode.util.interfaces.CarcassAnimal carcass && carcass.nm$isCarcass()) {
            ci.cancel();
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void tickVillagerHunger(CallbackInfo ci) {
        if ((Object)this instanceof EntityNetherPostVillager || this.worldObj.isRemote || !this.isEntityAlive()) {
            return;
        }

        if (this.getProfession() == 1) this.nightmareMode$refreshLibrarianLockout();

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
        if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemVillagerDebugTool tool){
            player.getHeldItem().func_111282_a(player, this);
            cir.setReturnValue(true);
            return;
        }
        if ((Object)this instanceof EntityNetherPostVillager) {
            return;
        }
        int profession = this.getProfession();
        if (profession >= 0 && profession <= 4 && !this.nightmareMode$questComplete) {
            if (!this.worldObj.isRemote) this.nightmareMode$handleQuestInteraction(player, profession);
            cir.setReturnValue(true);
            return;
        }
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
        if (!this.worldObj.isRemote && player != null && ArmorSetHelper.isWearingCompleteVerdantSet(player)
                && this.rand.nextInt(8) == 0 && recipe.getItemToBuy().itemID != Item.emerald.itemID) {
            ItemStack refund = recipe.getItemToBuy().copy();
            refund.stackSize = 1;
            if (!player.inventory.addItemStackToInventory(refund)) {
                player.dropPlayerItem(refund);
            }
        }
        if (!((Object)this instanceof EntityNetherPostVillager) && player != null && this.getCurrentTradeLevel() > this.nightmareMode$levelBeforeTrade
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
        if ((Object)this instanceof EntityNetherPostVillager) {
            return false;
        }
        int profession = this.getProfession();
        if (profession >= 0 && profession <= 4 && !this.nightmareMode$questComplete) {
            return false;
        }
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
        if (instance instanceof EntityNetherPostVillager) {
            return false;
        }
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
        professionMap.put(EntityTier1NetherVillager.PROFESSION_ID, EntityTier1NetherVillager.class);
        professionMap.put(EntityTier2NetherVillager.PROFESSION_ID, EntityTier2NetherVillager.class);
        professionMap.put(EntityTier3NetherVillager.PROFESSION_ID, EntityTier3NetherVillager.class);
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

    @Unique
    private void nightmareMode$handleQuestInteraction(EntityPlayer player, int profession) {
        if (profession == 1 && this.nightmareMode$questLockoutUntil > 0L) {
            if (!this.nightmareMode$refreshLibrarianLockout()) {
                this.nightmareMode$say(player, "Don't waste my time. I'm not gonna talk to you until next week.");
                return;
            }
        }
        if (this.nightmareMode$questDialogueStage == 0) {
            this.nightmareMode$say(player, "Oh, hello " + player.getEntityName() + ".");
            this.nightmareMode$questDialogueStage = 1;
            return;
        }
        if (this.nightmareMode$questDialogueStage == 1) {
            this.nightmareMode$say(player, "You want to trade with me?");
            this.nightmareMode$questDialogueStage = 2;
            return;
        }
        if (this.nightmareMode$questDialogueStage == 2) {
            this.nightmareMode$say(player, "First you'll have to prove your worth.");
            this.nightmareMode$questDialogueStage = 3;
            return;
        }
        if (!this.nightmareMode$questIssued) {
            this.nightmareMode$issueQuest(player, profession);
            this.nightmareMode$questIssued = true;
            this.nightmareMode$questDialogueStage = 4;
            return;
        }
        this.nightmareMode$tryCompleteQuest(player, profession);
    }

    @Unique
    private void nightmareMode$issueQuest(EntityPlayer player, int profession) {
        ItemStack questItem = null;
        switch (profession) {
            case 0 -> {
                questItem = new ItemStack(NMItems.brokenHoeFragment, 1, NMItems.brokenHoeFragment.getMaxDamage());
                this.nightmareMode$say(player, "Repair this with iron nuggets, then return my favorite hoe.");
            }
            case 1 -> {
                this.nightmareMode$questToken = this.getUniqueID().toString();
                this.nightmareMode$questStory = 1 + this.rand.nextInt(3);
                List<LibrarianStoryBook.Question> questions = LibrarianStoryBook.questionsForStory(this.nightmareMode$questStory);
                this.nightmareMode$questQuestion = this.rand.nextInt(questions.size());
                LibrarianStoryBook.Question question = questions.get(this.nightmareMode$questQuestion);
                this.nightmareMode$questAnswerItem = question.answerItemId();
                this.nightmareMode$questAnswerMeta = question.answerMetadata();
                this.nightmareMode$questAnswerAmount = question.answerAmount();
                this.nightmareMode$questWrongAnswers = 0;
                questItem = LibrarianStoryBook.create(this.nightmareMode$questStory,
                        this.nightmareMode$questQuestion, this.nightmareMode$questToken);
                this.nightmareMode$say(player, question.requestText());
            }
            case 2 -> this.nightmareMode$say(player, "Bring me a six-minute splash potion of fire resistance.");
            case 3 -> {
                questItem = new ItemStack(NMItems.brokenPickaxeFragment, 1, NMItems.brokenPickaxeFragment.getMaxDamage());
                this.nightmareMode$say(player, "Repair this with diamonds, then return my favorite pickaxe.");
            }
            case 4 -> {
                questItem = new ItemStack(NMItems.unbakedChocolateCake);
                this.nightmareMode$say(player, "Bake this chocolate cake for exactly two minutes, then bring it back.");
            }
        }
        if (questItem != null && !player.inventory.addItemStackToInventory(questItem)) player.dropPlayerItem(questItem);
    }

    @Unique
    private void nightmareMode$tryCompleteQuest(EntityPlayer player, int profession) {
        ItemStack held = player.inventory.getCurrentItem();
        if (profession == 4 && held != null && held.itemID == NMItems.burnedChocolateCake.itemID) {
            this.nightmareMode$consumeHeld(player, 1);
            ItemStack retry = new ItemStack(NMItems.unbakedChocolateCake);
            if (!player.inventory.addItemStackToInventory(retry)) player.dropPlayerItem(retry);
            this.nightmareMode$say(player, "Are you serious?! I'm not eating that. You had one job and you completely messed it up. This time, don't burn it.");
            return;
        }
        if (!this.nightmareMode$isCorrectQuestItem(held, profession)) {
            if (profession == 1) {
                this.nightmareMode$handleWrongLibrarianAnswer(player, held);
                return;
            }
            this.nightmareMode$say(player, "I'm still waiting for the item I requested.");
            return;
        }
        this.nightmareMode$consumeHeld(player, profession == 1 ? held.stackSize : 1);
        this.nightmareMode$questComplete = true;
        this.nightmareMode$say(player, "You have proven yourself.");
    }

    @Unique
    private boolean nightmareMode$isCorrectQuestItem(ItemStack held, int profession) {
        if (held == null) return false;
        return switch (profession) {
            case 0 -> held.itemID == NMItems.farmersFavoriteHoe.itemID;
            case 1 -> {
                LibrarianStoryBook.Question question = LibrarianStoryBook.getQuestion(
                        this.nightmareMode$questStory, this.nightmareMode$questQuestion);
                if (question != null) yield question.matches(held);
                NBTTagCompound tag = held.getTagCompound();
                yield held.itemID == Item.writtenBook.itemID && tag != null && tag.getBoolean("NMLibrarianStory")
                        && this.nightmareMode$questToken.equals(tag.getString("NMQuestToken"));
            }
            case 2 -> held.itemID == Item.potion.itemID && held.getItemDamage() == 16451;
            case 3 -> held.itemID == NMItems.blacksmithFavoritePickaxe.itemID;
            case 4 -> held.itemID == NMItems.chocolateCake.itemID;
            default -> false;
        };
    }

    @Unique
    private void nightmareMode$consumeHeld(EntityPlayer player, int amount) {
        ItemStack held = player.inventory.getCurrentItem();
        if (!player.capabilities.isCreativeMode && held != null) {
            held.stackSize -= amount;
            if (held.stackSize <= 0) player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
    }

    @Unique
    private void nightmareMode$handleWrongLibrarianAnswer(EntityPlayer player, ItemStack held) {
        if (held == null) {
            this.nightmareMode$say(player, "I'm still waiting for the item I requested.");
            return;
        }

        ++this.nightmareMode$questWrongAnswers;
        if (this.nightmareMode$questWrongAnswers >= 3) {
            this.nightmareMode$questLockoutUntil = this.worldObj.getTotalWorldTime() + (24000L * 7);
            this.nightmareMode$say(player, "Don't waste my time. I'm not gonna talk to you until next week.");
            return;
        }

        int attemptsLeft = 3 - this.nightmareMode$questWrongAnswers;
        this.nightmareMode$say(player, "That's not correct. Try again.");
    }

    @Unique
    private boolean nightmareMode$refreshLibrarianLockout() {
        if (this.nightmareMode$questLockoutUntil <= 0L) return false;
        if (this.worldObj.getTotalWorldTime() < this.nightmareMode$questLockoutUntil) return false;

        this.nightmareMode$questDialogueStage = 0;
        this.nightmareMode$questIssued = false;
        this.nightmareMode$questComplete = false;
        this.nightmareMode$questAnswerItem = -1;
        this.nightmareMode$questAnswerMeta = -1;
        this.nightmareMode$questAnswerAmount = 1;
        this.nightmareMode$questStory = 0;
        this.nightmareMode$questQuestion = -1;
        this.nightmareMode$questWrongAnswers = 0;
        this.nightmareMode$questLockoutUntil = 0L;
        this.nightmareMode$questToken = "";
        return true;
    }

    @Unique
    private void nightmareMode$say(EntityPlayer player, String message) {
        String profession = switch (this.getProfession()) {
            case 0 -> "Farmer";
            case 1 -> "Librarian";
            case 2 -> "Priest";
            case 3 -> "Blacksmith";
            case 4 -> "Butcher";
            default -> "Villager";
        };
        player.sendChatToPlayer(ChatMessageComponent.createFromText("<" + profession + "> " + message));
    }
}
