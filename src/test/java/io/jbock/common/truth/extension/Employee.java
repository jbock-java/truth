/*
 * Copyright (c) 2015 Google, Inc.
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
package io.jbock.common.truth.extension;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/** Represents an employee. */
public final class Employee {

    private final String username;
    private final long id;
    private final String name;
    private final Employee.Location location;
    private final boolean isCeo;

    Employee(
            String username,
            long id,
            String name,
            Employee.Location location,
            boolean isCeo) {
        this.username = requireNonNull(username);
        this.id = id;
        this.name = requireNonNull(name);
        this.location = requireNonNull(location);
        this.isCeo = isCeo;
    }

    public static Employee create(
            String username, long id, String name, Location location, boolean isCeo) {
        return new Employee(username, id, name, location, isCeo);
    }

    String username() {
        return username;
    }

    long id() {
        return id;
    }

    String name() {
        return name;
    }

    Location location() {
        return location;
    }

    boolean isCeo() {
        return isCeo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && isCeo == employee.isCeo && username.equals(employee.username) && name.equals(employee.name) && location == employee.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, id, name, location, isCeo);
    }

    public enum Location {
        MTV,
        PIT,
        CHI,
        NYC
    }
}
