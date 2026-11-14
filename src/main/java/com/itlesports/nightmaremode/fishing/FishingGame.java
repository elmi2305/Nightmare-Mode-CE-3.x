package com.itlesports.nightmaremode.fishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/** server-owned simulation, in resolution-independent water coordinates. */
public final class FishingGame {
    public static final float WIDTH = 800, HEIGHT = 450, LURE_RADIUS = 13;
    public static final float DEPTH_PIXELS_PER_METRE = 7;
    public static final int MAX_TARGETS = 24;
    public final int maxTargets;
    private final float fleeMultiplier, pullMultiplier;
    // tune this field to change swimming and escape speeds together.
    public static float FISH_SPEED_MULTIPLIER = 1.8F;
    public static float DESCENT_METRES_PER_SECOND = 0.57F;
    public static float BOTTOM_DESCENT_BONUS = 3.1F;
    public final List<Target> targets = new ArrayList<>();
    public float lureX = WIDTH / 2, lureY = 42, lureSpeed;
    public float depth = 1, maxDepth = 100, abundance = 1, rodResistance = 1, descentMultiplier = 1;
    public float lureRadius = LURE_RADIUS;
    public boolean depthWinch, twinHook;
    private float winchTravel, winchVelocity, pendingScroll;
    public int catchLimit = 1, catches, fightAge;
    public Target fighting;
    public boolean failed;
    private float fightGoalX, fightGoalY, fightStartY, fightStartDepth, fightDX, fightDY;
    private float fightResistanceDepth;
    private float extractionTicks;
    private int giantFirstSide;
    public float stock, arrivalTicks, fearMultiplier = 1, captureMultiplier = 1;
    public int age;
    private final Random random;
    private final Source pool;
    private float inputX = lureX, inputY = lureY;
    private int nextId;

    public FishingGame(Random random, Supplier<Spec> pool, float stock, float arrivalTicks) {
        this(random, (initial, depth) -> pool.get(), stock, arrivalTicks, 100, 1);
    }

    public FishingGame(Random random, Source pool, float stock, float arrivalTicks, float maxDepth, float abundance) {
        this(random, pool, stock, arrivalTicks, maxDepth, abundance, false, false);
    }

    public FishingGame(Random random, Source pool, float stock, float arrivalTicks, float maxDepth, float abundance,
                       boolean combatEasy, boolean lowProgression) {
        this.random = random;
        this.pool = pool;
        this.stock = clamp(stock, 0, 1);
        this.arrivalTicks = arrivalTicks;
        this.maxDepth = clamp(maxDepth, 20, 200);
        this.abundance = abundance;
        maxTargets = FishingBalance.schoolCap(lowProgression);
        fleeMultiplier = FishingBalance.flee(combatEasy);
        pullMultiplier = FishingBalance.pull(combatEasy);
        seedSchool();
    }

    private void seedSchool() {
        int goal = stock <= 0 ? 2 : Math.min(maxTargets - 2, 2 + Math.round((4 + 5 * abundance) * stock));
        for (int i = 0; i < goal * 10 && targets.size() < goal; i++) spawn(true);
    }

    public boolean keepFishing(Target caught) {
        targets.remove(caught);
        catches++;
        boolean pulledUp = fighting != null;
        fighting = null;
        if (catches >= catchLimit || caught.spec.kraken) return false;
        depth = Math.max(1, depth);
        if (pulledUp) {
            lureX = inputX = WIDTH / 2;
            lureY = inputY = 42;
        }
        for (Target t : targets) t.progress = 0;
        seedSchool();
        return true;
    }

    public interface Source { Spec pick(boolean initial, float depth); }

    public void scroll(float notches) {
        if (!depthWinch || winchLocked() || !Float.isFinite(notches)) return;
        pendingScroll = clamp(pendingScroll + notches, -9, 9);
    }

    public boolean winchLocked() {
        return fighting != null;
    }

    public void input(float x, float y) {
        if (!Float.isFinite(x) || !Float.isFinite(y)) return;
        if (fighting != null) {
            fightDX = clamp(x, -60, 60);
            fightDY = clamp(y, -60, 60);
            return;
        }
        inputX = clamp(x, 22, WIDTH - 22);
        inputY = clamp(y, 28, HEIGHT - 22);
    }

