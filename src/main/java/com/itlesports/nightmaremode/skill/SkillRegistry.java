package com.itlesports.nightmaremode.skill;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkillRegistry {
    private static final List<SkillBranch> BRANCHES = new ArrayList<>();
    private static final Map<String, SkillNode> NODES = new LinkedHashMap<>();

    static void registerBranch(SkillBranch branch) {
        BRANCHES.add(branch);
    }

    static void registerNode(SkillNode node) {
        NODES.put(node.id.toString(), node);
    }

    public static List<SkillBranch> getBranches() {
        return BRANCHES;
    }

    public static SkillNode getNode(String id) {
        return NODES.get(id);
    }

    public static Iterable<SkillNode> getNodes() {
        return NODES.values();
    }
}
