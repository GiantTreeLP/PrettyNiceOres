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
 * Base class for random number generators that generates bits streams.
 *
 * @since 2.0
 */
abstract class BitsStreamGenerator implements RandomGenerator {
    BitsStreamGenerator() {
    }

    /**
     * Generate next pseudorandom number.
     * <p>This method is the core generation algorithm. It is used by all the
     * public generation methods for the various primitive types.</p>
     *
     * @param bits number of random bits to produce
     * @return random bits generated
     */
    protected abstract int next(int bits);

    /** {@inheritDoc} */
    public final double nextDouble() {
        long high = (long) this.next(26) << 26;
        int low = this.next(26);
        return (double) (high | (long) low) * 2.220446049250313E-16D;
    }

}
