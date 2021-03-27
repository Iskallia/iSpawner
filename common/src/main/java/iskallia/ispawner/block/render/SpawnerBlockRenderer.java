package iskallia.ispawner.block.render;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.init.ModItems;
import iskallia.ispawner.util.Color;
import iskallia.ispawner.world.spawner.SpawnerController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpawnerBlockRenderer extends BlockEntityRenderer<SpawnerBlockEntity> {

	public static final Map<Integer, Color> COLOR_PER_WEIGHT = new LinkedHashMap<>();

	static {
		COLOR_PER_WEIGHT.put(0, new Color(0, 0, 0));
		COLOR_PER_WEIGHT.put(1, new Color(255, 0, 0));
		COLOR_PER_WEIGHT.put(2, new Color(0, 255, 0));
		COLOR_PER_WEIGHT.put(3, new Color(0, 0, 255));
		COLOR_PER_WEIGHT.put(4, new Color(255, 255, 0));
		COLOR_PER_WEIGHT.put(5, new Color(255, 0, 255));
		COLOR_PER_WEIGHT.put(6, new Color(0, 255, 255));
		COLOR_PER_WEIGHT.put(7, new Color(255, 255, 255));
	}

	public SpawnerBlockRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(SpawnerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null)return;

		boolean rendered = tryRender(entity, matrices, vertexConsumers, player.getStackInHand(Hand.MAIN_HAND))
				|| tryRender(entity, matrices, vertexConsumers, player.getStackInHand(Hand.OFF_HAND));

		if(rendered) {
			BlockPos pos = entity.getOffset();

			WorldRenderer.drawBox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()),
					pos.getX(), pos.getY(), pos.getZ(),
					pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + 1.0D,
					1.0F, 1.0F, 1.0F, 1.0F,
					1.0F, 1.0F, 1.0F);

			WorldRenderer.drawBox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()),
					0.0D, 0.0D, 0.0D,
					1.0D, 1.0D, 1.0D,
					1.0F, 1.0F, 1.0F, 1.0F,
					1.0F, 1.0F, 1.0F);
		}

		this.renderEntity(entity, tickDelta, matrices, vertexConsumers, light, overlay);
	}

	public boolean tryRender(SpawnerBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack) {
		if(stack.getItem() == ModItems.SPAWNER_CONTROLLER) {
			SpawnerController controller = new SpawnerController(stack.getOrCreateSubTag("Controller"));

			if(controller.getTarget().isPresent() && controller.getTarget().get().equals(entity.getPos())) {
				entity.renderer.refresh(entity);
				entity.renderer.render(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean rendersOutsideBoundingBox(SpawnerBlockEntity blockEntity) {
		return true;
	}

	public void renderEntity(SpawnerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		ItemStack stack = this.getRenderedItem(entity);

		if(stack != null) {
			matrices.translate(0.5D, 0.35D, 0.5D);
			long ticks = entity.getWorld().getTime();
			double prev = (ticks * 9.0D) % 360.0D;
			double next = (prev + 9.0D) % 360.0D;

			matrices.scale(1.5F, 1.5F, 1.5F);
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)MathHelper.lerp(tickDelta, prev, next) - 10.0F));
			MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
		}

		matrices.pop();
	}

	private ItemStack getRenderedItem(SpawnerBlockEntity entity) {
		List<ItemStack> items = IntStream.range(0, entity.inventory.size())
				.mapToObj(i -> entity.inventory.getStack(i))
				.filter(stack -> !stack.isEmpty())
				.collect(Collectors.toList());

		if(items.size() == 0)return null;
		int i = (int)((entity.getWorld().getTime() / 40) % items.size());
		return items.get(i).copy();
	}

	public static Color getColorFor(int weight) {
		Color color = null;

		for(Map.Entry<Integer, Color> entry: COLOR_PER_WEIGHT.entrySet()) {
			if(entry.getKey() > weight)break;
			color = entry.getValue();
		}

		return color;
	}

}
