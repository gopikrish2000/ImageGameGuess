package com.myntra.gopi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myntra.gopi.GameConfig;
import com.myntra.gopi.R;
import com.myntra.gopi.adapters.MemoryGameAdapter;
import com.myntra.gopi.domains.GameItem;
import com.myntra.gopi.intdefs.GameStates;
import com.myntra.gopi.interfaces.MVPMemoryGameInterface;
import com.myntra.gopi.presenters.MemoryGamePresenter;
import com.myntra.gopi.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.myntra.gopi.utils.CommonUtils.doUnsubscribe;
import static com.myntra.gopi.utils.CommonUtils.isNonNull;
import static com.myntra.gopi.utils.CommonUtils.isNullOrEmpty;
import static com.myntra.gopi.utils.CommonUtils.loadImageWithGlide;
import static com.myntra.gopi.utils.CommonUtils.stopMediaPlayer;
import static com.myntra.gopi.utils.GameConstants.CURRENT_INDEX_TO_BE_GUESSED;
import static com.myntra.gopi.utils.GameConstants.GAME_LIST;
import static com.myntra.gopi.utils.GameConstants.NON_GUESSED_INDICES;
import static com.myntra.gopi.utils.GameConstants.NO_OF_CORRECT_GUESSES;
import static com.myntra.gopi.utils.GameConstants.NO_OF_MOVES;
import static com.myntra.gopi.utils.ViewUtils.setGone;
import static com.myntra.gopi.utils.ViewUtils.setVisibleView;

/*
* ***** Components Used/Done in this project. ***
1. Used Latest Android SDK 24 ( which this app is currently targeting as well ) and all build tools etc using are updated ones.
2. Using Java8 with jackoptions which will enable Lambda expressions, method references etc.
3. Used RxJava, RxAndroid extensively to make UI responsive by pushing lot of code to Background and call backs in the Main thread with unsubscription in the onDestroy.
4. Using Retrofit to make network calls & using Glide as image library which provides caching options n image load failed etc.
5. Using SqlliteHelper for storing all finished games data.
6. Used MVP pattern in the code , to segregated UI part from the logic with Presenters.
7. Using PublishSubjects instead of interfaces for better code readability and handling of subscriptions.
8. Used RxBinding for binding view click events etc with throttleFirst , so that so avoid the multiple clicks
   /multiple events getting fired in a span of a second.
9. Using Espresso for Instrumentation test cases.

**** Features included in this project. Some of them i added more than which is provided in the Document ***
1. Added Offline game ( if network is not present or network call fails ) showing random images from the local. So internet is not mandatory for this app.
2. Normal game if some images are broken or erroring out , replacing those with random local images , so that game can continue using with help of Glide.
3. Added show all my games feature, will store all completed games data and show it.
4. If game is in between user can flip the mobile to landscape and still can play the game . Storing all data and handled use cases in onSavedInstanceState.
5. Written Instrumentation test cases to test the flow.
6. Created generic code with configuration (GameConfig.java). So upon changing the configuration game logic/UI changes.
7. Used extensively rxjava to delegate most of UI work to background thread ( like Databasecalls , some data processing work).
8. Put a timeout of 10 seconds to network call. So that game can be proceed to Offline mode without providing inconvience to user.
9. Added Play Again button , so user need not close the app to play the game again.
10. Added music ( music also composed by me using perfectpiano app ) for making the game interesting. See my full song at
   https://drive.google.com/open?id=0B40PmASIvkzEdWhFWUdrbkcxX3M

**** UI Features Added ******
1. Showing tick on top of image for conveying to user that is correct
2. Handled UI for landscape mode as well.
3. Showing loader incase network response is slow.
* */
public class MemoryGameActivity extends AppCompatActivity implements MVPMemoryGameInterface {

    private RecyclerView gameRv;
    private ArrayList<GameItem> gameItemList;
    private MemoryGameAdapter gameAdapter;
    private ImageView gameGuessIv;
    private MediaPlayer mediaPlayer;

    private Set<Integer> nonGuessedValueIndices;
    private MemoryGamePresenter memoryGamePresenter;
    private ProgressDialog progressDialog;

