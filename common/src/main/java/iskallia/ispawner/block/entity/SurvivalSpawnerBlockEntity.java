package iskallia.ispawner.block.entity;

import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.init.ModConfigs;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.item.nbt.SpawnData;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.screen.handler.SurvivalSpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerAction;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class SurvivalSpawnerBlockEntity extends SpawnerBlockEntity {

	public SimpleInventory input = new SimpleInventory(1) {
		@Override
		public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
			return ModConfigs.SURVIVAL_SPAWNER.isWhitelisted(stack);
		}
	};

	public SurvivalSpawnerBlockEntity(BlockPos pos, BlockState state) {
		this(ModBlocks.Entities.SURVIVAL_SPAWNER.get(), pos, state);
	}

	public SurvivalSpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.input.addListener(this);
	}

	public SimpleInventory getInput() {
		return this.input;
	}

	public static void tick(World world, BlockPos pos, BlockState state, SurvivalSpawnerBlockEntity spawner) {
		SpawnerSettings newConfig = ModConfigs.SURVIVAL_SPAWNER.defaultSettings.copy();
		newConfig.setMode(spawner.manager.settings.getMode());

		if(!spawner.manager.settings.equals(newConfig)) {
			spawner.manager.settings = newConfig;
			spawner.sendClientUpdates();
		}

		if (spawner.inventory.isEmpty()) {
			for (int i = 0; i < spawner.input.size(); i++) {
				ItemStack stack = spawner.input.getStack(i);
				if (stack.isEmpty()) continue;
				if (!spawner.input.canInsert(i, stack, Direction.NORTH)) continue;

				OptionalInt emptySlot = spawner.inventory.getEmptySlot();

				if (emptySlot.isPresent()) {
					ItemStack newStack = new ItemStack(stack.getItem());
					newStack.setCount(new SpawnData(stack).getCharges());
					spawner.inventory.setStack(emptySlot.getAsInt(), newStack);
					stack.decrement(1);
					spawner.input.setStack(i, stack);
					break;
				}
			}
		}

		if(spawner.manager.actions.isEmpty()) {
			for(int x = -4; x <= 4; x++) {
				for(int z = -4; z <= 4; z++) {
					for(int y = -2; y <= 1; y++) {
						int weight = 4 - Math.max(Math.abs(x), Math.abs(z)) + 1;
						BlockRotation rotation = spawner.getReverseRotation();
						Vec3d hitPosOffset = new Vec3d(0.5D, 1.0D, 0.5D);

						spawner.manager.addAction(new SpawnerAction(
							new BlockPos(x, y, z).rotate(rotation),
							rotation.rotate(Direction.UP),
							SpawnerAction.rotate(rotation, hitPosOffset),
							Hand.MAIN_HAND,
							new Direction[] {Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN}), weight);
					}
				}
			}
		}
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new SurvivalSpawnerScreenHandler(syncId, inv, this);
	}

	@Override
	public void onChargeUsed(ItemStack stack, int index) {
		stack.decrement(1);
		this.inventory.setStack(index, stack);
	}

	@Override
	public NbtCompound write(NbtCompound tag, UpdateType type) {
		NbtCompound nbt = super.write(tag, type);
		nbt.put("Input", this.input.writeToNBT());
		return nbt;
	}

	@Override
	public void read(NbtCompound tag, UpdateType type) {
		super.read(tag, type);

		if(tag.contains("Input", NBTConstants.COMPOUND)) {
			this.input.readFromNBT(tag.getCompound("Input"));
		}
	}

}
