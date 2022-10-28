package iskallia.ispawner.block;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;

public class SpawnerRotation {

    private static final int R0 = 0;
    private static final int R1 = 1;
    private static final int R2 = 2;
    private static final int R3 = 3;
    private static final int M1 = 4;
    private static final int M2 = 5;
    private static final int D1 = 6;
    private static final int D2 = 7;

    protected static int[][] LUT = new int[][] {
        {R0, R1, R2, R3, M1, M2, D1, D2},
        {R1, R2, R3, R0, D2, D1, M1, M2},
        {R2, R3, R0, R1, M2, M1, D2, D1},
        {R3, R0, R1, R2, D1, D2, M2, M1},
        {M1, D1, M2, D2, R0, R2, R1, R3},
        {M2, D2, M1, D1, R2, R0, R3, R1},
        {D1, M2, D2, M1, R3, R1, R0, R2},
        {D2, M1, D1, M2, R1, R3, R2, R0}
    };

    public static int getIndex(BlockRotation rotation, BlockMirror mirror) {
        return switch(rotation) {
            case NONE -> switch(mirror) {
                case NONE -> R0;
                case LEFT_RIGHT -> M1;
                case FRONT_BACK -> M2;
            };
            case CLOCKWISE_90 -> switch(mirror) {
                case NONE -> R1;
                case LEFT_RIGHT -> D2;
                case FRONT_BACK -> D1;
            };
            case COUNTERCLOCKWISE_90 -> switch(mirror) {
                case NONE -> R3;
                case LEFT_RIGHT -> D1;
                case FRONT_BACK -> D2;
            };
            case CLOCKWISE_180 -> switch(mirror) {
                case NONE -> R2;
                case LEFT_RIGHT -> M2;
                case FRONT_BACK -> M1;
            };
        };
    }

    public static BlockRotation getRotation(int index) {
        return switch(index) {
            case R0, M1, M2 -> BlockRotation.NONE;
            case R1, D1, D2 -> BlockRotation.CLOCKWISE_90;
            case R2 -> BlockRotation.CLOCKWISE_180;
            case R3 -> BlockRotation.COUNTERCLOCKWISE_90;
            default -> throw new UnsupportedOperationException();
        };
    }

    public static BlockMirror getMirror(int index) {
        return switch(index) {
            case R0, R1, R2, R3 -> BlockMirror.NONE;
            case M1, D2 -> BlockMirror.LEFT_RIGHT;
            case M2, D1 -> BlockMirror.FRONT_BACK;
            default -> throw new UnsupportedOperationException();
        };
    }

    public static int multiply(int index, BlockRotation rotation, BlockMirror mirror) {
        return LUT[index][getIndex(rotation, mirror)];
    }

}
