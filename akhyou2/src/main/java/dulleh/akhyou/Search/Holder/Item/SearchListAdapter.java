package dulleh.akhyou.Search.Holder.Item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dulleh.akhyou.Models.Anime;
import dulleh.akhyou.R;
import dulleh.akhyou.Utils.AdapterClickListener;
import dulleh.akhyou.Utils.AdapterDataHandler;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private final Context context;

    private final AdapterClickListener<Anime> clickListener;
    private final AdapterDataHandler<List<Anime>> dataHandler;

    public SearchListAdapter(Context context, AdapterClickListener<Anime> adapterClickListener, AdapterDataHandler<List<Anime>> adapterDataHandler) {
        this.context = context;
        this.clickListener = adapterClickListener;
        this.dataHandler = adapterDataHandler;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView descView;
        public ImageView imageView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View v) {
            super(v);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
            titleView = (TextView) relativeLayout.findViewById(R.id.title_view);
            descView = (TextView) relativeLayout.findViewById(R.id.desc_view);
            imageView = (ImageView) relativeLayout.findViewById(R.id.image_view);
        }
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.search_card, parent, false);

        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Anime anime = getItem(position);

        if (anime != null) {

            viewHolder.titleView.setText(anime.getTitle());
            viewHolder.descView.setText(anime.getDesc());

            Picasso.with(context)
                    .load(anime.getImageUrl())
                    .error(R.drawable.placeholder)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.imageView);

            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onCLick(getItem(position), null, viewHolder.imageView);
                }
            });

        }
        //setTransitionName(viewHolder, String.valueOf(position));
    }
/*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTransitionName (final ViewHolder viewHolder, String uniqueIdentifier) {
        searchFragment.transitionName = SearchFragment.POSTER_TRANSITION_BASE_NAME + uniqueIdentifier;
        viewHolder.imageView.setTransitionName(searchFragment.transitionName);
    }
*/

    private Anime getItem (int position) {
        if (dataHandler.getData() != null) {
            return dataHandler.getData().get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (dataHandler.getData() != null) {
            return dataHandler.getData().size();
        }
        return 0;
    }
}
