package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.render.SpawnerBlockRenderer;
import iskallia.ispawner.util.Color;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import java.util.*;

public class SpawnerRenderer {

	protected Set<Face> faces = new HashSet<>();

	public SpawnerRenderer() {

	}

	public void refresh(SpawnerManager manager) {
		this.faces.clear();

		manager.actions.forEach(entry -> {
			this.faces.add(new Face(entry.value.getPos(), entry.value.getSide(), SpawnerBlockRenderer.getColorFor(entry.weight)));
		});
	}

	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, BlockPos center) {
		new ArrayList<>(this.faces).forEach(face -> face.render(matrices, vertexConsumer, this.faces, center));
	}

	public static class Face {
		private final BlockPos pos;
		private final Direction side;
		private final Color color;

		public Face(BlockPos pos, Direction side, Color color) {
			this.pos = pos;
			this.side = side;
			this.color = color;
		}

		public void render(MatrixStack matrices, VertexConsumer vertexConsumer, Collection<Face> neighbors, BlockPos center) {
			if(this.side == Direction.DOWN) {
				if(!neighbors.contains(new Face(this.pos.north(), Direction.DOWN, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 0, 1, 0, 0, center);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.DOWN, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 1, 1, 0, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.DOWN, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 0, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.DOWN, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 0, 1, center);
				}
			} else if(this.side == Direction.UP) {
				if(!neighbors.contains(new Face(this.pos.north(), Direction.UP, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 1, 0, 1, 1, 0, center);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.UP, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 1, 1, 1, 1, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.UP, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 1, 0, 1, 1, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.UP, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 1, 0, 0, 1, 1, center);
				}
			} else if(this.side == Direction.NORTH) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.NORTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 0, 1, 0, 0, center);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.NORTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 1, 0, 1, 1, 0, center);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.NORTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 1, 0, center);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.NORTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 1, 0, center);
				}
			} else if(this.side == Direction.SOUTH) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.SOUTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 1, 1, 0, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.SOUTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 1, 1, 1, 1, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.east(), Direction.SOUTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 0, 1, 1, 1, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.west(), Direction.SOUTH, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 1, 0, 1, 1, center);
				}
			} else if(this.side == Direction.WEST) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 0, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 1, 0, 0, 1, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.north(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 0, 0, 1, 0, center);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 0, 0, 1, 0, 1, 1, center);
				}
			} else if(this.side == Direction.EAST) {
				if(!neighbors.contains(new Face(this.pos.down(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 0, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.up(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 1, 0, 1, 1, 1, center);
				}
				if(!neighbors.contains(new Face(this.pos.north(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 0, 0, 1, 1, 0, center);
				}
				if(!neighbors.contains(new Face(this.pos.south(), Direction.WEST, this.color))) {
					this.drawLine(matrices, vertexConsumer, 1, 0, 1, 1, 1, 1, center);
				}
			}
		}

		public void drawLine(MatrixStack matrices, VertexConsumer vertexConsumer,
		                     double x1, double y1, double z1,
		                     double x2, double y2, double z2, BlockPos center) {
			Matrix4f matrix = matrices.peek().getModel();
			BlockPos p = this.pos.subtract(center);
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
			return this.pos.equals(face.pos) && this.side == face.side && this.color.equals(face.color);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.pos, this.side, this.color);
		}
	}

}
