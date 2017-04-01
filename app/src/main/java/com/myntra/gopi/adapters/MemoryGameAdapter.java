package com.myntra.gopi.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jakewharton.rxbinding.view.RxView;
import com.myntra.gopi.R;
import com.myntra.gopi.domains.GameItem;
import com.myntra.gopi.intdefs.GameStates;
import com.myntra.gopi.interfaces.MVPMemoryGameInterface;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.subjects.PublishSubject;

import static com.myntra.gopi.utils.CommonUtils.getRandomDrawbleColor;
import static com.myntra.gopi.utils.ViewUtils.setGone;
import static com.myntra.gopi.utils.ViewUtils.setVisibleView;

/**
 * Created by gopikrishna on 27/11/16.
 */

public class MemoryGameAdapter extends RecyclerView.Adapter<MemoryGameAdapter.GameViewHolder> {

    private final Context context;
    private MVPMemoryGameInterface mvpMemoryGameInterface;
    private List<GameItem> gameItemList;
    public PublishSubject<Pair<Integer, GameItem>> onBackViewClickSubject;
    public PublishSubject<Integer> onImageLoadErrorSubject;
    int uniqueDefaultResourceId;

    public MemoryGameAdapter(Context context, List<GameItem> gameItemList, PublishSubject<Pair<Integer, GameItem>> onBackViewClickSubject, PublishSubject<Integer> onImageLoadErrorSubject, MVPMemoryGameInterface mvpMemoryGameInterface) {
        this.context = context;
        this.gameItemList = gameItemList;
        this.onBackViewClickSubject = onBackViewClickSubject;
        this.onImageLoadErrorSubject = onImageLoadErrorSubject;
        this.mvpMemoryGameInterface = mvpMemoryGameInterface;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.memory_game_item, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        GameItem gameItem = gameItemList.get(position);
        setGone(holder.itemOverlayIv);
        switch (gameItem.getState()) {
            case GameStates.NORMAL:
                setGone(holder.itemBackIv);
                setVisibleView(holder.itemFrontIv);
                loadImageWithGlide(context, gameItem.getBigImageUrl(), holder.itemFrontIv, position, gameItem);
                break;
            case GameStates.GUESSED_CORRECTLY:
                setGone(holder.itemBackIv);
                setVisibleView(holder.itemFrontIv);
                loadImageWithGlide(context, gameItem.getBigImageUrl(), holder.itemFrontIv, position, gameItem);
                setVisibleView(holder.itemOverlayIv);
                break;
            case GameStates.FLIPPED:
                setGone(holder.itemFrontIv);
                setVisibleView(holder.itemBackIv);
                break;
        }
        RxView.clicks(holder.itemBackIv).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(s -> {
            onBackViewClickSubject.onNext(Pair.create(holder.getAdapterPosition(), gameItem));
        });
    }

    private void loadImageWithGlide(Context context, String url, ImageView imageView, int position, GameItem gameItem) {
        if (!gameItem.isDrawableNull()) {
            Glide.with(context).load(gameItem.getdrawableResourceId()).into(imageView);
            return;
        }
        // if (isNullOrEmpty(url)) can never happen bcoz while populating. Please see MemoryGamePresenter.fillGameItemsWithPosition for reference.
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(getRandomDrawbleColor()).crossFade().priority(Priority.HIGH).into(new GlideDrawableImageViewTarget(imageView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                int uniqueDefaultResourceId = mvpMemoryGameInterface.getUniqueDefaultResourceIdWithRemoval();
                gameItem.setdrawableResourceId(uniqueDefaultResourceId);
                Glide.with(context).load(uniqueDefaultResourceId).into(imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameItemList.size();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemFrontIv;
        private ImageView itemBackIv;
        private ImageView itemOverlayIv;

        public GameViewHolder(View view) {
            super(view);
            itemFrontIv = (ImageView) view.findViewById(R.id.item_front_iv);
            itemBackIv = (ImageView) view.findViewById(R.id.item_back_iv);
            itemOverlayIv = (ImageView) view.findViewById(R.id.item_overlay_iv);
        }
    }


}
