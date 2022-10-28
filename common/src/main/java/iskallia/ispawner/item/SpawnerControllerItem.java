package iskallia.ispawner.item;

import iskallia.ispawner.block.SpawnerBlock;
import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.screen.SpawnerControllerScreen;
import iskallia.ispawner.world.spawner.SpawnerAction;
import iskallia.ispawner.world.spawner.SpawnerController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.spawner.Spawner;

public class SpawnerControllerItem extends Item {

	public SpawnerControllerItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if(!world.isClient) {
			ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
			SpawnerController controller = new SpawnerController(stack.getOrCreateSubNbt("Controller"));
			BlockHitResult context = raycast(world, player, RaycastContext.FluidHandling.NONE);

			if(controller.getMode() == SpawnerController.Mode.SPAWNING_SPACES) {
				controller.getTarget().ifPresent(spawnerPos -> {
					BlockEntity blockEntity = world.getBlockEntity(spawnerPos);
					if(!(blockEntity instanceof SpawnerBlockEntity spawner)) return;
					BlockRotation rotation = spawner.getReverseRotation();
					BlockPos offset = pos.subtract(spawner.getCenterPos());

					spawner.manager.addAction(new SpawnerAction(
							SpawnerBlockEntity.mirror(offset, spawner.getMirror()).rotate(rotation),
							SpawnerBlockEntity.mirror(rotation.rotate(context.getSide()), spawner.getMirror()),
							context.getPos(), //doesn't matter
							Hand.MAIN_HAND,
							Direction.getEntityFacingOrder(player)), -1);

					spawner.sendClientUpdates();
				});
			}
		}

		return false;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState state = context.getWorld().getBlockState(context.getBlockPos());

		if(context.getWorld().isClient || context.getPlayer() == null) {
			return ActionResult.SUCCESS;
		}

		SpawnerController controller = new SpawnerController(context.getStack().getOrCreateSubNbt("Controller"));

		if(state.getBlock() instanceof SpawnerBlock && !context.getBlockPos().equals(controller.getTarget().orElse(null))) {
			controller.setTarget(context.getBlockPos());

			if(context.getPlayer() != null) {
				context.getPlayer().sendMessage(new LiteralText("Bound to spawner.").formatted(Formatting.GREEN), true);
			}
		} else {
			controller.getTarget().ifPresent(spawnerPos -> {
				BlockEntity blockEntity = context.getWorld().getBlockEntity(spawnerPos);
				if(!(blockEntity instanceof SpawnerBlockEntity spawner)) return;

				if(controller.getMode() == SpawnerController.Mode.SPAWNING_SPACES) {
					BlockRotation rotation = spawner.getReverseRotation();
					BlockPos offset = context.getBlockPos().subtract(spawner.getCenterPos());
					Vec3d hitPosOffset = context.getHitPos().subtract(spawner.getCenterPos().getX(),
							spawner.getCenterPos().getY(), spawner.getCenterPos().getZ());

					spawner.manager.addAction(new SpawnerAction(
							SpawnerBlockEntity.mirror(offset, spawner.getMirror()).rotate(rotation),
							rotation.rotate(SpawnerBlockEntity.mirror(context.getSide(), spawner.getMirror())),
							SpawnerAction.rotate(rotation, SpawnerBlockEntity.mirror(hitPosOffset, spawner.getMirror())),
							context.getHand(),
							Direction.getEntityFacingOrder(context.getPlayer())), 1);

					spawner.sendClientUpdates();
				} else if(controller.getMode() == SpawnerController.Mode.RELOCATOR) {
					BlockRotation rotation = spawner.getReverseRotation();
					BlockPos offset = context.getBlockPos().subtract(spawner.getPos());
					spawner.setOffset(SpawnerBlockEntity.mirror(offset.rotate(rotation), spawner.getMirror()));
					spawner.sendClientUpdates();
				}
			});
		}

		return ActionResult.CONSUME;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		SpawnerController controller = new SpawnerController(stack.getOrCreateSubNbt("Controller"));

		if(player.isSneaking()) {
			if(world.isClient) {
				this.openScreen(controller);
			}
		} else if(controller.getMode() == SpawnerController.Mode.SPAWN_REMOTE && !world.isClient) {
			controller.getTarget().ifPresent(spawnerPos -> {
				BlockEntity blockEntity = world.getBlockEntity(spawnerPos);
				if(!(blockEntity instanceof SpawnerBlockEntity)) return;
				SpawnerBlockEntity spawner = (SpawnerBlockEntity)blockEntity;
				spawner.manager.spawn(world, world.getRandom(), spawner);
				player.sendMessage(new LiteralText("Spawned mobs.").formatted(Formatting.GREEN), true);
			});
		}


		return TypedActionResult.success(stack);
	}

	@Environment(EnvType.CLIENT)
	public void openScreen(SpawnerController controller) {
		MinecraftClient.getInstance().setScreenAndRender(new SpawnerControllerScreen(new LiteralText("Spawner Controller"), controller));
	}

}
