package com.google.common.truth;

import java.util.List;

final class AutoValue_ActualValueInference_FrameInfo extends ActualValueInference.FrameInfo {

    private final List<ActualValueInference.StackEntry> locals;

    private final List<ActualValueInference.StackEntry> stack;

    AutoValue_ActualValueInference_FrameInfo(
            List<ActualValueInference.StackEntry> locals,
            List<ActualValueInference.StackEntry> stack) {
        if (locals == null) {
            throw new NullPointerException("Null locals");
        }
        this.locals = locals;
        if (stack == null) {
            throw new NullPointerException("Null stack");
        }
        this.stack = stack;
    }

    @Override
    List<ActualValueInference.StackEntry> locals() {
        return locals;
    }

    @Override
    List<ActualValueInference.StackEntry> stack() {
        return stack;
    }

    @Override
    public String toString() {
        return "FrameInfo{"
                + "locals=" + locals + ", "
                + "stack=" + stack
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ActualValueInference.FrameInfo) {
            ActualValueInference.FrameInfo that = (ActualValueInference.FrameInfo) o;
            return this.locals.equals(that.locals())
                    && this.stack.equals(that.stack());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= locals.hashCode();
        h$ *= 1000003;
        h$ ^= stack.hashCode();
        return h$;
    }

}
