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
 * Interface extracted from <code>java.util.Random</code>.  This interface is
 * implemented by {@link }.
 *
 * @since 1.1
 */
public interface RandomGenerator {

    /**
     * Sets the seed of the underlying random number generator using an
     * <code>int</code> array seed.
     * <p>Sequences of values generated starting with the same seeds
     * should be identical.
     * </p>
     *
     * @param seed the seed value
     */
    void setSeed(int[] seed);

    /**
     * Sets the seed of the underlying random number generator using a
     * <code>long</code> seed.
     * <p>Sequences of values generated starting with the same seeds
     * should be identical.
     * </p>
     *
     * @param seed the seed value
     */
    void setSeed(long seed);

    /**
     * Returns the next pseudorandom, uniformly distributed
     * <code>double</code> value between <code>0.0</code> and
     * <code>1.0</code> from this random number generator's sequence.
     *
     * @return the next pseudorandom, uniformly distributed
     * <code>double</code> value between <code>0.0</code> and
     * <code>1.0</code> from this random number generator's sequence
     */
    double nextDouble();

}
