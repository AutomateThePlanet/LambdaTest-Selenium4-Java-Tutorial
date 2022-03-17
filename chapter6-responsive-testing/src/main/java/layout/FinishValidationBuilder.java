/*
 * Copyright 2021 Automate The Planet Ltd.
 * Author: Anton Angelov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package layout;

import lombok.Getter;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinishValidationBuilder {

    @Getter private final Predicate comparingFunction;
    private String notificationMessage;
    private String failedAssertionMessage;

    public FinishValidationBuilder(Predicate comparingFunction, Supplier<String> notificationMessageFunction, Supplier<String> failedAssertionMessageFunction) {
        this.comparingFunction = comparingFunction;
        this.notificationMessage = notificationMessageFunction.get();
        this.failedAssertionMessage = failedAssertionMessageFunction.get();
    }

    public FinishValidationBuilder(Predicate comparingFunction) {
        this.comparingFunction = comparingFunction;
    }

    public void validate() {
        assertTrue(comparingFunction.test(true), failedAssertionMessage);
    }
}
