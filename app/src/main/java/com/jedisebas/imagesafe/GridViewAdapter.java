package com.jedisebas.imagesafe;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class GridViewAdapter extends ArrayAdapter<GridItem> {

    public GridViewAdapter(@NonNull Context context, int resource, List<GridItem> gridItemList) {
        super(context, resource, gridItemList);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        GridItem gridItem = (GridItem) getItem(position);
        ImageView imageView = convertView.findViewById(R.id.gridImage);

        imageView.setImageBitmap(Bitmap.createScaledBitmap(gridItem.getImage(), 240, 240, false));

        return convertView;
    }
}
