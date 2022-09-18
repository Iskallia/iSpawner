package iskallia.ispawner.init;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import iskallia.ispawner.screen.handler.SpawnerScreenHandler;
import iskallia.ispawner.screen.handler.SurvivalSpawnerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ModMenus extends ModRegistries {

	public static RegistrySupplier<ScreenHandlerType<SpawnerScreenHandler>> SPAWNER;
	public static RegistrySupplier<ScreenHandlerType<SurvivalSpawnerScreenHandler>> SURVIVAL_SPAWNER;

	public static void register() {
		SPAWNER = ModScreens.register(SCREEN_HANDLERS, "spawner", () -> MenuRegistry.ofExtended(SpawnerScreenHandler::new));
		SURVIVAL_SPAWNER = ModScreens.register(SCREEN_HANDLERS, "survival_spawner", () -> MenuRegistry.ofExtended(SurvivalSpawnerScreenHandler::new));
	}

}
