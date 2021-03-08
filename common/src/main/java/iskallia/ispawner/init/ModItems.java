package iskallia.ispawner.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class ModItems extends ModRegistries {

	public static Item TEST;

	public static void register() {
		TEST = register("test_item", new Item(new Item.Settings().group(ItemGroup.SEARCH)));
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
