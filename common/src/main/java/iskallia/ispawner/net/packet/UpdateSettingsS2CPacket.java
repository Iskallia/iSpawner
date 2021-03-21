package iskallia.ispawner.net.packet;

import iskallia.ispawner.screen.SpawnerScreen;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class UpdateSettingsS2CPacket implements ModPacket<ClientPlayNetworkHandler> {

	protected SpawnerSettings settings;

	public UpdateSettingsS2CPacket() {

	}

	public UpdateSettingsS2CPacket(SpawnerSettings settings) {
		this.settings = settings;
	}

	public SpawnerSettings getSettings() {
		return this.settings;
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.settings = new SpawnerSettings().readFromBuf(buf);
	}

	@Override
	public void write(PacketByteBuf buf) {
		this.settings.writeToBuf(buf);
	}

	@Override
	public void onReceived(ClientPlayNetworkHandler listener) {
		if(MinecraftClient.getInstance().currentScreen instanceof SpawnerScreen) {
			SpawnerScreen screen = (SpawnerScreen)MinecraftClient.getInstance().currentScreen;
			screen.setSettings(this.getSettings());
		}
	}

}
