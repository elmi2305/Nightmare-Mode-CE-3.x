package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillNode;
import emi.dev.emi.emi.api.EmiApi;
import emi.dev.emi.emi.api.recipe.*;
import emi.dev.emi.emi.screen.RecipeScreen;
import net.minecraft.src.*;
import java.util.*;

public final class SkillRecipeBrowser {
    private SkillRecipeBrowser() {}

    public static boolean open(SkillNode skill) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || skill == null) return false;
        Map<EmiRecipeCategory, List<EmiRecipe>> categories = new LinkedHashMap<>();
        // Query the current manager each time so EMI recipe reloads cannot leave stale results.
        for (EmiRecipe recipe : EmiApi.getRecipeManager().getRecipes()) {
            List<SkillNode> required = recipe instanceof SkillRecipeSource source
                    ? source.nightmareMode$getRequiredSkills()
                    : SkillLockedCrafting.getRequiredSkills(recipe.getId());
            if (required.contains(skill)) {
                categories.computeIfAbsent(recipe.getCategory(), ignored -> new ArrayList<>()).add(recipe);
            }
        }
        if (categories.isEmpty()) {
            mc.thePlayer.addChatMessage("No recipes require " + skill.name + ".");
            return true;
        }
        GuiScreen returnScreen = mc.currentScreen;
        GuiContainer inventory = EmiApi.getHandledScreen();
        if (inventory == null) inventory = new GuiInventory(mc.thePlayer);
        mc.displayGuiScreen(new RecipeScreen(inventory, categories) {
            @Override public void close() {
                Minecraft.getMinecraft().displayGuiScreen(returnScreen);
            }
        });
        return true;
    }
}
