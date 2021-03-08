package iskallia.ispawner.fabric;

import iskallia.ispawner.ISpawner;
import net.fabricmc.api.ModInitializer;

public class ISpawnerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ISpawner.init();
    }

}
