package com.google.common.truth;

final class AutoValue_ActualValueInference_InferredType extends ActualValueInference.InferredType {

    private final String descriptor;

    AutoValue_ActualValueInference_InferredType(
            String descriptor) {
        if (descriptor == null) {
            throw new NullPointerException("Null descriptor");
        }
        this.descriptor = descriptor;
    }

    @Override
    String descriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ActualValueInference.InferredType) {
            ActualValueInference.InferredType that = (ActualValueInference.InferredType) o;
            return this.descriptor.equals(that.descriptor());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= descriptor.hashCode();
        return h$;
    }

}
