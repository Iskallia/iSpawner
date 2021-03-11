package iskallia.ispawner.mixin;

import iskallia.ispawner.block.entity.BaseBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

	@Shadow private ClientWorld world;

	@Inject(method = "onBlockEntityUpdate",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V",
					shift = At.Shift.AFTER),
			cancellable = true)
	private void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
		BlockState state = this.world.getBlockState(packet.getPos());
		BlockEntity blockEntity = this.world.getBlockEntity(packet.getPos());

		if(blockEntity instanceof BaseBlockEntity) {
			((BaseBlockEntity)blockEntity).read(state, packet.getCompoundTag(), BaseBlockEntity.UpdateType.UPDATE_PACKET);
			ci.cancel();
		}
	}

}
