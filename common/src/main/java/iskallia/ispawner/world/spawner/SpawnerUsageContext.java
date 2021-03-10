package iskallia.ispawner.world.spawner;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpawnerUsageContext extends ItemUsageContext {

	private final SpawnerAction action;

	public SpawnerUsageContext(World world, ItemStack stack, SpawnerAction action) {
		super(world, null, action.getHand(), stack, null);
		this.action = action;
	}

	public SpawnerUsageContext(World world, ItemStack stack, BlockPos pos, Direction side, Vec3d hitPos, Hand hand) {
		this(world, stack, new SpawnerAction(pos, side, hitPos, hand));
	}

	public SpawnerAction getAction() {
		return this.action;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.getAction().getPos();
	}

	@Override
	public Direction getSide() {
		return this.getAction().getSide();
	}

	@Override
	public Vec3d getHitPos() {
		return this.getAction().getHitPos();
	}

	@Override
	public boolean hitsInsideBlock() {
		return false;
	}

}
