package iskallia.ispawner.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.ispawner.screen.handler.SurvivalSpawnerScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SurvivalSpawnerScreen extends HandledScreen<SurvivalSpawnerScreenHandler> {

	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");

	public SurvivalSpawnerScreen(SurvivalSpawnerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		this.passEvents = false;
		this.backgroundHeight = 114 + 3 * 18;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		this.textRenderer.draw(matrices, this.title, (float)this.titleX + 110, (float)this.titleY, 4210752);
		this.textRenderer.draw(matrices, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX + 110, (float)this.playerInventoryTitleY, 4210752);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i + 110, j, 0, 0, this.backgroundWidth, 3 * 18 + 17);
		this.drawTexture(matrices, i + 110, j + 3 * 18 + 17, 0, 126, this.backgroundWidth, 96);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}
