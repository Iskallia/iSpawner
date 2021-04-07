package iskallia.ispawner.mixin;

import iskallia.ispawner.world.spawner.SpawnerUsageContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(ItemPlacementContext.class)
public abstract class MixinItemPlacementContext extends ItemUsageContext {

	@Shadow protected boolean canReplaceExisting;

	private Direction[] directions;

	public MixinItemPlacementContext(PlayerEntity player, Hand hand, BlockHitResult hit) {
		super(player, hand, hit);
	}

	protected MixinItemPlacementContext(World world, @Nullable PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hit) {
		super(world, player, hand, stack, hit);
	}

	@Inject(method = "<init>(Lnet/minecraft/item/ItemUsageContext;)V", at = @At("RETURN"))
	public void init(ItemUsageContext context, CallbackInfo ci) {
		if(context instanceof SpawnerUsageContext) {
			this.directions = Arrays.stream(((SpawnerUsageContext)context).getAction().getDirections()).toArray(Direction[]::new);
		}
	}

	@Inject(method = "getPlayerLookDirection", at = @At("HEAD"), cancellable = true)
	public void getPlayerLookDirection(CallbackInfoReturnable<Direction> ci) {
		if(this.directions != null) {
			ci.setReturnValue(this.directions[0]);
		}
	}

	@Inject(method = "getPlacementDirections", at = @At("HEAD"), cancellable = true)
	public void getPlacementDirections(CallbackInfoReturnable<Direction[]> ci) {
		if(this.directions != null) {
			Direction[] lvs = Arrays.stream(this.directions).toArray(Direction[]::new);

			if(!this.canReplaceExisting) {
				Direction lv = this.getSide();

				int i;

				for(i = 0; i < lvs.length && lvs[i] != lv.getOpposite(); ++i) {
				}

				if(i > 0) {
					System.arraycopy(lvs, 0, lvs, 1, i);
					lvs[0] = lv.getOpposite();
				}
			}

			ci.setReturnValue(lvs);
		}
	}

}
