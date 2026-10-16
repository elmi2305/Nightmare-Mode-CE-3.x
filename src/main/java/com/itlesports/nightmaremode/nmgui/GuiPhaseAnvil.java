package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import net.minecraft.src.InventoryPlayer;

public class GuiPhaseAnvil extends GuiAutomaticAnvil {
    public GuiPhaseAnvil(InventoryPlayer inventory, TileEntityHammerAnvil anvil) {
        super(new ContainerPhaseAnvil(inventory, anvil), anvil, "ifhyPhaseAnvil", 77);
    }
}
