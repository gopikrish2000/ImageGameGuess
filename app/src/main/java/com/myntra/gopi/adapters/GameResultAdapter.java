package com.myntra.gopi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.myntra.gopi.R;
import com.myntra.gopi.domains.GameResultItem;

import java.util.List;

import static com.myntra.gopi.utils.CommonUtils.getDateTimeFromString;
import static com.myntra.gopi.utils.CommonUtils.getFormattedDate;

/**
 * Created by gopikrishna on 01/12/16.
 */

public class GameResultAdapter extends RecyclerView.Adapter<GameResultAdapter.GameResultViewHolder> {
    private List<GameResultItem> gameResultItemList;

    public GameResultAdapter(List<GameResultItem> GameResultItemList) {
        this.gameResultItemList = GameResultItemList;
    }

    @Override
    public GameResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_result_item, parent, false);
        return new GameResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameResultViewHolder holder, int position) {
        GameResultItem item = gameResultItemList.get(position);
        holder.itemTitle.setText("Game " + item.getId());
        holder.itemDescription.setText("Moves " + item.getMoves());
        String createdDateString = item.getCreatedDate();
        String formattedString = getFormattedDate(getDateTimeFromString(createdDateString));
        holder.itemDate.setText(formattedString);
        RxView.clicks(holder.parent).subscribe(s -> {
            // do nothing.
        });
    }

    @Override
    public int getItemCount() {
        return gameResultItemList.size();
    }

    class GameResultViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTitle;
        private TextView itemDate;
        private TextView itemDescription;
        private View parent;

        GameResultViewHolder(View view) {
            super(view);
            itemTitle = (TextView) view.findViewById(R.id.item_title);
            itemDate = (TextView) view.findViewById(R.id.item_date);
            itemDescription = (TextView) view.findViewById(R.id.item_description);
            parent = view;
        }
    }
}
