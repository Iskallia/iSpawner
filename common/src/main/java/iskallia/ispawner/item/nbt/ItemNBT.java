package iskallia.ispawner.item.nbt;

import iskallia.ispawner.nbt.INBTSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ItemNBT implements INBTSerializable<NbtCompound> {

    public static final List<StackOverride> OVERRIDES = new ArrayList<>();

    protected final NbtCompound delegate;
    protected final String tagKey;

    public ItemNBT(ItemStack stack, String tagKey) {
        this(stack.getNbt(), tagKey);
    }

    public ItemNBT(NbtCompound delegate, String tagKey) {
        this.delegate = delegate;
        this.tagKey = tagKey;
        this.readFromNBT(this.getDelegate() != null ? this.getDelegate().getCompound(this.getTagKey()) : new NbtCompound());
	}

    public NbtCompound getDelegate() {
        return this.delegate;
    }

    public String getTagKey() {
        return this.tagKey;
    }

    protected void update() {
        if(this.delegate == null)return;
        this.delegate.put(this.getTagKey(), this.writeToNBT());
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

    }

    public static void registerOverride(Predicate<ItemStack> canApply, Function<ItemStack, ItemNBT> nbtSupplier) {
        OVERRIDES.add(new StackOverride(canApply, nbtSupplier));
    }

    public static class StackOverride {
        public Predicate<ItemStack> canApply;
        public Function<ItemStack, ItemNBT> nbtSupplier;

        public StackOverride(Predicate<ItemStack> canApply, Function<ItemStack, ItemNBT> nbtSupplier) {
            this.canApply = canApply;
            this.nbtSupplier = nbtSupplier;
        }

        public Optional<ItemNBT> getIfApplicable(ItemStack stack) {
            return this.canApply.test(stack) ? Optional.of(this.nbtSupplier.apply(stack)) : Optional.empty();
        }
    }

}
