package iskallia.ispawner.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks extends ModRegistries {

	public static Block TEST;

	public static void register() {
		TEST = register("test_block", new Block(AbstractBlock.Settings.of(Material.STONE)
				.requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque()),
				block -> new BlockItem(block, new Item.Settings().group(ItemGroup.SEARCH)));
	}

	public static <V extends Block> V register(Identifier id, V block) {
		return register(id, block);
	}

	public static <V extends Block> V register(String name, V block) {
		return register(name, block);
	}

	public static <V extends Block> V register(Identifier id, V block, Function<V, BlockItem> item) {
		return register(id, block, id, item);
	}

	public static <V extends Block> V register(String name, V block, Function<V, BlockItem> item) {
		return register(name, block, name, item);
	}

	public static <V extends Block> V register(Identifier id, V block, Identifier itemId, Function<V, BlockItem> item) {
		if(item != null) {
			BlockItem value = item.apply(block);

			if(value != null) {
				ModItems.register(itemId, value);
			}
		}

		return register(BLOCKS, id, block);
	}

	public static <V extends Block> V register(String name, V block, String itemName, Function<V, BlockItem> item) {
		if(item != null) {
			BlockItem value = item.apply(block);

			if(value != null) {
				ModItems.register(itemName, value);
			}
		}

		return register(BLOCKS, name, block);
	}

}
