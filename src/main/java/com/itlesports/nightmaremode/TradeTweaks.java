package com.itlesports.nightmaremode;

import btw.entity.mob.villager.trade.TradeItem;
import btw.entity.mob.villager.trade.TradeProvider;
import btw.entity.mob.villager.trade.VillagerTrade;
import com.itlesports.nightmaremode.mixin.TradeBuilderAccessor;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.ResourceLocation;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
public final class TradeTweaks {
    private TradeTweaks() {}

    private static final ConcurrentHashMap<String, Edit> edits = new ConcurrentHashMap<>();

    private static class Edit {
        volatile Float weight;        // null = no change
        volatile Integer outMin;
        volatile Integer outMax;
        volatile Integer inMin;
        volatile Integer inMax;
        volatile Boolean drop;        // true -> drop trade
    }

    // sets just the weight
    public static void setWeight(String name, float weight) {
        edits.computeIfAbsent(name, k -> new Edit()).weight = weight;
    }

    // basic trade output count editor. if the trade output is an emerald, it works
    public static void setOutputCount(String name, int min, int max) {
        edits.computeIfAbsent(name, k -> new Edit()).outMin = min;
        edits.get(name).outMax = max;
    }
    // basic trade input count editor
    public static void setInputCount(String name, int min, int max) {
        edits.computeIfAbsent(name, k -> new Edit()).inMin = min;
        edits.get(name).inMax = max;
    }

    public static void dropTrade(String name) {
        edits.computeIfAbsent(name, k -> new Edit()).drop = true;
    }

    public static ApplyAction applyEditIfPresent(Object tradeBuilder) {
        TradeBuilderAccessor acc = (TradeBuilderAccessor) tradeBuilder;
        ResourceLocation rl = acc.getName();
        if (rl == null) return ApplyAction.KEEP;
        String key = rl.toString();
        Edit e = edits.get(key);
        if (e == null) return ApplyAction.KEEP;
        if (Boolean.TRUE.equals(e.drop)) {
            debug("TradeTweaks: dropping trade %s", key);
            return ApplyAction.DROP;
        }

        // try to mutate in-place via Accessor (preferred)
        try {
            if (e.weight != null) {
                acc.setWeight(e.weight);
                debug("TradeTweaks: set weight %s -> %s", key, e.weight);
            }

            // OUTPUT change
            if (e.outMin != null && e.outMax != null) {
                TradeItem out = acc.getOutput();
                int id = tryExtractItemID(out);
                int meta = tryExtractMeta(out);
                boolean done = false;
                if (id >= 0) {
                    try {
                        acc.setOutput(TradeItem.fromIDAndMetadata(id, meta, e.outMin, e.outMax));
                        debug("TradeTweaks: set output counts %s -> [%d,%d] (via factory)", key, e.outMin, e.outMax);
                        done = true;
                    } catch (Throwable t) {
                        // factory failed; fall through to reflective mutate
                    }
                }
                if (!done) {
                    if (reflectivelySetTradeItemCounts(out, e.outMin, e.outMax)) {
                        debug("TradeTweaks: set output counts %s -> [%d,%d] (via reflection)", key, e.outMin, e.outMax);
                    } else {
                        debug("TradeTweaks: failed to set output counts for %s", key);
                        // we could try REPLACED fallback, but prefer to continue so keep original trade
                    }
                }
            }

            // INPUT change
            if (e.inMin != null && e.inMax != null) {
                TradeItem in = acc.getInput();
                int id = tryExtractItemID(in);
                int meta = tryExtractMeta(in);
                boolean done = false;
                if (id >= 0) {
                    try {
                        acc.setInput(TradeItem.fromIDAndMetadata(id, meta, e.inMin, e.inMax));
                        debug("TradeTweaks: set input counts %s -> [%d,%d] (via factory)", key, e.inMin, e.inMax);
                        done = true;
                    } catch (Throwable t) {
                        // factory failed
                    }
                }
                if (!done) {
                    if (reflectivelySetTradeItemCounts(in, e.inMin, e.inMax)) {
                        debug("TradeTweaks: set input counts %s -> [%d,%d] (via reflection)", key, e.inMin, e.inMax);
                    } else {
                        debug("TradeTweaks: failed to set input counts for %s", key);
                    }
                }
            }

            // if we got here, in-place mutation attempted; keep normal addToTradeList
            return ApplyAction.KEEP;

        } catch (Throwable t) {
            t.printStackTrace();
            // fallback - attempt to build replacement and add it explicitly
            try {
                // call build() and add replacement, then cancel original
                VillagerTrade replacement = ((TradeProvider.TradeBuilder) tradeBuilder).build();
                int prof = acc.getProfession();
                EntityVillager.addCustomTrade(prof, replacement);
                debug("TradeTweaks: replaced trade %s via fallback build()", key);
                return ApplyAction.REPLACED;
            } catch (Throwable ex) {
                ex.printStackTrace();
                // fail-safe: keep original
                return ApplyAction.KEEP;
            }
        }
    }

