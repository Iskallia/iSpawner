package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.block.render.SpawnerBlockRenderer;
import iskallia.ispawner.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.*;

import java.util.*;

public class SpawnerRenderer {

	protected Set<Face> faces = new HashSet<>();

	public SpawnerRenderer() {

	}

	public void refresh(SpawnerBlockEntity entity) {
		this.faces.clear();

		entity.manager.actions.forEach(entry -> {
			this.faces.add(new Face(entry.value.getPos().rotate(entity.getRotation()),
					entity.getRotation().rotate(entry.value.getSide()), entry.weight));
		});
	}

	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, SpawnerBlockEntity entity) {
		new ArrayList<>(this.faces).stream()
				.sorted(Comparator.comparingInt(o -> o.weight))
				.forEach(face -> face.render(matrices, vertexConsumer, this.faces, entity.getOffset()));
	}

	public static class Face {
		private final BlockPos pos;
		private final Direction side;
		private final int weight;
		private final Color color;

		public Face(BlockPos pos, Direction side, int weight) {
			this.pos = pos;
			this.side = side;
			this.weight = weight;
			this.color = SpawnerBlockRenderer.getColorFor(this.weight);
		}

		public void render(MatrixStack matrices, VertexConsumer vertexConsumer, Collection<Face> neighbors, BlockPos offset) {
			matrices.push();
			BlockPos p = this.pos.add(offset);

			if(this.side == Direction.UP) {
				double center = (8.0D + MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(this.weight)) / 2.0D) / 16.0D;
				matrices.translate(p.getX() + center - 0.5D / 16.0D, p.getY() + 1.01D, p.getZ() + 11.5D / 16.0D);
				matrices.multiply(new Quaternion(new Vec3f(1.0F, 0.0F, 0.0F), 90.0F, true));
			} else if(this.side == Direction.DOWN) {
				double center = (8.0D + MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(this.weight)) / 2.0D) / 16.0D;
				matrices.translate(p.getX() + center - 0.5D / 16.0D, p.getY() - 0.01D, p.getZ() + 4.5D / 16.0D);
				matrices.multiply(new Quaternion(new Vec3f(1.0F, 0.0F, 0.0F), -90.0F, true));
			} else if(this.side == Direction.NORTH) {
				double center = (8.0D + MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(this.weight)) / 2.0D) / 16.0D;
				matrices.translate(p.getX() + center - 0.5D / 16.0D, p.getY() + 11.5D / 16.0D, p.getZ() - 0.01D);
				matrices.multiply(new Quaternion(new Vec3f(0.0F, 1.0F, 0.0F), 0.0F, true));
			} else if(this.side == Direction.SOUTH) {
				double center = (8.0D - MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(this.weight)) / 2.0D) / 16.0D;
				matrices.translate(p.getX() + center + 0.5D / 16.0D, p.getY() + 11.5D / 16.0D, p.getZ() + 1.01D);
				matrices.multiply(new Quaternion(new Vec3f(0.0F, 1.0F, 0.0F), 180.0F, true));
			} else if(this.side == Direction.WEST) {
				double center = (8.0D - MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(this.weight)) / 2.0D) / 16.0D;
				matrices.translate(p.getX() - 0.01D, p.getY() + 11.5D / 16.0D, p.getZ() + center + 0.5D / 16.0D);
				matrices.multiply(new Quaternion(new Vec3f(0.0F, 1.0F, 0.0F), 90.0F, true));
			} else if(this.side == Direction.EAST) {
				double center = (8.0D + MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(this.weight)) / 2.0D) / 16.0D;
				matrices.translate(p.getX() + 1.01D, p.getY() + 11.5D / 16.0D, p.getZ() + center - 0.5D / 16.0D);
				matrices.multiply(new Quaternion(new Vec3f(0.0F, 1.0F, 0.0F), -90.0F, true));
			}

			matrices.scale(-0.0625F, -0.0625F, 0.0625F);
			MinecraftClient.getInstance().textRenderer.draw(matrices, new LiteralText(String.valueOf(this.weight)), 0, 0, this.color.getRBG());
			matrices.pop();

			if(this.side == Direction.DOWN) {
				if(!neighbors.contains(new Face(this.pos.north(), Direction.DOWN, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 0, 1, 0, 0, offset);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.DOWN, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 1, 1, 0, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.DOWN, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 0, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.DOWN, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 0, 1, offset);
				}
			} else if(this.side == Direction.UP) {
				if(!neighbors.contains(new Face(this.pos.north(), Direction.UP, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 1, 0, 1, 1, 0, offset);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.UP, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 1, 1, 1, 1, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.UP, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 1, 0, 1, 1, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.UP, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 1, 0, 0, 1, 1, offset);
				}
			} else if(this.side == Direction.NORTH) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.NORTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 0, 1, 0, 0, offset);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.NORTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 1, 0, 1, 1, 0, offset);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.NORTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 1, 0, offset);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.NORTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 1, 0, offset);
				}
			} else if(this.side == Direction.SOUTH) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.SOUTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 1, 1, 0, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.SOUTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 1, 1, 1, 1, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.SOUTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 0, 1, 1, 1, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.SOUTH, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 1, 0, 1, 1, offset);
				}
			} else if(this.side == Direction.WEST) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.WEST, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 0, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.WEST, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 1, 0, 0, 1, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.north(), Direction.WEST, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 1, 0, offset);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.WEST, this.weight))) {
					drawLine(matrices, vertexConsumer, 0, 0, 1, 0, 1, 1, offset);
				}
			} else if(this.side == Direction.EAST) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.EAST, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 0, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.EAST, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 1, 0, 1, 1, 1, offset);
				}
				if(!neighbors.contains(new Face(this.pos.north(), Direction.EAST, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 1, 0, offset);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.EAST, this.weight))) {
					drawLine(matrices, vertexConsumer, 1, 0, 1, 1, 1, 1, offset);
				}
			}
		}

		public void drawLine(MatrixStack matrices, VertexConsumer vertexConsumer,
		                     double x1, double y1, double z1,
		                     double x2, double y2, double z2, BlockPos offset) {
			Matrix4f matrix = matrices.peek().getModel();
			BlockPos p = this.pos.add(offset);

			vertexConsumer.vertex(matrix, p.getX() + (float)x1, p.getY() + (float)y1, p.getZ() + (float)z1)
					.color(this.color.getFRed(), this.color.getFGreen(), this.color.getFBlue(), 1.0F).next();
			vertexConsumer.vertex(matrix, p.getX() + (float)x2, p.getY() + (float)y2, p.getZ() + (float)z2)
					.color(this.color.getFRed(), this.color.getFGreen(), this.color.getFBlue(), 1.0F).next();
		}

		@Override
		public boolean equals(Object other) {
			if(this == other)return true;
			if(!(other instanceof Face))return false;
			Face face = (Face)other;
			return this.pos.equals(face.pos) && this.side == face.side && this.weight == face.weight;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.pos, this.side, this.weight);
		}
	}

}
