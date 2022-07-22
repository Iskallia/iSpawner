package iskallia.ispawner.screen;

import iskallia.ispawner.init.ModNetwork;
import iskallia.ispawner.net.packet.UpdateControllerC2SPacket;
import iskallia.ispawner.world.spawner.SpawnerController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class SpawnerControllerScreen extends Screen {

	private final SpawnerController controller;
	private final Map<SpawnerController.Mode, ButtonWidget> modeButtons = new HashMap<>();

	public SpawnerControllerScreen(Text title, SpawnerController controller) {
		super(title);
		this.controller = controller;
	}

	public SpawnerController getController() {
		return this.controller;
	}

	@Override
	protected void init() {
		int yOffset = -SpawnerController.Mode.values().length * 20 / 2;

		for(int i = 0; i < SpawnerController.Mode.values().length; i++) {
			SpawnerController.Mode mode = SpawnerController.Mode.values()[i];

			this.modeButtons.put(mode, this.addCenteredButton(0, i * 20 + yOffset, 160, 20, mode.instruction,
					button -> {
						this.controller.setMode(mode);
						ModNetwork.CHANNEL.sendToServer(new UpdateControllerC2SPacket(this.getController()));
						this.updateModeButtons();
					}));
		}

		this.updateModeButtons();
		super.init();
	}

	private void updateModeButtons() {
		this.modeButtons.values().forEach(button -> button.active = true);
		this.modeButtons.get(this.getController().getMode()).active = false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	public ButtonWidget addCenteredButton(int x, int y, int width, int height, String text, ButtonWidget.PressAction action) {
		return this.addDrawable(new ButtonWidget(this.width / 2 - width / 2 + x, this.height / 2 - height / 2 + y,
				width, height, new LiteralText(text), action));
	}

}
