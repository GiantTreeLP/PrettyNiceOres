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
package gtlp.relocate.org.apache.commons.math3.exception;

import gtlp.relocate.org.apache.commons.math3.exception.util.Localizable;
import gtlp.relocate.org.apache.commons.math3.exception.util.LocalizedFormats;

/**
 * Exception to be thrown when a number is too small.
 *
 * @since 2.2
 */
class NumberIsTooSmallException extends MathIllegalNumberException {
    /**
     * Serializable version Id.
     */
    private static final long serialVersionUID = -6100997100383932834L;

    /**
     * Construct the exception.
     *
     * @param wrong          Value that is smaller than the minimum.
     * @param min            Minimum.
     * @param boundIsAllowed Whether {@code min} is included in the allowed range.
     */
    NumberIsTooSmallException(Number wrong,
                              Number min,
                              boolean boundIsAllowed) {
        this(boundIsAllowed ?
                        LocalizedFormats.NUMBER_TOO_SMALL :
                        LocalizedFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED,
                wrong, min);
    }

    /**
     * Construct the exception with a specific context.
     *
     * @param specific Specific context pattern.
     * @param wrong    Value that is smaller than the minimum.
     * @param min      Minimum.
     */
    private NumberIsTooSmallException(Localizable specific,
                                      Number wrong,
                                      Number min) {
        super(specific, wrong, min);
    }

}
