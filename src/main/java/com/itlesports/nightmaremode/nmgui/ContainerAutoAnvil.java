package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import net.minecraft.src.InventoryPlayer;

public class ContainerAutoAnvil extends ContainerAutomaticAnvil {
    public static final int ID = 55;

    public ContainerAutoAnvil(InventoryPlayer inventory, TileEntityHammerAnvil anvil) {
        super(inventory, anvil, false);
    }
}
