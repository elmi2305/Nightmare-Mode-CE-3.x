package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.World;

public class EntityEnderSilverfish extends EntitySilverfish {
    public EntityEnderSilverfish(World world) { super(world); }

    @Override protected void dropFewItems(boolean killedByPlayer, int looting) {
        this.dropItem(NMItems.enderShell.itemID, 1 + this.rand.nextInt(2) + (looting > 0 ? this.rand.nextInt(looting + 1) : 0));
        super.dropFewItems(killedByPlayer, looting);
    }
}
