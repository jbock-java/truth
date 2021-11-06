package com.google.common.truth.extension;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Employee extends Employee {

    private final String username;

    private final long id;

    private final String name;

    private final Employee.Location location;

    private final boolean isCeo;

    AutoValue_Employee(
            String username,
            long id,
            String name,
            Employee.Location location,
            boolean isCeo) {
        if (username == null) {
            throw new NullPointerException("Null username");
        }
        this.username = username;
        this.id = id;
        if (name == null) {
            throw new NullPointerException("Null name");
        }
        this.name = name;
        if (location == null) {
            throw new NullPointerException("Null location");
        }
        this.location = location;
        this.isCeo = isCeo;
    }

    @Override
    String username() {
        return username;
    }

    @Override
    long id() {
        return id;
    }

    @Override
    String name() {
        return name;
    }

    @Override
    Employee.Location location() {
        return location;
    }

    @Override
    boolean isCeo() {
        return isCeo;
    }

    @Override
    public String toString() {
        return "Employee{"
                + "username=" + username + ", "
                + "id=" + id + ", "
                + "name=" + name + ", "
                + "location=" + location + ", "
                + "isCeo=" + isCeo
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Employee) {
            Employee that = (Employee) o;
            return this.username.equals(that.username())
                    && this.id == that.id()
                    && this.name.equals(that.name())
                    && this.location.equals(that.location())
                    && this.isCeo == that.isCeo();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= username.hashCode();
        h$ *= 1000003;
        h$ ^= (int) ((id >>> 32) ^ id);
        h$ *= 1000003;
        h$ ^= name.hashCode();
        h$ *= 1000003;
        h$ ^= location.hashCode();
        h$ *= 1000003;
        h$ ^= isCeo ? 1231 : 1237;
        return h$;
    }

}
