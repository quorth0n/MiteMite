package dulleh.akhyou.Anime;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.Models.Episode;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.AdapterDataHandler;
import dulleh.akhyou.Utils.AdapterClickListener;
import dulleh.akhyou.Utils.PaletteTransform;

public class AnimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    private final Context context;

    private final PaletteTransform paletteTransform = new PaletteTransform();
    private final AdapterDataHandler<Anime> dataHandler;
    private final AdapterClickListener<Episode> clickListener;
    private final AnimeAdapterListener animeAdapterListener;
    private final int unwatchedColour;
    private final int watchedColour;
    private boolean isInFavourites;
    //private String transitionName;

    public AnimeAdapter(Context context, AdapterDataHandler<Anime> adapterDataHandler, AdapterClickListener<Episode> adapterClickListener, AnimeAdapterListener animeAdapterListener, int unwatchedColour, int watchedColour) {
        this.context = context;
        this.dataHandler = adapterDataHandler;
        this.clickListener = adapterClickListener;
        this.animeAdapterListener = animeAdapterListener;
        this.unwatchedColour = unwatchedColour;
        this.watchedColour = watchedColour;
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        public EpisodeViewHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.episode_title_view);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImageView;
        public TextView descView;
        public TextView genresView;
        public TextView alternateTitleView;
        public TextView dateView;
        public TextView statusView;
        public FloatingActionButton favouriteFab;

        public HeaderViewHolder(View v) {
            super(v);
            posterImageView = (ImageView) v.findViewById(R.id.anime_image_view);
            descView = (TextView) v.findViewById(R.id.anime_desc_view);
            genresView = (TextView) v.findViewById(R.id.anime_genres_view);
            alternateTitleView = (TextView) v.findViewById(R.id.anime_alternate_title_view);
            dateView = (TextView) v.findViewById(R.id.anime_date_view);
            statusView = (TextView) v.findViewById(R.id.anime_status_view);
            favouriteFab = (FloatingActionButton) v.findViewById(R.id.favourite_fab);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                AnimeAdapter.HeaderViewHolder headerViewHolder = new HeaderViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.anime_header, parent, false));

                headerViewHolder.favouriteFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isInFavourites = !isInFavourites;
                        animeAdapterListener.onFavouriteCheckedChanged(isInFavourites);
                        headerViewHolder.favouriteFab.setImageDrawable(favouriteIcon());
                    }
                });

                headerViewHolder.posterImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        animeAdapterListener.showImageDialog();
                    }
                });

                return headerViewHolder;

            case VIEW_TYPE_ITEM:
                return new EpisodeViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.episode_item, parent, false));

            case VIEW_TYPE_FOOTER:
                return new RecyclerView.ViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.anime_footer, parent, false)) {
                };
        }
        throw new IllegalStateException("Unacceptable view type.");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        Anime anime = dataHandler.getData();
        if (anime != null) { // lazy fix for a null pointer
            List<Episode> episodes = anime.getEpisodes();

            if (episodes != null) {
                if (viewHolder instanceof HeaderViewHolder) {
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    headerViewHolder.posterImageView.setTransitionName(transitionName);
                */

                    Picasso.with(context)
                            .load(anime.getImageUrl())
                            .error(R.drawable.error_stock)
                            .fit()
                            .centerCrop()
                            .transform(paletteTransform)
                            .into(headerViewHolder.posterImageView, new Callback.EmptyCallback() {
                                @Override
                                public void onSuccess() {
                                    animeAdapterListener.onMajorColourChanged(paletteTransform.getPallete());
                                }
                            });

                    headerViewHolder.genresView.setText(anime.getGenresString());
                    headerViewHolder.descView.setText(anime.getDesc());
                    if(anime.getAlternateTitle() != null && !anime.getAlternateTitle().isEmpty()) {
                        headerViewHolder.alternateTitleView.setText(anime.getAlternateTitle());
                    } else {
                        headerViewHolder.alternateTitleView.setText("-");
                    }
                    if(anime.getDate() != null && !anime.getDate().isEmpty()) {
                        headerViewHolder.dateView.setText(anime.getDate());
                    } else {
                        headerViewHolder.dateView.setText("-");
                    }
                    if(anime.getStatus() != null && !anime.getStatus().isEmpty()) {
                        headerViewHolder.statusView.setText(anime.getStatus());
                    } else {
                        headerViewHolder.statusView.setText("-");
                    }
                    headerViewHolder.favouriteFab.setImageDrawable(favouriteIcon());

                } else if (viewHolder instanceof EpisodeViewHolder) {
                    EpisodeViewHolder episodeViewHolder = (EpisodeViewHolder) viewHolder;
                    final int actualPosition = position - 1;
                    episodeViewHolder.titleView.setText(episodes.get(actualPosition).getTitle());

                    if (episodes.get(actualPosition).isWatched()) {
                        episodeViewHolder.titleView.setTextColor(this.watchedColour);
                    } else {
                        episodeViewHolder.titleView.setTextColor(unwatchedColour);
                    }

                    episodeViewHolder.titleView.setOnClickListener(view -> clickListener.onCLick(episodes.get(actualPosition), actualPosition, view));

                    episodeViewHolder.titleView.setOnLongClickListener(view -> {
                        clickListener.onLongClick(episodes.get(actualPosition), actualPosition);
                        return false;
                    });
                }
            }
        }
    }
/*
    public void setTransitionName (final String transitionName) {
        this.transitionName = transitionName;
    }
*/

    @Override
    public int getItemCount() {
        if (dataHandler.getData() != null && dataHandler.getData().getEpisodes() != null) {
            return dataHandler.getData().getEpisodes().size() + 2;
        }
        return 0;
    }

    public void setWatched (int position) {
        List<Episode> episodes = dataHandler.getData().getEpisodes();
        if (episodes != null) {
            episodes.set(position, episodes.get(position).setWatched(true));
            this.notifyItemChanged(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == VIEW_TYPE_HEADER) {
            return VIEW_TYPE_HEADER;
        } else if (position < getItemCount() - 1) {
            return VIEW_TYPE_ITEM;
        }
        return VIEW_TYPE_FOOTER;
    }

    private Drawable favouriteIcon () {
        if (isInFavourites) {
            return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_white_24px, null);
        } else {
            return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_border_white_24px, null);
        }
    }

    public void setFabChecked (boolean isInFavourites) {
        this.isInFavourites = isInFavourites;
        notifyDataSetChanged();
    }

}
