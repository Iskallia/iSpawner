package iskallia.ispawner.config;

import com.google.gson.annotations.Expose;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class SurvivalSpawnerConfig extends Config {

	@Expose public int defaultCharges;
	@Expose public List<Item> itemWhitelist;

    @Override
    public String getName() {
        return "survival_spawner";
    }

    @Override
    protected void reset() {
		this.defaultCharges = 10;
		this.itemWhitelist = Registry.ITEM.stream().filter(item -> item instanceof SpawnEggItem).collect(Collectors.toList());
    }

    public boolean isWhitelisted(ItemStack stack) {
        return this.itemWhitelist.contains(stack.getItem());
    }
}
