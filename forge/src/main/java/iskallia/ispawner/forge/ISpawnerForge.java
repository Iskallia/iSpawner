package iskallia.ispawner.forge;

import dev.architectury.platform.forge.EventBuses;
import iskallia.ispawner.ISpawner;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(iskallia.ispawner.ISpawner.MOD_ID)
public class ISpawnerForge {

    public ISpawnerForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ISpawner.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ISpawner.init();
    }

}
