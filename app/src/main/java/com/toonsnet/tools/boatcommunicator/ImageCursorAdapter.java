package com.toonsnet.tools.boatcommunicator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageCursorAdapter extends SimpleCursorAdapter {
    private static final String TAG = "ImageCursorAdapter";

    // todo skala ner bild till 2MB
    // todo 1. ladda ner alla båtar, 2. sätt ett max MB på appen, 3.

    private Cursor cursor;
    private Context context;

    public ImageCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to, 0);
        this.cursor = cursor;
        this.context = context;
    }

    public View getView(int pos, View inView, ViewGroup parent) {
        View view = inView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.boat_info, null);
        }

        this.cursor.moveToPosition(pos);

        String firstName = this.cursor.getString(this.cursor.getColumnIndex("firstName"));
        String lastName = this.cursor.getString(this.cursor.getColumnIndex("lastName"));
        String titleStr = this.cursor.getString(this.cursor.getColumnIndex("title"));
        byte[] image = this.cursor.getBlob(this.cursor.getColumnIndex("personImage"));

        ImageView iv = (ImageView) view.findViewById(R.id.boatInfoImageView);
        if (image != null) {
            // If there is no image in the database "NA" is stored instead of a blob
            // test if there more than 3 chars "NA" + a terminating char if more than
            // there is an image otherwise load the default
            if(image.length > 3) {
                iv.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            } else {
                iv.setImageResource(R.mipmap.ic_launcher);
            }
        }

        TextView fname = (TextView) view.findViewById(R.id.modellInfoHeaderTextView);
        fname.setText(firstName);

        TextView lname = (TextView) view.findViewById(R.id.modellInfoTextView);
        lname.setText(lastName);

        return(view);
    }

    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File file) {
        try {
            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while((options.outWidth / scale / 2 >= REQUIRED_SIZE) && (options.outHeight / scale / 2 >= REQUIRED_SIZE)) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options1);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }
}
