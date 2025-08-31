package com.itlesports.nightmaremode.mixin;

import btw.achievement.BTWAchievements;
import btw.achievement.event.BTWAchievementEvents;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.achievements.AchievementExt;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static btw.achievement.BTWAchievements.*;


@Mixin(BTWAchievements.class)
public abstract class BTWAchievementsMixin implements AchievementAccessor{
    @Shadow @Final public static Achievement<BTWAchievementEvents.BeaconEventData> MAX_STEEL_BEACON;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void changeAchievements(CallbackInfo ci){
        addParent(FIND_SHAFT, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_REEDS, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_BONES, NMAchievements.MORNING_SECOND_DAY);

        addParent(COOK_FOOD, NMAchievements.KILL_ANIMAL);


        setHidden(CRAFT_HAMPER,true);

        kill(EQUIP_WOOL_ARMOR);

        move(FIND_REEDS,-1,0);
        move(CRAFT_WICKER,-1,0);
        move(CRAFT_BASKET,-1,0);
        move(CRAFT_KNITTING_NEEDLES,-1,0);
        move(CRAFT_WOOL_KNIT,-1,0);
        move(MINE_DIAMOND_ORE, 0, 1);

        // 2ND TAB - TAB_IRON_AGE
        move(CRAFT_CAULDRON, 0, -2);

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
        kill(EQUIP_STEEL_ARMOR);
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
}
