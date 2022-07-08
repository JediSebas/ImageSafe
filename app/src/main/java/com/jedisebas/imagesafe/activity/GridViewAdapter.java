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

    private final String password;

    public GridViewAdapter(@NonNull final Context context, final int resource, final List<GridItem> gridItemList) {
        super(context, resource, gridItemList);
        final SharedPreferences sessionPrefs = getContext().getSharedPreferences(getContext().getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        password = sessionPrefs.getString(getContext().getString(R.string.password_key), null);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @Nullable final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final XorCipher xor = new XorCipher();
        final File file = new File(getItem(position).getPathFile());
        final byte[] encryptedByteArray = xor.getEncryptedByteArray(file, password);

        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(
                BitmapFactory.decodeByteArray(encryptedByteArray, 0, encryptedByteArray.length),
                240, 240, false));

        return convertView;
    }

    private static class ViewHolder {
        final ImageView imageView;

        private ViewHolder(final ImageView imageView) {
            this.imageView = imageView;
        }

        private ViewHolder(final View view) {
            this(view.findViewById(R.id.gridImage));
        }
    }
}
