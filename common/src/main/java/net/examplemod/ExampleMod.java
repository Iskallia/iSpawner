package net.examplemod;

import me.shedaniel.architectury.registry.CreativeTabs;
import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;


public class ExampleMod {
    public static final String MOD_ID = "examplemod";
    // We can use this if we don't want to use DeferredRegister
    public static final Lazy<Registries> REGISTRIES = new Lazy<>(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
    public static final ItemGroup EXAMPLE_TAB = CreativeTabs.create(new Identifier(MOD_ID, "example_tab"), new Supplier<ItemStack>() {
        @Override
        public ItemStack get() {
            return new ItemStack(EXAMPLE_ITEM.get());
        }
    });
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY);
    public static final RegistrySupplier<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () ->
            new Item(new Item.Settings().group(ExampleMod.EXAMPLE_TAB)));
    
    public static void init() {
        ITEMS.register();
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
