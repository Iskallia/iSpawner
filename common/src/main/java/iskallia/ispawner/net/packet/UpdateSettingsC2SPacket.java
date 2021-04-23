package iskallia.ispawner.net.packet;

import iskallia.ispawner.screen.handler.SpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class UpdateSettingsC2SPacket implements ModPacket<ServerPlayNetworkHandler> {

	protected SpawnerSettings settings;

	public UpdateSettingsC2SPacket() {

	}

	public UpdateSettingsC2SPacket(SpawnerSettings settings) {
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
	public void onReceived(ServerPlayNetworkHandler listener) {
		if(listener.player.currentScreenHandler instanceof SpawnerScreenHandler) {
			SpawnerScreenHandler screen = (SpawnerScreenHandler) listener.player.currentScreenHandler;
			screen.getSpawner().manager.settings = this.getSettings();
			screen.getSpawner().sendClientUpdates();
		}
	}

}