package com.myntra.gopi;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.myntra.gopi.activities.MemoryGameActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.myntra.gopi.util.EspressoUtil.getText;
import static com.myntra.gopi.util.EspressoUtil.waitForSeconds;
import static com.myntra.gopi.utils.CommonUtils.stringToInt;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;


/**
 * Created by gopikrishna on 26/11/16.
 */

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MemoryGameEspressInstrumentedTest {

    @Rule
    public ActivityTestRule<MemoryGameActivity> mActivityRule = new ActivityTestRule<>(MemoryGameActivity.class);

    @Test
    public void globalTestCase() throws Exception {

        aCheckCountDownTimerShown();
        bCheckMovesNCorrectGuessNotShown();
        cCheckBeforeGameStatsClickShouldnotShowMoves();
        dWaitForGameToStartNCheckCountdownNotShown();
        eAfterGameStartedClickNCheckMovesView();
        fAfterGameStartedOnClickMovesCountIncreaseTest();
    }

    //    @Test
    public void aCheckCountDownTimerShown() throws Exception {
        onView(isRoot()).perform(waitForSeconds(12));
        onView(withId(R.id.game_count_down_tv)).check(matches(isDisplayed()));
    }

    //    @Test
    public void bCheckMovesNCorrectGuessNotShown() throws Exception {
        onView(withId(R.id.game_moves_tv)).check(matches(not(isDisplayed())));
        onView(withId(R.id.game_correct_guess_tv)).check(matches(not(isDisplayed())));
    }

    //    @Test
    public void cCheckBeforeGameStatsClickShouldnotShowMoves() throws Exception {
        onView(withId(R.id.game_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.game_moves_tv)).check(matches(not(isDisplayed())));
    }

    //    @Test
    public void dWaitForGameToStartNCheckCountdownNotShown() throws Exception {
        onView(isRoot()).perform(waitForSeconds(15));
        onView(withId(R.id.game_count_down_tv)).check(matches(not(isDisplayed())));
    }

    //    @Test
    public void eAfterGameStartedClickNCheckMovesView() throws Exception {
        onView(withId(R.id.game_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.game_moves_tv)).check(matches(isDisplayed()));
        onView(withId(R.id.game_correct_guess_tv)).check(matches(isDisplayed()));
    }

    public void fAfterGameStartedOnClickMovesCountIncreaseTest() throws Exception {
        onView(isRoot()).perform(waitForSeconds(5));
        String movesText = getText(withId(R.id.game_moves_tv));
        onView(withId(R.id.game_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        String updatedMovesText = getText(withId(R.id.game_moves_tv));
        assertTrue(stringToInt(updatedMovesText) > stringToInt(movesText));
    }


}
