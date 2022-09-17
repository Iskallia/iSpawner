package iskallia.ispawner.init;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import iskallia.ispawner.block.render.SpawnerBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

import java.util.Map;

public class ModRenderers extends ModRegistries {

	public static Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> FACTORIES;

	public static class BlockEntities extends ModRenderers {
		public static BlockEntityRendererFactory<SpawnerBlockEntity> SPAWNER;
		public static BlockEntityRendererFactory<SurvivalSpawnerBlockEntity> SURVIVAL_SPAWNER;

		public static void register() {
			SPAWNER = register(ModBlocks.Entities.SPAWNER, SpawnerBlockRenderer::new);
			SURVIVAL_SPAWNER = register(ModBlocks.Entities.SURVIVAL_SPAWNER, SpawnerBlockRenderer::new);
		}
	}

	public static class RenderLayers {
		public static void register(Map<Block, RenderLayer> registry, boolean fancyGraphicsOrBetter) {
			ModRenderers.register(registry, ModBlocks.SPAWNER, RenderLayer.getCutout());
			ModRenderers.register(registry, ModBlocks.SURVIVAL_SPAWNER, RenderLayer.getCutout());
		}
	}

	public static <T extends BlockEntity> BlockEntityRendererFactory<T> register(
		BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> renderer) {
		FACTORIES.put(type, renderer);
		return renderer;
	}

	public static void register(Map<Block, RenderLayer> registry, Block block, RenderLayer layer) {
		registry.put(block, layer);
	}

}
