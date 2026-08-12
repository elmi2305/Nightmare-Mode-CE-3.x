package com.itlesports.nightmaremode.mechanical;

import api.block.MechanicalBlock;
import api.entity.mechanical.MechanicalPowerSourceEntity;
import btw.block.BTWBlocks;
import btw.block.blocks.*;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import btw.entity.mechanical.source.WaterWheelEntity;
import com.itlesports.nightmaremode.block.blocks.BlockBloodSaw;
import com.itlesports.nightmaremode.block.blocks.BlockCisternStirrer;
import com.itlesports.nightmaremode.block.blocks.DualInputGearBoxBlock;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

import java.util.*;

/**
 * Computes stress from the existing directional BTW mechanical-power topology.
 * Nothing is persisted: powered networks are small and are recalculated only
 * when an axle or gearbox performs its normal validation pass.
 */
public final class MechanicalStressManager {
    public static final int WIND_MILL_CAPACITY = 128;
    public static final int VERTICAL_WIND_MILL_CAPACITY = 256;
    public static final int WATER_WHEEL_CAPACITY = 256;
    public static final int GEARBOX_MAX_STRESS = 128;
    public static final int GEARBOX_SOURCE_SUPPORT_RANGE = 4;
    public static final int STRESS_PER_GEARBOX_FROM_SOURCE = 20;

    private static final int MAX_NETWORK_NODES = 4096;
    private static boolean validating;

    private MechanicalStressManager() {
    }

    public static void validateNetwork(World world, int x, int y, int z) {
        if (world.isRemote || validating || !isMechanicalNode(blockAt(world, x, y, z))) {
            return;
        }

        validating = true;
        try {
            Set<Pos> topology = collectTopology(world, new Pos(x, y, z));
            List<PowerGroup> groups = collectPowerGroups(world, topology);
            Set<Pos> gearboxesToBreak = new HashSet<>();

            for (PowerGroup group : groups) {
                if (stressOf(world, group.graph) > group.capacity) {
                    for (Source source : group.sources) {
                        gearboxesToBreak.addAll(findFirstGearboxes(world, source.positions));
                    }
                }

                for (Pos pos : group.graph) {
                    Block block = blockAt(world, pos);
                    if (block instanceof GearBoxBlock
                            && calculateGearboxStress(world, pos, group).stress > GEARBOX_MAX_STRESS) {
                        gearboxesToBreak.add(pos);
                    }
                }
            }

            for (Pos pos : gearboxesToBreak) {
                Block block = blockAt(world, pos);
                if (block instanceof GearBoxBlock gearBox) {
                    gearBox.breakGearBox(world, pos.x, pos.y, pos.z);
                }
            }
        } finally {
            validating = false;
        }
    }

    public static StressReport inspectNetwork(World world, int x, int y, int z) {
        Pos inspected = new Pos(x, y, z);
        Set<Pos> topology = collectTopology(world, inspected);
        for (PowerGroup group : collectPowerGroups(world, topology)) {
            if (group.graph.contains(inspected)) {
                int stress = stressOf(world, group.graph);
                return new StressReport(stress, group.capacity, group.sources.size(), true,
                        stress > group.capacity);
            }
        }
        return new StressReport(stressOf(world, topology), 0, 0, false, false);
    }

    public static GearboxStressReport inspectGearbox(World world, int x, int y, int z) {
        Pos inspected = new Pos(x, y, z);
        Set<Pos> topology = collectTopology(world, inspected);
        for (PowerGroup group : collectPowerGroups(world, topology)) {
            if (!group.graph.contains(inspected)) continue;
            GearboxCalculation calculation = calculateGearboxStress(world, inspected, group);
            return new GearboxStressReport(calculation.stress, GEARBOX_MAX_STRESS,
                    calculation.localLoad, calculation.nearestSourceDistance,
                    calculation.supportingSources, true);
        }
        int localLoad = stressOf(world, collectGearboxSpan(world, inspected, topology));
        return new GearboxStressReport(localLoad, GEARBOX_MAX_STRESS, localLoad, -1, 0, false);
    }

