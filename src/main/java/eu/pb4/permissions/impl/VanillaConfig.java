package eu.pb4.permissions.impl;

import java.util.HashMap;
import java.util.Map;

public class VanillaConfig {
    public String _availableProviders;
    public String defaultProvider;

    public String _section = "Vanilla/Build in permissions";
    public Map<String, Boolean> defaultPermissions = new HashMap<>();
    public Map<String, Boolean> level1Permissions = new HashMap<>();
    public Map<String, Boolean> level2Permissions = new HashMap<>();
    public Map<String, Boolean> level3Permissions = new HashMap<>();
    public Map<String, Boolean> level4Permissions = new HashMap<>();

    public SimpleConfig asSimple() {
        SimpleConfig config = new SimpleConfig();
        config._availableProviders = this._availableProviders;
        config.defaultProvider = defaultProvider;
        return config;
    }
}