    public Target tick() {
        age++;
        if (fighting != null) return fightTick();
        float bottom = clamp((inputY - 80) / (HEIGHT - 100), 0, 1);
        float plateau = 0.14F + 0.86F * (1 - depth / maxDepth);
        float passive = (DESCENT_METRES_PER_SECOND + BOTTOM_DESCENT_BONUS * bottom * bottom) * descentMultiplier * plateau / 20;
        if (depthWinch && !winchLocked()) {
            if (pendingScroll * winchTravel < 0) winchTravel = 0;
            winchTravel = clamp(winchTravel + pendingScroll * FishingTuning.WINCH_METRES_PER_NOTCH, -80, 80);
            float targetVelocity = clamp(winchTravel * 0.16F, -2.4F, 2.4F);
            winchVelocity += (targetVelocity - winchVelocity) * 0.22F;
            winchTravel -= winchVelocity;
        } else winchTravel = winchVelocity = 0;
        pendingScroll = 0;
        float previousDepth = depth;
        depth = clamp(depth + passive + winchVelocity, 1, maxDepth);
        float cameraShift = (depth - previousDepth) * DEPTH_PIXELS_PER_METRE;
        for (Target target : targets) if (!target.spec.resident) target.y -= cameraShift;
        removeDepartedTargets();
        if (depth <= 1 && winchVelocity < 0 || depth >= maxDepth && winchVelocity > 0) winchTravel = winchVelocity = 0;
        float dx = inputX - lureX, dy = inputY - lureY;
        lureSpeed = (float)Math.hypot(dx, dy) * 20;
        lureX = inputX;
        lureY = inputY;
        float fear = fearForSpeed(lureSpeed) * fearMultiplier;
        float timedArrival = 1 / Math.max(8, arrivalTicks / Math.max(0.25F, abundance));
        float travelArrival = 1 - (float)Math.exp(-Math.abs(cameraShift)
                * (stock > 0 ? populationLimit(stock) : 2) * Math.max(0.4F, abundance) / HEIGHT);
        if (targets.size() < maxTargets && random.nextFloat() < 1 - (1 - timedArrival) * (1 - travelArrival))
            spawn(false, cameraShift > 0.01F ? 2 : cameraShift < -0.01F ? 3 : -1);
        Target best = null;
        float bestDistance = Float.MAX_VALUE;
        for (Target target : targets) {
            float distance = (float)Math.hypot(lureX - target.x, lureY - target.y);
            if (distance < target.spec.radius + lureRadius && distance < bestDistance
                    && (!target.spec.resident || target.entered)
                    && target.x >= 0 && target.x <= WIDTH && target.y >= 24 && target.y <= HEIGHT) {
                best = target;
                bestDistance = distance;
            }
            target.fright = target.spec.fish ? Math.max(fear * clamp(1 - distance / 175, 0, 1),
                    target.progress > 0.03F ? (target.spec.rare ? 0.85F : 0.45F) : 0) : 0;
            if (target.spec.fish) swim(target);
            else {
                target.x += (float)Math.sin(age * 0.025 + target.id) * 0.13F;
                target.y -= 0.23F;
            }
        }
        // resolve body collisions after every fish has moved, including frightened fish.
        for (int pass = 0; pass < 32; pass++) {
            boolean bounded = constrainSurface();
            if (!separate() && !bounded) break;
        }
        constrainSurface();
        for (Target target : targets) {
            boolean overlapping = Math.hypot(lureX - target.x, lureY - target.y) < target.spec.radius + lureRadius;
            target.progress = target == best && overlapping
                    ? Math.min(1, target.progress + captureMultiplier / target.spec.captureTicks)
                    : Math.max(0, target.progress - 0.007F);
            if (target.progress >= 1) {
                if (target.spec.pullStrength > 0) { beginFight(target); return null; }
                return target;
            }
        }
        removeDepartedTargets();
        return null;
    }

    private void removeDepartedTargets() {
        targets.removeIf(t -> !t.spec.resident && (t.x < -100 || t.x > WIDTH + 100 || t.y < -100 || t.y > HEIGHT + 100));
    }

    private boolean constrainSurface() {
        float surface = 24 - (depth - 1) * DEPTH_PIXELS_PER_METRE;
        boolean moved = false;
        for (Target target : targets) if (target.spec.fish && target.y < surface + target.spec.radius) {
            target.y = surface + target.spec.radius;
            moved = true;
        }
        return moved;
    }

    public static float fearForSpeed(float pixelsPerSecond) {
        return clamp(pixelsPerSecond / 360, 0, 1);
    }

    public static int populationLimit(float stock) {
        return stock <= 0 ? 0 : 2 + Math.round(13 * clamp(stock, 0, 1));
    }

    public static float depthCap(float sourceDepth, float offshore, float habitatBonus) {
        return depthCap(sourceDepth, offshore, habitatBonus, false);
    }

