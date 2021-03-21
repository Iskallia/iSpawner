package iskallia.ispawner.init;

import iskallia.ispawner.ISpawner;
import iskallia.ispawner.net.packet.ModPacket;
import iskallia.ispawner.net.packet.UpdateControllerC2SPacket;
import iskallia.ispawner.net.packet.UpdateSettingsC2SPacket;
import iskallia.ispawner.net.packet.UpdateSettingsS2CPacket;
import me.shedaniel.architectury.networking.NetworkChannel;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModNetwork extends ModRegistries {

	public static final NetworkChannel CHANNEL = NetworkChannel.create(ISpawner.id("network"));

	public static void register() {
		register(UpdateControllerC2SPacket.class, UpdateControllerC2SPacket::new, SERVER_PLAY);
		register(UpdateSettingsC2SPacket.class, UpdateSettingsC2SPacket::new, SERVER_PLAY);
		register(UpdateSettingsS2CPacket.class, UpdateSettingsS2CPacket::new, CLIENT_PLAY);
	}

	public static final Function<NetworkManager.PacketContext, ClientPlayNetworkHandler> CLIENT_PLAY = context -> MinecraftClient.getInstance().getNetworkHandler();
	public static final Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> SERVER_PLAY = context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;

	public static <R extends PacketListener, T extends ModPacket<R>> void register(Class<T> type, Supplier<T> packetSupplier,
	                                                                               Function<NetworkManager.PacketContext, R> contextMapper) {
		CHANNEL.register(type, (packet, buf) -> {
			try { packet.write(buf); }
			catch(IOException e) { e.printStackTrace(); }
		}, buf -> {
			T packet = packetSupplier.get();
			try { packet.read(buf); }
			catch(IOException e) { e.printStackTrace(); }
			return packet;
		}, (packet, contextSupplier) -> {
			packet.apply(contextMapper.apply(contextSupplier.get()));
		});
	}

}
