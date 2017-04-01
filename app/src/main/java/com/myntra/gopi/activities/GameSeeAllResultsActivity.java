package com.myntra.gopi.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.myntra.gopi.R;
import com.myntra.gopi.adapters.GameResultAdapter;
import com.myntra.gopi.db.DatabaseManager;
import com.myntra.gopi.domains.GameResultItem;
import com.myntra.gopi.utils.CommonUtils;
import com.myntra.gopi.utils.RxApiUtil;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

import static com.myntra.gopi.utils.ViewUtils.setGone;
import static com.myntra.gopi.utils.ViewUtils.setVisibleView;

public class GameSeeAllResultsActivity extends AppCompatActivity {
    private RecyclerView gameResultsListingRv;
    private RelativeLayout gameNoResultsFoundView;
    private List<GameResultItem> gameResultItemList;
    private ProgressDialog progressDialog;
    private GameResultAdapter gameResultAdapter;
    private CompositeSubscription lifeCycle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_see_all_results);
        initViews();
        initData();
        doMainLogic();
    }

    private void initViews() {
        gameResultsListingRv = (RecyclerView) findViewById(R.id.game_results_listing_rv);
        gameNoResultsFoundView = (RelativeLayout) findViewById(R.id.game_no_results_found_view);
    }

    private void initData() {
        lifeCycle = new CompositeSubscription();
        gameResultItemList = new ArrayList<>();
        gameResultAdapter = new GameResultAdapter(gameResultItemList);
        gameResultsListingRv.setLayoutManager(new LinearLayoutManager(this));
        gameResultsListingRv.setAdapter(gameResultAdapter);
        initializeProgressDialog();
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.GameDialogTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage(getString(R.string.loading));
    }

    private void doMainLogic() {
        progressDialog.show();
        lifeCycle.add(RxApiUtil.build(DatabaseManager.getAllGameResults()).subscribe((list) -> {
            CommonUtils.dismissDialog(progressDialog);
            if (CommonUtils.isNullOrEmpty(list)) {
                setVisibleView(gameNoResultsFoundView);
                return;
            }
            setGone(gameNoResultsFoundView);
            gameResultItemList.addAll(list);
            gameResultAdapter.notifyDataSetChanged();
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtils.doUnsubscribe(lifeCycle);
    }
}
