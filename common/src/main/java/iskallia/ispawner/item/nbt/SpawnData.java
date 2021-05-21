package iskallia.ispawner.item.nbt;

import iskallia.ispawner.init.ModConfigs;
import iskallia.ispawner.nbt.NBTConstants;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpawnData extends ItemNBT {

    public static final String KEY = "SpawnData";

    protected int charges;

	public SpawnData(ItemStack stack) {
        super(stack, KEY);
    }

    public SpawnData(CompoundTag delegate) {
        super(delegate, KEY);
    }

    public int getCharges() {
        return this.charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
        this.update();
    }

    @Override
    public CompoundTag writeToNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Charges", this.charges);
        return nbt;
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        if(nbt.contains("Charges", NBTConstants.INT)) {
            this.charges = nbt.getInt("Charges");
        } else this.charges = ModConfigs.SURVIVAL_SPAWNER.defaultCharges;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new LiteralText("Spawner Charges: ")
            .append(new LiteralText(String.valueOf(this.getCharges())).formatted(Formatting.GREEN)));
    }

}
