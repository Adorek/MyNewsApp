package com.example.android.mynewsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends ArrayAdapter<Article> {

    ArticleAdapter(@NonNull Context context, @NonNull List<Article> articles) {
        super(context, 0, articles);
    }

    // View holder for quick access to views
    static private class ViewHolder {
        TextView titleTextView;
        TextView trailTextView;
        TextView sectionTextView;
        TextView authorTexView;
        TextView publishedTexView;
        ImageView thumbnailImageView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.article_item, parent, false);

            // Set the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = listItemView.findViewById(R.id.title_text_view);
            viewHolder.trailTextView = listItemView.findViewById(R.id.trail_text_view);
            viewHolder.sectionTextView = listItemView.findViewById(R.id.section_text_view);
            viewHolder.authorTexView = listItemView.findViewById(R.id.author_text_view);
            viewHolder.publishedTexView = listItemView.findViewById(R.id.published_text_view);
            viewHolder.thumbnailImageView = listItemView.findViewById(R.id.thumbnail_image_view);

            // store the holder with the view.
            listItemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) listItemView.getTag();
        }
        // Get the {@link Article} object located at this position in the list
        final Article currentArticle = getItem(position);

        if (currentArticle != null) {
            // Set the current article's fields
            viewHolder.titleTextView.setText(currentArticle.getmTitle());
            viewHolder.trailTextView.setText(Html.fromHtml(currentArticle.getmTrail()));
            viewHolder.sectionTextView.setText(currentArticle.getmSection());
            if (currentArticle.getmAuthor() != null) {
                viewHolder.authorTexView.setText(currentArticle.getmAuthor());
                viewHolder.authorTexView.setVisibility(View.VISIBLE);
            }
            if (currentArticle.getmPublished() != null) {
                viewHolder.publishedTexView.setText(getDateString(currentArticle.getmPublished()));
                viewHolder.publishedTexView.setVisibility(View.VISIBLE);
            }
            if (currentArticle.getmThumbnail() != null) {
                viewHolder.thumbnailImageView.setImageBitmap(currentArticle.getmThumbnail());
                viewHolder.thumbnailImageView.setVisibility(View.VISIBLE);
            }
        }
        return listItemView;
    }
    /**
     * @param date is the given date to format
     * @return a string in specified format from given date
     */
    private String getDateString(Date date) {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                Locale.getDefault()).format(date);
    }
}