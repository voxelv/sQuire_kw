package org.reactfx.value;

import org.reactfx.Subscription;

import javafx.beans.value.ObservableValue;

class OrElseConst<T> extends ValBase<T> {
    private final ObservableValue<? extends T> src;
    private final T other;

    OrElseConst(ObservableValue<? extends T> src, T other) {
        this.src = src;
        this.other = other;
    }

    @Override
    protected T computeValue() {
        T val = src.getValue();
        return val != null ? val : other;
    }

    @Override
    protected Subscription connect() {
        return Val.observeInvalidations(src, obs -> invalidate());
    }
}
