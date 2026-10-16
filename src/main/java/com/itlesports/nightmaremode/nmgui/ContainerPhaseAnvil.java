package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import net.minecraft.src.InventoryPlayer;

public class ContainerPhaseAnvil extends ContainerAutomaticAnvil {
    public static final int ID = 56;

    public ContainerPhaseAnvil(InventoryPlayer inventory, TileEntityHammerAnvil anvil) {
        super(inventory, anvil, true);
    }
}
