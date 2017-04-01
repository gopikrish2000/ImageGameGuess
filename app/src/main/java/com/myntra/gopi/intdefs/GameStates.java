package com.myntra.gopi.intdefs;

import android.support.annotation.StringDef;

/**
 * Created by gopikrishna on 27/11/16.
 */
@StringDef(value = {
        GameStates.NORMAL, GameStates.FLIPPED,
        GameStates.GUESSED_CORRECTLY
})
public @interface GameStates {
    String NORMAL = "NORMAL";
    String FLIPPED = "FLIPPED";
    String GUESSED_CORRECTLY = "GUESSED_CORRECTLY";
}
