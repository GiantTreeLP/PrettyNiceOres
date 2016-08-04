/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gtlp.relocate.org.apache.commons.math3.distribution;

import com.google.common.collect.Lists;
import gtlp.relocate.org.apache.commons.math3.random.Well19937c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>A generic implementation of a
 * <a href="http://en.wikipedia.org/wiki/Probability_distribution#Discrete_probability_distribution">
 * discrete probability distribution (Wikipedia)</a> over a finite sample space,
 * based on an enumerated list of &lt;value, probability&gt; pairs.  Input probabilities must all be non-negative,
 * but zero values are allowed and their sum does not have to equal one. Constructors will normalize input
 * probabilities to make them sum to one.</p>
 * <p>
 * <p>The list of <value, probability> pairs does not, strictly speaking, have to be a function and it can
 * contain null values.  The pmf created by the constructor will combine probabilities of equal values and
 * will treat null values as equal.  For example, if the list of pairs &lt;"dog", 0.2&gt;, &lt;null, 0.1&gt;,
 * &lt;"pig", 0.2&gt;, &lt;"dog", 0.1&gt;, &lt;null, 0.4&gt; is provided to the constructor, the resulting
 * pmf will assign mass of 0.5 to null, 0.3 to "dog" and 0.2 to null.</p>
 *
 * @param <T> type of the elements in the sample space.
 * @since 3.2
 */
public final class EnumeratedDistribution<T> {
    private final ArrayList<T> singletons;
    private final double[] probabilities;
    private final double[] cumulativeProbabilities;
    private Well19937c random;

    /**
     * Create an enumerated distribution using the given probability mass function
     * enumeration.
     * <p>
     * <b>Note:</b> this constructor will implicitly create an instance of
     * {@link Well19937c} as random generator to be used for sampling only (see
     * {@link #sample()}. In case no sampling is
     * needed for the created distribution, it is advised to pass {@code null}
     * as random generator via the appropriate constructors to avoid the
     * additional initialisation overhead.
     *
     * @param pmf probability mass function enumerated as a list of <T, probability>
     *            pairs.
     */
    public EnumeratedDistribution(List<Pair<T, Double>> pmf) throws IllegalArgumentException {
        this.random = new Well19937c();
        this.singletons = Lists.newArrayListWithCapacity(pmf.size());
        double[] probs = new double[pmf.size()];

        for (int sum = 0; sum < pmf.size(); ++sum) {
            final Pair<T, Double> sample = pmf.get(sum);
            singletons.add(sample.getKey());
            final double p = sample.getValue();
            if (p < 0.0D) {
                throw new IllegalArgumentException(String.format("Probability less than 0 %f", sample.getValue()));
            }

            if (Double.isInfinite(p)) {
                throw new IllegalArgumentException("Probability is infinite");
            }

            if (Double.isNaN(p)) {
                throw new IllegalArgumentException("Probability is not a number (NaN)");
            }

            probs[sum] = p;
        }

        probabilities = normalizeArray(probs, 1.0D);
        cumulativeProbabilities = new double[probabilities.length];
        double sum = 0.0D;

        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            cumulativeProbabilities[i] = sum;
        }
    }

    /**
     * Normalizes an array to make it sum to a specified value.
     * Returns the result of the transformation
     * <pre>
     *    x |-> x * normalizedSum / sum
     * </pre>
     * applied to each non-NaN element x of the input array, where sum is the
     * sum of the non-NaN entries in the input array.
     * <p>
     * Throws IllegalArgumentException if {@code normalizedSum} is infinite
     * or NaN and ArithmeticException if the input array contains any infinite elements
     * or sums to 0.
     * <p>
     * Ignores (i.e., copies unchanged to the output array) NaNs in the input array.
     *
     * @param values        Input array to be normalized
     * @param normalizedSum Target sum for the normalized array
     * @return the normalized array.
     * @since 2.1
     */
    private static double[] normalizeArray(double[] values, double normalizedSum)
            throws IllegalArgumentException, ArithmeticException {
        if (Double.isInfinite(normalizedSum)) {
            throw new IllegalArgumentException("Cannot normalize to an infinite value");
        }
        if (Double.isNaN(normalizedSum)) {
            throw new IllegalArgumentException("Cannot normalize to NaN");
        }
        double sum = 0.0D;
        final int len = values.length;
        double[] out = new double[len];
        for (int i = 0; i < len; i++) {
            if (Double.isInfinite(values[i])) {
                throw new IllegalArgumentException(String.format("Array contains an infinite element, %f at index %d", values[i], i));
            }
            if (!Double.isNaN(values[i])) {
                sum += values[i];
            }
        }
        if (sum == 0) {
            throw new ArithmeticException("array sums to zero");
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

    /**
     * Generate a random value sampled from this distribution.
     *
     * @return a random value.
     */
    public final T sample() {
        final double randomValue = random.nextDouble();
        int index = Arrays.binarySearch(cumulativeProbabilities, randomValue);
        if (index < 0) {
            index = -index - 1;
        }

        if (index >= 0 && index < probabilities.length && randomValue < cumulativeProbabilities[index]) {
            return singletons.get(index);
        }

        return singletons.get(singletons.size() - 1);
    }

    /**
     * Generic pair.
     * <br/>
     * Although the instances of this class are immutable, it is impossible
     * to ensure that the references passed to the constructor will not be
     * modified by the caller.
     *
     * @param <K> Key type.
     * @param <V> Value type.
     * @since 3.0
     */
    public static class Pair<K, V> {
        /**
         * Key.
         */
        private final K key;
        /**
         * Value.
         */
        private final V value;

        /**
         * Create an entry representing a mapping from the specified key to the
         * specified value.
         *
         * @param k Key (first element of the pair).
         * @param v Value (second element of the pair).
         */
        public Pair(K k, V v) {
            key = k;
            value = v;
        }

        /**
         * Get the key.
         *
         * @return the key (first element of the pair).
         */
        K getKey() {
            return key;
        }

        /**
         * Get the value.
         *
         * @return the value (second element of the pair).
         */
        V getValue() {
            return value;
        }

        /**
         * Compare the specified object with this entry for equality.
         *
         * @param o Object.
         * @return {@code true} if the given object is also a map entry and
         * the two entries represent the same mapping.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof EnumeratedDistribution.Pair)) {
                return false;
            } else {
                Pair<?, ?> oP = (Pair<?, ?>) o;
                return (key == null ?
                        oP.key == null :
                        key.equals(oP.key)) &&
                        (value == null ?
                                oP.value == null :
                                value.equals(oP.value));
            }
        }

        /**
         * Compute a hash code.
         *
         * @return the hash code value.
         */
        @Override
        public int hashCode() {
            int result = key == null ? 0 : key.hashCode();

            final int h = value == null ? 0 : value.hashCode();
            result = 37 * result + h ^ (h >>> 16);

            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "[" + getKey() + ", " + getValue() + "]";
        }

    }
}
