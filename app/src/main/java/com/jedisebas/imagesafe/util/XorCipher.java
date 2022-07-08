package com.jedisebas.imagesafe.util;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class XorCipher {

    public int keyFromString(final String key) {
        int newKey = 0;

        for (int i=0; i<key.length(); i++) {
            newKey += key.charAt(i);
        }

        return newKey;
    }

    public byte[] getEncryptedByteArray(final File file, final int key) {
        try {
            final byte[] byteArray = Files.readAllBytes(file.toPath());
            final byte[] encryptedArray = new byte[byteArray.length];

            for (int i=0; i<byteArray.length; i++) {
                encryptedArray[i] = (byte) (byteArray[i] ^ key);
            }

            return encryptedArray;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public byte[] getEncryptedByteArray(final File file, final String key) {
        try {
            final int keyInt = keyFromString(key);
            final byte[] byteArray = Files.readAllBytes(file.toPath());
            final byte[] encryptedArray = new byte[byteArray.length];

            for (int i=0; i<byteArray.length; i++) {
                encryptedArray[i] = (byte) (byteArray[i] ^ keyInt);
            }

            return encryptedArray;
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public void writeFile(final byte[] byteArray, final File file) {
        try(final OutputStream os = new FileOutputStream(file)) {

            os.write(byteArray);

        } catch (final Exception e) {
            Log.e("exception", String.valueOf(e));
        }
    }
}
