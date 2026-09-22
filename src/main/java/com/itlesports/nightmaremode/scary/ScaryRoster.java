package com.itlesports.nightmaremode.scary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** cosmetic names only; never inserted into the network handler's real roster. */
public final class ScaryRoster {
    public static final long STAY_MILLIS = 15 * 60_000L;
    private final Map<String, Long> departures = new LinkedHashMap<>();
    private boolean eye;

    public void join(String name, long now) { departures.put(name, now + STAY_MILLIS); }
    public List<String> names() { return new ArrayList<>(departures.keySet()); }
    public void clear() { departures.clear(); eye = false; }
    public void armEye() { eye = true; }

    public boolean eyeActive(long now) {
        return eye && departures.values().stream().anyMatch(departure -> now < departure);
    }

    public List<String> expire(long now) {
        List<String> left = new ArrayList<>();
        departures.entrySet().removeIf(entry -> {
            if (now < entry.getValue()) return false;
            left.add(entry.getKey());
            return true;
        });
        if (departures.isEmpty()) eye = false;
        return left;
    }
}
