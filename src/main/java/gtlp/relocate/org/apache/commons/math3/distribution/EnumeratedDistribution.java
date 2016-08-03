package gtlp.relocate.org.apache.commons.math3.distribution;

import gtlp.relocate.org.apache.commons.math3.exception.MathArithmeticException;
import gtlp.relocate.org.apache.commons.math3.exception.MathIllegalArgumentException;
import gtlp.relocate.org.apache.commons.math3.exception.NotANumberException;
import gtlp.relocate.org.apache.commons.math3.exception.NotPositiveException;
import gtlp.relocate.org.apache.commons.math3.exception.util.LocalizedFormats;
import gtlp.relocate.org.apache.commons.math3.random.RandomGenerator;
import gtlp.relocate.org.apache.commons.math3.random.Well19937c;
import gtlp.relocate.org.apache.commons.math3.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class EnumeratedDistribution<T> implements Serializable {
    private final ArrayList<T> singletons;
    private final double[] probabilities;
    private final double[] cumulativeProbabilities;
    private RandomGenerator random;

    public EnumeratedDistribution(List<Pair<T, Double>> pmf) throws NotPositiveException, MathArithmeticException, NotANumberException {
        this(new Well19937c(), pmf);
    }

    private EnumeratedDistribution(RandomGenerator probs, List<Pair<T, Double>> pmf) throws NotPositiveException, MathArithmeticException, NotANumberException {
        this.random = probs;
        this.singletons = new ArrayList<>(pmf.size());
        double[] var8 = new double[pmf.size()];

        for (int sum = 0; sum < pmf.size(); ++sum) {
            Pair<T, Double> sample = pmf.get(sum);
            this.singletons.add(sample.getKey());
            double i;
            if ((i = sample.getValue()) < 0.0D) {
                throw new NotPositiveException(sample.getValue());
            }

            if (Double.isInfinite(i)) {
                throw new NotANumberException();
            }

            if (Double.isNaN(i)) {
                throw new NotANumberException();
            }

            var8[sum] = i;
        }

        this.probabilities = normalizeArray(var8, 1.0D);
        this.cumulativeProbabilities = new double[this.probabilities.length];
        double var9 = 0.0D;

        for (int var10 = 0; var10 < this.probabilities.length; ++var10) {
            var9 += this.probabilities[var10];
            this.cumulativeProbabilities[var10] = var9;
        }

    }

    private static double[] normalizeArray(double[] values, double normalizedSum)
            throws MathIllegalArgumentException, MathArithmeticException {
        if (Double.isInfinite(normalizedSum)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_INFINITE);
        }
        if (Double.isNaN(normalizedSum)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_NAN);
        }
        double sum = 0d;
        final int len = values.length;
        double[] out = new double[len];
        for (int i = 0; i < len; i++) {
            if (Double.isInfinite(values[i])) {
                throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, values[i], i);
            }
            if (!Double.isNaN(values[i])) {
                sum += values[i];
            }
        }
        if (sum == 0) {
            throw new MathArithmeticException(LocalizedFormats.ARRAY_SUMS_TO_ZERO);
        }
        for (int i = 0; i < len; i++) {
            if (Double.isNaN(values[i])) {
                out[i] = Double.NaN;
            } else {
                out[i] = values[i] * normalizedSum / sum;
            }
        }
        return out;
    }

    public final T sample() {
        double randomValue = this.random.nextDouble();
        int index;
        if ((index = Arrays.binarySearch(this.cumulativeProbabilities, randomValue)) < 0) {
            index = -index - 1;
        }

        return index >= 0 && index < this.probabilities.length && randomValue < this.cumulativeProbabilities[index] ? this.singletons.get(index) : this.singletons.get(this.singletons.size() - 1);
    }
}
