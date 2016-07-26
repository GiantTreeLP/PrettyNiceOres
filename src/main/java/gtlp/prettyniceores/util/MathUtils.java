package gtlp.prettyniceores.util;

/**
 * Created by Marv1 on 26.07.2016.
 */
public final class MathUtils {
    private MathUtils() {
    }

    public static int fastModulo(int dividend, int divisor) {
        return dividend & (divisor - 1);
    }
}