    public static int getStressCost(Block block) {
        if (block instanceof AxleBlock) return 2;
        if (block instanceof GearBoxBlock) return 8;
        if (block instanceof BlockBloodSaw) return 64;
        if (block instanceof BlockCisternStirrer) return 48;
        if (block instanceof SawBlock) return 48;
        if (block instanceof ScrewPumpBlock) return 48;
        if (block instanceof PulleyBlock) return 48;
        if (block instanceof LoomBlock) return 48;
        if (block instanceof MillstoneBlock) return 32;
        if (block instanceof BellowsBlock) return 32;
        if (block instanceof TurntableBlock) return 32;
        if (block instanceof VesselBlock) return 32;
        if (block instanceof HopperBlock) return 16;
        return block instanceof MechanicalBlock && !(block instanceof HandCrankBlock) ? 32 : 0;
    }

    public static boolean isInspectableNetworkBlock(Block block) {
        return block instanceof AxleBlock || block instanceof GearBoxBlock;
    }

    private static List<PowerGroup> collectPowerGroups(World world, Set<Pos> topology) {
        Map<Integer, Source> sourcesByKey = new LinkedHashMap<>();
        for (Pos pos : topology) {
            if (world.getBlockId(pos.x, pos.y, pos.z) != BTWBlocks.axlePowerSource.blockID) continue;
            SourceIdentity identity = findSourceIdentity(world, pos);
            Source source = sourcesByKey.computeIfAbsent(identity.key,
                    ignored -> new Source(identity.capacity));
            source.positions.add(pos);
        }

        List<PowerGroup> groups = new ArrayList<>();
        for (Source source : sourcesByKey.values()) {
            Set<Pos> graph = new HashSet<>();
            for (Pos sourcePos : source.positions) {
                graph.addAll(tracePower(world, sourcePos));
            }
            PowerGroup group = new PowerGroup(source.capacity, graph, source);

            for (int i = groups.size() - 1; i >= 0; --i) {
                PowerGroup existing = groups.get(i);
                if (intersects(group.graph, existing.graph)) {
                    group.merge(existing);
                    groups.remove(i);
                }
            }
            groups.add(group);
        }

        // A newly merged group may bridge two earlier groups, so collapse to a fixed point.
        boolean merged;
        do {
            merged = false;
            outer:
            for (int i = 0; i < groups.size(); ++i) {
                for (int j = i + 1; j < groups.size(); ++j) {
                    if (!intersects(groups.get(i).graph, groups.get(j).graph)) continue;
                    groups.get(i).merge(groups.remove(j));
                    merged = true;
                    break outer;
                }
            }
        } while (merged);
        return groups;
    }

