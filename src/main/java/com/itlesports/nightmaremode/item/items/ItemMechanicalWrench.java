package com.itlesports.nightmaremode.item.items;

import api.block.MechanicalBlock;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.mechanical.MechanicalStressManager;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemMechanicalWrench extends NMItem {
    public ItemMechanicalWrench(int itemId) {
        super(itemId);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float clickX, float clickY, float clickZ) {
        return inspect(player, world, x, y, z);
    }

    public static boolean inspect(EntityPlayer player, World world, int x, int y, int z) {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block == null) return false;

        if (MechanicalStressManager.isInspectableNetworkBlock(block)) {
            if (!world.isRemote) {
                MechanicalStressManager.StressReport report =
                        MechanicalStressManager.inspectNetwork(world, x, y, z);
                if (report.powered()) {
                    player.addChatMessage("Mechanical network: " + report.stress() + " / "
                            + report.capacity() + " SU | sources: " + report.sources()
                            + (report.overloaded() ? " | OVERLOADED" : ""));
                } else {
                    player.addChatMessage("Mechanical network: unpowered | connected load: "
                            + report.stress() + " SU");
                }
            }
            return true;
        }

        if (block instanceof MechanicalBlock) {
            if (!world.isRemote) {
                player.addChatMessage(block.getLocalizedName() + " stress cost: "
                        + MechanicalStressManager.getStressCost(block) + " SU");
            }
            return true;
        }
        return false;
    }
}