    public static float depthCap(float sourceDepth, float offshore, float habitatBonus, boolean ocean) {
        float effective = Math.max(0, sourceDepth);
        if (ocean) return clamp(32 + effective * 6 + 72 * clamp(offshore * 256
                / Math.max(1, FishingTuning.OCEAN_FULL_DEPTH_OFFSHORE_BLOCKS), 0, 1), 24, 200);
        return clamp(18 + effective * 4 + offshore * 70 + habitatBonus, 24, 200);
    }

    public static float deepWaterSpeed(float sourceDepth, boolean ocean) {
        return ocean ? 1 + clamp((sourceDepth - 20) * 0.05F, 0, 1) : 1;
    }

    public static float deepWaterRarity(float sourceDepth, boolean ocean) {
        return ocean ? 1 + clamp((sourceDepth - 20) * 0.025F, 0, 0.5F) : 1;
    }

    private void spawn(boolean initial) {
        spawn(initial, -1);
    }

    private void spawn(boolean initial, int entryEdge) {
        if (targets.size() >= maxTargets) return;
        Spec spec = pool.pick(initial, depth);
        if (spec == null || spec.minDepth > depth || initial && spec.rare) return;
        if (spec.resident && targets.stream().anyMatch(t -> t.spec.itemId == spec.itemId)) return;
        long fishCount = targets.stream().filter(t -> t.spec.fish).count();
        if (spec.fish && (fishCount >= Math.min(maxTargets - 2, Math.round(populationLimit(stock) * Math.max(0.4F, abundance))) || random.nextFloat() > stock)) return;
        if (!spec.fish && targets.stream().filter(t -> !t.spec.fish).count() >= 2) return;
        for (int attempt = 0; attempt < 20; attempt++) {
            float x, y, angle;
            if (initial) {
                x = 50 + random.nextFloat() * (WIDTH - 100);
                y = 80 + random.nextFloat() * (HEIGHT - 150);
                angle = random.nextFloat() * (float)(Math.PI * 2);
            } else {
                int edge = entryEdge >= 0 ? entryEdge : random.nextInt(3);
                if (edge == 3 && 24 - (depth - 1) * DEPTH_PIXELS_PER_METRE + spec.radius > 0) edge = random.nextInt(3);
                x = edge == 0 ? -spec.radius : edge == 1 ? WIDTH + spec.radius : 50 + random.nextFloat() * (WIDTH - 100);
                y = edge == 2 ? HEIGHT + spec.radius : edge == 3 ? -spec.radius : 70 + random.nextFloat() * (HEIGHT - 110);
                angle = edge == 0 ? 0 : edge == 1 ? (float)Math.PI : edge == 3 ? (float)Math.PI / 2 : -(float)Math.PI / 2;
            }
            boolean free = true;
            for (Target other : targets) {
                if (Math.hypot(x - other.x, y - other.y) < spec.radius + other.spec.radius + 12) free = false;
            }
            if (free) {
                targets.add(new Target(++nextId, spec, x, y, angle));
                return;
            }
        }
    }

