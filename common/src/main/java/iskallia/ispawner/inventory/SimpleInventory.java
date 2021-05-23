package iskallia.ispawner.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleInventory extends FixedInventory {

	private final int size;

	public SimpleInventory(int size) {
		this.size = size;

		this.stacks = DefaultedList.ofSize(this.size, ItemStack.EMPTY);
		this.availableSlots = Arrays.stream(Direction.values())
				.collect(Collectors.toMap(e -> e, e -> IntStream.range(0, this.size).toArray()));
	}

	public OptionalInt getEmptySlot() {
		return IntStream.range(0, this.size()).filter(i -> this.getStack(i).isEmpty()).findFirst();
	}

	public boolean isEmpty() {
		return IntStream.range(0, this.size()).allMatch(i -> this.getStack(i).isEmpty());
	}

}
