package com.itlesports.nightmaremode.item;

import btw.item.BTWTags;
import btw.item.items.*;
import com.itlesports.nightmaremode.item.items.*;
import com.itlesports.nightmaremode.item.items.bloodItems.*;
import com.itlesports.nightmaremode.item.items.template.ItemStructureLocator;
import com.itlesports.nightmaremode.item.items.template.NMFoodItem;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.*;

public class NMItems {
    public static final int BLOOD_MOON_DURABILITY = 1200;


    public static ItemRPG rpg;
    public static ItemAR rifle;
    public static ItemBandage bandage;
    public static ItemIronKnittingNeedles ironKnittingNeedles;
    public static ItemStructureLocator witchLocator;

    public static ItemBloodOrb bloodOrb;
    public static ItemBloodPickaxe bloodPickaxe;
    public static ItemBloodAxe bloodAxe;
    public static ItemBloodShovel bloodShovel;
    public static ItemBloodHoe bloodHoe;
    public static ItemBloodSword bloodSword;

    public static ItemBloodArmor bloodHelmet;
    public static ItemBloodArmor bloodChestplate;
    public static ItemBloodArmor bloodLeggings;
    public static ItemBloodArmor bloodBoots;
    public static Item bloodIngot;

    public static Item darksunFragment;
    public static Item magicFeather;
    public static ItemBucketMilk bloodMilk;
    public static FoodItem creeperChop;
    public static Item voidSack;
    public static RottenFleshItem charredFlesh;
    public static Item spiderFangs;
    public static Item fireRod;
    public static Item waterRod;
    public static Item sulfur;
    public static Item creeperTear;
    public static Item silverLump;
    public static Item witheredBone;
    public static Item voidMembrane;
    public static RottenFleshItem decayedFlesh;
    public static Item ghastTentacle;
    public static Item elementalRod;
    public static Item shadowRod;
    public static Item speedCoil;
    public static NetherStarItem starOfTheBloodGod;
    public static FoodItem calamari;
    public static FoodItem calamariRoast;
    public static FoodItem friedCalamari;
    public static Item steelBunch;

    public static ItemEclipseBow eclipseBow;
    public static ItemMagicArrow magicArrow;
    public static ItemIronFishingPole ironFishingPole;

    public static FoodItem dungApple;
    public static FoodItem creeperBallSoup;

    public static Item ACHIEVEMENT_SPECIAL_SNOWBALL;
    public static Item ACHIEVEMENT_SPECIAL_HARDMODE;
    public static Item ACHIEVEMENT_SPECIAL_BLOODMOON;
    public static Item ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER;
    public static Item ACHIEVEMENT_SPECIAL_ECLIPSE;
    public static Item ACHIEVEMENT_SPECIAL_MERCHANT;
    public static Item ACHIEVEMENT_SPECIAL_CHICKEN;
    public static Item ACHIEVEMENT_SPECIAL_DIAMOND;
    public static Item ACHIEVEMENT_SPECIAL_SKULL;
    public static Item ACHIEVEMENT_SPECIAL_ARROW_TRIPLE;
    public static Item ACHIEVEMENT_SPECIAL_ARROW_RED;
    public static Item ACHIEVEMENT_SPECIAL_TRIPLE_TEAR;
    public static Item ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE;

    public static ItemStructureLocator templeLocator;
    public static Item refinedDiamondIngot;
    public static Item lightningBolt;
    public static Item villagerOrb;
    public static Item refinedElement;
    public static Item witherSoul;

    public static final Item obsidianShard;
    public static final Item honeyBall;

