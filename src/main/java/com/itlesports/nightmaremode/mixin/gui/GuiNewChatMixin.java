package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.GuiNewChat;
import net.minecraft.src.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin {

    /**
     * Intercepts the chat message before rendering and applies localization and parameter substitution
     *
     * @param var17 The original chat line to render.
     * @return The localized and parameter-substituted chat line.
     */
    @ModifyVariable(method = "drawChat", at = @At(value = "STORE", ordinal = 0), name = "var17")

    private String localizeChatMessage(String var17) {
        if (var17 != null && var17.contains("nightmare.")) {
            int idx = var17.indexOf("nightmare.");
            int pipeIdx = var17.indexOf("|", idx);

            String key = pipeIdx > -1 ? var17.substring(idx, pipeIdx) : var17.substring(idx);
            String localized = I18n.getString(key);

            if (pipeIdx > -1) {
                String params = var17.substring(pipeIdx + 1);
                String[] paramPairs = params.split("\\|");
                for (String pair : paramPairs) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2) {
                        localized = localized.replace("{" + kv[0] + "}", kv[1]);
                    }
                }
            }
            return var17.replace(var17.substring(idx), localized);
        }
        return var17;
    }
}