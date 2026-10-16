package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import net.minecraft.src.InventoryPlayer;

public class GuiAutoAnvil extends GuiAutomaticAnvil {
    public GuiAutoAnvil(InventoryPlayer inventory, TileEntityHammerAnvil anvil) {
        super(new ContainerAutoAnvil(inventory, anvil), anvil, "ifhyAutoAnvil", 102);
    }
}
