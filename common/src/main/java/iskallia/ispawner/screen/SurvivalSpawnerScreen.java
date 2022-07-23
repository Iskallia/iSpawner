package iskallia.ispawner.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.ispawner.ISpawner;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import iskallia.ispawner.init.ModNetwork;
import iskallia.ispawner.net.packet.UpdateRedstoneModeC2SPacket;
import iskallia.ispawner.screen.handler.SurvivalSpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class SurvivalSpawnerScreen extends HandledScreen<SurvivalSpawnerScreenHandler> {

	private static final Identifier TEXTURE = ISpawner.id("textures/gui/survival_spawner.png");
	private SpawnerSettings.Mode lastKnownMode = null;

	public SurvivalSpawnerScreen(SurvivalSpawnerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		this.passEvents = false;
		this.backgroundWidth = 176;
		this.backgroundHeight = 241;
	}

	@Override
	protected void init() {
		super.init();

		this.refreshButtons();
	}

	private void refreshButtons() {
		this.addDrawableChild(new TexturedButtonWidget(this.x + 46, this.y + 123, 22, 22,
				212, this.lastKnownMode == SpawnerSettings.Mode.ALWAYS_ON ? 66 : 88, this.lastKnownMode == SpawnerSettings.Mode.ALWAYS_ON ? 44 : 22,
				TEXTURE, 256, 256, btn -> {
			if (this.lastKnownMode != SpawnerSettings.Mode.ALWAYS_ON) {
				ModNetwork.CHANNEL.sendToServer(new UpdateRedstoneModeC2SPacket(SpawnerSettings.Mode.ALWAYS_ON));
			}
		}, (btn, matrices, mouseX, mouseY) -> {
			this.renderTooltip(matrices, new LiteralText(SpawnerSettings.Mode.ALWAYS_ON.text), mouseX, mouseY);
		}, LiteralText.EMPTY));
		this.addDrawableChild(new TexturedButtonWidget(this.x + 70, this.y + 123, 22, 22,
				234, this.lastKnownMode == SpawnerSettings.Mode.REDSTONE_ON ? 66 : 88, this.lastKnownMode == SpawnerSettings.Mode.REDSTONE_ON ? 44 : 22,
				TEXTURE, 256, 256, btn -> {
			if (this.lastKnownMode != SpawnerSettings.Mode.REDSTONE_ON) {
				ModNetwork.CHANNEL.sendToServer(new UpdateRedstoneModeC2SPacket(SpawnerSettings.Mode.REDSTONE_ON));
			}
		}, (btn, matrices, mouseX, mouseY) -> {
			this.renderTooltip(matrices, new LiteralText(SpawnerSettings.Mode.REDSTONE_ON.text), mouseX, mouseY);
		}, LiteralText.EMPTY));
	}

	@Override
	public void handledScreenTick() {
		super.handledScreenTick();

		SurvivalSpawnerBlockEntity spawner = this.getScreenHandler().getSpawner();

		if(spawner != null) {
			SpawnerSettings.Mode redstoneMode = spawner.manager.settings.getMode();

			if(redstoneMode != this.lastKnownMode) {
				this.lastKnownMode = redstoneMode;
				this.refreshButtons();
			}
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		this.textRenderer.draw(matrices, this.title, this.titleX + 38, this.titleY, 0x404040);

		EntityType<?> spawningEntity = this.getScreenHandler().getSpawningEntity();
		int charges = this.getScreenHandler().getSpawnerCharges();

		if(spawningEntity != null && charges > 0) {
			EntityDimensions size = spawningEntity.getDimensions();
			float scale = 1F / (Math.max(size.height, size.width) / 1.4F) * 32F;

			matrices.push();
			matrices.translate(85, 80, 0);
			matrices.scale(1, 1, -1);
			matrices.translate(0, 0, -200);
			matrices.scale(scale, scale, scale);

			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(30));
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(135));
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));

			try {
				this.renderSpawningEntity(matrices, spawningEntity);
			} catch(Exception ignored) {}

			matrices.pop();

			matrices.push();
			matrices.translate(0, 0, 400);
			this.textRenderer.draw(matrices, spawningEntity.getName(), 75, 99, 0x404040);
			this.textRenderer.draw(matrices, new LiteralText("Charges: " + charges), 75, 109, 0x404040);
			matrices.pop();
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (this.width - this.backgroundWidth) / 2;
		int y = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private void renderSpawningEntity(MatrixStack renderStack, EntityType<?> entityType) {
		EntityRenderDispatcher entityRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher();
		Entity entity = entityType.create(MinecraftClient.getInstance().world);

		if(entity instanceof LivingEntity) {
			((LivingEntity)entity).headYaw = 0;
		}

		entityRenderer.setRenderShadows(false);
		DiffuseLighting.disableGuiDepthLighting();

		VertexConsumerProvider.Immediate renderBuffers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		entityRenderer.render(entity, 0, 0, 0, 0, 0F, renderStack, renderBuffers, 0xF000F0);
		renderBuffers.draw();

		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();

		DiffuseLighting.enableGuiDepthLighting();
		entityRenderer.setRenderShadows(true);
	}

}
