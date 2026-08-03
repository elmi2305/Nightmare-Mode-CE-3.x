package com.itlesports.nightmaremode.item.items;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.EntityNetherPostVillager;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.mixin.entity.EntityVillagerAccessor;
import net.minecraft.src.*;

import java.util.List;

/** Dev-mode-only direct controls for iterating on villager trade pools. */
public class ItemVillagerDebugTool extends NMItem {
    public enum Action {
        INCREASE_LEVEL,
        INCREASE_PROGRESS,
        REROLL_TRADES
    }

    private final Action action;

    public ItemVillagerDebugTool(int id, Action action) {
        super(id);
        this.action = action;
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (!NightmareMode.devMode || !(target instanceof EntityVillager villager)) {
            return false;
        }
        if (target.worldObj.isRemote) {
            return true;
        }

        String result;
        if (this.action == Action.INCREASE_LEVEL) {
            int previousLevel = villager.getCurrentTradeLevel();
            int newLevel = Math.min(5, previousLevel + 1);
            villager.setTradeLevel(newLevel);
            villager.setTradeExperience(0);
            this.reroll(villager, player);
            if (villager instanceof EntityNetherPostVillager postVillager) {
                postVillager.debugNotifyTradeLevelChanged(previousLevel);
            }
            result = "Villager rank: " + newLevel;
        } else if (this.action == Action.INCREASE_PROGRESS) {
            if (villager.getCurrentTradeLevel() >= 5) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("Villager is already at maximum rank."));
                return true;
            }
            int maximum = villager.getCurrentTradeMaxXP();
            int progress = Math.min(maximum - 1, villager.getCurrentTradeXP() + 1);
            villager.setTradeExperience(progress);
            if (progress == maximum) {
                this.reroll(villager, player);
            }
            result = "Villager rank progress: " + progress + "/" + maximum;
        } else {
            this.reroll(villager, player);
            result = "Villager trades rerolled.";
        }

        player.sendChatToPlayer(ChatMessageComponent.createFromText(result));
        target.worldObj.playSoundAtEntity(target, "random.click", 0.5F, 1.2F);
        return true;
    }

    private void reroll(EntityVillager villager, EntityPlayer player) {
        EntityVillagerAccessor accessor = (EntityVillagerAccessor) villager;
        accessor.nightmareMode$setBuyingList(null);
        accessor.nightmareMode$generateTrades(villager.getCurrentMaxNumTrades());
    }

    @Override
    public void getSubItems(int itemId, CreativeTabs tab, List list) {
        if (NightmareMode.devMode) {
            list.add(new ItemStack(itemId, 1, 0));
        }
    }
}
