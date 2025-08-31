package com.itlesports.nightmaremode.item;

import btw.item.items.*;
import com.itlesports.nightmaremode.item.items.*;
import com.itlesports.nightmaremode.item.items.bloodItems.*;
import net.minecraft.src.*;

public class NMItems {
    public static ItemRPG rpg;
    public static ItemAR rifle;
    public static ItemBandage bandage;
    public static ItemIronKnittingNeedles ironKnittingNeedles;
    public static ItemWitchLocator witchLocator;

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
    public static Item greg;
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
    public static Item ACHIEVEMENT_SPECIAL_LADDER_STONE;
    public static Item ACHIEVEMENT_SPECIAL_ROAD;
    public static Item ACHIEVEMENT_SPECIAL_LADDER_IRON;
    public static Item ACHIEVEMENT_SPECIAL_ASPHALT;
    public static Item ACHIEVEMENT_SPECIAL_HARDMODE;
    public static Item ACHIEVEMENT_SPECIAL_BLOODMOON;
    public static Item ACHIEVEMENT_SPECIAL_BLOODCHEST;
    public static Item ACHIEVEMENT_SPECIAL_LOCKER;
    public static Item ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER;
    public static Item ACHIEVEMENT_SPECIAL_ECLIPSE;
    public static Item ACHIEVEMENT_SPECIAL_MERCHANT;
    public static Item ACHIEVEMENT_SPECIAL_CHICKEN;
    public static Item ACHIEVEMENT_SPECIAL_DIAMOND;
    public static Item ACHIEVEMENT_SPECIAL_BLOOD_BONE;
    public static Item ACHIEVEMENT_SPECIAL_SKULL;
    public static Item ACHIEVEMENT_SPECIAL_ARROW_TRIPLE;
    public static Item ACHIEVEMENT_SPECIAL_ARROW_RED;
    public static Item ACHIEVEMENT_SPECIAL_TRIPLE_TEAR;
    public static Item ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE;



    public static final int BLOOD_MOON_DURABILITY = 1200;


