package com.itlesports.nightmaremode.util;

import api.achievement.AchievementEventDispatcher;
import api.achievement.AchievementEvents;
import api.achievement.AchievementHandler;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import static btw.achievement.BTWAchievements.*;

public final class JourneyJournals {
    public static final int COUNT = 3;
    private static final String[] TITLES = {
            "A Thousand Steps Below", "Where the World Wears Thin", "The Last Shore"
    };
    private static final String[][] PAGES = {
            {
                    "I went below because I thought the world above had nothing left to hide.\n\nThe first red cavern seemed endless. I nearly mistook it for the whole of that country.\n\nKeep walking. That was the first thing it taught me.",
                    "A thousand blocks from the beginning, the stone changed.\n\nI checked my reckoning twice. The boundary was not a wall, nor a gate. I had crossed it before I understood there was a boundary at all.",
                    "At two thousand, another country. At three thousand, another still.\n\nThere were people out there. They had built places to shelter and things to trade, where I had expected only fire. I began to listen before I drew my blade.",
                    "I asked whether their roads could take me beyond the furthest lands above.\n\nI found my answer between the second thousand and the third: my portal would not wake.\n\nI had brought enough stone for a doorway. I should have brought provisions for the walk back.",
                    "When I returned, I unfolded my old surface map. It covered so little of the table.\n\nIf there were countries hidden inside the fire, what had I failed to see beneath the open sky?\n\nI left this account where the sand keeps its dead."
            },
            {
                    "The Wither was dead. At home, every master had taught us what they could. For the first time in years, there was no urgent reason to leave.\n\nThat was when I unfolded the map again.",
                    "Twenty thousand blocks from the place our journey began, the familiar earth gave way.\n\nI called it the Deadzone. The name comforted me until I saw what moved there.\n\nDo not measure from your newest house. Remember where you began.",
                    "Our portal would not open there. We tried it twice before admitting what we already knew.\n\nThe fire country could carry us only so far. Whatever waited at the end of this road, we would have to reach it ourselves.",
                    "Beyond the dead earth lay a cruel desert. Beyond the sand, the ground itself deserted us.\n\nLater there was a sea, and after the sea came cold.\n\nI write their order because, in the empty places, I needed proof that I was still going somewhere.",
                    "The Eye kept its counsel. It did not point towards comfort, or shelter, or anything I could see.\n\nBut it pointed.\n\nI began to believe that the old stronghold was real, and that the world had hidden it behind everything we feared to cross.",
                    "I entrusted these pages to a keeper of books before setting out again.\n\nIf they have reached you, ask yourself whether you are ready for a journey measured in tens of thousands.\n\nPack for the return, too. I still intended to make one."
            },
            {
                    "I have come back.\n\nThat is the first thing I must put on paper. Some mornings I wake convinced that I am still beneath those walls, dreaming of an ordinary room.\n\nThere is dirt under my nails. There is rain outside. I have come back.",
                    "Fifty thousand blocks behind me, and at last there were walls.\n\nAfter the dead earth, the sand, the gulf, the sea and the frozen wastes, cut stone seemed impossibly kind.\n\nI put my hand against it as though it were the door of my own house.",
                    "Inside, passages folded into passages. I found books, but none that told me who had last read them.\n\nI had expected an answer at the end of the road. Instead I found another threshold.",
                    "It was a ring of stone. Empty places looked back at me.\n\nAll that distance, and the Eye had led me to something that was still waiting.\n\nI cannot tell you whether its builders meant to enter, or whether they hoped to keep something on the other side.",
                    "I did not cross.\n\nFor a long while I was ashamed to write that. Then I remembered the first time I saw the red caverns, and how certain I was that I had seen everything.\n\nPerhaps the land we know is only the first room.",
                    "I have hidden this last account underground, among things that may outlive me. My other pages have gone their own ways.\n\nIf you find this one first, know that there was a road, and that someone returned along it.\n\nWhat lies beyond the ring must be your story."
            }
    };

    private JourneyJournals() {}

    public static String title(int index) { return TITLES[index]; }

    public static Item item(int index) { return NMItems.journeyJournals[index]; }

    public static Achievement achievement(int index) { return NMAchievements.JOURNEY_JOURNALS[index]; }

    public static boolean isUnlocked(EntityPlayer player, int index) {
        return player != null && AchievementHandler.hasUnlocked(player, achievement(index));
    }

    public static void collect(EntityPlayer player, ItemStack stack, int index) {
        if (!player.worldObj.isRemote && !isUnlocked(player, index)) {
            AchievementEventDispatcher.triggerEvent(AchievementEvents.ItemEvent.class, player, stack);
        }
    }

    public static ItemStack create(int index) {
        ItemStack stack = new ItemStack(item(index));
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("title", title(index));
        tag.setString("author", "The Wayfarer");
        NBTTagList pages = new NBTTagList();
        for (String page : PAGES[index]) pages.appendTag(new NBTTagString("", page));
        tag.setTag("pages", pages);
        stack.setTagCompound(tag);
        return stack;
    }

    public static boolean addToInventory(IInventory inventory, int index) {
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null && stack.itemID == item(index).itemID) return true;
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            if (inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot, create(index));
                return true;
            }
        }
        return false;
    }

    public static boolean canBuyExpeditionJournal(EntityPlayer player) {
        return player != null && !isUnlocked(player, 1)
                && AchievementHandler.hasUnlocked(player, KILLED_WITHER)
                && AchievementHandler.hasUnlocked(player, MAX_LEVEL_FARMER)
                && AchievementHandler.hasUnlocked(player, MAX_LEVEL_BUTCHER)
                && AchievementHandler.hasUnlocked(player, MAX_LEVEL_PRIEST)
                && AchievementHandler.hasUnlocked(player, MAX_LEVEL_BLACKSMITH)
                && AchievementHandler.hasUnlocked(player, NMAchievements.JOURNEY_MASTER_FISHERMAN);
    }
}
