package com.madhanarts.artsvoicerecorder.sort;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.Comparator;

public class SizeSort implements Comparator<File> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compare(File o1, File o2) {
        return Long.compare(o1.length(), o2.length());
    }
}
