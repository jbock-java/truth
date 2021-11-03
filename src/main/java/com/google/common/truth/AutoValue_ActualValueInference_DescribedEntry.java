package com.google.common.truth;

final class AutoValue_ActualValueInference_DescribedEntry extends ActualValueInference.DescribedEntry {

    private final ActualValueInference.InferredType type;

    private final String description;

    AutoValue_ActualValueInference_DescribedEntry(
            ActualValueInference.InferredType type,
            String description) {
        if (type == null) {
            throw new NullPointerException("Null type");
        }
        this.type = type;
        if (description == null) {
            throw new NullPointerException("Null description");
        }
        this.description = description;
    }

    @Override
    ActualValueInference.InferredType type() {
        return type;
    }

    @Override
    String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ActualValueInference.DescribedEntry) {
            ActualValueInference.DescribedEntry that = (ActualValueInference.DescribedEntry) o;
            return this.type.equals(that.type())
                    && this.description.equals(that.description());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= type.hashCode();
        h$ *= 1000003;
        h$ ^= description.hashCode();
        return h$;
    }

}
