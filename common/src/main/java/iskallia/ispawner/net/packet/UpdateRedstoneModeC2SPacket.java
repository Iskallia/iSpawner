package iskallia.ispawner.net.packet;

import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import iskallia.ispawner.screen.handler.SurvivalSpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class UpdateRedstoneModeC2SPacket implements ModPacket<ServerPlayNetworkHandler> {

    private SpawnerSettings.Mode redstoneMode;

    public UpdateRedstoneModeC2SPacket(PacketByteBuf buf) {
        this.redstoneMode = buf.readEnumConstant(SpawnerSettings.Mode.class);
    }

    public UpdateRedstoneModeC2SPacket(SpawnerSettings.Mode redstoneMode) {
        this.redstoneMode = redstoneMode;
    }

    public SpawnerSettings.Mode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.redstoneMode);
    }

    @Override
    public void onReceived(ServerPlayNetworkHandler listener) {
        if (listener.player.currentScreenHandler instanceof SurvivalSpawnerScreenHandler) {
            SurvivalSpawnerScreenHandler screen = (SurvivalSpawnerScreenHandler) listener.player.currentScreenHandler;
            SurvivalSpawnerBlockEntity spawner = screen.getSpawner();
            if (spawner != null) {
                spawner.manager.settings.setMode(this.getRedstoneMode());
                spawner.sendClientUpdates();
            }
        }
    }

}