    private void swim(Target t) {
        t.life++;
        boolean leaving = !t.spec.resident && t.life > 20 * (t.spec.rare ? 55 : 18 + t.id % 12);
        float desiredX = (float)Math.cos(t.angle + Math.sin(age * 0.035 + t.id * 3.7) * 0.18);
        float desiredY = (float)Math.sin(t.angle + Math.sin(age * 0.035 + t.id * 3.7) * 0.18);
        if (t.fright > 0) {
            float length = Math.max(1, (float)Math.hypot(t.x - lureX, t.y - lureY));
            desiredX += (t.x - lureX) / length * t.fright * 5;
            desiredY += (t.y - lureY) / length * t.fright * 5;
        }
        for (Target other : targets) {
            if (other == t) continue;
            float dx = t.x - other.x, dy = t.y - other.y;
            float distance = Math.max(0.1F, (float)Math.hypot(dx, dy));
            float reach = t.spec.radius + other.spec.radius + 36;
            if (distance < reach) {
                float force = (reach - distance) / reach * 4;
                desiredX += dx / distance * force;
                desiredY += dy / distance * force;
            }
        }
        // calm fish curve back into view; frightened or crowded fish may leave.
        float surfaceClearance = 24 - (depth - 1) * DEPTH_PIXELS_PER_METRE + t.spec.radius + 22 - t.y;
        if (surfaceClearance > 0) desiredY += surfaceClearance * 0.2F;
        if (leaving) {
            desiredX += t.x < WIDTH / 2 ? -3 : 3;
            desiredY += t.y > HEIGHT * 0.7F ? 2 : 0;
        }
        if (!leaving && t.fright < 0.25F && targets.size() < maxTargets - 3) {
            if (t.x < 50) desiredX += 1.7F;
            if (t.x > WIDTH - 50) desiredX -= 1.7F;
            if (t.y < 65) desiredY += 1.7F;
            if (t.y > HEIGHT - 40) desiredY -= 1.7F;
        }
        if (t.spec.resident) {
            if (!t.entered) {
                desiredX = (WIDTH / 2 - t.x) * 0.05F;
                desiredY = ((HEIGHT + 24) / 2 - t.y) * 0.05F;
            } else if (t.spec.kraken) {
                if (t.cruiseDirection == 0) t.cruiseDirection = Math.cos(t.angle) >= 0 ? 1 : -1;
                if (t.cruiseDirection > 0 && t.x > WIDTH - t.spec.residentInsetX - 95
                        || t.cruiseDirection < 0 && t.x < t.spec.residentInsetX + 95) t.cruiseDirection = -t.cruiseDirection;
                desiredX = t.cruiseDirection;
                desiredY = 0;
            } else {
                if (t.x < t.spec.residentInsetX + 40) desiredX += 5;
                if (t.x > WIDTH - t.spec.residentInsetX - 40) desiredX -= 5;
                if (t.y < t.spec.residentInsetY + 40) desiredY += 5;
                if (t.y > HEIGHT - t.spec.residentInsetY - 25) desiredY -= 5;
            }
        }
        float desired = (float)Math.atan2(desiredY, desiredX);
        float turn = wrap(desired - t.angle);
        float turnLimit = t.spec.kraken ? FishingTuning.GIANT_SQUID_TURN_RATE : t.spec.rare ? 0.25F : 0.18F;
        t.angle = wrap(t.angle + clamp(turn, -turnLimit, turnLimit));
        float speed = t.spec.speed * FISH_SPEED_MULTIPLIER * (1 + depth / 350)
                * (t.spec.rare ? 1.35F : 1) * (1 + t.fright * (t.spec.kraken ? 0.2F : t.spec.fleeStrength) * fleeMultiplier);
        t.x += (float)Math.cos(t.angle) * speed;
        t.y += (float)Math.sin(t.angle) * speed * (t.spec.kraken && t.entered ? 0.065F : 1) - (t.spec.kraken ? 0 : 0.12F);
        if (t.spec.resident) {
            float insetX = t.spec.residentInsetX, insetY = t.spec.residentInsetY;
            if (t.x >= insetX && t.x <= WIDTH - insetX && t.y >= insetY + 24 && t.y <= HEIGHT - insetY) t.entered = true;
            if (t.entered) {
                t.x = clamp(t.x, insetX, WIDTH - insetX);
                t.y = clamp(t.y, insetY + 24, HEIGHT - insetY);
            }
        }
    }

    private void beginFight(Target target) {
        fighting = target;
        lureX = target.x; lureY = target.y;
        fightStartY = Math.max(85, lureY);
        fightStartDepth = depth;
        fightResistanceDepth = Math.max(depth, target.spec.minDepth);
        fightAge = 0;
        extractionTicks = 0;
        target.progress = 0;
        fightDX = fightDY = 0;
        winchTravel = winchVelocity = pendingScroll = 0;
        int edge = random.nextInt(3);
        fightGoalX = edge == 0 ? -60 : edge == 1 ? WIDTH + 60 : random.nextFloat() * WIDTH;
        fightGoalY = edge == 2 ? HEIGHT + 60 : HEIGHT * (0.45F + random.nextFloat() * 0.55F);
        giantFirstSide = fightGoalX < WIDTH / 2 ? -1 : 1;
        targets.removeIf(t -> t != target);
    }

