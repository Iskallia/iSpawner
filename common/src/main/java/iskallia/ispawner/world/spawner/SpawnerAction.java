package iskallia.ispawner.world.spawner;

import iskallia.ispawner.nbt.INBTSerializable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpawnerAction implements INBTSerializable<CompoundTag> {

	protected BlockPos pos;
	protected Direction side;
	protected Vec3d hitPos;
	protected Hand hand;

	public SpawnerAction() {

	}

	public SpawnerAction(BlockPos pos, Direction side, Vec3d hitPos, Hand hand) {
		this.pos = pos;
		this.side = side;
		this.hitPos = hitPos;
		this.hand = hand;
	}

	public SpawnerAction toAbsolute(BlockPos pos, BlockRotation rotation) {
		return new SpawnerAction(this.getPos().rotate(rotation).add(pos), rotation.rotate(this.getSide()), this.getHitPos(), this.getHand());
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Direction getSide() {
		return this.side;
	}

	public Vec3d getHitPos() {
		return this.hitPos;
	}

	public Hand getHand() {
		return this.hand;
	}

	public void execute(World world, ItemStack stack) {
		stack.useOnBlock(new SpawnerUsageContext(world, stack, this));
	}

	@Override
	public CompoundTag writeToNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putLong("Pos", this.getPos().asLong());
		nbt.putInt("Side", this.getSide().ordinal());
		nbt.putDouble("HitPosX", this.getHitPos().x);
		nbt.putDouble("HitPosY", this.getHitPos().y);
		nbt.putDouble("HitPosZ", this.getHitPos().z);
		nbt.putInt("Hand", this.getHand().ordinal());
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		this.pos = BlockPos.fromLong(nbt.getLong("Pos"));
		this.side = Direction.values()[nbt.getInt("Side")];
		this.hitPos = new Vec3d(nbt.getDouble("HitPosX"), nbt.getDouble("HitPosY"), nbt.getDouble("HitPosZ"));
		this.hand = Hand.values()[nbt.getInt("Hand")];
	}

	@Override
	public boolean equals(Object other) {
		if(this == other)return true;
		if(!(other instanceof SpawnerAction))return false;
		SpawnerAction action = (SpawnerAction)other;
		return this.getPos().equals(action.getPos()) && this.getSide() == action.getSide();
	}

	@Override
	public int hashCode() {
		return this.getSide().ordinal() * 31 + this.getPos().hashCode();
	}

}
