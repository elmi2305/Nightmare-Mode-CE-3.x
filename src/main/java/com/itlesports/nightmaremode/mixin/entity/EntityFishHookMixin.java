package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.util.BTWSounds;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Map;

@Mixin(EntityFishHook.class)
public abstract class EntityFishHookMixin extends Entity implements EntityFishHookAccessor{
    @Unique private static final Map<Item, Integer> preHardmode = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Item.reed, 3),
            new AbstractMap.SimpleEntry<>(BTWItems.tangledWeb, 9),
            new AbstractMap.SimpleEntry<>(BTWItems.ironOreChunk, 3),
            new AbstractMap.SimpleEntry<>(Item.melonSeeds, 1),
            new AbstractMap.SimpleEntry<>(Item.bone, 10),
            new AbstractMap.SimpleEntry<>(NMItems.calamari, 3),
            new AbstractMap.SimpleEntry<>(BTWItems.hempSeeds, 2),
            new AbstractMap.SimpleEntry<>(Item.dyePowder, 10),
            new AbstractMap.SimpleEntry<>(Item.clay, 12),
            new AbstractMap.SimpleEntry<>(BTWItems.sugarCaneRoots, 3),
            new AbstractMap.SimpleEntry<>(Item.goldenCarrot, 1),
            new AbstractMap.SimpleEntry<>(BTWItems.mysteriousGland, 7)
    );
    @Unique private static final Map<Item, Integer> hardmode = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(BTWItems.ironNugget, 7),
            new AbstractMap.SimpleEntry<>(BTWItems.goldOreChunk, 4),
            new AbstractMap.SimpleEntry<>(Item.goldNugget, 2),
            new AbstractMap.SimpleEntry<>(Item.melonSeeds, 5),
            new AbstractMap.SimpleEntry<>(Item.arrow, 10),
            new AbstractMap.SimpleEntry<>(BTWItems.tannedLeather, 8),
            new AbstractMap.SimpleEntry<>(Item.enchantedBook, 2),
            new AbstractMap.SimpleEntry<>(BTWItems.witchWart, 2),
            new AbstractMap.SimpleEntry<>(BTWItems.emeraldPile, 6),
            new AbstractMap.SimpleEntry<>(BTWItems.gear, 12),
            new AbstractMap.SimpleEntry<>(BTWItems.mysteriousGland, 8),
            new AbstractMap.SimpleEntry<>(NMItems.calamari, 8),
            new AbstractMap.SimpleEntry<>(BTWItems.nethercoal, 3),
            new AbstractMap.SimpleEntry<>(Item.glowstone, 3),
            new AbstractMap.SimpleEntry<>(Item.book, 4),
            new AbstractMap.SimpleEntry<>(Item.diamond, 1),
            new AbstractMap.SimpleEntry<>(NMItems.bloodOrb, 2)
    );
    @Unique private static final Map<Item, Integer> endgame = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Item.goldNugget, 6),
            new AbstractMap.SimpleEntry<>(BTWItems.steelNugget, 4),
            new AbstractMap.SimpleEntry<>(BTWItems.broadheadArrowHead, 2),
            new AbstractMap.SimpleEntry<>(BTWItems.rope, 6),
            new AbstractMap.SimpleEntry<>(NMItems.calamari, 8),
            new AbstractMap.SimpleEntry<>(Item.diamond, 1),
            new AbstractMap.SimpleEntry<>(BTWItems.soulFlux, 4),
            new AbstractMap.SimpleEntry<>(Item.expBottle, 7),
            new AbstractMap.SimpleEntry<>(Item.book, 8),
            new AbstractMap.SimpleEntry<>(Item.emerald, 9)
    );
    @Unique private static final Map<Item, Integer> eclipse = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(NMItems.magicFeather, 4),
            new AbstractMap.SimpleEntry<>(NMItems.creeperChop, 3),
            new AbstractMap.SimpleEntry<>(NMItems.magicArrow, 5),
            new AbstractMap.SimpleEntry<>(NMItems.bloodMilk, 2),
            new AbstractMap.SimpleEntry<>(NMItems.calamari, 9),
            new AbstractMap.SimpleEntry<>(NMItems.silverLump, 7),
            new AbstractMap.SimpleEntry<>(BTWItems.soulFlux, 9),
            new AbstractMap.SimpleEntry<>(NMItems.voidMembrane, 1),
            new AbstractMap.SimpleEntry<>(NMItems.voidSack, 8),
            new AbstractMap.SimpleEntry<>(NMItems.charredFlesh, 3),
            new AbstractMap.SimpleEntry<>(NMItems.ghastTentacle, 4),
            new AbstractMap.SimpleEntry<>(NMItems.creeperTear, 1),
            new AbstractMap.SimpleEntry<>(NMItems.spiderFangs, 6),
            new AbstractMap.SimpleEntry<>(NMItems.speedCoil, 3),
            new AbstractMap.SimpleEntry<>(NMItems.waterRod, 9),
            new AbstractMap.SimpleEntry<>(NMItems.elementalRod, 1)
    );
    @Unique private static final Map<Integer, Map<Item, Integer>> mapOfLootPools = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(0, preHardmode),
            new AbstractMap.SimpleEntry<>(1, hardmode),
            new AbstractMap.SimpleEntry<>(2, endgame),
            new AbstractMap.SimpleEntry<>(3, eclipse)
    );

    @Unique
    private static int getItemOccurrences(Item item, int index) {
        return mapOfLootPools.get(index).getOrDefault(item,0);
    }
    @Shadow private boolean isBaited;
    @Shadow public EntityPlayer angler;
    @Shadow public Entity bobber;

    @Unique private boolean isIron;
    @Unique private ItemStack fishItem = new ItemStack(Item.fishRaw);
    @Unique private int cap = 1;

    public EntityFishHookMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 8))
    private int increaseBiteOdds(int constant){
        return 2;
    }

    @Redirect(method = "catchFish", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityFishHook;bobber:Lnet/minecraft/src/Entity;", ordinal = 0))
    private Entity cannotHookEnemies(EntityFishHook instance){
        return null;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageFishingEnemies(CallbackInfo ci){
        if(this.bobber instanceof EntityMob && this.angler != null){
            this.angler.getHeldItem().attemptDamageItem(4, this.rand);
            this.angler.playSound("random.splash", 0.5f, 2.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
            this.angler.dropOneItem(false);
        }
    }
//    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 1500))
//    private int startingBiteOdds(int constant){
//        return this.isIron ? 6 : constant;
//    }
    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 4))
    private int biteChanceMultiplierDay(int constant){
        return 20;
    }
//    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 2))
//    private int dawnDuskRain(int constant){
//        return this.isIron ? 2 : constant;
//    }
    @ModifyConstant(method = "isBodyOfWaterLargeEnoughForFishing", constant = @Constant(intValue = 2))
    private int decreaseWaterDepthRequirement(int constant){
        return this.isIron ? 1 : constant;
    }


    @ModifyArg(method = "catchFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;)V",ordinal = 1))
    private Item randomFishingLoot(Item item){
        if(this.fishItem.getItem() != Item.fishRaw && NightmareMode.shouldDisplayFishingAnnouncements){
            int worldProgress = this.angler.worldObj != null ? NMUtils.getWorldProgress() : 0;
            int iMoonPhase = this.worldObj.getMoonPhase();
            int phaseMultiplier = 1;
            if (iMoonPhase == 0) {
                phaseMultiplier = 2;
            }
            double rarity = getRarity(this.fishItem, this.cap / phaseMultiplier, worldProgress);
            String textToDisplay = "You caught: " + this.fishItem.getDisplayName() + "! Rarity: " + roundIfNeeded(rarity) + "% " + getRarityName(rarity);
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText(textToDisplay);
            text2.setColor(getRarityColor(rarity));
            this.angler.sendChatToPlayer(text2);
        }
        return this.fishItem.getItem();
    }

    @Inject(method = "catchFish", at = @At("TAIL"))
    private void trackSkillFishing(CallbackInfoReturnable<Integer> cir) {
        if (this.angler != null && cir.getReturnValueI() > 0) {
            SkillHandler.incrementFishCaught(this.angler, this.fishItem.getItem() != Item.fishRaw);
        }
    }
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityFishHook;playSound(Ljava/lang/String;FF)V"))
    private void playCatchSoundAtPlayer(EntityFishHook instance, String s, float v, float p){
        if ((this.fishItem = this.getRandomItemForRod()).getItem() != Item.fishRaw) {
            instance.worldObj.playSoundAtEntity(instance.angler, BTWSounds.GEM_STEP.sound(), 2f, 1f + (float)this.rand.nextGaussian());
        } else {
            instance.worldObj.playSoundAtEntity(instance.angler, s, v, p);
        }
    }
    @Unique
    private int getWorldProgressBonus(){
        if(this.worldObj != null){
            return NMUtils.getWorldProgress();
        }
        return 0;
    }

    @Unique
    private ItemStack getRandomItemForRod(){
        int worldProgress = this.worldObj != null ? NMUtils.getWorldProgress() : 0;
        this.cap = 1600;
        int iMoonPhase = this.worldObj.getMoonPhase();
        int phaseMultiplier = 1;
        if (iMoonPhase == 0) {
            phaseMultiplier = 2;
        }
        double capModifier = (double) (this.isIron ? 1 : (5 - this.getWorldProgressBonus())) / phaseMultiplier;
        int j = this.rand.nextInt((int) (this.cap * capModifier));
        Item itemToDrop;
        if (worldProgress == 0) {
            itemToDrop = switch (j) {
                case  0,  1,  2                              -> NMItems.bonusChestLoot;                // 3 occurrences (paper)
                case  3,  4,  5,  6,  7,  8,  9, 10, 11      -> BTWItems.tangledWeb;      // 9 occurrences (silk)
                case 12, 13, 14                              -> NMItems.bonusChestLoot;    // 3 occurrences (iron ore chunk)
                case 15                                      -> NMItems.bonusChestLoot;          // 1 occurrence (melon seeds)
                case 16, 17, 18, 19, 20, 21, 22, 23, 24, 25  -> NMItems.bonusChestLoot;                // 10 occurrences (bone)
                case 26, 27, 28                              -> NMItems.calamari;         // 3 occurrences (calamari)
                case 29, 30                                  -> NMItems.bonusChestLoot;       // 2 occurrences (hemp seeds)
                case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40  -> Item.dyePowder;           // 10 occurrences (dye powder)
                case 41, 42, 43, 44, 45, 46, 47, 48,
                     49, 50, 51, 52                          -> Item.clay;                // 12 occurrences (clay)
                case 53, 54, 55                              -> NMItems.bonusChestLoot;  // 3 occurrence (sugar cane)
                case 56                                      -> NMItems.bonusChestLoot;        // 1 occurrence (golden carrot)
                case 57, 58, 59, 60, 61, 62, 63              -> BTWItems.mysteriousGland; // 7 occurrences (mysterious gland)
                default -> NMItems.bonusChestLoot;  // Fallback in case of unexpected input
            };
        } else if(worldProgress == 1){
            this.cap = 905 * 2;
            j = this.rand.nextInt((int) (this.cap * capModifier));
            itemToDrop = switch (j) {
                case  0,  1,  2,  3,  4,  5,  6                     -> NMItems.bonusChestLoot;       // 7 occurrences (iron nugget)
                case  7,  8,  9, 10                                 -> BTWItems.goldOreChunk;     // 4 occurrences (gold chunk)
                case 11, 12                                         -> NMItems.bonusChestLoot;           // 2 occurrences (gold nugget)
                case 13, 14, 15, 16, 17                             -> NMItems.bonusChestLoot;           // 5 occurrences (melon seeds)
                case 18, 19, 20, 21, 22, 23, 24, 25, 26, 27         -> Item.arrow;                // 10 occurrences (arrow)
                case 28, 29, 30, 31, 32, 33, 34, 35                 -> NMItems.bonusChestLoot;    // 8 occurrences (glue)
                case 36, 37                                         -> NMItems.bonusChestLoot;        // 2 occurrences (enchanted book)
                case 38, 39                                         -> NMItems.bonusChestLoot;        // 2 occurrences (witch wart)
                case 40, 41, 42, 43, 44, 45                         -> NMItems.bonusChestLoot;      // 6 occurrences (emerald pile)
                case 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 -> BTWItems.gear;             // 12 occurrences (gear)
                case 58, 59, 60, 61, 62, 63, 64, 65                 -> BTWItems.mysteriousGland;  // 8 occurrences (mysterious gland)
                case 66, 67, 68, 69, 70, 71, 72, 73                 -> NMItems.calamari;          // 8 occurrences (calamari)
                case 74, 75, 76                                     -> NMItems.bonusChestLoot;       // 3 occurrences (nethercoal)
                case 77, 78, 79                                     -> NMItems.bonusChestLoot;            // 3 occurrences (glowstone dust)
                case 80, 81, 82, 83                                 -> Item.book;                 // 4 occurrences (book)
                case 84                                             -> NMItems.bonusChestLoot;              // 1 occurrence (diamond)
                case 85, 86                                         -> NMItems.bonusChestLoot;          // 2 occurrences (blood orb)
                default -> NMItems.bonusChestLoot;  // Fallback in case of unexpected input
            };
        } else{
            this.cap = 600 * 2;
            j = this.rand.nextInt((int) (this.cap * capModifier));
            itemToDrop = switch (j) {
                case  0,  1,  2,  3,  4,  5             -> Item.goldNugget;             // 6 occurrences (gold nugget)
                case  6,  7,  8,  9                     -> NMItems.bonusChestLoot;        // 4 occurrences (steel nugget)
                case 10, 11                             -> NMItems.bonusChestLoot; // 2 occurrences (broad-head arrowhead)
                case 12, 13, 14, 15, 16, 17             -> BTWItems.rope;               // 6 occurrences (rope)
                case 18, 19, 20, 21, 22, 23, 24, 25     -> NMItems.calamari;            // 8 occurrences (calamari)
                case 26                                 -> NMItems.bonusChestLoot;                // 1 occurrence (diamond)
                case 27, 28, 29, 30                     -> NMItems.bonusChestLoot;           // 4 occurrences (soul flux)
                case 31, 32, 33, 34, 35, 36, 37         -> NMItems.bonusChestLoot;              // 7 occurrences (exp bottle)
                case 38, 39, 40, 41, 42, 43, 44, 45     -> Item.book;                   // 8 occurrences (book)
                case 46, 47, 48, 49, 50, 51, 52, 53, 54 -> NMItems.bonusChestLoot;                // 9 occurrences (emerald)
                default -> NMItems.bonusChestLoot;  // Fallback in case of unexpected input
            };
        }
        return new ItemStack(itemToDrop,1,0);
    }


    @Unique
    private static double roundIfNeeded(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.stripTrailingZeros(); // Remove trailing zeros

        // Count decimal places
        int scale = Math.max(0, bd.scale());

        // If more than 3 decimal places, round to 3
        if (scale > 3) {
            bd = bd.setScale(3, RoundingMode.HALF_UP);
        }

        return bd.doubleValue();
    }

    @Unique
    private static double getRarity(ItemStack item, int cap, int progress){
        return (double) getItemOccurrences(item.getItem(), progress) * 100 / cap;
    }

    @Unique
    private static EnumChatFormatting getRarityColor(double rarityPercent){
        double rarity = rarityPercent / 100;
        if (rarity >= 0.011) {
            return EnumChatFormatting.WHITE;
        }
        else if (rarity > 0.0073) {
            return EnumChatFormatting.YELLOW;
        }
        else if (rarity > 0.0035) {
            return EnumChatFormatting.BLUE;
        }
        else if (rarity > 0.0025) {
            return EnumChatFormatting.RED;
        }
        else if (rarity > 0.002) {
            return EnumChatFormatting.LIGHT_PURPLE;
        }
        else {
            return EnumChatFormatting.GOLD;
        }
    }
    @Unique
    private static String getRarityName(double rarityPercent){
        double rarity = rarityPercent / 100;
        if (rarity >= 0.011) {
            return "(Common)";
        }
        else if (rarity > 0.0073) {
            return "(Uncommon)";
        }
        else if (rarity > 0.0035) {
            return "(Rare)";
        }
        else if (rarity > 0.0025) {
            return "(Epic)";
        }
        else if (rarity > 0.002) {
            return "(Legendary)";
        }
        else {
            return "(Mythical)";
        }
    }
}
