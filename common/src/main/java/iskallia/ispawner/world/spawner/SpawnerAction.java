package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.nbt.INBTSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Objects;

public class SpawnerAction implements INBTSerializable<NbtCompound> {

	protected BlockPos pos;
	protected Direction side;
	protected Vec3d hitPos;
	protected Hand hand;
	protected Direction[] directions;

	public SpawnerAction() {

	}

	public SpawnerAction(BlockPos pos, Direction side, Vec3d hitPos, Hand hand, Direction[] directions) {
		this.pos = pos;
		this.side = side;
		this.hitPos = hitPos;
		this.hand = hand;
		this.directions = directions;
	}

	public SpawnerAction toAbsolute(BlockPos pos, BlockRotation rotation, BlockMirror mirror) {
		return new SpawnerAction(
				SpawnerBlockEntity.mirror(this.getPos().rotate(rotation), mirror).add(pos),
				SpawnerBlockEntity.mirror(rotation.rotate(this.getSide()), mirror),
				SpawnerBlockEntity.mirror(rotate(rotation, this.getHitPos()), mirror).add(pos.getX(), pos.getY(), pos.getZ()),
				this.getHand(), this.getDirections());
	}

	public static Vec3d rotate(BlockRotation rotation, Vec3d position) {
		switch(rotation) {
			case NONE:
			default:
				return position;
			case CLOCKWISE_90:
				return new Vec3d(-position.getZ(), position.getY(), position.getX());
			case CLOCKWISE_180:
				return new Vec3d(-position.getX(), position.getY(), -position.getZ());
			case COUNTERCLOCKWISE_90:
				return new Vec3d(position.getZ(), position.getY(), -position.getX());
		}
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

	public Direction[] getDirections() {
		return this.directions;
	}

	public boolean execute(World world, ItemStack stack) {
		if(stack.getItem() instanceof ThrowablePotionItem) {
			return this.applyPotionOverride(world, stack);
		} else if(stack.getItem() instanceof SpawnEggItem) {
			return this.applyEggOverride(world, stack);
		} else {
			stack.useOnBlock(new SpawnerUsageContext(world, stack, this));
		}

		return true;
	}

	@Override
	public NbtCompound writeToNBT() {
		NbtCompound nbt = new NbtCompound();
		nbt.putLong("Pos", this.getPos().asLong());
		nbt.putInt("Side", this.getSide().ordinal());
		nbt.putDouble("HitPosX", this.getHitPos().x);
		nbt.putDouble("HitPosY", this.getHitPos().y);
		nbt.putDouble("HitPosZ", this.getHitPos().z);
		nbt.putInt("Hand", this.getHand().ordinal());
		nbt.putIntArray("Directions", Arrays.stream(this.getDirections()).mapToInt(Enum::ordinal).toArray());
		return nbt;
	}

	@Override
	public void readFromNBT(NbtCompound nbt) {
		this.pos = BlockPos.fromLong(nbt.getLong("Pos"));
		this.side = Direction.values()[nbt.getInt("Side")];
		this.hitPos = new Vec3d(nbt.getDouble("HitPosX"), nbt.getDouble("HitPosY"), nbt.getDouble("HitPosZ"));
		this.hand = Hand.values()[nbt.getInt("Hand")];
		this.directions = Arrays.stream(nbt.getIntArray("Directions")).mapToObj(i -> Direction.values()[i]).toArray(Direction[]::new);
	}

	public boolean applyPotionOverride(World world, ItemStack stack) {
		PotionEntity potion = new PotionEntity(world, this.getHitPos().getX(), this.getHitPos().getY(), this.getHitPos().getZ());
		potion.setItem(stack);
		world.spawnEntity(potion);
		return true;
	}

	public boolean applyEggOverride(World world, ItemStack stack) {
		BlockState blockState = world.getBlockState(this.getPos());
		EntityType<?> entityType = ((SpawnEggItem)stack.getItem()).getEntityType(stack.getNbt());

		if(blockState.isOf(Blocks.SPAWNER)) {
			BlockEntity blockEntity = world.getBlockEntity(this.getPos());

			if(blockEntity instanceof MobSpawnerBlockEntity) {
				MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
				mobSpawnerLogic.setEntityId(entityType);
				blockEntity.markDirty();
				world.updateListeners(this.getPos(), blockState, blockState, 3);
				stack.decrement(1);
				return true;
			}
		}

		BlockPos blockPos3;

		if(blockState.getCollisionShape(world, this.getPos()).isEmpty()) {
			blockPos3 = this.getPos();
		} else {
			blockPos3 = this.getPos().offset(this.getSide());
		}

		EntityType<?> entityType2 = ((SpawnEggItem)stack.getItem()).getEntityType(stack.getNbt());

		Entity entity = entityType2.create((ServerWorld)world, stack.getNbt(), stack.hasCustomName() ? stack.getName() : null, null, blockPos3,
			SpawnReason.SPAWN_EGG, true, !Objects.equals(this.getPos(), blockPos3) && this.getSide() == Direction.UP);

		boolean isMob = entity instanceof MobEntity;

		if(entity != null && (!world.isSpaceEmpty(entity.getBoundingBox()) || isMob && !((MobEntity)entity).canSpawn(world))) {
			entity = null;
		}

		if(entity != null) {
			((ServerWorld)world).spawnEntityAndPassengers(entity);
			stack.decrement(1);
			return true;
		}

		return false;
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
