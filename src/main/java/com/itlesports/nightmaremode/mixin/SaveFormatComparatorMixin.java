package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NMConfUtils;
import com.itlesports.nightmaremode.SaveFormatExt;
import net.minecraft.src.SaveFormatComparator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mixin(SaveFormatComparator.class)
public abstract class SaveFormatComparatorMixin implements SaveFormatExt, Comparable {

    @Unique private int[] confArray;
    @Override
    public int[] nightmareMode$getConfArray() {
        int targetSize = NMConfUtils.CONFIG_COUNT;

        // Old saves or pre-config-era worlds
        if (this.confArray == null) {
            this.confArray = new int[targetSize]; // all defaults = 0
            return this.confArray;
        }

        // Config count changed between versions
        if (this.confArray.length != targetSize) {
            int[] fixed = new int[targetSize];
            System.arraycopy(this.confArray, 0, fixed, 0,
                    Math.min(this.confArray.length, targetSize));
            this.confArray = fixed;
        }

        return this.confArray;
    }


    @Override
    public void nightmareMode$setConfArray(int[] arr) {
        int targetSize = NMConfUtils.CONFIG_COUNT;

        if (arr == null) {
            this.confArray = new int[targetSize];
            return;
        }

        if (arr.length == targetSize) {
            this.confArray = arr;
            return;
        }

        int[] fixed = new int[targetSize];
        System.arraycopy(arr, 0, fixed, 0, Math.min(arr.length, targetSize));
        this.confArray = fixed;
    }
}
