package com.paulzzh.yuzu;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.paulzzh.yuzu.config.YuZuUIConfig;
import com.paulzzh.yuzu.mixins.Mixins;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class YuZuUICore implements IFMLLoadingPlugin, IEarlyMixinLoader {
    static {
        try {
            ConfigurationManager.registerConfig(YuZuUIConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMixinConfig() {
        return "mixins.yuzu.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return Mixins.getEarlyMixins(loadedCoreMods);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
