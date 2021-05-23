package iskallia.ispawner.block.entity;

import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.init.ModConfigs;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.item.nbt.SpawnData;
import iskallia.ispawner.nbt.NBTConstants;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class SurvivalSpawnerBlockEntity extends SpawnerBlockEntity {

	public SimpleInventory input = new SimpleInventory(1) {
		@Override
		public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
			return ModConfigs.SURVIVAL_SPAWNER.itemWhitelist.contains(stack.getItem());
		}
	};

	public SurvivalSpawnerBlockEntity() {
		super(ModBlocks.Entities.SURVIVAL_SPAWNER);
		this.input.addListener(this);
	}

	public SimpleInventory getInput() {
		return this.input;
	}

	@Override
	public void tick() {
		super.tick();

		for(int i = 0; i < this.input.size(); i++) {
			ItemStack stack = this.input.getStack(i);
			if(stack.isEmpty())continue;
			if(!this.input.canInsert(i, stack, Direction.NORTH))continue;

			for(int j = 0; j < stack.getCount(); j++) {
				OptionalInt emptySlot = this.inventory.getEmptySlot();

				if(emptySlot.isPresent()) {
					ItemStack newStack = new ItemStack(stack.getItem());
					newStack.setCount(new SpawnData(stack).getCharges());
					this.inventory.setStack(emptySlot.getAsInt(), newStack);
					stack.decrement(1);
					this.input.setStack(i, stack);
				}
			}
		}
	}

	@Override
	public void onChargeUsed(ItemStack stack, int index) {
		stack.decrement(1);
		this.inventory.setStack(index, stack);
	}

	@Override
	public CompoundTag write(CompoundTag tag, UpdateType type) {
		CompoundTag nbt = super.write(tag, type);
		nbt.put("Input", this.input.writeToNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundTag tag, UpdateType type) {
		super.read(state, tag, type);

		if(tag.contains("Input", NBTConstants.COMPOUND)) {
			this.input.readFromNBT(tag.getCompound("Input"));
		}
	}

}
