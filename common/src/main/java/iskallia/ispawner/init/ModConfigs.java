package iskallia.ispawner.init;

import iskallia.ispawner.config.SurvivalSpawnerConfig;

public class ModConfigs extends ModRegistries {

    public static SurvivalSpawnerConfig SURVIVAL_SPAWNER;

    public static void register() {
        SURVIVAL_SPAWNER = new SurvivalSpawnerConfig().readConfig();
    }

}
