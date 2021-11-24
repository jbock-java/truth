/*
 * Copyright (c) 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.truth;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assert_;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests for chained subjects (produced with {@link Subject#check(String, Object...)}, etc.). */
final class ChainingTest extends BaseSubjectTestCase {
    private static final Throwable throwable = new Throwable("root");

    @Test
    void noChaining() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "message");
    }

    @Test
    void oneLevel() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingTo("child")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "message");
    }

    @Test
    void twoLevels() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingTo("child")
                        .delegatingTo("grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "message");
    }

    @Test
    void noChainingRootThrowable() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that(throwable)
                        .isThePresentKingOfFrance());
        assertHasCause(
                failure,
                "message");
    }

    @Test
    void oneLevelRootThrowable() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that(throwable)
                        .delegatingTo("child")
                        .isThePresentKingOfFrance());
        assertHasCause(
                failure,
                "message");
    }

    @Test
    void twoLevelsRootThrowable() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that(throwable)
                        .delegatingTo("child")
                        .delegatingTo("grandchild")
                        .isThePresentKingOfFrance());
        assertHasCause(
                failure,
                "message");
    }

    // e.g., future.failureCause()
    @Test
    void oneLevelDerivedThrowable() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingTo(throwable)
                        .isThePresentKingOfFrance());
        assertHasCause(
                failure,
                "message");
    }

    @Test
    void twoLevelsDerivedThrowableMiddle() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingTo(throwable)
                        .delegatingTo("grandchild")
                        .isThePresentKingOfFrance());
        assertHasCause(
                failure,
                "message");
    }

    @Test
    void twoLevelsDerivedThrowableLast() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("child")
                        .delegatingTo("child")
                        .delegatingTo(throwable)
                        .isThePresentKingOfFrance());
        assertHasCause(
                failure,
                "message");
    }

    @Test
    void oneLevelNamed() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamed("child", "child")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of    : myObject.child\nmessage\nmyObject was: root");
    }

    @Test
    void twoLevelsNamed() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamed("child", "child")
                        .delegatingToNamed("grandchild", "grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of    : myObject.child.grandchild\nmessage\nmyObject was: root");
    }

    @Test
    void twoLevelsOnlyFirstNamed() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamed("child", "child")
                        .delegatingTo("grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "message\nmyObject was: root");
    }

    @Test
    void twoLevelsOnlySecondNamed() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingTo("child")
                        .delegatingToNamed("grandchild", "grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of    : myObject.grandchild\nmessage\nmyObject was: root");
    }

    @Test
    void oneLevelNamedNoNeedToDisplayBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamedNoNeedToDisplayBoth("child", "child")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of: myObject.child\nmessage");
    }

    @Test
    void twoLevelsNamedNoNeedToDisplayBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamedNoNeedToDisplayBoth("child", "child")
                        .delegatingToNamedNoNeedToDisplayBoth("grandchild", "grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of: myObject.child.grandchild\nmessage");
    }

    @Test
    void twoLevelsOnlyFirstNamedNoNeedToDisplayBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamedNoNeedToDisplayBoth("child", "child")
                        .delegatingTo("grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "message");
    }

    @Test
    void twoLevelsOnlySecondNamedNoNeedToDisplayBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingTo("child")
                        .delegatingToNamedNoNeedToDisplayBoth("grandchild", "grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of: myObject.grandchild\nmessage");
    }

    @Test
    void twoLevelsNamedOnlyFirstNoNeedToDisplayBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamedNoNeedToDisplayBoth("child", "child")
                        .delegatingToNamed("grandchild", "grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of    : myObject.child.grandchild\nmessage\nmyObject was: root");
    }

    @Test
    void twoLevelsNamedOnlySecondNoNeedToDisplayBoth() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .delegatingToNamed("child", "child")
                        .delegatingToNamedNoNeedToDisplayBoth("grandchild", "grandchild")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "value of    : myObject.child.grandchild\nmessage\nmyObject was: root");
    }

    @Test
    void namedAndMessage() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> StandardSubjectBuilder.forCustomFailureStrategy(f -> {
                            throw f;
                        })
                        .withMessage("prefix")
                        .about(myObjects())
                        .that("root")
                        .delegatingToNamed("child", "child")
                        .isThePresentKingOfFrance());
        assertNoCause(
                failure,
                "prefix\nvalue of    : myObject.child\nmessage\nmyObject was: root");
    }

    @Test
    void checkFail() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .doCheckFail());
        assertNoCause(
                failure,
                "message");
    }

    @Test
    void checkFailWithName() {
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertAbout(myObjects())
                        .that("root")
                        .doCheckFail("child"));
        assertNoCause(
                failure,
                "message\nvalue of    : myObject.child\nmyObject was: root");
    }

    @Test
    void badFormat() {
        try {
            Object unused = assertThat("root").check("%s %s", 1, 2, 3);
            assert_().fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    /*
     * TODO(cpovirk): It would be nice to have multiple Subject subclasses so that we know we're
     * pulling the type from the right link in the chain. But we get some coverage of that from other
     * tests like MultimapSubjectTest.
     */

    private static final class MyObjectSubject extends Subject {
        static final Factory<MyObjectSubject, Object> FACTORY =
                MyObjectSubject::new;

        private MyObjectSubject(FailureMetadata metadata, Object actual) {
            super(metadata, actual);
        }

        /** Runs a check that always fails with the generic message "message." */
        void isThePresentKingOfFrance() {
            failWithoutActual(simpleFact("message"));
        }

        void doCheckFail() {
            check().withMessage("message").fail();
        }

        void doCheckFail(String name) {
            check(name).withMessage("message").fail();
        }

        /**
         * Returns a new {@code MyObjectSubject} for the given actual value, chaining it to the current
         * subject with {@link Subject#check}.
         */
        MyObjectSubject delegatingTo(Object actual) {
            return check().about(myObjects()).that(actual);
        }

        /**
         * Returns a new {@code MyObjectSubject} for the given actual value, chaining it to the current
         * subject with {@link Subject#check}.
         */
        MyObjectSubject delegatingToNamed(Object actual, String name) {
            return check(name).about(myObjects()).that(actual);
        }

        MyObjectSubject delegatingToNamedNoNeedToDisplayBoth(Object actual, String name) {
            return checkNoNeedToDisplayBothValues(name).about(myObjects()).that(actual);
        }
    }

    private static Subject.Factory<MyObjectSubject, Object> myObjects() {
        return MyObjectSubject.FACTORY;
    }

    private void assertNoCause(AssertionError failure, String message) {
        assertThat(failure).hasMessageThat().isEqualTo(message);
        assertThat(failure).hasCauseThat().isNull();
    }

    private void assertHasCause(AssertionError failure, String message) {
        assertThat(failure).hasMessageThat().isEqualTo(message);
        assertThat(failure).hasCauseThat().isEqualTo(throwable);
    }
}
