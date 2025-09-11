package com.itlesports.nightmaremode.mixin;

import btw.achievement.AchievementTab;
import btw.achievement.BTWAchievements;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.achievements.AchievementExt;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static btw.achievement.BTWAchievements.*;
import static com.itlesports.nightmaremode.achievements.NMAchievements.CRAFT_FABRIC;
import static com.itlesports.nightmaremode.achievements.NMAchievements.CRAFT_STEEL_INGOT;


@Mixin(BTWAchievements.class)
public abstract class BTWAchievementsMixin implements AchievementAccessor{


    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void changeAchievements(CallbackInfo ci){
        addParent(FIND_SHAFT, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_REEDS, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_BONES, NMAchievements.MORNING_SECOND_DAY);

        addParent(COOK_FOOD, NMAchievements.KILL_ANIMAL);


        setHidden(CRAFT_HAMPER,true);

        kill(EQUIP_WOOL_ARMOR);

        move(FIND_REEDS,-1,0);
        move(CRAFT_BASKET,-2,0);
        move(CRAFT_KNITTING_NEEDLES,-1,0);
        move(CRAFT_WOOL_KNIT,-1,0);
        move(MINE_DIAMOND_ORE, 0, 1);

        // wicker stuff for snap 6
        move(FIND_REEDS, 0, -12);
        move(CRAFT_WICKER, -1, -12);
        move(CRAFT_BASKET, -1, -12);

        removeParent(CRAFT_BASKET,GRIND_HEMP_FIBERS);
        move(CRAFT_BEDROLL, 0, -1);

        // the hemp purge

        switchTab(GRIND_HEMP_FIBERS, TAB_IRON_AGE);
        switchTab(HARVEST_HEMP, TAB_IRON_AGE);
        switchTab(CRAFT_MILLSTONE, TAB_IRON_AGE);
        switchTab(CRAFT_HAND_CRANK, TAB_IRON_AGE);
        switchTab(FIND_HEMP_SEEDS, TAB_IRON_AGE);
        switchTab(CRAFT_GEAR, TAB_IRON_AGE);
        removeParent(CRAFT_GEAR, FIND_LOGS);
        removeParent(FIND_HEMP_SEEDS, CRAFT_STONE_HOE);
        kill(CRAFT_STONE_HOE);

        // move everything that was moved down back up
        move(CRAFT_STONE_SHOVEL, -2, 0);
        move(CRAFT_WET_BRICKS, -2, 0);
        move(DRY_BRICKS, -2, 0);
        move(CRAFT_OVEN, -2, 0);
        move(SMELT_IRON, -2, 0);
        move(CRAFT_IRON_CHISEL, -2, 0);
        move(MAKE_WORK_STUMP, -2, 0);
        move(FIND_STONE_BRICK, -2, 0);
        move(CRAFT_STONE_PICKAXE, -2, 0);
        move(CRAFT_IRON_INGOT, -2, 0);
        move(MINE_DIAMOND_ORE, -2, 0);
        move(MINE_REDSTONE_ORE, -2, 0);
        move(CRAFT_COMPASS, -2, 0);
        move(CRAFT_BOW_DRILL, 4, 0);
        // done moving up



        move(CRAFT_KNITTING_NEEDLES, 3, -2);
        move(CRAFT_WOOL_KNIT, 3, -2);

        move(CRAFT_KNITTING_NEEDLES, -1, 2);
        move(CRAFT_WOOL_KNIT, -1, 2);
        move(CRAFT_BEDROLL, 2, 1);
        kill(CRAFT_BARK_BOX);

        // 2ND TAB - TAB_IRON_AGE
        move(CRAFT_CAULDRON, 0, -2);

            // hemp arc

        move(FIND_HEMP_SEEDS, 0, -6);
        addParent(FIND_HEMP_SEEDS, CRAFT_IRON_HOE);
        move(HARVEST_HEMP, 2, -7);
        move(CRAFT_GEAR, -2, -5);
        addParent(CRAFT_GEAR, CRAFT_PLANKS);
        move(CRAFT_MILLSTONE, -1, -6);
        move(CRAFT_HAND_CRANK, -3, -6);
        move(GRIND_HEMP_FIBERS, 2, -6);

        removeParent(CRAFT_SAIL, CRAFT_PLANKS);
        addParent(CRAFT_SAIL, CRAFT_FABRIC);
        destroyParents(CRAFT_BASKET);
        kill(CRAFT_BASKET);
            // hemp arc over

        move(USE_EMERALD_PILE, 1, 1);





        // 3RD TAB - TAB_AUTOMATION
        kill(TOSS_THE_MILK);
        move(CONVERT_SOULFORGE, 0, -2);
        addParent(CONVERT_EYES_OF_ENDER, NMAchievements.CRAFT_CORPSE_EYE);
        move(FIND_DORMANT_SOULFORGE, 0, -2);

        // 4TH TAB - TAB_END_GAME
        removeParent(EQUIP_STEEL_ARMOR, CRAFT_STEEL);
        removeParent(CRAFT_STEEL_COMBO_TOOL, CRAFT_STEEL);
        removeParent(CRAFT_INFERNAL_ENCHANTER, CRAFT_STEEL);
        removeParent(USE_INFERNAL_ENCHANTER, CRAFT_INFERNAL_ENCHANTER);
        removeParent(MAX_INFERNAL_ENCHANT, USE_INFERNAL_ENCHANTER);
        removeParent(MAX_STEEL_BEACON, CRAFT_STEEL);
        removeParent(STEEL_BEACON_RESPAWN_ACROSS_DIMENSIONS, MAX_STEEL_BEACON);
        removeParent(CRAFT_STEEL, KILLED_DRAGON);
        kill(CRAFT_STEEL_COMBO_TOOL);
        kill(CRAFT_STEEL);

        // moving steel armor quest to automation
        switchTab(EQUIP_STEEL_ARMOR, TAB_AUTOMATION);
        move(EQUIP_STEEL_ARMOR, -2 ,12);
        addParent(EQUIP_STEEL_ARMOR, CRAFT_STEEL_INGOT);
        // done, back to endgame
        removeParent(GET_BOOTSIES, MAX_STEEL_BEACON);
        kill(GET_BOOTSIES);


        kill(CRAFT_INFERNAL_ENCHANTER);

        kill(USE_INFERNAL_ENCHANTER);
        kill(MAX_INFERNAL_ENCHANT);
        kill(STEEL_BEACON_RESPAWN_ACROSS_DIMENSIONS);

        move(MAX_STEEL_BEACON, 4, 6);

        setIcon(MAX_STEEL_BEACON, BTWBlocks.soulforgedSteelBlock);


    }

