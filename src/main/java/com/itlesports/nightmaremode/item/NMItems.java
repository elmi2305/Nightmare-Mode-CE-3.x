package com.itlesports.nightmaremode.item;

import btw.item.items.FoodItem;
import btw.item.items.NetherStarItem;
import btw.item.items.RottenFleshItem;
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

        bloodHelmet = (ItemBloodArmor)(new ItemBloodArmor(2321,0,3, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nmBloodHelmet").setUnlocalizedName("nmBloodHelmet");
        bloodChestplate = (ItemBloodArmor)(new ItemBloodArmor(2322,1,6, BLOOD_MOON_DURABILITY, 0.1d)).setTextureName("nmBloodChestplate").setUnlocalizedName("nmBloodChestplate");
        bloodLeggings = (ItemBloodArmor)(new ItemBloodArmor(2323,2,5, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nmBloodLeggings").setUnlocalizedName("nmBloodLeggings");
        bloodBoots = (ItemBloodArmor)(new ItemBloodArmor(2324,3,2, BLOOD_MOON_DURABILITY, 0d)).setTextureName("nmBloodBoots").setUnlocalizedName("nmBloodBoots");
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

    }
}
