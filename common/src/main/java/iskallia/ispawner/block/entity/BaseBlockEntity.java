package iskallia.ispawner.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public abstract class BaseBlockEntity extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public abstract NbtCompound write(NbtCompound tag, UpdateType type);

	public abstract void read(NbtCompound tag, UpdateType type);

	@Override
	public final void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		this.write(tag, UpdateType.SERVER);
	}

	@Override
	public final void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		this.read(tag, UpdateType.SERVER);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.write(super.toInitialChunkDataNbt(), UpdateType.INITIAL_PACKET);
	}

	@Override
	public final BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this, e -> ((BaseBlockEntity)e).write(new NbtCompound(), UpdateType.UPDATE_PACKET));
	}

	public void sendClientUpdates() {
		this.markDirty();

		if(this.getWorld() != null) {
			this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
			this.getWorld().updateNeighbors(this.getPos(), this.getCachedState().getBlock());
		}
	}

	public enum UpdateType {
		SERVER, INITIAL_PACKET, UPDATE_PACKET
	}

}