    private static Set<Pos> collectTopology(World world, Pos start) {
        Set<Pos> visited = new HashSet<>();
        if (!isMechanicalNode(blockAt(world, start))) return visited;
        ArrayDeque<Pos> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty() && visited.size() < MAX_NETWORK_NODES) {
            Pos current = queue.removeFirst();
            if (!visited.add(current)) continue;
            for (int side = 0; side < 6; ++side) {
                Pos next = current.offset(side);
                if (!visited.contains(next) && areStructurallyConnected(world, current, next, side)) {
                    queue.addLast(next);
                }
            }
        }
        return visited;
    }

    private static Set<Pos> tracePower(World world, Pos source) {
        Set<Pos> visited = new HashSet<>();
        ArrayDeque<Pos> queue = new ArrayDeque<>();
        queue.add(source);

        while (!queue.isEmpty() && visited.size() < MAX_NETWORK_NODES) {
            Pos current = queue.removeFirst();
            if (!visited.add(current)) continue;
            Block block = blockAt(world, current);
            if (block instanceof AxleBlock axle) {
                for (int side = 0; side < 6; ++side) {
                    if (!axle.isAxleOrientedTowardsFacing(world, current.x, current.y, current.z, side)) continue;
                    Pos next = current.offset(side);
                    Block nextBlock = blockAt(world, next);
                    int nextFacing = Block.getOppositeFacing(side);
                    if (nextBlock instanceof AxleBlock nextAxle
                            && nextAxle.isAxleOrientedTowardsFacing(world, next.x, next.y, next.z, nextFacing)) {
                        queue.addLast(next);
                    } else if (nextBlock instanceof GearBoxBlock gearBox
                            && gearBox.canInputAxlePowerToFacing(world, next.x, next.y, next.z, nextFacing)) {
                        queue.addLast(next);
                    } else if (nextBlock instanceof MechanicalBlock device
                            && !(nextBlock instanceof HandCrankBlock)
                            && device.canInputAxlePowerToFacing(world, next.x, next.y, next.z, nextFacing)) {
                        visited.add(next);
                    }
                }
            } else if (block instanceof GearBoxBlock gearBox) {
                if (gearBox instanceof RedstoneClutchBlock
                        && !gearBox.isGearBoxOn(world, current.x, current.y, current.z)) {
                    continue;
                }
                for (int side = 0; side < 6; ++side) {
                    if (isGearboxInputSide(world, current, gearBox, side)) continue;
                    Pos next = current.offset(side);
                    Block nextBlock = blockAt(world, next);
                    int nextFacing = Block.getOppositeFacing(side);
                    if (nextBlock instanceof AxleBlock nextAxle
                            && nextAxle.isAxleOrientedTowardsFacing(world, next.x, next.y, next.z, nextFacing)) {
                        queue.addLast(next);
                    }
                }
            }
        }
        return visited;
    }

    private static Set<Pos> findFirstGearboxes(World world, Collection<Pos> sourcePositions) {
        Set<Pos> found = new HashSet<>();
        Set<Pos> visited = new HashSet<>();
        ArrayDeque<Pos> queue = new ArrayDeque<>(sourcePositions);
        while (!queue.isEmpty() && visited.size() < MAX_NETWORK_NODES) {
            Pos current = queue.removeFirst();
            if (!visited.add(current)) continue;
            Block block = blockAt(world, current);
            if (!(block instanceof AxleBlock axle)) continue;
            for (int side = 0; side < 6; ++side) {
                if (!axle.isAxleOrientedTowardsFacing(world, current.x, current.y, current.z, side)) continue;
                Pos next = current.offset(side);
                Block nextBlock = blockAt(world, next);
                int nextFacing = Block.getOppositeFacing(side);
                if (nextBlock instanceof AxleBlock nextAxle
                        && nextAxle.isAxleOrientedTowardsFacing(world, next.x, next.y, next.z, nextFacing)) {
                    queue.addLast(next);
                } else if (nextBlock instanceof GearBoxBlock gearBox
                        && gearBox.canInputAxlePowerToFacing(world, next.x, next.y, next.z, nextFacing)) {
                    found.add(next);
                }
            }
        }
        return found;
    }

    private static GearboxCalculation calculateGearboxStress(World world, Pos gearboxPos, PowerGroup group) {
        int localLoad = stressOf(world, collectGearboxSpan(world, gearboxPos, group.graph));
        int nearestSourceDistance = Integer.MAX_VALUE;
        int supportingSources = 0;
        float supportUnits = 0.0F;

        for (Source source : group.sources) {
            int distance = gearboxDistance(world, source.positions, gearboxPos, group.graph);
            nearestSourceDistance = Math.min(nearestSourceDistance, distance);
            if (distance <= GEARBOX_SOURCE_SUPPORT_RANGE) {
                ++supportingSources;
                supportUnits += (float) source.capacity / (float) WIND_MILL_CAPACITY;
            }
        }

        if (nearestSourceDistance == Integer.MAX_VALUE) nearestSourceDistance = 0;
        // One windmill is one support unit, a vertical windmill is 1.5, and a
        // water wheel is 2. Sources farther than four gearbox hops do not help.
        int sharedLocalLoad = (int) Math.ceil(localLoad / Math.max(1.0F, supportUnits));
        int distanceStress = Math.max(0, nearestSourceDistance - 1) * STRESS_PER_GEARBOX_FROM_SOURCE;
        return new GearboxCalculation(sharedLocalLoad + distanceStress, localLoad,
                nearestSourceDistance, supportingSources);
    }

    /**
     * The physical load carried directly by one gearbox. Traversal stops at the
     * next gearbox, preventing the old cumulative-whole-subtree behavior.
     */
    private static Set<Pos> collectGearboxSpan(World world, Pos gearboxPos, Set<Pos> poweredGraph) {
        Set<Pos> span = new HashSet<>();
        if (!poweredGraph.contains(gearboxPos)) return span;
        ArrayDeque<Pos> queue = new ArrayDeque<>();
        queue.add(gearboxPos);
        while (!queue.isEmpty() && span.size() < MAX_NETWORK_NODES) {
            Pos current = queue.removeFirst();
            if (!span.add(current)) continue;
            Block currentBlock = blockAt(world, current);
            if (!(currentBlock instanceof AxleBlock) && !(currentBlock instanceof GearBoxBlock)) continue;
            for (int side = 0; side < 6; ++side) {
                Pos next = current.offset(side);
                if (!poweredGraph.contains(next) || span.contains(next)
                        || !areStructurallyConnected(world, current, next, side)) continue;
                if (blockAt(world, next) instanceof GearBoxBlock && !next.equals(gearboxPos)) continue;
                queue.addLast(next);
            }
        }
        return span;
    }

    /** Returns the fewest gearboxes crossed from this source to the target. */
    private static int gearboxDistance(World world, Collection<Pos> starts, Pos target, Set<Pos> poweredGraph) {
        Map<Pos, Integer> best = new HashMap<>();
        PriorityQueue<DistanceState> queue = new PriorityQueue<>(Comparator.comparingInt(state -> state.distance));
        for (Pos start : starts) queue.add(new DistanceState(start, 0));

        while (!queue.isEmpty() && best.size() < MAX_NETWORK_NODES) {
            DistanceState state = queue.poll();
            Integer known = best.get(state.pos);
            if (known != null && known <= state.distance) continue;
            best.put(state.pos, state.distance);
            if (state.pos.equals(target)) return state.distance;
            Block currentBlock = blockAt(world, state.pos);
            if (!(currentBlock instanceof AxleBlock) && !(currentBlock instanceof GearBoxBlock)) continue;

            for (int side = 0; side < 6; ++side) {
                Pos next = state.pos.offset(side);
                if (!poweredGraph.contains(next)
                        || !areStructurallyConnected(world, state.pos, next, side)) continue;
                int nextDistance = state.distance + (blockAt(world, next) instanceof GearBoxBlock ? 1 : 0);
                Integer nextKnown = best.get(next);
                if (nextKnown == null || nextDistance < nextKnown) {
                    queue.add(new DistanceState(next, nextDistance));
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    private static boolean areStructurallyConnected(World world, Pos current, Pos next, int side) {
        Block currentBlock = blockAt(world, current);
        Block nextBlock = blockAt(world, next);
        if (currentBlock == null || nextBlock == null) return false;

        if (currentBlock instanceof AxleBlock axle) {
            if (!axle.isAxleOrientedTowardsFacing(world, current.x, current.y, current.z, side)) return false;
            int nextFacing = Block.getOppositeFacing(side);
            if (nextBlock instanceof AxleBlock nextAxle) {
                return nextAxle.isAxleOrientedTowardsFacing(world, next.x, next.y, next.z, nextFacing);
            }
            if (nextBlock instanceof GearBoxBlock) return true;
            return nextBlock instanceof MechanicalBlock device
                    && !(nextBlock instanceof HandCrankBlock)
                    && device.canInputAxlePowerToFacing(world, next.x, next.y, next.z, nextFacing);
        }

        // Mechanical devices only connect through axles. Evaluate the same edge
        // from the axle side so direction/orientation rules remain identical.
        if (nextBlock instanceof AxleBlock) {
            return areStructurallyConnected(world, next, current, Block.getOppositeFacing(side));
        }
        return false;
    }

    private static boolean isGearboxInputSide(World world, Pos pos, GearBoxBlock gearBox, int side) {
        int facing = gearBox.getFacing(world, pos.x, pos.y, pos.z);
        return side == facing || gearBox instanceof DualInputGearBoxBlock
                && side == Block.getOppositeFacing(facing);
    }

    private static SourceIdentity findSourceIdentity(World world, Pos sourcePos) {
        AxisAlignedBB searchBox = AxisAlignedBB.getAABBPool().getAABB(
                sourcePos.x - 13, sourcePos.y - 7, sourcePos.z - 13,
                sourcePos.x + 14, sourcePos.y + 8, sourcePos.z + 14);
        List entities = world.getEntitiesWithinAABB(MechanicalPowerSourceEntity.class, searchBox);
        for (Object value : entities) {
            MechanicalPowerSourceEntity entity = (MechanicalPowerSourceEntity) value;
            int entityX = MathHelper.floor_double(entity.posX);
            int entityY = MathHelper.floor_double(entity.posY);
            int entityZ = MathHelper.floor_double(entity.posZ);
            boolean ownsAxle = entity instanceof VerticalWindMillEntity
                    ? entityX == sourcePos.x && entityZ == sourcePos.z && Math.abs(entityY - sourcePos.y) <= 3
                    : entityX == sourcePos.x && entityY == sourcePos.y && entityZ == sourcePos.z;
            if (!ownsAxle) continue;
            int capacity = entity instanceof WaterWheelEntity ? WATER_WHEEL_CAPACITY
                    : entity instanceof VerticalWindMillEntity ? VERTICAL_WIND_MILL_CAPACITY
                    : WIND_MILL_CAPACITY;
            return new SourceIdentity(entity.entityId, capacity);
        }
        // A powered source axle should always have an entity, but retain a safe
        // fallback for legacy saves during the entity's first load tick.
        return new SourceIdentity(sourcePos.hashCode() ^ 0x40000000, WIND_MILL_CAPACITY);
    }

    private static int stressOf(World world, Collection<Pos> positions) {
        int stress = 0;
        for (Pos pos : positions) stress += getStressCost(blockAt(world, pos));
        return stress;
    }

    private static boolean intersects(Set<Pos> first, Set<Pos> second) {
        Set<Pos> smaller = first.size() <= second.size() ? first : second;
        Set<Pos> larger = smaller == first ? second : first;
        for (Pos pos : smaller) if (larger.contains(pos)) return true;
        return false;
    }

    private static boolean isMechanicalNode(Block block) {
        return block instanceof AxleBlock || block instanceof MechanicalBlock && !(block instanceof HandCrankBlock);
    }

    private static Block blockAt(World world, Pos pos) {
        return blockAt(world, pos.x, pos.y, pos.z);
    }

    private static Block blockAt(World world, int x, int y, int z) {
        if (!world.blockExists(x, y, z)) return null;
        return Block.blocksList[world.getBlockId(x, y, z)];
    }

    public record StressReport(int stress, int capacity, int sources, boolean powered, boolean overloaded) { }

    public record GearboxStressReport(int stress, int limit, int localLoad, int nearestSourceDistance, int supportingSources, boolean powered) { }

    private record GearboxCalculation(int stress, int localLoad, int nearestSourceDistance, int supportingSources) {}

    private record DistanceState(Pos pos, int distance) {}

    private record SourceIdentity(int key, int capacity) {}

    private static final class Source {
        final int capacity;
        final Set<Pos> positions = new HashSet<>();

        Source(int capacity) {
            this.capacity = capacity;
        }
    }

    private static final class PowerGroup {
        int capacity;
        final Set<Pos> graph;
        final List<Source> sources = new ArrayList<>();

        PowerGroup(int capacity, Set<Pos> graph, Source source) {
            this.capacity = capacity;
            this.graph = graph;
            this.sources.add(source);
        }

        void merge(PowerGroup other) {
            this.capacity += other.capacity;
            this.graph.addAll(other.graph);
            this.sources.addAll(other.sources);
        }
    }

    private record Pos(int x, int y, int z) {

        Pos offset(int side) {
                return switch (side) {
                    case 0 -> new Pos(x, y - 1, z);
                    case 1 -> new Pos(x, y + 1, z);
                    case 2 -> new Pos(x, y, z - 1);
                    case 3 -> new Pos(x, y, z + 1);
                    case 4 -> new Pos(x - 1, y, z);
                    default -> new Pos(x + 1, y, z);
                };
            }

            @Override
            public boolean equals(Object object) {
                if (this == object) return true;
                if (!(object instanceof Pos pos)) return false;
                return x == pos.x && y == pos.y && z == pos.z;
            }

    }
}
