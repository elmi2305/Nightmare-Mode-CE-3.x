package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.util.sounds.BTWSoundManager;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            new AbstractMap.SimpleEntry<>(NMItems.greg, 3),
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
    @Unique private Item fishItem = Item.fishRaw;
    @Unique private int cap = 1;

    public EntityFishHookMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 8))
    private int increaseBiteOdds(int constant){
        if (NightmareUtils.getIsBloodMoon()) {
            return 16;
        }
        return this.isIron ? 8 : 10;
    }
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void ensureBaitingTrue(CallbackInfo ci){
        if(this.getAngler() != null && this.getAngler().getHeldItem() != null && this.getAngler().getHeldItem().itemID == NMItems.ironFishingPole.itemID){
            this.isIron = true;
            this.isBaited = true;
        }
    }
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;getItem()Lnet/minecraft/src/Item;",ordinal = 1))
    private Item ironFishingRod1(ItemStack instance){
        if(instance.itemID == NMItems.ironFishingPole.itemID){
            return BTWItems.baitedFishingRod;
        }
        return instance.getItem();
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
        return this.isIron ? 2 : constant;
    }
//    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 2))
//    private int dawnDuskRain(int constant){
//        return this.isIron ? 2 : constant;
//    }
    @ModifyConstant(method = "isBodyOfWaterLargeEnoughForFishing", constant = @Constant(intValue = 2))
    private int decreaseWaterDepthRequirement(int constant){
        return this.isIron ? 1 : constant;
    }
    @Inject(method = "loseBait", at = @At("HEAD"),cancellable = true)
    private void cannotLoseBait(CallbackInfo ci){
        if(this.isIron){
            ci.cancel();
        }
    }
    @ModifyConstant(method = "onUpdate", constant = @Constant(intValue = 10,ordinal = 1))
    private int increaseCatchableTime(int constant){
        return this.isIron ? 40 : constant;
    }
    @ModifyArg(method = "catchFish", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 0))
    private int increaseChanceToCatchSpecialItem(int bound){
        return 1;
    }

    @ModifyArg(method = "catchFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemStack;<init>(Lnet/minecraft/src/Item;)V",ordinal = 1))
    private Item randomFishingLoot(Item item){
        if(this.fishItem != Item.fishRaw && NightmareMode.shouldDisplayFishingAnnouncements){
            int worldProgress = this.angler.worldObj != null ? NightmareUtils.getWorldProgress(this.angler.worldObj) : 0;
            double rarity = getRarity(this.fishItem, this.cap, worldProgress);
            String textToDisplay = "You caught: " + this.fishItem.getItemDisplayName(new ItemStack(this.fishItem)) + "! Rarity: " + roundIfNeeded(rarity) + "% " + getRarityName(rarity);
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText(textToDisplay);
            text2.setColor(getRarityColor(rarity));
            this.angler.sendChatToPlayer(text2);
        }
        return this.fishItem;
    }
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityFishHook;playSound(Ljava/lang/String;FF)V"))
    private void playCatchSoundAtPlayer(EntityFishHook instance, String s, float v, float p){
        if ((this.fishItem = this.getRandomItemForRod()) != Item.fishRaw) {
            instance.worldObj.playSoundAtEntity(instance.angler,BTWSoundManager.GEM_STEP.sound(), 2f, 1f + (float)this.rand.nextGaussian());
        } else {
            instance.worldObj.playSoundAtEntity(instance.angler, s, v, p);
        }
    }
    @Unique
    private int getWorldProgressBonus(){
        if(this.worldObj != null){
            return NightmareUtils.getWorldProgress(this.worldObj);
        }
        return 0;
    }

    @Unique
    private Item getRandomItemForRod(){
        int worldProgress = this.worldObj != null ? NightmareUtils.getWorldProgress(this.worldObj) : 0;
        this.cap = 800;
        double capModifier = this.isIron ? 1 : (5 - this.getWorldProgressBonus());
        int j = this.rand.nextInt((int) (this.cap * capModifier));
        Item itemToDrop;
        if (worldProgress == 0) {
            itemToDrop = switch (j) {
                case  0,  1,  2                              -> Item.reed;                // 3 occurrences (paper)
                case  3,  4,  5,  6,  7,  8,  9, 10, 11      -> BTWItems.tangledWeb;      // 9 occurrences (silk)
                case 12, 13, 14                              -> BTWItems.ironOreChunk;    // 3 occurrences (iron ore chunk)
                case 15                                      -> Item.melonSeeds;          // 1 occurrence (melon seeds)
                case 16, 17, 18, 19, 20, 21, 22, 23, 24, 25  -> Item.bone;                // 10 occurrences (bone)
                case 26, 27, 28                              -> NMItems.calamari;         // 3 occurrences (calamari)
                case 29, 30                                  -> BTWItems.hempSeeds;       // 2 occurrences (hemp seeds)
                case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40  -> Item.dyePowder;           // 10 occurrences (dye powder)
                case 41, 42, 43, 44, 45, 46, 47, 48,
                     49, 50, 51, 52                          -> Item.clay;                // 12 occurrences (clay)
                case 53, 54, 55                              -> BTWItems.sugarCaneRoots;  // 3 occurrence (sugar cane)
                case 56                                      -> Item.goldenCarrot;        // 1 occurrence (golden carrot)
                case 57, 58, 59, 60, 61, 62, 63              -> BTWItems.mysteriousGland; // 7 occurrences (mysterious gland)
                default -> Item.fishRaw;  // Fallback in case of unexpected input
            };
        } else if(worldProgress == 1){
            this.cap = 905;
            j = this.rand.nextInt((int) (this.cap * capModifier));
            itemToDrop = switch (j) {
                case  0,  1,  2,  3,  4,  5,  6                     -> BTWItems.ironNugget;       // 7 occurrences (iron nugget)
                case  7,  8,  9, 10                                 -> BTWItems.goldOreChunk;     // 4 occurrences (gold chunk)
                case 11, 12                                         -> Item.goldNugget;           // 2 occurrences (gold nugget)
                case 13, 14, 15, 16, 17                             -> Item.melonSeeds;           // 5 occurrences (melon seeds)
                case 18, 19, 20, 21, 22, 23, 24, 25, 26, 27         -> Item.arrow;                // 10 occurrences (arrow)
                case 28, 29, 30, 31, 32, 33, 34, 35                 -> BTWItems.tannedLeather;    // 8 occurrences (glue)
                case 36, 37                                         -> Item.enchantedBook;        // 2 occurrences (enchanted book)
                case 38, 39                                         -> BTWItems.witchWart;        // 2 occurrences (witch wart)
                case 40, 41, 42, 43, 44, 45                         -> BTWItems.emeraldPile;      // 6 occurrences (emerald pile)
                case 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57 -> BTWItems.gear;             // 12 occurrences (gear)
                case 58, 59, 60, 61, 62, 63, 64, 65                 -> BTWItems.mysteriousGland;  // 8 occurrences (mysterious gland)
                case 66, 67, 68, 69, 70, 71, 72, 73                 -> NMItems.calamari;          // 8 occurrences (calamari)
                case 74, 75, 76                                     -> BTWItems.nethercoal;       // 3 occurrences (nethercoal)
                case 77, 78, 79                                     -> Item.glowstone;            // 3 occurrences (glowstone dust)
                case 80, 81, 82, 83                                 -> Item.book;                 // 4 occurrences (book)
                case 84                                             -> Item.diamond;              // 1 occurrence (diamond)
                case 85, 86                                         -> NMItems.bloodOrb;          // 2 occurrences (blood orb)
                default -> Item.fishRaw;  // Fallback in case of unexpected input
            };
        } else if(worldProgress == 2){
            this.cap = 600;
            j = this.rand.nextInt((int) (this.cap * capModifier));
            itemToDrop = switch (j) {
                case  0,  1,  2,  3,  4,  5             -> Item.goldNugget;             // 6 occurrences (gold nugget)
                case  6,  7,  8,  9                     -> BTWItems.steelNugget;        // 4 occurrences (steel nugget)
                case 10, 11                             -> BTWItems.broadheadArrowHead; // 2 occurrences (broad-head arrowhead)
                case 12, 13, 14, 15, 16, 17             -> BTWItems.rope;               // 6 occurrences (rope)
                case 18, 19, 20, 21, 22, 23, 24, 25     -> NMItems.calamari;            // 8 occurrences (calamari)
                case 26                                 -> Item.diamond;                // 1 occurrence (diamond)
                case 27, 28, 29, 30                     -> BTWItems.soulFlux;           // 4 occurrences (soul flux)
                case 31, 32, 33, 34, 35, 36, 37         -> Item.expBottle;              // 7 occurrences (exp bottle)
                case 38, 39, 40, 41, 42, 43, 44, 45     -> Item.book;                   // 8 occurrences (book)
                case 46, 47, 48, 49, 50, 51, 52, 53, 54 -> Item.emerald;                // 9 occurrences (emerald)
                default -> Item.fishRaw;  // Fallback in case of unexpected input
            };
        } else{
            this.cap = 740;
            j = this.rand.nextInt((int) (this.cap * capModifier));
            itemToDrop = switch (j) {
                case  0,  1,  2,  3                     -> NMItems.magicFeather;       // 4 occurrences (magic feather)
                case  4,  5,  6                         -> NMItems.creeperChop;        // 3 occurrences (creeper chop)
                case  7,  8,  9, 10, 11                 -> NMItems.magicArrow;         // 5 occurrences (magic arrow)
                case 12, 13                             -> NMItems.bloodMilk;          // 2 occurrences (blood-milk)
                case 14, 15, 16, 17, 18, 19, 20, 21, 22 -> NMItems.calamari;           // 9 occurrences (calamari)
                case 23, 24, 25, 26, 27, 28, 29         -> NMItems.silverLump;         // 7 occurrences (silver lump)
                case 30, 31, 32, 33, 34, 35, 36, 37, 38 -> BTWItems.soulFlux;          // 9 occurrences (soul flux)
                case 39                                 -> NMItems.voidMembrane;       // 1 occurrence (void membrane)
                case 40, 41, 42, 43, 44, 45, 46, 47     -> NMItems.voidSack;           // 8 occurrences (void sack)
                case 48, 49, 50                         -> NMItems.charredFlesh;       // 3 occurrences (charred flesh)
                case 51, 52, 53, 54                     -> NMItems.ghastTentacle;      // 4 occurrences (ghast tentacle)
                case 55                                 -> NMItems.creeperTear;        // 1 occurrence (creeper tear)
                case 56, 57, 58, 59, 60, 61             -> NMItems.spiderFangs;        // 6 occurrences (spider fangs)
                case 62, 63, 64                         -> NMItems.greg;               // 3 occurrences (greg)
                case 65, 66, 67, 68, 69, 70, 71, 72, 73 -> NMItems.waterRod;           // 9 occurrences (water rod)
                case 74                                 -> NMItems.elementalRod;       // 1 occurrence (elemental rod)
                default -> Item.fishRaw;  // Fallback in case of unexpected input
            };
        }
        return itemToDrop;
    }
    //6 / 540 = 0.0111
    //4 / 540 = 0.0074
    //2 / 540 = 0.0037
    //6 / 540 = 0.0111
    //8 / 540 = 0.0148
    //1 / 540 = 0.0019
    //4 / 540 = 0.0074
    //7 / 540 = 0.0130
    //8 / 540 = 0.0148
    //9 / 540 = 0.0167
    //```
    //4 / 365 = 0.01095890
    //3 / 365 = 0.00821918
    //5 / 365 = 0.01369863
    //2 / 365 = 0.00547945
    //9 / 365 = 0.02465753
    //7 / 365 = 0.01917808
    //9 / 365 = 0.02465753
    //1 / 365 = 0.00273973
    //8 / 365 = 0.02191781
    //3 / 365 = 0.00821918
    //4 / 365 = 0.01095890
    //1 / 365 = 0.00273973
    //6 / 365 = 0.01643836
    //3 / 365 = 0.00821918
    //9 / 365 = 0.02465753
    //1 / 365 = 0.00273973
    //```
    //7 / 905 = 0.0077
    //4 / 905 = 0.0044
    //2 / 905 = 0.0022
    //5 / 905 = 0.0055
    //10 /905 = 0.0110
    //8 / 905 = 0.0088
    //2 / 905 = 0.0022
    //2 / 905 = 0.0022
    //6 / 905 = 0.0066
    //12 /905 = 0.0133
    //8 / 905 = 0.0088
    //8 / 905 = 0.0088
    //3 / 905 = 0.0033
    //3 / 905 = 0.0033
    //4 / 905 = 0.0044
    //1 / 905 = 0.0011
    //2 / 905 = 0.0022
    //```


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
    private static double getRarity(Item item, int cap, int progress){
        return (double) getItemOccurrences(item, progress) * 100 / cap;
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