    private int numberOfMoves, numberOfCorrectGuesses;
    private int currentIndexToBeGuessed = -1;
    private CompositeSubscription lifeCycle, customCompositeSubscription;
    private View gameCountDownParent, gamesMovesParent, gamesCorrectParent, gameGuessImageParent;
    private TextView gameMovesTv, gameCorrectGuessTv, gameCountdownTv;
    private List<Integer> resourceIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        initViews();
        initData();
        if (!getDataFromSavedInstanceStateIfPresent(savedInstanceState)) {
            memoryGamePresenter.doLogicForFreshLaunch();
        }
        initClicks();
    }

    private void initViews() {
        gameMovesTv = (TextView) findViewById(R.id.game_moves_tv);
        gameCorrectGuessTv = (TextView) findViewById(R.id.game_correct_guess_tv);
        gameCountdownTv = (TextView) findViewById(R.id.game_count_down_tv);
        gameRv = (RecyclerView) findViewById(R.id.game_rv);
        gameGuessIv = ((ImageView) findViewById(R.id.game_guess_image_iv));
        gameCountDownParent = findViewById(R.id.game_count_down_wrapper);
        gamesMovesParent = findViewById(R.id.game_moves_parent);
        gamesCorrectParent = findViewById(R.id.game_correct_parent);
        gameGuessImageParent = findViewById(R.id.game_guess_image_parent);
        initializeProgressDialog();
    }

    private void initData() {
        mediaPlayer = MediaPlayer.create(this, R.raw.my_small_song);
        lifeCycle = new CompositeSubscription();
        customCompositeSubscription = new CompositeSubscription();
        gameItemList = new ArrayList<>();
        nonGuessedValueIndices = new HashSet<>(GameConfig.getInstance().getNumberOfElements());
        memoryGamePresenter = new MemoryGamePresenter(this, this, gameItemList, nonGuessedValueIndices);

        gameAdapter = new MemoryGameAdapter(this, gameItemList, PublishSubject.create(), PublishSubject.create(), this);
        gameRv.setLayoutManager(new GridLayoutManager(this, 3));
        gameRv.setAdapter(gameAdapter);
        gameAdapter.notifyDataSetChanged();
        setGone(gameGuessImageParent, gamesMovesParent, gamesCorrectParent);
    }

    private boolean getDataFromSavedInstanceStateIfPresent(Bundle savedInstanceState) {
        if (isNonNull(savedInstanceState)) {
            ArrayList<GameItem> savedGameItemList = savedInstanceState.getParcelableArrayList(GAME_LIST);
            ArrayList<Integer> savedIndicesList = savedInstanceState.getIntegerArrayList(NON_GUESSED_INDICES);
            currentIndexToBeGuessed = savedInstanceState.getInt(CURRENT_INDEX_TO_BE_GUESSED);
            // Only when game started consider the values of below => currentIndexToBeGuessed is already set
            if (!isNullOrEmpty(savedGameItemList) && isNonNull(savedIndicesList) && currentIndexToBeGuessed > -1) {
                gameItemList.addAll(savedGameItemList);
                if (!isNullOrEmpty(savedIndicesList)) {
                    nonGuessedValueIndices.addAll(savedIndicesList);
                }
                gameAdapter.notifyDataSetChanged();

                GameItem gameItem = gameItemList.get(currentIndexToBeGuessed);
                setVisibleView(gameGuessImageParent);
                loadImageWithGlide(this, gameItem, gameGuessIv);

                numberOfMoves = savedInstanceState.getInt(NO_OF_MOVES);
                numberOfCorrectGuesses = savedInstanceState.getInt(NO_OF_CORRECT_GUESSES);
                handleGuessInfoViews(true);
                return true;
            }
        }
        return false;
    }

    private void initClicks() {
        lifeCycle.add(gameAdapter.onBackViewClickSubject.subscribe(this::onBackViewClicked));
    }

    private void onBackViewClicked(Pair<Integer, GameItem> pair) {
        numberOfMoves++;
        handleGuessInfoViews(true);
        if (currentIndexToBeGuessed == pair.first) {
            nonGuessedValueIndices.remove(currentIndexToBeGuessed);
            numberOfCorrectGuesses++;
            handleGuessInfoViews();
            doFlipAnimationBasedOnState(pair, GameStates.GUESSED_CORRECTLY, 1000); // GUESSED_CORRECTLY shows tick along with image.
        } else {
            doFlipAnimationBasedOnState(pair, GameStates.NORMAL, 700);  // DO Flip and show flipped Image.
            return;
        }
        if (nonGuessedValueIndices.isEmpty()) {   // if Empty => All items has been guessed correctly. Forward to GameFinishActivity
            handleGuessInfoViews();
            doUnsubscribe(lifeCycle);
        } else {
            memoryGamePresenter.generateNextRandomImage();  // Still some elements left to be guessed. Get next random non guessed image.
        }
    }

    private void doFlipAnimationBasedOnState(Pair<Integer, GameItem> pair, String gameState, int delay) {
        if (GameConfig.getInstance().isDoFlipAnimationOnClick()) {
            pair.second.setState(gameState);
            gameAdapter.notifyItemChanged(pair.first);
            lifeCycle.add(Observable.just(true).delay(delay, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                pair.second.setState(GameStates.FLIPPED);
                gameAdapter.notifyItemChanged(pair.first);
            }));
        }
    }

    private void handleGuessInfoViews() {
        handleGuessInfoViews(false);
    }

    private void handleGuessInfoViews(boolean updateMovesViewAswell) {
        if (nonGuessedValueIndices.isEmpty()) {   // if Empty => All items has been guessed correctly. Forward to GameFinishActivity
            setGone(gamesMovesParent, gamesCorrectParent, gameGuessImageParent);
            gameItemList.clear();
            gameAdapter.notifyDataSetChanged();
            Intent intent = new Intent(MemoryGameActivity.this, GameFinishActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(GameFinishActivity.NO_OF_MOVES, numberOfMoves);
            startActivity(intent);
            finish();
        } else {
            if (numberOfMoves > 0) {   // Moves > 0 , show the Moves n Correct views.
                setVisibleView(gamesMovesParent, gamesCorrectParent);
                gameCorrectGuessTv.setText(numberOfCorrectGuesses + "");
                if (updateMovesViewAswell) {
                    gameMovesTv.setText(numberOfMoves + "");
                }
            } else {
                setGone(gamesMovesParent, gamesCorrectParent);
            }
        }
    }

    @Override
    public void loadNextRandomImageInUI(String url, int indexOfElement) {
        currentIndexToBeGuessed = indexOfElement;
        setVisibleView(gameGuessImageParent);
        loadImageWithGlide(this, gameItemList.get(indexOfElement), gameGuessIv);
    }

    private void flipCardsAfter15Seconds() {
        mediaPlayer.start();
        // After GameConfig.getInstance().getDelayTimeInSeconds() seconds , all items are flipped and music stops.
        lifeCycle.add(Observable.just(true).delay(GameConfig.getInstance().getDelayTimeInSeconds(), TimeUnit.SECONDS)
                .flatMap(s -> Observable.from(gameItemList))
                .map(gameItem -> {
                    gameItem.setState(GameStates.FLIPPED);
                    return gameItem;
                })
                .observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                    gameAdapter.notifyDataSetChanged();
                    memoryGamePresenter.generateNextRandomImage();
                    stopMediaPlayer(mediaPlayer);
                }));

        // Update the countdown values every second.
        customCompositeSubscription.add(Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    setVisibleView(gameCountDownParent);
                    gameCountdownTv.setText((GameConfig.getInstance().getDelayTimeInSeconds() - s) + "");
                    if (s == (GameConfig.getInstance().getDelayTimeInSeconds() - 1)) {
                        setGone(gameCountDownParent);
                        CommonUtils.doUnsubscribe(customCompositeSubscription);
                    }
                }));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { // Storing all data , so on phone flip can continue the game.
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(GAME_LIST, gameItemList);
        outState.putIntegerArrayList(NON_GUESSED_INDICES, new ArrayList<>(nonGuessedValueIndices));
        outState.putInt(NO_OF_MOVES, numberOfMoves);
        outState.putInt(NO_OF_CORRECT_GUESSES, numberOfCorrectGuesses);
        outState.putInt(CURRENT_INDEX_TO_BE_GUESSED, currentIndexToBeGuessed);
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

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.GameDialogTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        memoryGamePresenter.unSubscribe();
        doUnsubscribe(lifeCycle);
        CommonUtils.stopMediaPlayer(mediaPlayer);
    }

    @Override
    public void showProgress() {
        if (!memoryGamePresenter.isSubscribed()) {
            return;
        }
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (!memoryGamePresenter.isSubscribed()) {
            return;
        }
        CommonUtils.dismissDialog(progressDialog);
    }

    @Override
    public void processNetworkData() {
        if (!memoryGamePresenter.isSubscribed()) {
            return;
        }
        gameAdapter.notifyDataSetChanged();
        handleGuessInfoViews();
        flipCardsAfter15Seconds();
    }

    @Override
    public synchronized int getUniqueDefaultResourceIdWithRemoval() { // synchronized bcoz any number broken image urls exists & can be called asynchronously from adapter
        // upon notifyDatasetChanged . So synchronized is needed.
        // This is mostly for Offline Flow. For Offline flow supporting only upto 9 images.
        if (isNullOrEmpty(resourceIdList)) {
            resourceIdList = new ArrayList<>(Arrays.asList(R.drawable.default_0, R.drawable.default_1, R.drawable.default_2,
                    R.drawable.default_3, R.drawable.default_4,
                    R.drawable.default_5, R.drawable.default_6, R.drawable.default_7,
                    R.drawable.default_8));
        }
        int randomNumberInRange = CommonUtils.getRandomNumberInRange(0, resourceIdList.size() - 1);
        int uniqueDefaultResourceId = resourceIdList.remove(randomNumberInRange);
        return uniqueDefaultResourceId;
    }

    private void seeAllResultsFlow() {
        MemoryGameActivity context = MemoryGameActivity.this;
        Intent intent = new Intent(context, GameSeeAllResultsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.finish();
        context.startActivity(intent);
    }
}
