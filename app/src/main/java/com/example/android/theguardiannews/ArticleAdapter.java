package com.example.android.theguardiannews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by viki on 2018.06.20..
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        // Image

        ImageView thumbnail = (ImageView) listItemView.findViewById(R.id.thumbnail_view);
        Picasso.get().load(currentArticle.getImageUrl()).into(thumbnail);

        // Title
        String title = currentArticle.getTitle();
        TextView titleView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleView.setText(title);

        // Section
        String section = currentArticle.getSection();
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section_text_view);
        sectionView.setText(section);

        // Author(s)
        TextView authorView = (TextView) listItemView.findViewById(R.id.author_text_view);
        TextView byView = (TextView) listItemView.findViewById(R.id.by_text_view);

        // if there an author make VISIBLE
        if (!currentArticle.getAuthor().equals("")) {
            String author = currentArticle.getAuthor();
            authorView.setText(author);
            authorView.setVisibility(View.VISIBLE);
            byView.setVisibility(View.VISIBLE);

            // if not do not show
        } else {
            authorView.setVisibility(View.GONE);
            byView.setVisibility(View.GONE);
        }

        // Date & Time
        TextView dateView = (TextView) listItemView.findViewById(R.id.date_text_view);

        // if there a date make VISIBLE
        if (!currentArticle.getDate().equals("")){
            // Create a new Date object from the publication date of the article
            Date dateObject = new Date(String.valueOf(currentArticle.getDate()));
            String formattedDate = formatDate(dateObject);
            dateView.setText(formattedDate);
            dateView.setVisibility(View.VISIBLE);
        } else {
            dateView.setVisibility(View.INVISIBLE);
        }

        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        return dateFormat.format(dateObject);
    }


}