    static {
        rpg =(ItemRPG)(new ItemRPG(2309)).setTextureName("nmRPG");
        rifle = (ItemAR)(new ItemAR(2310)).setTextureName("nmRifle");
        bandage = (ItemBandage)(new ItemBandage(2311,0,0f,false)).setTextureName("nmBandage");
        ironKnittingNeedles = (ItemIronKnittingNeedles)(new ItemIronKnittingNeedles(2312)).setTextureName("nmNeedles");
        witchLocator = (ItemWitchLocator)(new ItemWitchLocator(2314)).setTextureName("nmWitchDust");

        bloodOrb = (ItemBloodOrb)(new ItemBloodOrb(2315)).setTextureName("nmBloodOrb");
        bloodPickaxe = (ItemBloodPickaxe)(new ItemBloodPickaxe(2316, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nmBloodPickaxe");
        bloodAxe = (ItemBloodAxe)(new ItemBloodAxe(2317, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nmBloodAxe");
        bloodShovel = (ItemBloodShovel)(new ItemBloodShovel(2318, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nmBloodShovel");
        bloodHoe = (ItemBloodHoe)(new ItemBloodHoe(2319, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nmBloodHoe");
        bloodSword = (ItemBloodSword)(new ItemBloodSword(2320, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nmBloodSword");

        bloodHelmet = (ItemBloodArmor)(new ItemBloodArmor(2321,EnumArmorMaterial.IRON,0,2, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nmBloodHelmet").setUnlocalizedName("nmBloodHelmet");
        bloodChestplate = (ItemBloodArmor)(new ItemBloodArmor(2322,EnumArmorMaterial.DIAMOND,1,4, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nmBloodChestplate").setUnlocalizedName("nmBloodChestplate").setMaxDamage(BLOOD_MOON_DURABILITY);
        bloodLeggings = (ItemBloodArmor)(new ItemBloodArmor(2323,EnumArmorMaterial.IRON,2,3, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nmBloodLeggings").setUnlocalizedName("nmBloodLeggings");
        bloodBoots = (ItemBloodArmor)(new ItemBloodArmor(2324,EnumArmorMaterial.IRON, 3,1, BLOOD_MOON_DURABILITY, 0d)).setTextureName("nmBloodBoots").setUnlocalizedName("nmBloodBoots");
        bloodIngot = (new Item(2325)).setTextureName("nmBloodIngot").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodIngot");

        darksunFragment = (new Item(2326)).setTextureName("nmDarksunFragment").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDarksunFragment");

        magicFeather = (new Item(2327)).setTextureName("nmMagicFeather").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmMagicFeather");
        bloodMilk = (ItemBucketMilk) (new ItemBucketMilk(2328)).setTextureName("nmBloodMilk").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodMilk");
        creeperChop = (FoodItem) (new FoodItem(2329,6,0.25f,false,"nmCreeperChop",false)).setTextureName("nmCreeperChop").setCreativeTab(CreativeTabs.tabFood);
        voidSack = (new Item(2330)).setTextureName("nmVoidSack").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidSack");
        voidMembrane = (new Item(2331)).setTextureName("nmVoidMembrane").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidMembrane");
        charredFlesh = (RottenFleshItem) (new RottenFleshItem(2332)).setMaxStackSize(64).setTextureName("nmCharredFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCharredFlesh");
        spiderFangs = (new Item(2333)).setTextureName("nmSpiderFangs").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSpiderFangs");
        fireRod = (new Item(2334)).setTextureName("nmHellFireRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmHellFireRod");
        waterRod = (new Item(2335)).setTextureName("nmWaterRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWaterRod");
        sulfur = (new Item(2336)).setTextureName("nmSulfur").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSulfur");
        creeperTear = (new Item(2337)).setTextureName("nmCreeperTear").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCreeperTear");
        silverLump = (new Item(2338)).setTextureName("nmSilverLump").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSilverLump");
        witheredBone = (new Item(2339)).setTextureName("nmWitheredBone").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWitheredBone");
        decayedFlesh = (RottenFleshItem) (new RottenFleshItem(2340)).setMaxStackSize(64).setTextureName("nmDecayedFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDecayedFlesh");
        ghastTentacle = (new Item(2341)).setTextureName("nmGhastTentacle").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmGhastTentacle");
        elementalRod = (new Item(2342)).setTextureName("nmElementalRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmElementalRod");
        shadowRod = (new Item(2343)).setTextureName("nmShadowRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmShadowRod");
        greg = (new Item(2344)).setTextureName("nmGreg").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmGreg");
        starOfTheBloodGod = (NetherStarItem) new NetherStarItem(2345).setMaxStackSize(1).setTextureName("nmStarOfTheBloodGod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmStarOfTheBloodGod");

        calamari = (FoodItem) new FoodItem(2346, 3, 0f, true, "nmCalamari",true).setStandardFoodPoisoningEffect().setTextureName("nmCalamari").setCreativeTab(CreativeTabs.tabFood);
        calamariRoast = (FoodItem) new FoodItem(2347, 8, 0.25f, true, "nmCalamariRoast",true).setTextureName("nmCalamariRoast").setCreativeTab(CreativeTabs.tabFood);
        friedCalamari = (FoodItem) new FoodItem(2348, 12, 0.5f, true,"nmFriedCalamari").setTextureName("nmFriedCalamari").setCreativeTab(CreativeTabs.tabFood);

        steelBunch = new Item(2349).setTextureName("nmSteelBunch").setUnlocalizedName("nmSteelBunch").setCreativeTab(CreativeTabs.tabMaterials);
        eclipseBow = (ItemEclipseBow) new ItemEclipseBow(2350).setCreativeTab(CreativeTabs.tabCombat);
        magicArrow = (ItemMagicArrow) new ItemMagicArrow(2351).setTextureName("nmMagicArrow").setUnlocalizedName("nmMagicArrow").setCreativeTab(CreativeTabs.tabCombat);
        ironFishingPole = (ItemIronFishingPole) new ItemIronFishingPole(2352).setCreativeTab(CreativeTabs.tabTools);

        dungApple = (FoodItem) new FoodItem(2353, 2, 0.25f, false, "nmDungApple",false).setPotionEffect(Potion.poison.id, 1, 128, 1.0f).setTextureName("nmDungApple").setCreativeTab(CreativeTabs.tabFood);
        creeperBallSoup = (FoodItem) new FoodItem(2354, 6, 1f, false, "nmOysterSoup",false).setPotionEffect(Potion.regeneration.id, 10, 4, 1.0f).setTextureName("nmOysterSoup").hideFromEMI();

        ACHIEVEMENT_SPECIAL_SNOWBALL = new Item(2400).setTextureName("nmAchievementSpecialSnowball");
        ACHIEVEMENT_SPECIAL_LADDER_STONE = new Item(2401).setTextureName("nmAchievementStoneLadder");
        ACHIEVEMENT_SPECIAL_LADDER_IRON = new Item(2402).setTextureName("nmAchievementIronLadder");
        ACHIEVEMENT_SPECIAL_ROAD = new Item(2403).setTextureName("nmAchievementRoad");
        ACHIEVEMENT_SPECIAL_ASPHALT = new Item(2404).setTextureName("nmAchievementAsphalt");
        ACHIEVEMENT_SPECIAL_HARDMODE = new Item(2405).setTextureName("nmAchievementHardmode");
        ACHIEVEMENT_SPECIAL_BLOODMOON = new Item(2406).setTextureName("nmAchievementBloodMoon");
        ACHIEVEMENT_SPECIAL_BLOODCHEST = new Item(2407).setTextureName("nmAchievementBloodChest");
        ACHIEVEMENT_SPECIAL_LOCKER = new Item(2408).setTextureName("nmAchievementSteelLocker");
        ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER = new Item(2409).setTextureName("nmAchievementBloodMoonWither");
        ACHIEVEMENT_SPECIAL_ECLIPSE = new Item(2410).setTextureName("nmAchievementEclipse");
        ACHIEVEMENT_SPECIAL_MERCHANT = new Item(2411).setTextureName("nmAchievementMerchant");
        ACHIEVEMENT_SPECIAL_CHICKEN = new Item(2412).setTextureName("nmAchievementChicken");
        ACHIEVEMENT_SPECIAL_DIAMOND = new Item(2413).setTextureName("nmAchievementDiamond");
        ACHIEVEMENT_SPECIAL_BLOOD_BONE = new Item(2414).setTextureName("nmAchievementBloodBone");
        ACHIEVEMENT_SPECIAL_SKULL = new Item(2415).setTextureName("nmAchievementBloodSkull");
        ACHIEVEMENT_SPECIAL_ARROW_TRIPLE = new Item(2416).setTextureName("nmAchievementTripleArrow");
        ACHIEVEMENT_SPECIAL_ARROW_RED = new Item(2417).setTextureName("nmAchievementArrowRed");
        ACHIEVEMENT_SPECIAL_TRIPLE_TEAR = new Item(2418).setTextureName("nmAchievementTripleTear");
        ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE = new Item(2419).setTextureName("nmAchievementBloodZombie");

    }
}
