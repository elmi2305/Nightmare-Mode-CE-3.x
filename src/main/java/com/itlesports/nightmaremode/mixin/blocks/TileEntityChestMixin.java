package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntityChest.class)
public abstract class TileEntityChestMixin extends TileEntity implements IInventory {
    @Override
    public void onInventoryChanged() {
        World w = this.worldObj;
        if (NightmareMode.getInstance().isGriefLogging() && !w.isRemote && NightmareMode.getInstance().getLogSettings().logItemRemoval) {

            int x = this.xCoord;
            int y = this.yCoord;
            int z = this.zCoord;
            EntityPlayer p = w.getClosestPlayer(x, y, z, 12);

            String text = "Player " + (p == null ? "NULL" : p.username) + " edited Chest at " + x + " " + y + " " + z;
            NightmareMode.appendLogLine(text);
        }

        super.onInventoryChanged();
    }
}
