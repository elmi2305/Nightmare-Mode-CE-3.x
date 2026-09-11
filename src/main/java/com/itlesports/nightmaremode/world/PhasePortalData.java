package com.itlesports.nightmaremode.world;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class PhasePortalData {
    private final List<Endpoint> endpoints = new ArrayList<>();

    public List<Endpoint> getEndpoints(int color) {
        List<Endpoint> result = new ArrayList<>();
        for (Endpoint endpoint : this.endpoints) if (endpoint.color == color) result.add(endpoint);
        return result;
    }

    public void add(Endpoint endpoint) { this.endpoints.add(endpoint); }
    public boolean remove(int dimension, int x, int y, int z) {
        return this.endpoints.removeIf(endpoint -> endpoint.dimension == dimension
                && endpoint.x == x && endpoint.y == y && endpoint.z == z);
    }

    public static PhasePortalData readFromNBT(NBTTagCompound tag) {
        PhasePortalData data = new PhasePortalData();
        NBTTagList list = tag.getTagList("PhasePortals");
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound entry = (NBTTagCompound)list.tagAt(i);
            data.endpoints.add(new Endpoint(entry.getInteger("Color"), entry.getInteger("Dimension"),
                    entry.getInteger("X"), entry.getInteger("Y"), entry.getInteger("Z"), entry.getInteger("Axis")));
        }
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, PhasePortalData data) {
        NBTTagList list = new NBTTagList("PhasePortals");
        for (Endpoint endpoint : data.endpoints) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("Color", endpoint.color);
            entry.setInteger("Dimension", endpoint.dimension);
            entry.setInteger("X", endpoint.x);
            entry.setInteger("Y", endpoint.y);
            entry.setInteger("Z", endpoint.z);
            entry.setInteger("Axis", endpoint.axis);
            list.appendTag(entry);
        }
        tag.setTag("PhasePortals", list);
    }

    public static final class Endpoint {
        public final int color, dimension, x, y, z, axis;
        public Endpoint(int color, int dimension, int x, int y, int z, int axis) {
            this.color = color; this.dimension = dimension; this.x = x; this.y = y; this.z = z; this.axis = axis;
        }
        public boolean contains(int dimension, int x, int y, int z) {
            if (this.dimension != dimension || y < this.y || y > this.y + 2) return false;
            return this.axis == 0 ? z == this.z && x >= this.x && x <= this.x + 1
                    : x == this.x && z >= this.z && z <= this.z + 1;
        }
    }
}
