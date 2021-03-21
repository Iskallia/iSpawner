package iskallia.ispawner.net.packet;

import iskallia.ispawner.init.ModItems;
import iskallia.ispawner.world.spawner.SpawnerController;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class UpdateControllerC2SPacket implements ModPacket<ServerPlayNetworkHandler> {

	protected BlockPos target;
	protected SpawnerController.Mode mode;

	public UpdateControllerC2SPacket() {

	}

	public UpdateControllerC2SPacket(SpawnerController controller) {
		this.target = controller.getTarget().orElse(null);
		this.mode = controller.getMode();
	}

	public BlockPos getTarget() {
		return this.target;
	}

	public SpawnerController.Mode getMode() {
		return this.mode;
	}

	@Override
	public void read(PacketByteBuf buf) {
		this.target = buf.readBoolean() ? BlockPos.fromLong(buf.readLong()) : null;
		this.mode = SpawnerController.Mode.values()[buf.readVarInt()];
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.target != null);
		if(this.target != null)buf.writeLong(this.target.asLong());
		buf.writeVarInt(this.mode.ordinal());
	}

	@Override
	public void onReceived(ServerPlayNetworkHandler listener) {
		ItemStack stack = listener.player.getStackInHand(Hand.MAIN_HAND);
		if(stack.getItem() != ModItems.SPAWNER_CONTROLLER)return;

		SpawnerController controller = new SpawnerController(stack.getOrCreateSubTag("Controller"));
		controller.setTarget(this.getTarget());
		controller.setMode(this.getMode());
	}

}
