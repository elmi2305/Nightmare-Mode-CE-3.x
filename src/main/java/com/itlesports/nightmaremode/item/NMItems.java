package com.itlesports.nightmaremode.item;

import btw.achievement.BTWAchievements;
import btw.item.items.*;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.item.items.*;
import com.itlesports.nightmaremode.item.items.bloodItems.*;
import net.minecraft.src.*;

public class NMItems {
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




    public static final int BLOOD_MOON_DURABILITY = 1200;


    public static final Item obsidianShard;

    static {
        rpg =(ItemRPG)(new ItemRPG(2309)).setTextureName("nmRPG");
        rifle = (ItemAR)(new ItemAR(2310)).setTextureName("nmRifle");
        bandage = (ItemBandage)(new ItemBandage(2311,0,0f,false)).setTextureName("nmBandage");
        ironKnittingNeedles = (ItemIronKnittingNeedles)(new ItemIronKnittingNeedles(2312)).setTextureName("nmNeedles");
        witchLocator = (ItemStructureLocator)(new ItemStructureLocator(2314, true)).setTextureName("nmWitchDust").setUnlocalizedName("nmItemWitchLocator");

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

        bloodIngot = (new NMItem(2325)).setTextureName("nmBloodIngot").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodIngot");

        darksunFragment = (new NMItem(2326)).setTextureName("nmDarksunFragment").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDarksunFragment");

        magicFeather = (new NMItem(2327)).setTextureName("nmMagicFeather").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmMagicFeather");
        bloodMilk = (NMItemBucketMilk) (new NMItemBucketMilk(2328)).setTextureName("nmBloodMilk").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodMilk");
        creeperChop = (NMFoodItem) (new NMFoodItem(2329,6,0.25f,false,"nmCreeperChop",false)).setTextureName("nmCreeperChop").setCreativeTab(CreativeTabs.tabFood);
        voidSack = (new NMItem(2330)).setTextureName("nmVoidSack").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidSack");
        voidMembrane = (new NMItem(2331)).setTextureName("nmVoidMembrane").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidMembrane");
        charredFlesh = (NMRottenFleshItem) (new NMRottenFleshItem(2332)).setMaxStackSize(64).setTextureName("nmCharredFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCharredFlesh");
        spiderFangs = (new NMItem(2333)).setTextureName("nmSpiderFangs").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSpiderFangs");
        fireRod = (new NMItem(2334)).setTextureName("nmHellFireRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmHellFireRod");
        waterRod = (new NMItem(2335)).setTextureName("nmWaterRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWaterRod");
        sulfur = (new NMItem(2336)).setTextureName("nmSulfur").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSulfur");
        creeperTear = (new NMItem(2337)).setTextureName("nmCreeperTear").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCreeperTear");
        silverLump = (new NMItem(2338)).setTextureName("nmSilverLump").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSilverLump");
        witheredBone = (new NMItem(2339)).setTextureName("nmWitheredBone").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWitheredBone");
        decayedFlesh = (NMRottenFleshItem) (new NMRottenFleshItem(2340)).setMaxStackSize(64).setTextureName("nmDecayedFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDecayedFlesh");
        ghastTentacle = (new NMItem(2341)).setTextureName("nmGhastTentacle").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmGhastTentacle");
        elementalRod = (new NMItem(2342)).setTextureName("nmElementalRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmElementalRod");
        shadowRod = (new NMItem(2343)).setTextureName("nmShadowRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmShadowRod");
        greg = (new NMItem(2344)).setTextureName("nmGreg").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmGreg");
        starOfTheBloodGod = (NMNetherStarItem) new NMNetherStarItem(2345).setMaxStackSize(1).setTextureName("nmStarOfTheBloodGod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmStarOfTheBloodGod");

        calamari = (NMFoodItem) new NMFoodItem(2346, 3, 0f, true, "nmCalamari",true).setStandardFoodPoisoningEffect().setTextureName("nmCalamari").setCreativeTab(CreativeTabs.tabFood);
        calamariRoast = (NMFoodItem) new NMFoodItem(2347, 8, 0.25f, true, "nmCalamariRoast",true).setTextureName("nmCalamariRoast").setCreativeTab(CreativeTabs.tabFood);
        friedCalamari = (NMFoodItem) new NMFoodItem(2348, 12, 0.5f, true,"nmFriedCalamari", true).setTextureName("nmFriedCalamari").setCreativeTab(CreativeTabs.tabFood);

        steelBunch = new NMItem(2349).setTextureName("nmSteelBunch").setUnlocalizedName("nmSteelBunch").setCreativeTab(CreativeTabs.tabMaterials);
        eclipseBow = (ItemEclipseBow) new ItemEclipseBow(2350).setCreativeTab(CreativeTabs.tabCombat);
        magicArrow = (ItemMagicArrow) new ItemMagicArrow(2351).setTextureName("nmMagicArrow").setUnlocalizedName("nmMagicArrow").setCreativeTab(CreativeTabs.tabCombat);
        ironFishingPole = (ItemIronFishingPole) new ItemIronFishingPole(2352).setCreativeTab(CreativeTabs.tabTools);

        dungApple = (NMFoodItem) new NMFoodItem(2353, 2, 0.25f, false, "nmDungApple",false).setPotionEffect(Potion.poison.id, 1, 128, 1.0f).setTextureName("nmDungApple").setCreativeTab(CreativeTabs.tabFood);
        creeperBallSoup = (NMFoodItem) new NMFoodItem(2354, 6, 1f, false, "nmOysterSoup",false).setPotionEffect(Potion.regeneration.id, 10, 4, 1.0f).setTextureName("nmOysterSoup").hideFromEMI();

        templeLocator = (ItemStructureLocator)(new ItemStructureLocator(2355, false)).setTextureName("nmTempleDust").setUnlocalizedName("nmTempleDust");


        obsidianShard = new NMItem(2356).setTextureName("nmObsidianShard").setUnlocalizedName("nmObsidianShard").setCreativeTab(CreativeTabs.tabMaterials);

        // this code is done in ItemMixin to replace horse armor
//        ironHorseArmorAdvanced      = new ItemAdvancedHorseArmor(2357, ItemAdvancedHorseArmor.ArmorTier.IRON).setUnlocalizedName("nmHorseArmorIron").setTextureName("nmHorseArmorIron");
//        goldHorseArmorAdvanced      = new ItemAdvancedHorseArmor(2358, ItemAdvancedHorseArmor.ArmorTier.GOLD).setUnlocalizedName("nmHorseArmorGold").setTextureName("nmHorseArmorGold");
//        diamondHorseArmorAdvanced   = new ItemAdvancedHorseArmor(2359, ItemAdvancedHorseArmor.ArmorTier.DIAMOND).setUnlocalizedName("nmHorseArmorDiamond").setTextureName("nmHorseArmorDiamond");

        refinedDiamondIngot = new NMItem(2360).setTextureName("nmRefinedDiamondIngot").setUnlocalizedName("nmRefinedDiamondIngot").setCreativeTab(CreativeTabs.tabMaterials);



        ACHIEVEMENT_SPECIAL_SNOWBALL = new NMItem(2400).setTextureName("nmAchievementSpecialSnowball").hideFromEMI();
        ACHIEVEMENT_SPECIAL_HARDMODE = new NMItem(2405).setTextureName("nmAchievementHardmode").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOODMOON = new NMItem(2406).setTextureName("nmAchievementBloodMoon").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER = new NMItem(2409).setTextureName("nmAchievementBloodMoonWither").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ECLIPSE = new NMItem(2410).setTextureName("nmAchievementEclipse").hideFromEMI();
        ACHIEVEMENT_SPECIAL_MERCHANT = new NMItem(2411).setTextureName("nmAchievementMerchant").hideFromEMI();
        ACHIEVEMENT_SPECIAL_CHICKEN = new NMItem(2412).setTextureName("nmAchievementChicken").hideFromEMI();
        ACHIEVEMENT_SPECIAL_DIAMOND = new NMItem(2413).setTextureName("nmAchievementDiamond").hideFromEMI();
        ACHIEVEMENT_SPECIAL_SKULL = new NMItem(2415).setTextureName("nmAchievementBloodSkull").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ARROW_TRIPLE = new NMItem(2416).setTextureName("nmAchievementTripleArrow").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ARROW_RED = new NMItem(2417).setTextureName("nmAchievementArrowRed").hideFromEMI();
        ACHIEVEMENT_SPECIAL_TRIPLE_TEAR = new NMItem(2418).setTextureName("nmAchievementTripleTear").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE = new NMItem(2419).setTextureName("nmAchievementBloodZombie").hideFromEMI();




    }
}
