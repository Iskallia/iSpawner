package iskallia.ispawner.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

public abstract class BaseBlockEntity extends BlockEntity {

	public BaseBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	public abstract CompoundTag write(CompoundTag tag, UpdateType type);

	public abstract void read(BlockState state, CompoundTag tag, UpdateType type);

	@Override
	public final CompoundTag toTag(CompoundTag tag) {
		return this.write(super.toTag(tag), UpdateType.SERVER);
	}

	@Override
	public final void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.read(state, tag, UpdateType.SERVER);
	}

	@Override
	public final BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.getPos(), 127, this.write(new CompoundTag(), UpdateType.PACKET));
	}

	public void sendClientUpdates() {
		this.markDirty();

		if(this.getWorld() != null) {
			this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
			this.getWorld().updateNeighbors(this.getPos(), this.getCachedState().getBlock());
		}
	}

	public enum UpdateType {
		SERVER, PACKET
	}

}
