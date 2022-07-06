package com.jedisebas.imagesafe.util;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class XorCipher {

    public int keyFromString(String key) {
        int newKey = 0;

        for (int i=0; i<key.length(); i++) {
            newKey += key.charAt(i);
        }

        return newKey;
    }

    public byte[] getEncryptedByteArray(File file, int key) {
        try {
            byte[] byteArray = Files.readAllBytes(file.toPath());
            byte[] encryptedArray = new byte[byteArray.length];

            for (int i=0; i<byteArray.length; i++) {
                encryptedArray[i] = (byte) (byteArray[i] ^ key);
            }

            return encryptedArray;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public byte[] getEncryptedByteArray(File file, String key) {
        try {
            int keyInt = keyFromString(key);
            byte[] byteArray = Files.readAllBytes(file.toPath());
            byte[] encryptedArray = new byte[byteArray.length];

            for (int i=0; i<byteArray.length; i++) {
                encryptedArray[i] = (byte) (byteArray[i] ^ keyInt);
            }

            return encryptedArray;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public void writeFile(byte[] byteArray, File file) {
        try(OutputStream os = new FileOutputStream(file)) {

            os.write(byteArray);

        } catch (Exception e) {
            Log.e("exception", String.valueOf(e));
        }
    }
}
