package iskallia.ispawner.init;

import iskallia.ispawner.block.render.SpawnerBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import java.util.Map;

public class ModRenderers extends ModRegistries {

	public static class BlockEntities extends ModRenderers {
		public static SpawnerBlockRenderer SPAWNER;

		public static void register(Map<BlockEntityType<?>, BlockEntityRenderer<?>> registry, BlockEntityRenderDispatcher dispatcher) {
			SPAWNER = register(registry, ModBlocks.Entities.SPAWNER, new SpawnerBlockRenderer(dispatcher));
		}
	}

	public static class RenderLayers {
		public static void register(Map<Block, RenderLayer> registry, boolean fancyGraphicsOrBetter) {
			ModRenderers.register(registry, ModBlocks.SPAWNER, RenderLayer.getCutout());
		}
	}

	public static <T extends BlockEntity, R extends BlockEntityRenderer<T>> R register(Map<BlockEntityType<?>, BlockEntityRenderer<?>> registry,
	                                                                                   BlockEntityType<T> type, R renderer) {
		registry.put(type, renderer);
		return renderer;
	}

	public static void register(Map<Block, RenderLayer> registry, Block block, RenderLayer layer) {
		registry.put(block, layer);
	}

}
