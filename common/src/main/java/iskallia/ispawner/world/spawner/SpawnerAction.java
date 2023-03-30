package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.item.GenericSpawnEggItem;
import iskallia.ispawner.nbt.INBTSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

import static net.minecraft.entity.EntityType.ENTITY_TAG_KEY;

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

	public boolean execute(World world, ItemStack stack, SpawnerContext context) {
		if(stack.getItem() instanceof ThrowablePotionItem) {
			return this.applyPotionOverride(world, stack, context);
		} else if(stack.getItem() instanceof SpawnEggItem || stack.getItem() instanceof GenericSpawnEggItem) {
			return this.applyEggOverride(world, stack, context);
		} else if(context.getExecution() == SpawnerExecution.USE) {
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

	public boolean applyPotionOverride(World world, ItemStack stack, SpawnerContext context) {
		if(context.getExecution() != SpawnerExecution.USE) return false;
		PotionEntity potion = new PotionEntity(world, this.getHitPos().getX(), this.getHitPos().getY(), this.getHitPos().getZ());
		potion.setItem(stack);
		world.spawnEntity(potion);
		return true;
	}

	public boolean applyEggOverride(World world, ItemStack stack, SpawnerContext context) {
		BlockState state = world.getBlockState(this.getPos());
		EntityType<?> type = stack.getItem() instanceof SpawnEggItem egg
			? egg.getEntityType(stack.getNbt())
			: GenericSpawnEggItem.getType(stack);

		if(context.getExecution() == SpawnerExecution.USE && state.isOf(Blocks.SPAWNER)) {
			BlockEntity blockEntity = world.getBlockEntity(this.getPos());

			if(blockEntity instanceof MobSpawnerBlockEntity spawner) {
				if(type != null) {
					spawner.getLogic().setEntityId(type);
					spawner.markDirty();
					world.updateListeners(this.getPos(), state, state, 3);
					stack.decrement(1);
				}

				return true;
			}
		}

		BlockPos pos;

		if(state.getCollisionShape(world, this.getPos()).isEmpty()) {
			pos = this.getPos();
		} else {
			pos = this.getPos().offset(this.getSide());
		}

		stack.getOrCreateSubNbt(ENTITY_TAG_KEY).put("Spawner", context.getEntity().createNbt());

		Entity entity = create(type, (ServerWorld)world, stack.getNbt(), stack.hasCustomName() ? stack.getName() : null,
			null, pos, SpawnReason.SPAWN_EGG, true,
			!Objects.equals(this.getPos(), pos) && this.getSide() == Direction.UP);

		if(entity != null && !world.isSpaceEmpty(entity.getBoundingBox())) {
			entity = null;
		}

		if(entity != null) {
			((ServerWorld)world).spawnEntityAndPassengers(entity);
			stack.decrement(1);
			return true;
		}

		return false;
	}

	@Nullable
	public <T extends Entity> T create(EntityType<T> type, ServerWorld world, @Nullable NbtCompound itemNbt, @Nullable Text name, @Nullable PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY) {
		if(type == null) return null;
		T entity = type.create(world);
		if(entity == null) return null;

		double offsetY;

		if(alignPosition) {
			entity.setPosition(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 1.0D);
			offsetY = getOriginY(world, pos, invertY, entity.getBoundingBox());
		} else {
			offsetY = 0.0;
		}

		entity.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY() + offsetY, pos.getZ() + 0.5D,
			MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);

		if(entity instanceof MobEntity mob) {
			mob.headYaw = mob.getYaw();
			mob.bodyYaw = mob.getYaw();
			mob.initialize(world, world.getLocalDifficulty(mob.getBlockPos()), spawnReason, null, itemNbt);
		}

		if(name != null && entity instanceof LivingEntity) {
			entity.setCustomName(name);
		}

		EntityType.loadFromEntityNbt(world, player, entity, itemNbt);
		return entity;
	}

	protected static double getOriginY(WorldView world, BlockPos pos, boolean invertY, Box boundingBox) {
		Box box = new Box(pos);

		if(invertY) {
			box = box.stretch(0.0D, -1.0D, 0.0D);
		}

		Iterable<VoxelShape> iterable = world.getCollisions(null, box);
		return 1.0D + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, iterable, invertY ? -2.0D : -1.0D);
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
