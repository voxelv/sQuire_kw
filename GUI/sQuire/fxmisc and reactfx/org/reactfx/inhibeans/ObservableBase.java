package org.reactfx.inhibeans;

import org.reactfx.util.ListHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

@Deprecated
public abstract class ObservableBase implements Observable {
    private ListHelper<InvalidationListener> listeners;

    @Override
    public void addListener(InvalidationListener listener) {
        listeners = ListHelper.add(listeners, listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners = ListHelper.remove(listeners, listener);
    }

    protected void notifyListeners() {
        ListHelper.forEach(listeners, l -> l.invalidated(this));
    }
}