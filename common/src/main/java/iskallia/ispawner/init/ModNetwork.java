package iskallia.ispawner.init;

import iskallia.ispawner.ISpawner;
import iskallia.ispawner.net.packet.*;
import me.shedaniel.architectury.networking.NetworkChannel;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.utils.Env;
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
		if(Platform.getEnvironment() == Env.CLIENT) {
			Client.register();
		} else {
			Server.register();
		}
	}

	public static Function<NetworkManager.PacketContext, ClientPlayNetworkHandler> getClient() {
		return context -> MinecraftClient.getInstance().getNetworkHandler();
	}

	public static Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> getServer() {
		return context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;
	}

	public static class Client {
		public static final Function<NetworkManager.PacketContext, ClientPlayNetworkHandler> CLIENT_PLAY = context -> MinecraftClient.getInstance().getNetworkHandler();
		public static final Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> SERVER_PLAY = context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;

		public static void register() {
			ModNetwork.register(UpdateSettingsS2CPacket.class, UpdateSettingsS2CPacket::new, CLIENT_PLAY);
			ModNetwork.register(UpdateControllerC2SPacket.class, UpdateControllerC2SPacket::new, SERVER_PLAY);
			ModNetwork.register(UpdateSettingsC2SPacket.class, UpdateSettingsC2SPacket::new , SERVER_PLAY);
			ModNetwork.register(UpdateRedstoneModeC2SPacket.class, UpdateRedstoneModeC2SPacket::new, SERVER_PLAY);
		}
	}

	public static class Server {
		public static final Function<NetworkManager.PacketContext, ServerPlayNetworkHandler> SERVER_PLAY = context -> ((ServerPlayerEntity)context.getPlayer()).networkHandler;

		public static void register() {
			ModNetwork.register(UpdateSettingsS2CPacket.class, UpdateSettingsS2CPacket::new, null);
			ModNetwork.register(UpdateControllerC2SPacket.class, UpdateControllerC2SPacket::new, SERVER_PLAY);
			ModNetwork.register(UpdateSettingsC2SPacket.class, UpdateSettingsC2SPacket::new , SERVER_PLAY);
			ModNetwork.register(UpdateRedstoneModeC2SPacket.class, UpdateRedstoneModeC2SPacket::new, SERVER_PLAY);
		}
	}

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
			if(contextMapper != null) {
				packet.apply(contextMapper.apply(contextSupplier.get()));
			}
		});
	}

}
