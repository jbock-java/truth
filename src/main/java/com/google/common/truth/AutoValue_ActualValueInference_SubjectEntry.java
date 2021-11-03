package com.google.common.truth;

final class AutoValue_ActualValueInference_SubjectEntry extends ActualValueInference.SubjectEntry {

    private final ActualValueInference.InferredType type;

    private final ActualValueInference.StackEntry actualValue;

    AutoValue_ActualValueInference_SubjectEntry(
            ActualValueInference.InferredType type,
            ActualValueInference.StackEntry actualValue) {
        if (type == null) {
            throw new NullPointerException("Null type");
        }
        this.type = type;
        if (actualValue == null) {
            throw new NullPointerException("Null actualValue");
        }
        this.actualValue = actualValue;
    }

    @Override
    ActualValueInference.InferredType type() {
        return type;
    }

    @Override
    ActualValueInference.StackEntry actualValue() {
        return actualValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ActualValueInference.SubjectEntry) {
            ActualValueInference.SubjectEntry that = (ActualValueInference.SubjectEntry) o;
            return this.type.equals(that.type())
                    && this.actualValue.equals(that.actualValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= type.hashCode();
        h$ *= 1000003;
        h$ ^= actualValue.hashCode();
        return h$;
    }

}
