package com.google.common.truth;

final class AutoValue_ActualValueInference_OpaqueEntry extends ActualValueInference.OpaqueEntry {

    private final ActualValueInference.InferredType type;

    AutoValue_ActualValueInference_OpaqueEntry(
            ActualValueInference.InferredType type) {
        if (type == null) {
            throw new NullPointerException("Null type");
        }
        this.type = type;
    }

    @Override
    ActualValueInference.InferredType type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ActualValueInference.OpaqueEntry) {
            ActualValueInference.OpaqueEntry that = (ActualValueInference.OpaqueEntry) o;
            return this.type.equals(that.type());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= type.hashCode();
        return h$;
    }

}
