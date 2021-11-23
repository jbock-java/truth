/*
 * Copyright (c) 2014 Google, Inc.
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

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.Tables;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for Table Subjects.
 *
 * @author Kurt Alfred Kluever
 */
class TableSubjectTest extends BaseSubjectTestCase {

    @Test
    void tableIsEmpty() {
        ImmutableTable<String, String, String> table = ImmutableTable.of();
        assertThat(table).isEmpty();
    }

    @Test
    void tableIsEmptyWithFailure() {
        ImmutableTable<Integer, Integer, Integer> table = ImmutableTable.of(1, 5, 7);
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(table)
                        .isEmpty());
        assertFailureKeys(
                failure,
                "expected to be empty", "but was");
    }

    @Test
    void tableIsNotEmpty() {
        ImmutableTable<Integer, Integer, Integer> table = ImmutableTable.of(1, 5, 7);
        assertThat(table).isNotEmpty();
    }

    @Test
    void tableIsNotEmptyWithFailure() {
        ImmutableTable<Integer, Integer, Integer> table = ImmutableTable.of();
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(table)
                        .isNotEmpty());
        assertFailureKeys(
                failure,
                "expected not to be empty");
    }

    @Test
    void hasSize() {
        assertThat(ImmutableTable.of(1, 2, 3)).hasSize(1);
    }

    @Test
    void hasSizeZero() {
        assertThat(ImmutableTable.of()).hasSize(0);
    }

    @Test
    void hasSizeNegative() {
        try {
            assertThat(ImmutableTable.of(1, 2, 3)).hasSize(-1);
            fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    void contains() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        assertThat(table).contains("row", "col");
    }

    @Test
    void containsFailure() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(table)
                        .contains("row", "row"));
        assertThat(failure)
                .hasMessageThat()
                .isEqualTo("Not true that <{row={col=val}}> contains mapping for row/column <row> <row>");
    }

    @Test
    void doesNotContain() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        assertThat(table).doesNotContain("row", "row");
        assertThat(table).doesNotContain("col", "row");
        assertThat(table).doesNotContain("col", "col");
        assertThat(table).doesNotContain(null, null);
    }

    @Test
    void doesNotContainFailure() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(table)
                        .doesNotContain("row", "col"));
        assertThat(failure)
                .hasMessageThat()
                .isEqualTo(
                        "Not true that <{row={col=val}}> does not contain mapping for "
                                + "row/column <row> <col>");
    }

    @Test
    void containsCell() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        assertThat(table).containsCell("row", "col", "val");
        assertThat(table).containsCell(cell("row", "col", "val"));
    }

    @Test
    void containsCellFailure() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(table)
                        .containsCell("row", "row", "val"));
        assertFailureKeys(
                failure,
                "value of", "expected to contain", "but was");
        assertFailureValue(
                failure,
                "value of", "table.cellSet()");
        assertFailureValue(
                failure,
                "expected to contain", "(row,row)=val");
        assertFailureValue(
                failure,
                "but was", "[(row,col)=val]");
    }

    @Test
    void doesNotContainCell() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        assertThat(table).doesNotContainCell("row", "row", "val");
        assertThat(table).doesNotContainCell("col", "row", "val");
        assertThat(table).doesNotContainCell("col", "col", "val");
        assertThat(table).doesNotContainCell(null, null, null);
        assertThat(table).doesNotContainCell(cell("row", "row", "val"));
        assertThat(table).doesNotContainCell(cell("col", "row", "val"));
        assertThat(table).doesNotContainCell(cell("col", "col", "val"));
        assertThat(table).doesNotContainCell(cell(null, null, null));
    }

    @Test
    void doesNotContainCellFailure() {
        ImmutableTable<String, String, String> table = ImmutableTable.of("row", "col", "val");
        AssertionError failure = assertThrows(
                AssertionError.class,
                () -> assertThat(table)
                        .doesNotContainCell("row", "col", "val"));
        assertFailureKeys(
                failure,
                "value of", "expected not to contain", "but was");
        assertFailureValue(
                failure,
                "value of", "table.cellSet()");
        assertFailureValue(
                failure,
                "expected not to contain", "(row,col)=val");
        assertFailureValue(
                failure,
                "but was", "[(row,col)=val]");
    }

    private static <R, C, V> Cell<R, C, V> cell(R row, C col, V val) {
        return Tables.immutableCell(row, col, val);
    }
}
