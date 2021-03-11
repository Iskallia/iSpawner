package iskallia.ispawner.net.packet;

import iskallia.ispawner.ISpawner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.io.*;

public abstract class ModPacket<T extends PacketListener> implements Packet<T> {

	public abstract void onReceived(T listener);

	public void writeNBT(PacketByteBuf buf, CompoundTag nbt) throws IOException {
		NbtIo.write(nbt, new DataOutputStream(new OutputStream() {
			@Override
			public void write(int b) {
				buf.writeByte(b);
			}
		}));
	}

	public CompoundTag readNBT(PacketByteBuf buf) throws IOException {
		return NbtIo.read(new DataInputStream(new InputStream() {
			@Override
			public int read() {
				return buf.readByte();
			}
		}));
	}

	@Override
	public void apply(T listener) {
		if(listener instanceof ServerPlayNetworkHandler) {
			ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler)listener;
			MinecraftServer server = handler.player.getServer();

			if(server != null && !server.isOnThread()) {
				server.execute(() -> {
					if(listener.getConnection().isOpen()) {
						this.apply(listener);
						return;
					}

					ISpawner.LOGGER.debug("Ignoring packet server-side due to disconnection: " + this);
				});

				throw OffThreadException.INSTANCE;
			}
		} else if(listener instanceof ClientPlayNetworkHandler) {
			if(!MinecraftClient.getInstance().isOnThread()) {
				MinecraftClient.getInstance().execute(() -> {
					if(listener.getConnection().isOpen()) {
						this.apply(listener);
						return;
					}

					ISpawner.LOGGER.debug("Ignoring packet client-side due to disconnection: " + this);
				});

				throw OffThreadException.INSTANCE;
			}
		}

		this.onReceived(listener);
	}

}
