package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.skill.SkillNode;
import java.util.List;

/** Recipe adapters whose skill requirements are registered by object identity. */
public interface SkillRecipeSource {
    List<SkillNode> nightmareMode$getRequiredSkills();
}
