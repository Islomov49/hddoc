package com.isoma.homiladavridoctor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.isoma.homiladavridoctor.R;


public class SpinnerAdapter extends BaseAdapter {
    String[] objects;
    Integer[] icons;

    public SpinnerAdapter( String[] strings, Integer[] icons) {
        this.objects = strings;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return objects.length;
    }

    @Override
    public Object getItem(int position) {
        return objects[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View customSpinner = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_spinner, parent, false);
        TextView tvSpinnerItems = (TextView) customSpinner.findViewById(R.id.tvLanguage);
        ImageView ivSpinnerIcons = (ImageView) customSpinner.findViewById(R.id.ivLanguageIcon);
        tvSpinnerItems.setText(objects[position]);
        ivSpinnerIcons.setImageResource(icons[position]);
        return customSpinner;
    }
}