    // unnecessary, but it's fun to watch it print into the console, so I'm keeping it
    private static void debug(String fmt, String key) {
        System.out.printf((fmt) + "%n", key);
    }

    private static void debug(String fmt, String key, int a, int b) {
        System.out.printf((fmt) + "%n", key, a, b);
    }

    private static void debug(String fmt, String key, float weight) {
        System.out.printf((fmt) + "%n", key, weight);
    }


    // small helper: try to set min/max fields reflectively on a TradeItem instance
    private static boolean reflectivelySetTradeItemCounts(TradeItem item, int min, int max) {
        if (item == null) return false;
        String[][] namePairs = {
                {"minCount","maxCount"},
                {"countMin","countMax"},
                {"min","max"},
                {"stackSizeMin","stackSizeMax"},
                {"minStack","maxStack"}
        };
        for (String[] p : namePairs) {
            try {
                Field fMin = findField(item.getClass(), p[0]);
                Field fMax = findField(item.getClass(), p[1]);
                fMin.setAccessible(true);
                fMax.setAccessible(true);
                Class<?> tmin = fMin.getType();
                Class<?> tmax = fMax.getType();
                // only set if numeric
                if (Number.class.isAssignableFrom(tmin) || tmin.isPrimitive()) {
                    setNumericField(fMin, item, min);
                    setNumericField(fMax, item, max);
                    return true;
                }
            } catch (Throwable ignored) {}
        }
        return false;
    }


    private static void setNumericField(Field f, Object target, int value) throws IllegalAccessException {
        Class<?> t = f.getType();
        if (t == int.class || t == Integer.class) f.set(target, value);
        else if (t == short.class || t == Short.class) f.set(target, (short)value);
        else if (t == byte.class || t == Byte.class) f.set(target, (byte)value);
        else if (t == long.class || t == Long.class) f.set(target, (long)value);
        else if (t == double.class || t == Double.class) f.set(target, (double)value);
        else f.set(target, value); // try generic
    }

    // naive helpers that try common field names (add more if your TradeItem differs)
    private static int tryExtractItemID(TradeItem t) {
        try {
            Field f = findField(t.getClass(), "itemID");
            f.setAccessible(true);
            return ((Number) f.get(t)).intValue();
        } catch (Throwable ignored) {}
        try {
            Field f = findField(t.getClass(), "id");
            f.setAccessible(true);
            return ((Number) f.get(t)).intValue();
        } catch (Throwable ignored) {}
        return -1;
    }

    private static int tryExtractMeta(TradeItem t) {
        try { Field f = findField(t.getClass(), "metadata"); f.setAccessible(true); return ((Number) f.get(t)).intValue(); } catch (Throwable ignored) {}
        try { Field f = findField(t.getClass(), "meta"); f.setAccessible(true); return ((Number) f.get(t)).intValue(); } catch (Throwable ignored) {}
        return 0;
    }
    private static Field findField(Class<?> cls, String name) throws NoSuchFieldException {
        Class<?> cur = cls;
        while (cur != null) {
            try { return cur.getDeclaredField(name); } catch (NoSuchFieldException e) { cur = cur.getSuperclass(); }
        }
        throw new NoSuchFieldException(name);
    }
}
