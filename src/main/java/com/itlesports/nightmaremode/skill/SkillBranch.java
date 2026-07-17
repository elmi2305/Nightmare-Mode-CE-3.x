package com.itlesports.nightmaremode.skill;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkillBranch {
    private static int nextIndex;

    private final String name;
    private final int index;
    private final ItemStack icon;
    private final List<SkillNode> nodes = new ArrayList<>();
    int minDisplayColumn;
    int minDisplayRow;
    int maxDisplayColumn;
    int maxDisplayRow;

    public SkillBranch(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
        this.index = nextIndex++;
        SkillRegistry.registerBranch(this);
    }

    public SkillBranch(String name, Item item) {
        this(name, new ItemStack(item));
    }

    public SkillBranch(String name, Block block) {
        this(name, new ItemStack(block));
    }

    void add(SkillNode node) {
        this.nodes.add(node);
        if (this.nodes.size() == 1) {
            this.minDisplayColumn = this.maxDisplayColumn = node.displayColumn;
            this.minDisplayRow = this.maxDisplayRow = node.displayRow;
        } else {
            this.minDisplayColumn = Math.min(this.minDisplayColumn, node.displayColumn);
            this.maxDisplayColumn = Math.max(this.maxDisplayColumn, node.displayColumn);
            this.minDisplayRow = Math.min(this.minDisplayRow, node.displayRow);
            this.maxDisplayRow = Math.max(this.maxDisplayRow, node.displayRow);
        }
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public ItemStack getIcon() {
        return this.icon.copy();
    }

    public List<SkillNode> getNodes() {
        return this.nodes;
    }
}