    public static final Item lifeFruit;
    public static final Item honeyMelon;
    public static final Item awakenedStar;
    public static final Item hellGem;
    public static final Item rawTitanium;
    public static final Item titaniumIngot;
    public static final Item titaniumSteelPlate;
    public static final Item rawTungsten;
    public static final Item tungstenIngot;
    public static final Item tungstenSteelPlate;
    public static final ItemUnderworldPickaxe titaniumPickaxe;
    public static final ItemUnderworldPickaxe tungstenPickaxe;
    public static final ItemUnderworldSword titaniumSword;
    public static final ItemUnderworldAxe titaniumAxe;
    public static final ItemUnderworldShovel titaniumShovel;
    public static final ItemUnderworldHoe titaniumHoe;
    public static final ItemUnderworldSword tungstenSword;
    public static final ItemUnderworldAxe tungstenAxe;
    public static final ItemUnderworldShovel tungstenShovel;
    public static final ItemUnderworldHoe tungstenHoe;
    public static final ItemUnderworldArmor titaniumHelmet;
    public static final ItemUnderworldArmor titaniumChestplate;
    public static final ItemUnderworldArmor titaniumLeggings;
    public static final ItemUnderworldArmor titaniumBoots;
    public static final ItemUnderworldArmor tungstenHelmet;
    public static final ItemUnderworldArmor tungstenChestplate;
    public static final ItemUnderworldArmor tungstenLeggings;
    public static final ItemUnderworldArmor tungstenBoots;
    public static final ItemSanityAnchor verdantHeart;
    public static final ItemSanityAnchor mycelialHeart;
    public static final Item bloomPollen;
    public static final Item bloomResin;
    public static final Item cinderResin;
    public static final Item underwebThread;
    public static final Item blastDust;
    public static final Item blightedFlesh;
    public static final Item brittleBone;
    public static final ItemSanityConsumable lucidFruit;
    public static final ItemSanityConsumable clarityDraught;
    public static final Item tungstenLens;
    public static final Item lucidBloomSeeds;
    public static final Item lucidPetal;
    public static final Item mycelialCore;





