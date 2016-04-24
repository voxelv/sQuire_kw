package org.fxmisc.wellbehaved.event.experimental.template;

import org.fxmisc.wellbehaved.event.experimental.InputHandler.Result;

import javafx.event.Event;

@FunctionalInterface
public interface InputHandlerTemplate<S, E extends Event> {

    Result process(S state, E event);

}