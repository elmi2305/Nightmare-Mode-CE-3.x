package com.itlesports.nightmaremode.world;

import btw.achievement.BTWAchievements;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.skill.SkillRegistry;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import com.itlesports.nightmaremode.skill.WorldSkillData;
import net.minecraft.src.Achievement;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Item;
import net.minecraft.src.Block;
import com.itlesports.nightmaremode.item.items.ItemVillagerDebugTool;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;
import net.minecraft.src.World;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Compact, root-level world metadata used by the Journey Mode title card. */
public final class JourneyProfile {
    public static final String TAG = "NightmareJourneyProfile";
    private static final int FORMAT = 4;
    private static final Achievement<?>[] PROGRESSION = new Achievement<?>[] {
            NMAchievements.MORNING_SECOND_DAY,
            BTWAchievements.SMELT_IRON,
            BTWAchievements.CRAFT_STONE_PICKAXE,
            BTWAchievements.CRAFT_IRON_INGOT,
            NMAchievements.IFHY_LITHIUM_REFINED,
            NMAchievements.IFHY_NICKEL_INGOT,
            NMAchievements.IFHY_CISTERN,
            BTWAchievements.FIND_DIAMONDS,
            NMAchievements.HARDMODE,
            BTWAchievements.KILLED_WITHER,
            NMAchievements.CRAFT_STEEL_INGOT,
            BTWAchievements.MAX_LEVEL_LIBRARIAN,
            BTWAchievements.KILLED_DRAGON,
            NMAchievements.GET_ECLIPSE_SHARD,
            NMAchievements.ECLIPSE_WITCH,
            NMAchievements.KILL_BLOODWITHER
    };

    public final boolean valid;
    public long createdAt;
    public long playTicks;
    public int deaths;
    public int joins;
    public long kills;
    public int progressIndex;
    public int worldState;
    private final Set<String> completedSkillNodes = new HashSet<String>();
    private final Set<String> collectedItems = new HashSet<String>();
    private final Map<String, Set<NBTTagCompound>> collectedTags = new HashMap<String, Set<NBTTagCompound>>();

    private JourneyProfile(boolean valid) { this.valid = valid; }

    public static JourneyProfile missing() { return new JourneyProfile(false); }

    public static JourneyProfile create() {
        JourneyProfile profile = new JourneyProfile(true);
        profile.createdAt = System.currentTimeMillis();
        return profile;
    }

    /** Begins collecting live data for an older save without inventing a creation date. */
    public static JourneyProfile beginTrackingLegacyWorld() { return new JourneyProfile(true); }

    public static JourneyProfile read(NBTTagCompound root) {
        try {
            if (root == null || !root.hasKey(TAG)) return missing();
            NBTTagCompound tag = root.getCompoundTag(TAG);
            int format = tag.getInteger("Format");
            if (format < 1 || format > FORMAT) return missing();
            JourneyProfile profile = new JourneyProfile(true);
            profile.createdAt = Math.max(0L, tag.getLong("CreatedAt"));
            profile.playTicks = Math.max(0L, tag.getLong("PlayTicks"));
            profile.deaths = Math.max(0, tag.getInteger("Deaths"));
            profile.joins = Math.max(0, tag.getInteger("Joins"));
            profile.kills = Math.max(0L, tag.getLong("Kills"));
            profile.progressIndex = clamp(tag.getInteger("Progress"), 0, PROGRESSION.length - 1);
            profile.worldState = clamp(tag.getInteger("WorldState"), 0, 3);
            NBTTagList skills = tag.getTagList("CompletedSkillNodes");
            for (int i = 0; i < skills.tagCount(); ++i) {
                profile.completedSkillNodes.add(((NBTTagString)skills.tagAt(i)).data);
            }
            NBTTagList items = tag.getTagList("CollectedItems");
            for (int i = 0; i < items.tagCount(); ++i) {
                profile.collectedItems.add(((NBTTagString)items.tagAt(i)).data);
            }
            NBTTagList tags = tag.getTagList("CollectedItemTags");
            for (int i = 0; i < tags.tagCount(); ++i) {
                NBTTagCompound entry = (NBTTagCompound)tags.tagAt(i);
                String key = entry.getString("Item");
                if (!key.isEmpty() && entry.hasKey("Tag")) {
                    profile.collectedTags.computeIfAbsent(key, ignored -> new HashSet<NBTTagCompound>())
                            .add(entry.getCompoundTag("Tag"));
                }
            }
            return profile;
        } catch (Throwable ignored) {
            return missing();
        }
    }