    static {
        rpg =(ItemRPG)(new ItemRPG(2309)).setTextureName("nightmare:nmRPG");
        rifle = (ItemAR)(new ItemAR(2310)).setTextureName("nightmare:nmRifle");
        bandage = (ItemBandage)(new ItemBandage(2311,0,0f,false)).setTextureName("nightmare:nmBandage");
        ironKnittingNeedles = (ItemIronKnittingNeedles)(new ItemIronKnittingNeedles(2312)).setTextureName("nightmare:nmNeedles");
        witchLocator = (ItemStructureLocator)(new ItemStructureLocator(2314, true, 0x84bdb8)).setTextureName("nightmare:nmWitchDust").setUnlocalizedName("nmItemWitchLocator");

        bloodOrb = (ItemBloodOrb)(new ItemBloodOrb(2315)).setTextureName("nightmare:nmBloodOrb");
        bloodPickaxe = (ItemBloodPickaxe)(new ItemBloodPickaxe(2316, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodPickaxe");
        bloodAxe = (ItemBloodAxe)(new ItemBloodAxe(2317, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodAxe");
        bloodShovel = (ItemBloodShovel)(new ItemBloodShovel(2318, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodShovel");
        bloodHoe = (ItemBloodHoe)(new ItemBloodHoe(2319, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodHoe");
        bloodSword = (ItemBloodSword)(new ItemBloodSword(2320, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodSword");

        bloodHelmet = (ItemBloodArmor)(new ItemBloodArmor(2321,EnumArmorMaterial.IRON,0,2, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nightmare:nmBloodHelmet").setUnlocalizedName("nmBloodHelmet");
        bloodChestplate = (ItemBloodArmor)(new ItemBloodArmor(2322,EnumArmorMaterial.DIAMOND,1,4, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nightmare:nmBloodChestplate").setUnlocalizedName("nmBloodChestplate");
        bloodLeggings = (ItemBloodArmor)(new ItemBloodArmor(2323,EnumArmorMaterial.IRON,2,3, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nightmare:nmBloodLeggings").setUnlocalizedName("nmBloodLeggings");
        bloodBoots = (ItemBloodArmor)(new ItemBloodArmor(2324,EnumArmorMaterial.IRON, 3,1, BLOOD_MOON_DURABILITY, 0d)).setTextureName("nightmare:nmBloodBoots").setUnlocalizedName("nmBloodBoots");

        bloodIngot = (new NMItem(2325)).setTextureName("nightmare:nmBloodIngot").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodIngot");

        darksunFragment = (new NMItem(2326)).setTextureName("nightmare:nmDarksunFragment").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDarksunFragment");

        magicFeather = (new NMItem(2327)).setIndestructible().setTextureName("nightmare:nmMagicFeather").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmMagicFeather");
        bloodMilk = (NMItemBucketMilk) (new NMItemBucketMilk(2328)).setTextureName("nightmare:nmBloodMilk").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodMilk");
        creeperChop = (NMFoodItem) (new NMFoodItem(2329,6,0.25f,false,"nmCreeperChop",false)).setTextureName("nightmare:nmCreeperChop").setCreativeTab(CreativeTabs.tabFood);
        voidSack = (new NMItem(2330)).setTextureName("nightmare:nmVoidSack").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidSack");
        voidMembrane = (new NMItem(2331)).setTextureName("nightmare:nmVoidMembrane").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidMembrane");
        charredFlesh = (NMRottenFleshItem) (new NMRottenFleshItem(2332)).setMaxStackSize(64).setTextureName("nightmare:nmCharredFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCharredFlesh");
        spiderFangs = (new NMItem(2333)).setTextureName("nightmare:nmSpiderFangs").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSpiderFangs");
        fireRod = (new NMItem(2334)).setTextureName("nightmare:nmHellFireRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmHellFireRod");
        waterRod = (new NMItem(2335)).setTextureName("nightmare:nmWaterRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWaterRod");
        sulfur = (new NMItem(2336)).setTextureName("nightmare:nmSulfur").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSulfur");
        creeperTear = (new NMItem(2337)).setTextureName("nightmare:nmCreeperTear").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCreeperTear");
        silverLump = (new NMItem(2338)).setTextureName("nightmare:nmSilverLump").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSilverLump");
        witheredBone = (new NMItem(2339)).setTextureName("nightmare:nmWitheredBone").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWitheredBone");
        decayedFlesh = (NMRottenFleshItem) (new NMRottenFleshItem(2340)).setMaxStackSize(64).setTextureName("nightmare:nmDecayedFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDecayedFlesh");
        ghastTentacle = (new NMItem(2341)).setTextureName("nightmare:nmGhastTentacle").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmGhastTentacle");
        elementalRod = (new NMItem(2342)).setTextureName("nightmare:nmElementalRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmElementalRod");
        shadowRod = (new NMItem(2343)).setTextureName("nightmare:nmShadowRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmShadowRod");
        speedCoil = (new NMItem(2344)).setTextureName("nightmare:nmSpeedCoil").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSpeedCoil");
        starOfTheBloodGod = (NMBloodStarItem) new NMBloodStarItem(2345).setMaxStackSize(1).setTextureName("nightmare:nmStarOfTheBloodGod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmStarOfTheBloodGod");

        calamari = (NMFoodItem) new NMFoodItem(2346, 2, 0f, true, "nmCalamari",true).setStandardFoodPoisoningEffect().setTextureName("nightmare:nmCalamari").setCreativeTab(CreativeTabs.tabFood);
        calamariRoast = (NMFoodItem) new NMFoodItem(2347, 5, 0.25f, true, "nmCalamariRoast",true).setTextureName("nightmare:nmCalamariRoast").setCreativeTab(CreativeTabs.tabFood);
        friedCalamari = (NMFoodItem) new NMFoodItem(2348, 9, 0.5f, true,"nmFriedCalamari", true).setTextureName("nightmare:nmFriedCalamari").setCreativeTab(CreativeTabs.tabFood);

        steelBunch = new NMItem(2349).setTextureName("nightmare:nmSteelBunch").setUnlocalizedName("nmSteelBunch").setCreativeTab(CreativeTabs.tabMaterials);
        eclipseBow = (ItemEclipseBow) new ItemEclipseBow(2350).setCreativeTab(CreativeTabs.tabCombat);
        magicArrow = (ItemMagicArrow) new ItemMagicArrow(2351).setTextureName("nightmare:nmMagicArrow").setUnlocalizedName("nmMagicArrow").setCreativeTab(CreativeTabs.tabCombat);
        ironFishingPole = (ItemIronFishingPole) new ItemIronFishingPole(2352).setCreativeTab(CreativeTabs.tabTools);

        dungApple = (NMFoodItem) new NMFoodItem(2353, 2, 0.25f, false, "nmDungApple",false).setPotionEffect(Potion.poison.id, 1, 128, 1.0f).setTextureName("nightmare:nmDungApple").setCreativeTab(CreativeTabs.tabFood);
        creeperBallSoup = (NMFoodItem) new NMFoodItem(2354, 6, 1f, false, "nmOysterSoup",false).setPotionEffect(Potion.regeneration.id, 10, 4, 1.0f).setTextureName("nightmare:nmOysterSoup").hideFromEMI();

        templeLocator = (ItemStructureLocator)(new ItemStructureLocator(2355, false, 0xFFFF00)).setTextureName("nightmare:nmTempleDust").setUnlocalizedName("nmTempleDust");


        obsidianShard = new NMItem(2356).setTextureName("nightmare:nmObsidianShard").setUnlocalizedName("nmObsidianShard").setCreativeTab(CreativeTabs.tabMaterials);

        // this code is done in ItemMixin to replace horse armor
//        ironHorseArmorAdvanced      = new ItemAdvancedHorseArmor(2357, ItemAdvancedHorseArmor.ArmorTier.IRON).setUnlocalizedName("nmHorseArmorIron").setTextureName("nmHorseArmorIron");
//        goldHorseArmorAdvanced      = new ItemAdvancedHorseArmor(2358, ItemAdvancedHorseArmor.ArmorTier.GOLD).setUnlocalizedName("nmHorseArmorGold").setTextureName("nmHorseArmorGold");
//        diamondHorseArmorAdvanced   = new ItemAdvancedHorseArmor(2359, ItemAdvancedHorseArmor.ArmorTier.DIAMOND).setUnlocalizedName("nmHorseArmorDiamond").setTextureName("nmHorseArmorDiamond");

        refinedDiamondIngot = new NMItem(2360).setTextureName("nightmare:nmRefinedDiamondIngot").setUnlocalizedName("nmRefinedDiamondIngot").setCreativeTab(CreativeTabs.tabMaterials);

        lightningBolt = new ItemLightningBolt(2361).setTextureName("nightmare:nmLightning").setUnlocalizedName("nmLightning").setCreativeTab(CreativeTabs.tabMisc);

        villagerOrb = new ItemVillagerOrb(2362).setUnlocalizedName("nmVillagerOrb");

        refinedElement = new NMItem(2363).setTextureName("nightmare:refinedElement").setUnlocalizedName("nmRefinedElement").setCreativeTab(CreativeTabs.tabMaterials).hideFromEMI();

        witherSoul = new NMItem(2364).setIndestructible().setTextureName("nightmare:nmWitherSoul").setUnlocalizedName("nmWitherSoul").setCreativeTab(CreativeTabs.tabMaterials);

        honeyBall = new NMItem(2365).setTextureName("nightmare:nmHoneyBall").setUnlocalizedName("nmHoneyBall").setCreativeTab(CreativeTabs.tabMaterials);

        lifeFruit = new ItemLifeFruit(2366, "nmLifeFruit").setTextureName("nightmare:nmLifeFruit").setCreativeTab(CreativeTabs.tabFood);

        honeyMelon = new ItemLifeFruit(2367, "nmHoneyMelon").setTextureName("nightmare:nmHoneyMelon").setCreativeTab(CreativeTabs.tabFood);

        awakenedStar = new NMBloodStarItem(2368).setTextureName("nightmare:nmAwakenedStar").setUnlocalizedName("nmAwakenedStar").setCreativeTab(CreativeTabs.tabMaterials);

        hellGem = new NMItem(2369).setIndestructible().setTextureName("nightmare:nmHellGem").setUnlocalizedName("nmHellGem").setCreativeTab(CreativeTabs.tabMaterials);

        rawTitanium = material(2370, "nmRawTitanium");
        titaniumIngot = material(2371, "nmTitaniumIngot");
        titaniumSteelPlate = material(2372, "nmTitaniumSteelPlate");
        rawTungsten = material(2373, "nmRawTungsten");
        tungstenIngot = material(2374, "nmTungstenIngot");
        tungstenSteelPlate = material(2375, "nmTungstenSteelPlate");

        titaniumPickaxe = (ItemUnderworldPickaxe)new ItemUnderworldPickaxe(2376, UnderworldToolTier.TITANIUM, 2800, 13.0F)
                .setUnlocalizedName("nmTitaniumPickaxe").setTextureName("nightmare:nmTitaniumPickaxe");
        tungstenPickaxe = (ItemUnderworldPickaxe)new ItemUnderworldPickaxe(2377, UnderworldToolTier.TUNGSTEN, 3600, 16.0F)
                .setUnlocalizedName("nmTungstenPickaxe").setTextureName("nightmare:nmTungstenPickaxe");
        verdantHeart = (ItemSanityAnchor)new ItemSanityAnchor(2378, 0).setUnlocalizedName("nmVerdantHeart").setTextureName("nightmare:nmVerdantHeart");
        mycelialHeart = (ItemSanityAnchor)new ItemSanityAnchor(2379, 1).setUnlocalizedName("nmMycelialHeart").setTextureName("nightmare:nmMycelialHeart");

        bloomPollen = material(2380, "nmBloomPollen");
        bloomResin = material(2382, "nmBloomResin");
        cinderResin = material(2383, "nmCinderResin");
        underwebThread = material(2384, "nmUnderwebThread");
        blastDust = material(2385, "nmBlastDust");
        blightedFlesh = material(2386, "nmBlightedFlesh");
        brittleBone = material(2387, "nmBrittleBone");
        lucidFruit = (ItemSanityConsumable)new ItemSanityConsumable(2388, "nmLucidFruit", 0.10).setTextureName("nightmare:nmLucidFruit").setCreativeTab(CreativeTabs.tabFood);
        clarityDraught = (ItemSanityConsumable)new ItemSanityConsumable(2389, "nmClarityDraught", 0.25).setTextureName("nightmare:nmClarityDraught").setCreativeTab(CreativeTabs.tabFood);
        tungstenLens = material(2390, "nmTungstenLens");

        titaniumHelmet = armor(2391, UnderworldToolTier.TITANIUM, 0, 10, "nmTitaniumHelmet");
        titaniumChestplate = armor(2392, UnderworldToolTier.TITANIUM, 1, 14, "nmTitaniumChestplate");
        titaniumLeggings = armor(2393, UnderworldToolTier.TITANIUM, 2, 12, "nmTitaniumLeggings");
        titaniumBoots = armor(2394, UnderworldToolTier.TITANIUM, 3, 8, "nmTitaniumBoots");
        tungstenHelmet = armor(2395, UnderworldToolTier.TUNGSTEN, 0, 10, "nmTungstenHelmet");
        tungstenChestplate = armor(2396, UnderworldToolTier.TUNGSTEN, 1, 14, "nmTungstenChestplate");
        tungstenLeggings = armor(2397, UnderworldToolTier.TUNGSTEN, 2, 12, "nmTungstenLeggings");
        tungstenBoots = armor(2398, UnderworldToolTier.TUNGSTEN, 3, 8, "nmTungstenBoots");














        ACHIEVEMENT_SPECIAL_SNOWBALL = new NMItem(2400).setTextureName("nightmare:nmAchievementSpecialSnowball").hideFromEMI();
        ACHIEVEMENT_SPECIAL_HARDMODE = new NMItem(2405).setTextureName("nightmare:nmAchievementHardmode").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOODMOON = new NMItem(2406).setTextureName("nightmare:nmAchievementBloodMoon").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER = new NMItem(2409).setTextureName("nightmare:nmAchievementBloodMoonWither").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ECLIPSE = new NMItem(2410).setTextureName("nightmare:nmAchievementEclipse").hideFromEMI();
        ACHIEVEMENT_SPECIAL_MERCHANT = new NMItem(2411).setTextureName("nightmare:nmAchievementMerchant").hideFromEMI();
        ACHIEVEMENT_SPECIAL_CHICKEN = new NMItem(2412).setTextureName("nightmare:nmAchievementChicken").hideFromEMI();
        ACHIEVEMENT_SPECIAL_DIAMOND = new NMItem(2413).setTextureName("nightmare:nmAchievementDiamond").hideFromEMI();
        ACHIEVEMENT_SPECIAL_SKULL = new NMItem(2415).setTextureName("nightmare:nmAchievementBloodSkull").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ARROW_TRIPLE = new NMItem(2416).setTextureName("nightmare:nmAchievementTripleArrow").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ARROW_RED = new NMItem(2417).setTextureName("nightmare:nmAchievementArrowRed").hideFromEMI();
        ACHIEVEMENT_SPECIAL_TRIPLE_TEAR = new NMItem(2418).setTextureName("nightmare:nmAchievementTripleTear").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE = new NMItem(2419).setTextureName("nightmare:nmAchievementBloodZombie").hideFromEMI();

        titaniumSword = sword(2420, UnderworldToolTier.TITANIUM, 2800, "nmTitaniumSword");
        titaniumAxe = axe(2421, UnderworldToolTier.TITANIUM, 2800, "nmTitaniumAxe");
        titaniumShovel = shovel(2422, UnderworldToolTier.TITANIUM, 2800, "nmTitaniumShovel");
        titaniumHoe = hoe(2423, UnderworldToolTier.TITANIUM, 2800, "nmTitaniumHoe");
        tungstenSword = sword(2424, UnderworldToolTier.TUNGSTEN, 3600, "nmTungstenSword");
        tungstenAxe = axe(2425, UnderworldToolTier.TUNGSTEN, 3600, "nmTungstenAxe");
        tungstenShovel = shovel(2426, UnderworldToolTier.TUNGSTEN, 3600, "nmTungstenShovel");
        tungstenHoe = hoe(2427, UnderworldToolTier.TUNGSTEN, 3600, "nmTungstenHoe");
        lucidBloomSeeds = new ItemLucidBloomSeeds(2428);
        lucidPetal = material(2429, "nmLucidPetal");
        mycelialCore = material(2430, "nmMycelialCore");


    }

    private static Item material(int id, String name) {
        return new NMItem(id).setTextureName("nightmare:" + name).setUnlocalizedName(name).setCreativeTab(CreativeTabs.tabMaterials);
    }

    private static ItemUnderworldArmor armor(int id, UnderworldToolTier tier, int armorType, int weight, String name) {
        int durability = tier == UnderworldToolTier.TUNGSTEN ? 1400 : 1000;
        double knockback = tier == UnderworldToolTier.TUNGSTEN ? 0.18D : 0.12D;
        double sanityReduction = tier == UnderworldToolTier.TUNGSTEN ? 0.10D : 0.05D;
        return (ItemUnderworldArmor)new ItemUnderworldArmor(id, tier, armorType, weight, durability, knockback, sanityReduction)
                .setUnlocalizedName(name).setTextureName("nightmare:" + name).setCreativeTab(CreativeTabs.tabCombat);
    }

    private static ItemUnderworldSword sword(int id, UnderworldToolTier tier, int durability, String name) {
        return (ItemUnderworldSword)new ItemUnderworldSword(id, tier, durability).setUnlocalizedName(name).setTextureName("nightmare:" + name);
    }

    private static ItemUnderworldAxe axe(int id, UnderworldToolTier tier, int durability, String name) {
        return (ItemUnderworldAxe)new ItemUnderworldAxe(id, tier, durability).setUnlocalizedName(name).setTextureName("nightmare:" + name);
    }

    private static ItemUnderworldShovel shovel(int id, UnderworldToolTier tier, int durability, String name) {
        return (ItemUnderworldShovel)new ItemUnderworldShovel(id, tier, durability).setUnlocalizedName(name).setTextureName("nightmare:" + name);
    }

    private static ItemUnderworldHoe hoe(int id, UnderworldToolTier tier, int durability, String name) {
        return (ItemUnderworldHoe)new ItemUnderworldHoe(id, tier, durability).setUnlocalizedName(name).setTextureName("nightmare:" + name);
    }

    public static void hideItems(){
        lightningBolt.hideFromEMI().setCreativeTab(null);
        refinedElement.hideFromEMI().setCreativeTab(null);
        awakenedStar.hideFromEMI().setCreativeTab(null);
    }

    public static void addItemsToTags(){
        // adds all the NM items to their respective tag. mostly food

        BTWTags.foods.add(calamari);
        BTWTags.foods.add(calamariRoast);
        BTWTags.foods.add(friedCalamari);
        BTWTags.foods.add(creeperChop);
        BTWTags.foods.add(dungApple);
        BTWTags.foods.add(creeperBallSoup);
        BTWTags.foods.add(lucidFruit);
        BTWTags.foods.add(clarityDraught);
        BTWTags.foods.add(verdantHeart);
        BTWTags.foods.add(mycelialHeart);

        BTWTags.swords.addIgnoringMeta(titaniumSword, tungstenSword);
        BTWTags.highQualityAxes.addIgnoringMeta(titaniumAxe, tungstenAxe);
        BTWTags.highQualityShovels.addIgnoringMeta(titaniumShovel, tungstenShovel);
        BTWTags.highQualityHoes.addIgnoringMeta(titaniumHoe, tungstenHoe);
        BTWTags.helmets.addIgnoringMeta(titaniumHelmet, tungstenHelmet);
        BTWTags.chestplates.addIgnoringMeta(titaniumChestplate, tungstenChestplate);
        BTWTags.leggings.addIgnoringMeta(titaniumLeggings, tungstenLeggings);
        BTWTags.boots.addIgnoringMeta(titaniumBoots, tungstenBoots);
    }

}
