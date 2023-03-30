package iskallia.ispawner.item;

import iskallia.ispawner.init.ModConfigs;
import iskallia.ispawner.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;

import static net.minecraft.entity.EntityType.ENTITY_TAG_KEY;

public class GenericSpawnEggItem extends Item {

	public GenericSpawnEggItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();

		if(!(world instanceof ServerWorld)) {
			return ActionResult.SUCCESS;
		}

		ItemStack stack = context.getStack();
		BlockPos pos = context.getBlockPos();
		Direction direction = context.getSide();
		BlockState state = world.getBlockState(pos);

		if(state.isOf(Blocks.SPAWNER) && world.getBlockEntity(pos) instanceof MobSpawnerBlockEntity spawner) {
			EntityType<?> type = getType(stack);

			if(type != null) {
				spawner.getLogic().setEntityId(type);
				spawner.markDirty();
				world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
				stack.decrement(1);
			}

			return ActionResult.CONSUME;
		}

		BlockPos offsetPos = state.getCollisionShape(world, pos).isEmpty() ? pos : pos.offset(direction);
		EntityType<?> type = getType(stack);

		if(type == null || type.spawnFromItemStack((ServerWorld)world, stack, context.getPlayer(), offsetPos,
			SpawnReason.SPAWN_EGG, true, !Objects.equals(pos, offsetPos) && direction == Direction.UP) != null) {
			stack.decrement(1);
			world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
		}

		return ActionResult.CONSUME;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		BlockHitResult hitResult = SpawnEggItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);

		if(hitResult.getType() != HitResult.Type.BLOCK) {
			return TypedActionResult.pass(stack);
		}

		if(!(world instanceof ServerWorld)) {
			return TypedActionResult.success(stack);
		}

		BlockPos pos = hitResult.getBlockPos();

		if(!(world.getBlockState(pos).getBlock() instanceof FluidBlock)) {
			return TypedActionResult.pass(stack);
		}

		if(world.canPlayerModifyAt(user, pos) && user.canPlaceOn(pos, hitResult.getSide(), stack)) {
			EntityType<?> type = getType(stack);

			if(type == null || type.spawnFromItemStack((ServerWorld)world, stack, user, pos,
				SpawnReason.SPAWN_EGG, false, false) == null) {
				return TypedActionResult.pass(stack);
			}

			if(!user.getAbilities().creativeMode) {
				stack.decrement(1);
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			world.emitGameEvent(GameEvent.ENTITY_PLACE, user);
			return TypedActionResult.consume(stack);
		}

		return TypedActionResult.fail(stack);
	}

	@Override
	public Text getName(ItemStack stack) {
		EntityType<?> type = getType(stack);

		if(type != null) {
			return type.getName().copy().append(new LiteralText(" ")).append(super.getName(stack));
		}

		return super.getName(stack);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if(!this.isIn(group)) return;

		for(EntityType<?> type : Registry.ENTITY_TYPE) {
			stacks.add(of(type));
		}
	}

	public static int getColor(ItemStack stack, int tintIndex) {
		return ModConfigs.SPAWN_EGG.getColor(getType(stack), tintIndex);
	}

	public static EntityType<?> getType(ItemStack stack) {
		Identifier id = getTypeId(stack);
		return id == null ? null : EntityType.get(id.toString()).orElse(null);
	}

	public static Identifier getTypeId(ItemStack stack) {
		NbtCompound nbt = stack.getNbt();
		if(nbt == null) return null;
		nbt = nbt.getCompound(ENTITY_TAG_KEY);

		if(nbt.contains("id", NbtElement.STRING_TYPE)) {
			return Identifier.tryParse(nbt.getString("id"));
		}

		return null;
	}

	public static ItemStack of(EntityType<?> type) {
		ItemStack stack = new ItemStack(ModItems.SPAWN_EGG.get());
		stack.getOrCreateSubNbt(ENTITY_TAG_KEY).putString("id", EntityType.getId(type).toString());
		return stack;
	}

}
