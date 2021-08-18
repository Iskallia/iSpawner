package iskallia.ispawner.screen.handler.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

public class FilteredSlot extends Slot {

    private final Predicate<ItemStack> insertFilter;

    public FilteredSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> insertFilter) {
        super(inventory, index, x, y);
        this.insertFilter = insertFilter;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (!this.insertFilter.test(stack)) {
            return false;
        }
        return super.canInsert(stack);
    }
}
