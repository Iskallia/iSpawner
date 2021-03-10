package iskallia.ispawner.item;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.world.spawner.SpawnerAction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class SpawnerControllerItem extends Item {

	public SpawnerControllerItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState state = context.getWorld().getBlockState(context.getBlockPos());

		if(context.getWorld().isClient) {
			return ActionResult.SUCCESS;
		}

		if(state.getBlock() == ModBlocks.SPAWNER) {
			setSpawnerTarget(context.getStack(), context.getBlockPos());

			if(context.getPlayer() != null) {
				context.getPlayer().sendMessage(new LiteralText("Bound to spawner.").formatted(Formatting.GREEN), true);
			}
		} else {
			Optional<BlockPos> target = getSpawnerTarget(context.getStack());

			target.ifPresent(spawnerPos -> {
				BlockEntity blockEntity = context.getWorld().getBlockEntity(spawnerPos);
				if(!(blockEntity instanceof SpawnerBlockEntity))return;

				SpawnerBlockEntity spawner = (SpawnerBlockEntity)blockEntity;
				spawner.manager.addAction(new SpawnerAction(context.getBlockPos(), context.getSide(), context.getHitPos(), context.getHand()), 1);
				spawner.sendClientUpdates();
			});
		}

		return ActionResult.CONSUME;
	}

	public static void setSpawnerTarget(ItemStack stack, BlockPos pos) {
		CompoundTag posTag = new CompoundTag();
		posTag.putInt("X", pos.getX());
		posTag.putInt("Y", pos.getY());
		posTag.putInt("Z", pos.getZ());
		stack.getOrCreateTag().put("SpawnerTarget", posTag);
	}

	public static Optional<BlockPos> getSpawnerTarget(ItemStack stack) {
		if(stack.getTag() == null || !stack.getTag().contains("SpawnerTarget", NBTConstants.COMPOUND)) {
			return Optional.empty();
		}

		CompoundTag posTag = stack.getTag().getCompound("SpawnerTarget");
		return Optional.of(new BlockPos(posTag.getInt("X"), posTag.getInt("Y"), posTag.getInt("Z")));
	}

}
