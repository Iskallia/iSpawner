package iskallia.ispawner.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.ispawner.init.ModNetwork;
import iskallia.ispawner.net.packet.UpdateSettingsC2SPacket;
import iskallia.ispawner.screen.handler.SpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

public class SpawnerScreen extends HandledScreen<SpawnerScreenHandler> {

	private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");

	protected SpawnerSettings settings;
	private Slider attemptsSlider;
	private TextFieldWidget spawnDelayTextField;
	private ButtonWidget modeButton;
	private Map<SpawnGroup, Slider> spawnGroupTextFields = new HashMap<>();
	private Slider checkRangeSlider;
	private Slider playerRangeSlider;

	public SpawnerScreen(SpawnerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		this.passEvents = false;
		this.backgroundHeight = 114 + 3 * 18;
		this.playerInventoryTitleY = this.backgroundHeight - 94;
	}

	public SpawnerSettings getSettings() {
		return this.settings;
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
		this.textRenderer.draw(matrices, this.playerInventoryTitle, (float)this.playerInventoryTitleX + 110, (float)this.playerInventoryTitleY, 4210752);
	}

	@Override
	protected void init() {
		super.init();

		int offset = 2;

		this.attemptsSlider = this.addDrawableChild(new Slider(40, offset, 160, 20, LiteralText.EMPTY,
				0.0D, "Attempts: ", 0.0D, 32.0D, i -> this.settings.setAttempts(i)));

		this.spawnDelayTextField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
				40, offset + 25, 160, 20, LiteralText.EMPTY);

		this.spawnDelayTextField.setChangedListener(text -> {
			if(SpawnerScreen.this.settings != null && !text.isEmpty()) {
				try {
					int delay = Integer.parseInt(text.trim());
					SpawnerScreen.this.settings.setSpawnDelay(delay);
					SpawnerScreen.this.sendSettings();
				} catch(NumberFormatException ignored) {
				}
			}
		});

		this.addDrawableChild(this.spawnDelayTextField);

		this.modeButton = this.addDrawableChild(new ButtonWidget(40, offset + 50, 160, 20, new LiteralText(SpawnerSettings.Mode.values()[0].text),
				button -> {
					if(SpawnerScreen.this.settings != null) {
						SpawnerSettings.Mode[] modes = SpawnerSettings.Mode.values();
						this.settings.setMode(modes[(this.settings.getMode().ordinal() + 1) % modes.length]);
						button.setMessage(new LiteralText(this.settings.getMode().text));
						this.sendSettings();
					}
				}));

		for(int i = 0; i < SpawnGroup.values().length; i++) {
			SpawnGroup spawnGroup = SpawnGroup.values()[i];

			String[] parts = spawnGroup.getName().split(Pattern.quote("_"));

			for(int j = 0; j < parts.length; j++) {
				parts[j] = Character.toUpperCase(parts[j].charAt(0)) + parts[j].substring(1);
			}

			String prefix = String.join(" ", parts);

			Slider spawnField = this.addDrawableChild(new Slider(40, offset + 80 + i * 20, 160, 20, LiteralText.EMPTY,
					0.0D, prefix + " Cap: ", 0.0D, 32.0D, j -> {
				this.settings.getCapRestrictions().get(spawnGroup).limit = j;
			}));

			this.spawnGroupTextFields.put(spawnGroup, spawnField);
		}

		this.checkRangeSlider = this.addDrawableChild(new Slider(40, offset + 80 + SpawnGroup.values().length * 20 + 10, 160, 20, LiteralText.EMPTY,
			0.0D, "Check Radius: ", 0.0D, 128.0D, i -> this.settings.setCheckRadius(i)));

		this.playerRangeSlider = this.addDrawableChild(new Slider(40, offset + 80 + SpawnGroup.values().length * 20 + 30, 160, 20, LiteralText.EMPTY,
			0.0D, "Player Radius: ", 0.0D, 128.0D, i -> this.settings.setPlayerRadius(i)));
	}

	public void setSettings(SpawnerSettings settings) {
		this.settings = settings;
		this.attemptsSlider.setIntValue(settings.getAttempts());

		if(this.settings.getSpawnDelay() >= 0) {
			this.spawnDelayTextField.setText(String.valueOf(this.settings.getSpawnDelay()));
		}

		this.modeButton.setMessage(new LiteralText(this.settings.getMode().text));

		this.spawnGroupTextFields.forEach((spawnGroup, textField) -> {
			if(settings.getCapRestrictions().get(spawnGroup).limit > 0) {
				textField.setIntValue(settings.getCapRestrictions().get(spawnGroup).limit);
			}
		});

		this.checkRangeSlider.setIntValue(settings.getCheckRadius());
		this.playerRangeSlider.setIntValue(settings.getPlayerRadius());
	}

	private void sendSettings() {
		if(this.settings != null) {
			ModNetwork.CHANNEL.sendToServer(new UpdateSettingsC2SPacket(this.getSettings()));
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (this.width - this.backgroundWidth) / 2;
		int j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i + 110, j, 0, 0, this.backgroundWidth, 3 * 18 + 17);
		this.drawTexture(matrices, i + 110, j + 3 * 18 + 17, 0, 126, this.backgroundWidth, 96);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	public class Slider extends SliderWidget {
		private final String prefix;
		private final double min;
		private final double max;
		private final IntConsumer onChanged;

		public Slider(int x, int y, int width, int height, Text text, double value, String prefix, double min, double max, IntConsumer onChanged) {
			super(x, y, width, height, text, value);
			this.prefix = prefix;
			this.min = min;
			this.max = max;
			this.onChanged = onChanged;
			this.updateMessage();
		}

		public int getIntValue() {
			return MathHelper.floor(MathHelper.clampedLerp(this.min, this.max, this.value));
		}

		@Override
		protected void updateMessage() {
			this.setMessage(new LiteralText(this.prefix + this.getIntValue()));
		}

		@Override
		protected void applyValue() {
			if(SpawnerScreen.this.settings != null) {
				this.onChanged.accept(this.getIntValue());
				SpawnerScreen.this.sendSettings();
			}
		}

		public void setIntValue(int attempts) {
			this.value = attempts / (this.max - this.min) + this.min;
			this.updateMessage();
		}
	}

}
