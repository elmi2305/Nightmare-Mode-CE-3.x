package com.itlesports.nightmaremode.mixin;

import api.config.AddonConfig;
import api.config.ConfigElementValidator;
import com.itlesports.nightmaremode.util.interfaces.AddonConfigExtender;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AddonConfig.class)
public abstract class AddonConfigMixin implements AddonConfigExtender {
    @Shadow public Config currentConfig;
    @Shadow @Final Map<String, ConfigElementValidator> pathValidatorMap;
    @Shadow private boolean hasChanged;
    @Shadow protected abstract void writeConfig();

    @Override
    public void nightmareMode$modifyProperty(String path, boolean newValue) {
        // ensure the path exists and is registered
        if (!this.currentConfig.hasPath(path)) {
            throw new IllegalArgumentException("Config path '" + path + "' does not exist");
        }

        ConfigElementValidator validator = this.pathValidatorMap.get(path);
        if (validator == null) {
            throw new IllegalArgumentException("Path '" + path + "' was not registered with a validator");
        }

        // create new value
        ConfigValue newValueObj = ConfigValueFactory.fromAnyRef(newValue);

        // validate new value
        if (!validator.validate(newValueObj)) {
            throw new IllegalArgumentException(validator.getErrorMessage(newValueObj, this.currentConfig.getValue(path), path));
        }

        // preserve comments
        ConfigValue currentValue = this.currentConfig.getValue(path);
        newValueObj = newValueObj.withOrigin(newValueObj.origin().withComments(currentValue.origin().comments()));

        // Apply the new value
        this.currentConfig = this.currentConfig.withValue(path, newValueObj);
        this.hasChanged = true;
        this.writeConfig();
    }
}