    @Unique private static void addParent(Achievement acObj, Achievement achievementToAdd){
        ((AchievementExt) acObj).nightmareMode$appendParent(achievementToAdd);
    }
    @Unique private static void setHidden(Achievement acObj, boolean hidden){
        acObj.isHidden = hidden;
    }
    @Unique private static void kill(Achievement acObj){
        acObj.tab.achievementList.remove(acObj);
    }
    @Unique private static void setDisplay(Achievement acObj, int row, int column){
        ((AchievementExt) acObj).nightmareMode$setDisplay(row, column);
    }
    @Unique private static void move(Achievement acObj, int down, int right){
       setDisplay(acObj,acObj.displayRow + down, acObj.displayColumn + right);
    }
    @Unique
    private static void removeParent(Achievement myAchievement, Achievement parentToRemove) {
        AchievementExt ext = (AchievementExt) (Object) myAchievement;
        Achievement[] current = ((AchievementAccessor)myAchievement).getParents();
        Achievement[] updated = ext.nightmareMode$removeParent(current, parentToRemove);
        ((AchievementAccessor)myAchievement).setParentAchievements(updated);
    }
    @Unique
    private static void destroyParents(Achievement myAchievement){
        ((AchievementAccessor)myAchievement).setParentAchievements(new Achievement[0]);
    }

    @Unique
    private static void setIcon(Achievement acObj, Block block){
        AchievementExt ext = (AchievementExt) (Object) acObj;
        ext.nightmareMode$setIcon(new ItemStack(block));
    }
    @Unique
    private static void setIcon(Achievement acObj, Item item){
        AchievementExt ext = (AchievementExt) (Object) acObj;
        ext.nightmareMode$setIcon(new ItemStack(item));
    }
    @Unique
    private static void setIcon(Achievement acObj, ItemStack stack){
        AchievementExt ext = (AchievementExt) (Object) acObj;
        ext.nightmareMode$setIcon(stack);
    }

    @Unique
    private static void switchTab(Achievement acObj, AchievementTab tab){
        acObj.tab.achievementList.remove(acObj);
        acObj.tab = tab;
        tab.achievementList.add(acObj);
    }
}
