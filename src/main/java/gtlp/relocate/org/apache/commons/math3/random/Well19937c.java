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
package gtlp.relocate.org.apache.commons.math3.random;


/**
 * This class implements the WELL19937c pseudo-random number generator
 * from Fran&ccedil;ois Panneton, Pierre L'Ecuyer and Makoto Matsumoto.
 * <p>
 * <p>This generator is described in a paper by Fran&ccedil;ois Panneton,
 * Pierre L'Ecuyer and Makoto Matsumoto <a
 * href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng.pdf">Improved
 * Long-Period Generators Based on Linear Recurrences Modulo 2</a> ACM
 * Transactions on Mathematical Software, 32, 1 (2006). The errata for the paper
 * are in <a href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/wellrng-errata.txt">wellrng-errata.txt</a>.</p>
 *
 * @see <a href="http://www.iro.umontreal.ca/~panneton/WELLRNG.html">WELL Random number generator</a>
 * @since 2.2
 */
public class Well19937c {

    /**
     * Number of bits in the pool.
     */
    private static final int K = 19937;
    /**
     * First parameter of the algorithm.
     */
    private static final int M1 = 70;
    /**
     * Second parameter of the algorithm.
     */
    private static final int M2 = 179;
    /**
     * Third parameter of the algorithm.
     */
    private static final int M3 = 449;
    /**
     * Bytes pool.
     */
    private final int[] v;
    /**
     * Index indirection table giving for each index its predecessor taking table size into account.
     */
    private final int[] iRm1;
    /**
     * Index indirection table giving for each index its second predecessor taking table size into account.
     */
    private final int[] iRm2;
    /**
     * Index indirection table giving for each index the value index + m1 taking table size into account.
     */
    private final int[] i1;
    /**
     * Index indirection table giving for each index the value index + m2 taking table size into account.
     */
    private final int[] i2;
    /**
     * Index indirection table giving for each index the value index + m3 taking table size into account.
     */
    private final int[] i3;
    /**
     * Current index in the bytes pool.
     */
    private int index;

    /**
     * Creates a new random number generator.
     * <p>The instance is initialized using the current time as the
     * seed.</p>
     */
    public Well19937c() {
        // the bits pool contains k bits, k = r w - p where r is the number
        // of w bits blocks, w is the block size (always 32 in the original paper)
        // and p is the number of unused bits in the last block
        final int w = 32;
        final int r = (K + w - 1) / w;
        v = new int[r];
        index = 0;

        // precompute indirection index tables. These tables are used for optimizing access
        // they allow saving computations like "(j + r - 2) % r" with costly modulo operations
        iRm1 = new int[r];
        iRm2 = new int[r];
        i1 = new int[r];
        i2 = new int[r];
        i3 = new int[r];
        for (int j = 0; j < r; ++j) {
            iRm1[j] = (j + r - 1) % r;
            iRm2[j] = (j + r - 2) % r;
            i1[j] = (j + M1) % r;
            i2[j] = (j + M2) % r;
            i3[j] = (j + M3) % r;
        }

        // initialize the pool content
        setSeed(null);
    }

    /**
     * Reinitialize the generator as if just built with the given int array seed.
     * <p>The state of the generator is exactly the same as a new
     * generator built with the same seed.</p>
     *
     * @param seed the initial seed (32 bits integers array). If null
     *             the seed of the generator will be the system time plus the system identity
     *             hash code of the instance.
     */
    private void setSeed(final int[] seed) {
        if (seed == null) {
            setSeed(System.currentTimeMillis() + System.identityHashCode(this));
            return;
        }

        System.arraycopy(seed, 0, v, 0, seed.length <= v.length ? seed.length : v.length);

        if (seed.length < v.length) {
            for (int i = seed.length; i < v.length; ++i) {
                final long l = v[i - seed.length];
                v[i] = (int) ((1812433253L * (l ^ (l >> 30)) + i) & 0xffffffffL);
            }
        }

        index = 0;
    }

    /**
     * Reinitialize the generator as if just built with the given long seed.
     * <p>The state of the generator is exactly the same as a new
     * generator built with the same seed.</p>
     *
     * @param seed the initial seed (64 bits integer)
     */
    private void setSeed(final long seed) {
        setSeed(new int[]{(int) (seed >>> 32), (int) (seed & 0xffffffffL)});
    }


    private int next(final int bits) {

        final int indexRm1 = iRm1[index];
        final int indexRm2 = iRm2[index];

        final int v0 = v[index];
        final int vM1 = v[i1[index]];
        final int vM2 = v[i2[index]];
        final int vM3 = v[i3[index]];

        final int z0 = (0x80000000 & v[indexRm1]) ^ (0x7FFFFFFF & v[indexRm2]);
        final int z1 = (v0 ^ (v0 << 25)) ^ (vM1 ^ (vM1 >>> 27));
        final int z2 = (vM2 >>> 9) ^ (vM3 ^ (vM3 >>> 1));
        final int z3 = z1 ^ z2;
        int z4 = z0 ^ (z1 ^ (z1 << 9)) ^ (z2 ^ (z2 << 21)) ^ (z3 ^ (z3 >>> 21));

        v[index] = z3;
        v[indexRm1] = z4;
        v[indexRm2] &= 0x80000000;
        index = indexRm1;


        // add Matsumoto-Kurita tempering
        // to get a maximally-equidistributed generator
        z4 ^= (z4 << 7) & 0xe46e1700;
        z4 ^= (z4 << 15) & 0x9b868000;

        return z4 >>> (32 - bits);

    }

    /**
     * {@inheritDoc}
     */
    public final double nextDouble() {
        long high = (long) this.next(26) << 26;
        int low = this.next(26);
        return (double) (high | (long) low) * 2.220446049250313E-16D;
    }

}