    public void write(NBTTagCompound root) {
        if (!this.valid || root == null) return;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Format", FORMAT);
        tag.setLong("CreatedAt", Math.max(0L, this.createdAt));
        tag.setLong("PlayTicks", Math.max(0L, this.playTicks));
        tag.setInteger("Deaths", Math.max(0, this.deaths));
        tag.setInteger("Joins", Math.max(0, this.joins));
        tag.setLong("Kills", Math.max(0L, this.kills));
        tag.setInteger("Progress", clamp(this.progressIndex, 0, PROGRESSION.length - 1));
        tag.setInteger("WorldState", clamp(this.worldState, 0, 3));
        NBTTagList skills = new NBTTagList("CompletedSkillNodes");
        for (String id : this.completedSkillNodes) skills.appendTag(new NBTTagString("", id));
        tag.setTag("CompletedSkillNodes", skills);
        NBTTagList items = new NBTTagList("CollectedItems");
        for (String key : this.collectedItems) items.appendTag(new NBTTagString("", key));
        tag.setTag("CollectedItems", items);
        NBTTagList tags = new NBTTagList("CollectedItemTags");
        for (Map.Entry<String, Set<NBTTagCompound>> item : this.collectedTags.entrySet()) {
            for (NBTTagCompound itemTag : item.getValue()) {
                NBTTagCompound entry = new NBTTagCompound();
                entry.setString("Item", item.getKey());
                entry.setTag("Tag", itemTag.copy());
                tags.appendTag(entry);
            }
        }
        tag.setTag("CollectedItemTags", tags);
        root.setCompoundTag(TAG, tag);
    }

    /** Called only after AchievementHandler has successfully stored an unlock. */
    public void updateProgressFromAchievement(Achievement<?> achievement) {
        if (!this.valid || achievement == null) return;
        for (int i = 0; i < PROGRESSION.length; ++i) if (PROGRESSION[i] == achievement) this.progressIndex = Math.max(this.progressIndex, i);
    }

    /** Stores the union of every player's unlocked skills plus shared world skills. */
    public void recordSkillState(EntityPlayer player, World world) {
        if (!this.valid || player == null || world == null) return;
        try {
            SkillTreeData playerData = player.getData(NightmareMode.SKILL_TREE);
            WorldSkillData worldData = world.getData(NightmareMode.WORLD_SKILL_TREE);
            for (SkillNode node : SkillRegistry.getNodes()) {
                boolean unlocked = node.worldReward ? worldData != null && worldData.isUnlocked(node)
                        : playerData != null && playerData.isUnlocked(node);
                if (unlocked) this.completedSkillNodes.add(node.id.toString());
            }
        } catch (Throwable ignored) {
            // The profile must remain usable while optional player/world data is still being initialized.
        }
    }

    public int getCompletedSkillCount() { return this.completedSkillNodes.size(); }
    public boolean hasCompletedSkill(String id) { return this.completedSkillNodes.contains(id); }

    public boolean recordItem(ItemStack stack) {
        if (!this.valid || !isAllowedItem(stack)) return false;
        String key = itemKey(stack);
        boolean changed = this.collectedItems.add(key);
        if (stack.getTagCompound() != null) {
            changed |= this.collectedTags.computeIfAbsent(key, ignored -> new HashSet<NBTTagCompound>())
                    .add((NBTTagCompound)stack.getTagCompound().copy());
        }
        return changed;
    }

    public boolean hasItem(ItemStack stack) {
        if (!this.valid || !isAllowedItem(stack)) return false;
        String key = itemKey(stack);
        if (!this.collectedItems.contains(key)) return false;
        return stack.getTagCompound() == null || this.collectedTags.containsKey(key)
                && this.collectedTags.get(key).contains(stack.getTagCompound());
    }

    public boolean addItemsFrom(JourneyProfile other) {
        if (other == null || !this.valid) return false;
        boolean changed = this.collectedItems.addAll(other.collectedItems);
        for (Map.Entry<String, Set<NBTTagCompound>> item : other.collectedTags.entrySet()) {
            changed |= this.collectedTags.computeIfAbsent(item.getKey(), ignored -> new HashSet<NBTTagCompound>())
                    .addAll(item.getValue());
        }
        return changed;
    }

    private static String itemKey(ItemStack stack) {
        return stack.itemID + ":" + (stack.isItemStackDamageable() ? 0 : stack.getItemDamage());
    }

    private static boolean isAllowedItem(ItemStack stack) {
        return stack != null && stack.itemID != Block.commandBlock.blockID
                && stack.itemID != Item.monsterPlacer.itemID
                && !(stack.getItem() instanceof ItemVillagerDebugTool);
    }
    public int getSkillTotal() {
        int total = 0;
        for (SkillNode ignored : SkillRegistry.getNodes()) ++total;
        return total;
    }
    public int getSkillCompletionPercent() {
        int total = getSkillTotal();
        return total <= 0 ? 0 : Math.min(100, this.completedSkillNodes.size() * 100 / total);
    }

    public static JourneyProfile getOrCreate(World world) {
        if (world == null) return beginTrackingLegacyWorld();
        JourneyProfile profile = world.getData(NightmareMode.JOURNEY_PROFILE);
        if (profile == null || !profile.valid) {
            profile = world.getTotalWorldTime() <= 1L ? create() : beginTrackingLegacyWorld();
            world.setData(NightmareMode.JOURNEY_PROFILE, profile);
        }
        return profile;
    }

    public static int progressCount() { return PROGRESSION.length; }
    private static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }
}
