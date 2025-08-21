package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.client.NightmareKeyBindings;
import net.minecraft.src.GameSettings;
import net.minecraft.src.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/**
 * Injects Nightmare Mode custom key bindings to GameSettings so user rebindings persist across restarts.
 * Implementation details:
 * - Custom key bindings are added to the keyBindings array before GameSettings.loadOptions() is called,
 *   ensuring vanilla options.txt handling applies to them.
 * - This allows the vanilla key binding screen and options.txt persistence to work automatically
 *   for custom keys, without manual parsing or restoring keyCodes.
 */
@Mixin(GameSettings.class)
public abstract class GameSettingsMixin {

    @Shadow public KeyBinding[] keyBindings;

    @Inject(method = "<init>*", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GameSettings;loadOptions()V"))
    private void nightmaremode$injectCustomKeys(CallbackInfo ci) {
        if (!NightmareKeyBindings.markRegistered()) return;

        NightmareKeyBindings.nightmareZoom = new KeyBinding("key.nightmaremode.zoom", Keyboard.KEY_C);

        KeyBinding[] custom = NightmareKeyBindings.all();
        KeyBinding[] neu = Arrays.copyOf(keyBindings, keyBindings.length + custom.length);
        System.arraycopy(custom, 0, neu, keyBindings.length, custom.length);
        keyBindings = neu;
    }
}