    private Target fightTick() {
        fightAge++;
        boolean giant = fighting.spec.kraken;
        if (giant) {
            int side = (fightAge / FishingStruggle.CYCLE_TICKS % 2 == 0 ? 1 : -1) * giantFirstSide;
            fightGoalX = WIDTH / 2 + side * 520;
            fightGoalY = HEIGHT + 120;
        }
        float dx = fightGoalX - lureX, dy = fightGoalY - lureY;
        float distance = Math.max(1, (float)Math.hypot(dx, dy));
        float force = fighting.spec.pullStrength * Math.max(0, FishingTuning.FISH_PULL_MULTIPLIER)
                * (fighting.spec.kraken ? Math.max(0, FishingTuning.GIANT_SQUID_PULL_MULTIPLIER) : 1)
                * (1.6F + fightResistanceDepth / 90) * pullMultiplier / Math.max(0.5F, rodResistance);
        if (giant) force *= FishingStruggle.pullScale(fightAge);
        float jitter = giant ? 0 : (float)Math.sin(fightAge * 0.16 + fighting.id) * force * 0.45F;
        lureX += dx / distance * force + fightDX * 0.85F + jitter;
        lureY += dy / distance * force + fightDY * 0.85F;
        // the surface is a ceiling to slide along, never an escape edge.
        lureY = Math.max(FishingStruggle.SURFACE_Y, lureY);
        fightDX = fightDY = 0;
        float fightTurn = wrap((float)Math.atan2(dy, dx) - fighting.angle);
        fighting.angle += giant ? clamp(fightTurn, -FishingTuning.GIANT_SQUID_TURN_RATE * 1.5F,
                FishingTuning.GIANT_SQUID_TURN_RATE * 1.5F) : fightTurn * 0.18F;
        fighting.x = lureX; fighting.y = lureY; fighting.fright = 1;
        depth = Math.max(0, Math.min(fightStartDepth, fightStartDepth * (lureY - 36) / (fightStartY - 36)));
        boolean atSurface = Math.abs(lureX - WIDTH / 2) <= FishingStruggle.gateHalfWidth(giant)
                && lureY <= FishingStruggle.SURFACE_Y + FishingStruggle.GATE_HEIGHT;
        extractionTicks = atSurface ? extractionTicks + 1 : Math.max(0, extractionTicks - (giant ? 0.25F : 2));
        fighting.progress = Math.min(1, extractionTicks / (giant ? FishingStruggle.GIANT_HOLD_TICKS : 6));
        if (fightAge > 8 && fighting.progress >= 1) { depth = 1; return fighting; }
        if (lureX < 4 || lureX > WIDTH - 4 || lureY > HEIGHT - 4 || fightAge > 20 * 40) failed = true;
        return null;
    }

    private boolean separate() {
        boolean moved = false;
        for (int i = 0; i < targets.size(); i++) for (int j = i + 1; j < targets.size(); j++) {
            Target a = targets.get(i), b = targets.get(j);
            float dx = b.x - a.x, dy = b.y - a.y;
            float distance = (float)Math.hypot(dx, dy);
            float minimum = a.spec.radius + b.spec.radius + 5;
            if (distance >= minimum) continue;
            moved = true;
            if (distance < 0.001F) { dx = 1; dy = 0; distance = 1; }
            float push = minimum - distance + 0.02F;
            float share = a.spec.resident ? 0 : b.spec.resident ? 1 : 0.5F;
            a.x -= dx / distance * push * share;
            a.y -= dy / distance * push * share;
            b.x += dx / distance * push * (1 - share);
            b.y += dy / distance * push * (1 - share);
        }
        return moved;
    }

    public static float wrap(float angle) {
        while (angle > Math.PI) angle -= (float)(Math.PI * 2);
        while (angle < -Math.PI) angle += (float)(Math.PI * 2);
        return angle;
    }

    public static float clamp(float value, float min, float max) { return Math.max(min, Math.min(max, value)); }

    public static final class Spec {
        public final int itemId;
        public final boolean fish, rare;
        public final float radius, speed, flutter;
        public float captureTicks;
        public float minDepth, pullStrength, residentInsetX, residentInsetY;
        public float fleeStrength;
        public int animation;
        public boolean reverse, mirrorFacing, resident, kraken;
        public Spec depth(float value) { minDepth = value; return this; }
        public Spec pull(float value) { pullStrength = value; return this; }
        public Spec flee(float value) { fleeStrength = value; return this; }
        public Spec animate(int value) { animation = value; return this; }
        public Spec reversed() { reverse = true; return this; }
        public Spec flippedFacing() { mirrorFacing = true; return this; }
        public Spec kraken() { kraken = resident = true; captureTicks = 400; residentInsetX = 270; residentInsetY = 202; return this; }

        public Spec(int itemId, boolean fish, boolean rare, float radius, float speed, float flutter) {
            this.itemId = itemId;
            this.fish = fish;
            this.rare = rare;
            this.radius = radius;
            this.speed = speed;
            this.flutter = flutter;
            this.fleeStrength = rare ? 2.5F : 1.9F;
            this.captureTicks = fish ? (rare ? 160 : 100) : 36;
            this.pullStrength = rare && fish ? 2 : 0;
        }
    }

    public static final class Target {
        public final int id;
        public final Spec spec;
        public int life;
        public boolean entered;
        private int cruiseDirection;
        public float x, y, angle, progress, fright;

        public Target(int id, Spec spec, float x, float y, float angle) {
            this.id = id;
            this.spec = spec;
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }
}
