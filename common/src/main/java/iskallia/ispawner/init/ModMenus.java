package iskallia.ispawner.init;

import iskallia.ispawner.screen.handler.SpawnerScreenHandler;
import me.shedaniel.architectury.registry.MenuRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class ModMenus extends ModRegistries {

	public static ScreenHandlerType<SpawnerScreenHandler> SPAWNER;

	public static void register() {
		SPAWNER = ModScreens.register(SCREEN_HANDLERS, "spawner", MenuRegistry.of(SpawnerScreenHandler::new));
	}

}
