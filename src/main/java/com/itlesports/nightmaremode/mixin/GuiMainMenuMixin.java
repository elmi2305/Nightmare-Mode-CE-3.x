package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.GuiMainMenu;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin {
    @Shadow private String splashText;
    @Shadow @Final private static Random rand;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void manageSplashText(CallbackInfo ci){
        this.splashText = getQuotes().get(rand.nextInt(getQuotes().size()));
    }



    @Unique
    private static @NotNull List<String> getQuotes() {
        List<String> quotesList = new ArrayList<>();
        quotesList.add("Nightmare Mode!");
        quotesList.add("Also try MEA!");
        quotesList.add("Oops! All Hostile!");
        quotesList.add("Also try Hostile!");
        quotesList.add("Am I dreaming?");
        quotesList.add("Wakey wakey!");
        quotesList.add("Orange-flavored creepers?!");
        quotesList.add("Better Than MEA?");
        quotesList.add("It's just a bad dream!");
        quotesList.add("Not the water creatures!");
        quotesList.add("MEN! WITH ME!");
        quotesList.add("Wake up!");
        quotesList.add("Type 1 if you agree!");
        quotesList.add("Now with Blood Moons!");
        quotesList.add("It's easy if you're good!");
        quotesList.add("Hug the saw!");
        quotesList.add("Oops! All Bloodmoons!");
        return quotesList;
    }
}
