package iskallia.ispawner.mixin;

import iskallia.ispawner.item.nbt.ItemNBT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public class MixinItem {

    @Environment(EnvType.CLIENT)
    @Inject(method = "appendTooltip", at = @At("RETURN"))
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        ItemNBT.OVERRIDES.forEach(override -> override.getIfApplicable(stack).ifPresent(itemNBT -> {
            itemNBT.appendTooltip(stack, world, tooltip, context);
        }));
    }

}
