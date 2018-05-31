package com.example.android.news.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.news.R;
import com.example.android.news.entity.New;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    /** Tag for the log messages */
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    private static final String PUBBLICATION_DATE_SEPARATOR = "T";

    private Context mContext;
    private List<New> mNewsList;

    private static ClickListener clickListener;

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param newsList is the list of news, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<New> newsList) {
        this.mContext = context;
        this.mNewsList = newsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView sectionView, webTitleView, trialTextView, dateTextView, authorTextView;
        private ImageView thumbnailView;

        public MyViewHolder(View view) {

            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            sectionView = view.findViewById(R.id.section);
            webTitleView = view.findViewById(R.id.webtitle);
            trialTextView = view.findViewById(R.id.trial_text);
            dateTextView = view.findViewById(R.id.date);
            authorTextView = view.findViewById(R.id.author);
            thumbnailView = view.findViewById(R.id.thumbnail);

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {

            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;

        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {

        NewsAdapter.clickListener = clickListener;

    }

    public interface ClickListener {

        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);

    }

    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(NewsAdapter.MyViewHolder holder, int position) {

        New currentNew = mNewsList.get(position);

        holder.webTitleView.setText(currentNew.getmWebTitle());

        String sectionName = currentNew.getmSectionName();

        GradientDrawable sectionRectangle = (GradientDrawable) holder.webTitleView.getBackground();

        int sectionColor = getSectionColor(sectionName);

        sectionRectangle.setColor(sectionColor);

        String currentThumbnailUrl = currentNew.getmThumbnailUrl();

        if (currentThumbnailUrl!=null) {

            //Picasso library to download the thumbnail
            Picasso.get().load(currentThumbnailUrl).into(holder.thumbnailView);

        }

        String date = currentNew.getmWebPubblicationDate();

        String pubblicationDate = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat finalDateFormat = new SimpleDateFormat("MMM d, yyyy");

        if(date.contains(PUBBLICATION_DATE_SEPARATOR)){

            String[] parts = date.split(PUBBLICATION_DATE_SEPARATOR);

            try {

                Date dateParsed = dateFormat.parse(parts[0]);

                pubblicationDate = finalDateFormat.format(dateParsed);

            } catch (ParseException e) {

                Log.e(LOG_TAG, "Problem with parsing the date ", e);

            }

        }

        holder.dateTextView.setText(pubblicationDate);

        holder.sectionView.setText(currentNew.getmSectionName());

        holder.trialTextView.setText(currentNew.getmTrialText());

        String author = currentNew.getmAuthor();

        if (author != null)    {

            holder.authorTextView.setText(author);

        } else {

            holder.authorTextView.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public New getItem(int position){

        New currentNew = mNewsList.get(position);
        return currentNew;

    }

    private int getSectionColor(String sectionColor){

        int resourceIdColor;

        switch (sectionColor){
            case "News":
            case "World news":
            case "UK news":
            case "Australia news":
            case "US news":
            case "Science":
            case "Cities":
            case "Global development":
            case "Tech":
            case "Business":
            case "Environment":
            case "Obituaries":

                resourceIdColor = R.color.newsSectionColor;
                break;

            case "Opinion":
            case "Teh Guardian view":
            case "Columnists":
            case "Cartoons":
            case "Opinion videos":
            case "Letters":

                resourceIdColor = R.color.opinionSectionColor;
                break;

            case "Sport":
            case "Football":
            case "Rugby union":
            case "Cricket":
            case "Tennis":
            case "Cycling":
            case "F1":
            case "Golf":
            case "US sports":

                resourceIdColor = R.color.sportSectionColor;
                break;

            case "Culture":
            case "Books":
            case "Music":
            case "Television & radio":
            case "Art & design":
            case "Film":
            case "Games":
            case "Classical":
            case "Stage":

                resourceIdColor = R.color.cultureSectionColor;
                break;

            case "Lifestyle":
            case "Fashion":
            case "Food":
            case "Recipes":
            case "Love & sex":
            case "Health & fitness":
            case "Home & garden":
            case "Women":
            case "Family":
            case "Travel":
            case "Money":
            case "Life and style":

                resourceIdColor = R.color.lifestyleSectionColor;
                break;

            default:

                resourceIdColor = R.color.backgroundTitle;

        }

        return mContext.getResources().getColor(resourceIdColor);
    }

}
