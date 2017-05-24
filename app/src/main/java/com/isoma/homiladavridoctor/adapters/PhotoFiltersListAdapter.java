package com.isoma.homiladavridoctor.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;
import com.isoma.homiladavridoctor.utils.ColorFilterNames;
import com.isoma.homiladavridoctor.utils.FiltersCollectionByTojiev;

/**
 * Created by developer on 18.11.2016.
 */

public class PhotoFiltersListAdapter extends RecyclerView.Adapter<PhotoFiltersListAdapter.ViewHolder> {
    Bitmap currentBitmap;
    Context context;
    AddPhotoEffects addPhotoEffects;
    public interface AddPhotoEffects{
         void  effectSelected(int positionEffect);
    }
    public PhotoFiltersListAdapter(Context context, Bitmap currentBitmap, AddPhotoEffects addPhotoEffects){
        this.currentBitmap=currentBitmap;
        this.context = context;
        this.addPhotoEffects =addPhotoEffects;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item, parent, false);
        PhotoFiltersListAdapter.ViewHolder vh = new PhotoFiltersListAdapter.ViewHolder(v);
        return vh;
    }
    TextView secondView;
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.d("ImageSizeTest", "onBindViewHolder: "+position);
        if(position == 0){
            secondView = holder.filterName;
            holder.filterName.setTextColor(ContextCompat.getColor(context,R.color.glavniy_cherniy));
        }
        switch (position){
            case 0:
                holder.filterName.setText(ColorFilterNames.NORMAL_FILTER);
                holder.imageView.setImageBitmap(currentBitmap.copy(currentBitmap.getConfig(), true));
                break;
            case 1:
                holder.filterName.setText(ColorFilterNames.VAMPIRIC_FILTER);
                holder.imageView.setImageBitmap( FiltersCollectionByTojiev.getStarLitFilter().processFilter(currentBitmap.copy(currentBitmap.getConfig(), true)));
                break;
            case 2:
                holder.filterName.setText(ColorFilterNames.LIME_FILTER);
                holder.imageView.setImageBitmap(  FiltersCollectionByTojiev.getLimeStutterFilter().processFilter(currentBitmap.copy(currentBitmap.getConfig(), true)));
                break;
            case 3:
                holder.filterName.setText(ColorFilterNames.COLDER_FILTER);
                holder.imageView.setImageBitmap( FiltersCollectionByTojiev.getNightWhisperFilter().processFilter(currentBitmap.copy(currentBitmap.getConfig(), true)));
                break;
            case 4:
                holder.filterName.setText(ColorFilterNames.SAPPHIRE_FILTER);
                holder.imageView.setImageBitmap( FiltersCollectionByTojiev.getAweStruckVibeFilter().processFilter(currentBitmap.copy(currentBitmap.getConfig(), true)));
                break;
            case 5:
                holder.filterName.setText(ColorFilterNames.COLORABLE_FILTER);
                holder.imageView.setImageBitmap( FiltersCollectionByTojiev.getBlueMessFilter().processFilter(currentBitmap.copy(currentBitmap.getConfig(), true)));
                break;
            default:
                holder.imageView.setImageBitmap( FiltersCollectionByTojiev.getStarLitFilter().processFilter(currentBitmap.copy(currentBitmap.getConfig(), true)));
                break;
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondView.setTextColor(ContextCompat.getColor(context,R.color.secondary_color));
                addPhotoEffects.effectSelected(position);
                holder.filterName.setTextColor(ContextCompat.getColor(context,R.color.glavniy_cherniy));
                secondView = holder.filterName;

            }
        });

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView imageView;
        TextView filterName;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageWithFilter);
            filterName = (TextView) v.findViewById(R.id.filterName);
        }
    }
}
