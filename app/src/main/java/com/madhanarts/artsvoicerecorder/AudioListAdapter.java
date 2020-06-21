package com.madhanarts.artsvoicerecorder;

import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.madhanarts.artsvoicerecorder.sort.LastModifiedSort;
import com.madhanarts.artsvoicerecorder.sort.NameSort;
import com.madhanarts.artsvoicerecorder.sort.SizeSort;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;


public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    private ArrayList<File> allFiles;
    private TimeAgo timeAgo;
    private SizeConverter sizeConverter;

    private OnItemListClick onItemListClick;

    private boolean isSortedNameAs = false;
    private boolean isSortedLastModifiedAs = false;
    private boolean isSortedSizeAs = false;

    public AudioListAdapter(ArrayList<File> allFiles, OnItemListClick onItemListClick)
    {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;

    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        sizeConverter = new SizeConverter();

        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {

        holder.listFileName.setText(allFiles.get(position).getName());
        holder.listFileDate.setText(timeAgo.getTimeAgo(allFiles.get(position).lastModified()));
        holder.listFileSize.setText(sizeConverter.convertSize(allFiles.get(position).length()));

    }

    @Override
    public int getItemCount() {
        return allFiles.size();
    }




    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private ImageView listImage;
        private TextView listFileName, listFileDate, listFileSize;
        private ConstraintLayout singleItem;


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            listImage = itemView.findViewById(R.id.audio_list_image_view);
            listFileName = itemView.findViewById(R.id.audio_list_file_name);
            listFileDate = itemView.findViewById(R.id.audio_list_file_date);
            listFileSize = itemView.findViewById(R.id.audio_list_file_size);
            singleItem = itemView.findViewById(R.id.single_list_item_view);

            listFileName.setSelected(true);

            itemView.setOnClickListener(this);

            singleItem.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {

            onItemListClick.onClickListener(allFiles.get(getAdapterPosition()), getAdapterPosition());

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(this.getAdapterPosition(), 101, 0, "delete");
            menu.add(this.getAdapterPosition(), 102, 1, "rename");

        }



    }



    // To remove Audio from the AudioList
    public boolean removeAudio(int position)
    {

        File currentFile = allFiles.remove(position);

        if (currentFile.delete())
        {

            notifyDataSetChanged();
            return true;

        }
        else
        {
            return false;
        }

    }

    public boolean renameAudio(File directory, String name, int position)
    {


        File currentFile = allFiles.remove(position);
        File tempFile = new File(directory, name);

        if (currentFile.renameTo(tempFile))
        {
            allFiles.add(position, tempFile);
            notifyDataSetChanged();

            return true;
        }
        else
        {
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByName()
    {

        if (!isSortedNameAs)
        {
            allFiles.sort(new NameSort());
            isSortedNameAs = true;
        }
        else
        {
            Collections.reverse(allFiles);
            isSortedNameAs = false;
        }
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByLastModified()
    {
        if (!isSortedLastModifiedAs)
        {
            allFiles.sort(new LastModifiedSort());
            isSortedLastModifiedAs = true;
        }
        else
        {
            Collections.reverse(allFiles);
            isSortedLastModifiedAs = false;
        }
        notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortBySize()
    {
        if (!isSortedSizeAs)
        {
            allFiles.sort(new SizeSort());
            isSortedSizeAs = true;
        }
        else
        {
            Collections.reverse(allFiles);
            isSortedSizeAs = false;
        }
        notifyDataSetChanged();
    }


    public interface OnItemListClick
    {
        void onClickListener(File file, int position);
    }

}
