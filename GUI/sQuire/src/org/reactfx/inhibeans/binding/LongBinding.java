package org.reactfx.inhibeans.binding;

import org.reactfx.Guard;
import org.reactfx.value.Val;

import com.sun.javafx.binding.ExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Inhibitory version of {@link javafx.beans.binding.LongBinding}.
 */
@Deprecated
public abstract class LongBinding
extends javafx.beans.binding.LongBinding
implements Binding<Number> {

    /**
     * @deprecated Use {@link Val#suspendable(javafx.beans.value.ObservableValue)}.
     */
    @Deprecated
    public static LongBinding wrap(ObservableValue<? extends Number> source) {
        return new LongBinding() {
            { bind(source); }

            @Override
            protected long computeValue() { return source.getValue().longValue(); }
        };
    }

    private ExpressionHelper<Number> helper = null;
    private boolean blocked = false;
    private boolean fireOnRelease = false;

    @Override
    public Guard block() {
        if(blocked) {
            return Guard.EMPTY_GUARD;
        } else {
            blocked = true;
            return this::release;
        }
    }

    private void release() {
        blocked = false;
        if(fireOnRelease) {
            fireOnRelease = false;
            ExpressionHelper.fireValueChangedEvent(helper);
        }
    }

    @Override
    protected final void onInvalidating() {
        if(blocked)
            fireOnRelease = true;
        else
            ExpressionHelper.fireValueChangedEvent(helper);
    }


    /*******************************************
     *** Override adding/removing listeners. ***
     *******************************************/

    @Override
    public void addListener(InvalidationListener listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

    @Override
    public void addListener(ChangeListener<? super Number> listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

}
