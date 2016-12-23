package com.qlhui.thoughtlight.utils;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

public class BitmapUtil {

	
	
	  static int findBestSampleSize (
	            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
	        double wr = (double) actualWidth / desiredWidth;
	        double hr = (double) actualHeight / desiredHeight;
	        double ratio = Math.min(wr, hr);
	        float n = 1.0f;
	        while ((n * 2) <= ratio) {
	            n *= 2;
	        }

	        return (int) n;
	    }
}
