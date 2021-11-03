package com.google.common.truth;

final class AutoValue_ActualValueInference_Invocation extends ActualValueInference.Invocation {

    private final ActualValueInference.StackEntry receiver;

    private final ActualValueInference.StackEntry actualValue;

    private final ActualValueInference.StackEntry boxingInput;

    private final String name;

    private AutoValue_ActualValueInference_Invocation(
            ActualValueInference.StackEntry receiver,
            ActualValueInference.StackEntry actualValue,
            ActualValueInference.StackEntry boxingInput,
            String name) {
        this.receiver = receiver;
        this.actualValue = actualValue;
        this.boxingInput = boxingInput;
        this.name = name;
    }

    @Override
    ActualValueInference.StackEntry receiver() {
        return receiver;
    }

    @Override
    ActualValueInference.StackEntry actualValue() {
        return actualValue;
    }

    @Override
    ActualValueInference.StackEntry boxingInput() {
        return boxingInput;
    }

    @Override
    String name() {
        return name;
    }

    @Override
    public String toString() {
        return "Invocation{"
                + "receiver=" + receiver + ", "
                + "actualValue=" + actualValue + ", "
                + "boxingInput=" + boxingInput + ", "
                + "name=" + name
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ActualValueInference.Invocation) {
            ActualValueInference.Invocation that = (ActualValueInference.Invocation) o;
            return (this.receiver == null ? that.receiver() == null : this.receiver.equals(that.receiver()))
                    && (this.actualValue == null ? that.actualValue() == null : this.actualValue.equals(that.actualValue()))
                    && (this.boxingInput == null ? that.boxingInput() == null : this.boxingInput.equals(that.boxingInput()))
                    && this.name.equals(that.name());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= (receiver == null) ? 0 : receiver.hashCode();
        h$ *= 1000003;
        h$ ^= (actualValue == null) ? 0 : actualValue.hashCode();
        h$ *= 1000003;
        h$ ^= (boxingInput == null) ? 0 : boxingInput.hashCode();
        h$ *= 1000003;
        h$ ^= name.hashCode();
        return h$;
    }

    static final class Builder extends ActualValueInference.Invocation.Builder {
        private ActualValueInference.StackEntry receiver;
        private ActualValueInference.StackEntry actualValue;
        private ActualValueInference.StackEntry boxingInput;
        private String name;

        Builder() {
        }

        @Override
        ActualValueInference.Invocation.Builder setReceiver(ActualValueInference.StackEntry receiver) {
            this.receiver = receiver;
            return this;
        }

        @Override
        ActualValueInference.Invocation.Builder setActualValue(ActualValueInference.StackEntry actualValue) {
            this.actualValue = actualValue;
            return this;
        }

        @Override
        ActualValueInference.Invocation.Builder setBoxingInput(ActualValueInference.StackEntry boxingInput) {
            this.boxingInput = boxingInput;
            return this;
        }

        @Override
        ActualValueInference.Invocation.Builder setName(String name) {
            if (name == null) {
                throw new NullPointerException("Null name");
            }
            this.name = name;
            return this;
        }

        @Override
        ActualValueInference.Invocation build() {
            if (this.name == null) {
                String missing = " name";
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_ActualValueInference_Invocation(
                    this.receiver,
                    this.actualValue,
                    this.boxingInput,
                    this.name);
        }
    }

}
