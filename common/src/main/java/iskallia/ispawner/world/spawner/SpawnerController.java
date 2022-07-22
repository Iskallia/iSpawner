package iskallia.ispawner.world.spawner;

import iskallia.ispawner.nbt.NBTConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class SpawnerController {

	private final NbtCompound delegate;

	public SpawnerController(NbtCompound delegate) {
		this.delegate = delegate;
	}

	public Optional<BlockPos> getTarget() {
		return this.delegate.contains("Target", NBTConstants.LONG) ?
				Optional.of(BlockPos.fromLong(this.delegate.getLong("Target"))) : Optional.empty();
	}

	public void setTarget(BlockPos pos) {
		if(pos == null) {
			this.delegate.remove("Target");
			return;
		}

		this.delegate.putLong("Target", pos.asLong());
	}

	public Mode getMode() {
		String raw = this.delegate.getString("Mode");
		return raw.isEmpty() ? Mode.SPAWNING_SPACES : Mode.valueOf(raw);
	}

	public void setMode(Mode mode) {
		this.delegate.putString("Mode", mode.name());
	}

	public enum Mode {
		SPAWNING_SPACES("Spawning Spaces"),
		SPAWN_REMOTE("Spawn Remote"),
		RELOCATOR("Relocator");

		public final String instruction;

		Mode(String instruction) {
			this.instruction = instruction;
		}
	}

}
