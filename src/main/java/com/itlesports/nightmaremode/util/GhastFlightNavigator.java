package com.itlesports.nightmaremode.util;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class GhastFlightNavigator {
    private static final double STEP = 3.0D;
    private static final int SEARCH_BUDGET = 256;
    private static final int[][] DIRECTIONS = {{1,0,0}, {-1,0,0}, {0,1,0}, {0,-1,0}, {0,0,1}, {0,0,-1}};
    private Entity target;
    private boolean hasLastSight;
    private int unseenTicks;
    private int nextSearchTick;
    private double goalX, goalY, goalZ;
    private List<Point> path = List.of();
    private int pathIndex;

    public void update(EntityGhast ghast, Entity candidate, double preferredDistance) {
        if (ghast.worldObj.isRemote) return;
        if (candidate != target) {
            target = candidate;
            hasLastSight = false;
            unseenTicks = 0;
            nextSearchTick = 0;
            path = List.of();
        }
        boolean visible = target != null && target.isEntityAlive() && ghast.canEntityBeSeen(target);
        if (visible) {
            goalX = target.posX;
            goalY = target.posY + 3.0D;
            goalZ = target.posZ;
            hasLastSight = true;
            unseenTicks = 0;
        } else {
            ++unseenTicks;
        }
        if (target == null || !target.isEntityAlive() || !hasLastSight || unseenTicks > 200
                || visible && ghast.getDistanceSqToEntity(target) <= preferredDistance * preferredDistance) {
            path = List.of();
            // leave ordinary wandering active, replacing an obstructed waypoint when necessary.
            if (ghast.ticksExisted >= nextSearchTick) {
                nextSearchTick = ghast.ticksExisted + 20;
                if (!canTravel(ghast, ghast.waypointX, ghast.waypointY, ghast.waypointZ)) wander(ghast);
            }
            return;
        }

        while (pathIndex < path.size() && distanceSquared(ghast.posX, ghast.posY, ghast.posZ, path.get(pathIndex)) < 2.25D) {
            ++pathIndex;
        }
        if (ghast.ticksExisted >= nextSearchTick) {
            nextSearchTick = ghast.ticksExisted + 20;
            double dx = goalX - ghast.posX, dy = goalY - ghast.posY, dz = goalZ - ghast.posZ;
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double fraction = length > 12.0D ? 12.0D / length : 1.0D;
            Point direct = new Point(ghast.posX + dx * fraction, ghast.posY + dy * fraction, ghast.posZ + dz * fraction);
            path = length > 1.5D && canTravel(ghast, direct.x, direct.y, direct.z)
                    ? List.of(direct) : findPath(ghast, new Point(goalX, goalY, goalZ));
            pathIndex = 0;
            if (path.isEmpty()) wander(ghast);
        }
        if (pathIndex < path.size()) {
            Point next = path.get(pathIndex);
            if (canTravel(ghast, next.x, next.y, next.z)) {
                ghast.waypointX = next.x;
                ghast.waypointY = next.y;
                ghast.waypointZ = next.z;
            } else {
                path = List.of();
                wander(ghast);
            }
        }
    }

    private static List<Point> findPath(EntityGhast ghast, Point goal) {
        Point origin = new Point(ghast.posX, ghast.posY, ghast.posZ);
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(node -> node.cost + node.remaining));
        Map<Cell, Node> visited = new HashMap<>();
        Node start = new Node(new Cell(0, 0, 0), origin, 0.0D, remaining(origin, goal), null);
        Node best = start;
        open.add(start);
        visited.put(start.cell, start);
        int expanded = 0;
        while (!open.isEmpty() && expanded < SEARCH_BUDGET) {
            Node current = open.poll();
            if (visited.get(current.cell) != current) continue;
            ++expanded;
            if (distanceSquared(current.point.x, current.point.y, current.point.z, goal)
                    < distanceSquared(best.point.x, best.point.y, best.point.z, goal)) best = current;
            if (distanceSquared(current.point.x, current.point.y, current.point.z, goal) < STEP * STEP) {
                best = current;
                break;
            }
            for (int[] direction : DIRECTIONS) {
                Cell cell = new Cell(current.cell.x + direction[0], current.cell.y + direction[1], current.cell.z + direction[2]);
                if (Math.abs(cell.x) > 10 || Math.abs(cell.y) > 6 || Math.abs(cell.z) > 10) continue;
                double cost = current.cost + STEP;
                Node previous = visited.get(cell);
                if (previous != null && previous.cost <= cost) continue;
                Point point = new Point(origin.x + cell.x * STEP, origin.y + cell.y * STEP, origin.z + cell.z * STEP);
                AxisAlignedBB box = ghast.boundingBox.copy().offset(current.point.x - origin.x,
                        current.point.y - origin.y, current.point.z - origin.z);
                // axis-aligned edges let one swept box cover the ghast's entire body.
                box = box.addCoord(point.x - current.point.x, point.y - current.point.y, point.z - current.point.z);
                if (!isClear(ghast, box)) continue;
                Node next = new Node(cell, point, cost, remaining(point, goal), current);
                visited.put(cell, next);
                open.add(next);
            }
        }
        if (best == start) return List.of();
        List<Point> result = new ArrayList<>();
        for (Node node = best; node.parent != null; node = node.parent) result.add(node.point);
        Collections.reverse(result);
        return result;
    }

    public static boolean canTravel(EntityGhast ghast, double x, double y, double z) {
        double dx = x - ghast.posX, dy = y - ghast.posY, dz = z - ghast.posZ;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > 64.0D) return false;
        int steps = Math.max(1, (int)Math.ceil(distance));
        for (int step = 1; step <= steps; ++step) {
            double fraction = (double)step / steps;
            if (!isClear(ghast, ghast.boundingBox.copy().offset(dx * fraction, dy * fraction, dz * fraction))) return false;
        }
        return true;
    }

    private static boolean isClear(EntityGhast ghast, AxisAlignedBB box) {
        World world = ghast.worldObj;
        if (box.minY < 1.0D || box.maxY >= world.getActualHeight()) return false;
        int minX = (int)Math.floor(box.minX), minY = (int)Math.floor(box.minY), minZ = (int)Math.floor(box.minZ);
        int maxX = (int)Math.floor(box.maxX), maxY = (int)Math.floor(box.maxY), maxZ = (int)Math.floor(box.maxZ);
        if (!world.blockExists(minX, minY, minZ) || !world.blockExists(minX, minY, maxZ)
                || !world.blockExists(maxX, maxY, minZ) || !world.blockExists(maxX, maxY, maxZ)) return false;
        return world.getCollidingBoundingBoxes(ghast, box).isEmpty();
    }

    private static void wander(EntityGhast ghast) {
        for (int attempt = 0; attempt < 12; ++attempt) {
            double angle = ghast.getRNG().nextDouble() * Math.PI * 2.0D;
            double radius = 4.0D + ghast.getRNG().nextDouble() * 6.0D;
            double x = ghast.posX + Math.cos(angle) * radius;
            double y = ghast.posY + (ghast.getRNG().nextDouble() * 2.0D - 1.0D) * 4.0D;
            double z = ghast.posZ + Math.sin(angle) * radius;
            if (!canTravel(ghast, x, y, z)) continue;
            ghast.waypointX = x;
            ghast.waypointY = y;
            ghast.waypointZ = z;
            return;
        }
    }

    private static double remaining(Point point, Point goal) {
        return Math.abs(point.x - goal.x) + Math.abs(point.y - goal.y) + Math.abs(point.z - goal.z);
    }

    private static double distanceSquared(double x, double y, double z, Point point) {
        double dx = x - point.x, dy = y - point.y, dz = z - point.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private record Point(double x, double y, double z) {}
    private record Cell(int x, int y, int z) {}
    private record Node(Cell cell, Point point, double cost, double remaining, Node parent) {}
}
