package com.jedisebas.imagesafe;

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

import java.io.File;
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

        SharedPreferences sessionPrefs = getContext().getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
        String password = sessionPrefs.getString(Session.PASSWORD_KEY, null);

        GridItem gridItem = getItem(position);
        ImageView imageView = convertView.findViewById(R.id.gridImage);

        XorCipher xor = new XorCipher();
        File file = new File(gridItem.getPathFile());
        byte[] encryptedByteArray = xor.getEncryptedByteArray(file, password);

        imageView.setImageBitmap(Bitmap.createScaledBitmap(
                BitmapFactory.decodeByteArray(encryptedByteArray, 0, encryptedByteArray.length),
                240, 240, false));

        return convertView;
    }
}
