package gtlp.relocate.org.apache.commons.math3.random;

import java.io.Serializable;

abstract class BitsStreamGenerator implements Serializable, RandomGenerator {
    BitsStreamGenerator() {
    }

    protected abstract int next(int var1);

    public final double nextDouble() {
        long high = (long) this.next(26) << 26;
        int low = this.next(26);
        return (double) (high | (long) low) * 2.220446049250313E-16D;
    }
}
