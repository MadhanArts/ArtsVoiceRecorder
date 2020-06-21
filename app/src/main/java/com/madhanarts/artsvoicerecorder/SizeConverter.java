package com.madhanarts.artsvoicerecorder;

public class SizeConverter {

    public String convertSize(Long size)
    {

        float s = size.floatValue();

        if (size == 1)
        {
            String format = String.format("%.2f", s);
            return format + " byte";
        }
        else if (size < 1000)
        {
            String format = String.format("%.2f", s);
            return format + " bytes";
        }
        else if (size < 1000*1000)
        {
            String format = String.format("%.2f", s/1000);
            return format + " " + "KB";
        }
        else if (size < 1000*1000*1000)
        {
            String format = String.format("%.2f", s/(1000*1000));
            return format + " " + "MB";
        }
        else if (size < 1000*1000*1000*1000)
        {
            String format = String.format("%.2f", s/(1000*1000*1000));
            return format + " " + "MB";
        }
        else
        {
            return "size";
        }


    }

}
