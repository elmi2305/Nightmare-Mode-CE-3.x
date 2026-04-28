package com.itlesports.nightmaremode.rendering.entities;

import net.minecraft.src.*;

public class RenderBloodAltar extends RenderEntity {


    @Override
    public void doRender(Entity entity, double d, double e, double f, float g, float h) {
        BossStatus.setBossStatus((IBossDisplayData) entity, true);
        super.doRender(entity, d, e, f, g, h);
    }

}
