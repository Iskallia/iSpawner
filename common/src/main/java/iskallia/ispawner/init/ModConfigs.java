package iskallia.ispawner.init;

import iskallia.ispawner.config.SpawnEggConfig;
import iskallia.ispawner.config.SurvivalSpawnerConfig;

public class ModConfigs extends ModRegistries {

    public static SurvivalSpawnerConfig SURVIVAL_SPAWNER;
    public static SpawnEggConfig SPAWN_EGG;

    public static void register() {
        SURVIVAL_SPAWNER = new SurvivalSpawnerConfig().readConfig();
        SPAWN_EGG = new SpawnEggConfig().readConfig();
    }

}
