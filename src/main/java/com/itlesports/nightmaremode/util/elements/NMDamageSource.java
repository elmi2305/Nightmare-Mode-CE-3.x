package com.itlesports.nightmaremode.util.elements;

import net.minecraft.src.DamageSource;

public class NMDamageSource extends DamageSource {
    public static NMDamageSource insanity = (NMDamageSource) new NMDamageSource("nmInsanity").setDamageBypassesArmor().setMagicDamage();

    protected NMDamageSource(String name) {
        super(name);
    }
}
