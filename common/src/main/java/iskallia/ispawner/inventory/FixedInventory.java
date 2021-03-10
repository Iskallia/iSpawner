package iskallia.ispawner.inventory;

import iskallia.ispawner.nbt.INBTSerializable;
import iskallia.ispawner.nbt.NBTConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class FixedInventory implements SidedInventory, INBTSerializable<CompoundTag> {

	protected DefaultedList<ItemStack> stacks;
	protected Map<Direction, int[]> availableSlots;
	protected List<InventoryChangedListener> listeners = new ArrayList<>();

	public FixedInventory() {
	}

	public void addListener(InventoryChangedListener arg) {
		this.listeners.add(arg);
	}

	public void removeListener(InventoryChangedListener arg) {
		this.listeners.remove(arg);
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return this.availableSlots.get(side);
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return true;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public int size() {
		return this.stacks.size();
	}

	@Override
	public boolean isEmpty() {
		return this.stacks.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= this.size() || slot < 0 ? ItemStack.EMPTY : this.stacks.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack result = Inventories.splitStack(this.stacks, slot, amount);

		if(!result.isEmpty()) {
			this.markDirty();
		}

		return result;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack result = this.stacks.get(slot);

		if(!result.isEmpty()) {
			this.stacks.set(slot, ItemStack.EMPTY);
			return result;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);

		if(!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}

		this.markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void markDirty() {
		this.listeners.forEach(listener -> listener.onInventoryChanged(this));
	}

	@Override
	public CompoundTag writeToNBT() {
		CompoundTag nbt = new CompoundTag();

		ListTag stacksList = new ListTag();
		this.stacks.forEach(stack -> {
			stacksList.add(stack.toTag(new CompoundTag()));
		});
		nbt.put("Stacks", stacksList);

		ListTag slotsList = new ListTag();
		this.availableSlots.forEach((direction, slots) -> {
			CompoundTag tag = new CompoundTag();
			tag.putInt("Direction", direction.ordinal());
			tag.putIntArray("Slots", slots);
			slotsList.add(tag);
		});
		nbt.put("AvailableSlots", slotsList);

		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		this.stacks.clear();
		this.availableSlots.clear();

		ListTag stacksList = nbt.getList("Stacks", NBTConstants.COMPOUND);

		for(int i = 0; i < stacksList.size() && i < this.stacks.size(); i++) {
			this.stacks.set(i, ItemStack.fromTag(stacksList.getCompound(i)));
		}

		ListTag slotsList = nbt.getList("AvailableSlots", NBTConstants.COMPOUND);

		for(int i = 0; i < stacksList.size(); i++) {
			CompoundTag tag = slotsList.getCompound(i);
			Direction direction = Direction.values()[tag.getInt("Direction")];
			int[] slots = tag.getIntArray("Slots");
			this.availableSlots.put(direction, slots);
		}
	}

	@Override
	public void clear() {
		this.stacks.clear();
		this.markDirty();
	}

}
