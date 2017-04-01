package com.myntra.gopi.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.myntra.gopi.R;
import com.myntra.gopi.db.DatabaseManager;
import com.myntra.gopi.domains.GameResultItem;
import com.myntra.gopi.utils.CommonUtils;

import static com.myntra.gopi.utils.RxApiUtil.build;
import static com.myntra.gopi.utils.ViewUtils.setVisibleView;

public class GameFinishActivity extends AppCompatActivity {

    public static final String NO_OF_MOVES = "NO_OF_MOVES";
    private TextView gameCongratulationsTv;
    private Button playAgain, seeAllResultsBtn;
    private int numberOfMoves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finish);
        numberOfMoves = CommonUtils.getIntegerIntentValue(getIntent(), NO_OF_MOVES);
        initViews();
        initData(numberOfMoves);
        initClicks();
        doMainLogic();
    }


    private void initData(int numberOfMoves) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        gameCongratulationsTv.setText(getString(R.string.congratulations_text) + numberOfMoves + " moves");
        gameCongratulationsTv.setAlpha(0f);
        gameCongratulationsTv.animate().translationY(-400).setInterpolator(new AccelerateDecelerateInterpolator()).alpha(1f)
                .setDuration(4000).start();
        setVisibleView(playAgain, seeAllResultsBtn);
    }

    private void initViews() {
        playAgain = ((Button) findViewById(R.id.game_play_again));
        seeAllResultsBtn = ((Button) findViewById(R.id.game_see_all_results));
        gameCongratulationsTv = (TextView) findViewById(R.id.game_congratulations_tv);
    }

    private void initClicks() {
        RxView.clicks(playAgain).subscribe(s -> {
            GameFinishActivity context = GameFinishActivity.this;
            Intent intent = new Intent(context, MemoryGameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.finish();
            context.startActivity(intent);
        });
        RxView.clicks(seeAllResultsBtn).subscribe(s -> {
            seeAllResultsFlow();
        });
    }

    private void seeAllResultsFlow() {
        GameFinishActivity context = GameFinishActivity.this;
        Intent intent = new Intent(context, GameSeeAllResultsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.finish();
        context.startActivity(intent);
    }

    private void doMainLogic() {
        build(DatabaseManager.insertGameResult(new GameResultItem(numberOfMoves + ""))).subscribe(insertedId -> {
            Log.d(GameFinishActivity.class.getSimpleName(), "Item inserted Successfully with id " + insertedId);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memory_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Toast.makeText(this, R.string.about_text, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_see_all_results) {
            seeAllResultsFlow();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
