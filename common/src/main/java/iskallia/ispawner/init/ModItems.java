package iskallia.ispawner.init;

import iskallia.ispawner.item.SpawnerControllerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class ModItems extends ModRegistries {

	public static Item SPAWNER_CONTROLLER;

	public static void register() {
		SPAWNER_CONTROLLER = register("spawner_controller", new SpawnerControllerItem(new Item.Settings().maxCount(1).group(ItemGroup.SEARCH)));
	}

	public static <V extends Item> V register(Identifier id, V item) {
		if(item instanceof BlockItem) {
			((BlockItem)item).appendBlocks(BlockItem.BLOCK_ITEMS, item);
		}

		return register(ITEMS, id, item);
	}

	public static <V extends Item> V register(String name, V item) {
		if(item instanceof BlockItem) {
			((BlockItem)item).appendBlocks(BlockItem.BLOCK_ITEMS, item);
		}

		return register(ITEMS, name, item);
	}

}
