package com.madhanarts.artsvoicerecorder.sort;

import java.io.File;
import java.util.Comparator;

public class NameSort implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
