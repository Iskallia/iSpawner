package iskallia.ispawner.init;

import dev.architectury.registry.registries.RegistrySupplier;
import iskallia.ispawner.item.GenericSpawnEggItem;
import iskallia.ispawner.item.SpawnerControllerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModItems extends ModRegistries {

	public static RegistrySupplier<SpawnerControllerItem> SPAWNER_CONTROLLER;
	public static RegistrySupplier<Item> CAGE_DUST;
	public static RegistrySupplier<Item> CAGE_PIECE;
	public static RegistrySupplier<Item> SPAWN_EGG;

	public static void register() {
		SPAWNER_CONTROLLER = register("spawner_controller", () -> new SpawnerControllerItem(new Item.Settings()
			.maxCount(1).group(ItemGroup.REDSTONE)));
		CAGE_DUST = register("cage_dust", () -> new Item(new Item.Settings()
			.maxCount(7).group(ItemGroup.REDSTONE)));
		CAGE_PIECE = register("cage_piece", () -> new Item(new Item.Settings()
			.maxCount(3).group(ItemGroup.REDSTONE)));
		SPAWN_EGG = register("spawn_egg", () -> new GenericSpawnEggItem(new Item.Settings()
			.maxCount(64).group(ItemGroup.REDSTONE)));
	}

	public static <V extends Item> RegistrySupplier<V> register(Identifier id, Supplier<V> item) {
		return register(ITEMS, id, item);
	}

	public static <V extends Item> RegistrySupplier<V> register(String name, Supplier<V> item) {
		return register(ITEMS, name, item);
	}

	public static class NBT {
		public static void register() {

		}
	}

}
