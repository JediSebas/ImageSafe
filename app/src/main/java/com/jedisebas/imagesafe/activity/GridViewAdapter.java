package com.jedisebas.imagesafe.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jedisebas.imagesafe.R;
import com.jedisebas.imagesafe.util.XorCipher;

import java.io.File;
import java.util.List;

public class GridViewAdapter extends ArrayAdapter<GridItem> {

    public GridViewAdapter(@NonNull Context context, int resource, List<GridItem> gridItemList) {
        super(context, resource, gridItemList);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {

        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.gridImage);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SharedPreferences sessionPrefs = getContext().getSharedPreferences(getContext().getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        String password = sessionPrefs.getString(getContext().getString(R.string.password_key), null);

        GridItem gridItem = getItem(position);

        XorCipher xor = new XorCipher();
        File file = new File(gridItem.getPathFile());
        byte[] encryptedByteArray = xor.getEncryptedByteArray(file, password);

        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(
                BitmapFactory.decodeByteArray(encryptedByteArray, 0, encryptedByteArray.length),
                240, 240, false));

        return view;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
