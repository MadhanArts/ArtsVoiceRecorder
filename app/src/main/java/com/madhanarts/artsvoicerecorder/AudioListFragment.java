package com.madhanarts.artsvoicerecorder;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.madhanarts.artsvoicerecorder.dialog.RenameDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AudioListFragment extends Fragment implements AudioListAdapter.OnItemListClick, RenameDialog.RenameDialogListener {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView audioList;

    private ArrayList<File> allFiles;
    private File directory;

    private AudioListAdapter audioListAdapter;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private File fileToPlay;

    //UI elements of Player Sheet
    private int fileToPlayPosition;
    private ImageButton playerPlayButton;
    private ImageButton playerBackButton;
    private ImageButton playerForwardButton;

    private TextView playerHeader;
    private TextView playerFileName;

    private SeekBar playerSeekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;

    private boolean isPlayerCompleted = false;

    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_list, container, false);

        toolbar = view.findViewById(R.id.tool_bar_layout);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return view;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_layout, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_sort_by_name:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    audioListAdapter.sortByName();
                }
                else
                {
                    Toast.makeText(getContext(), "Sorting is supported only from Nougat", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_sort_by_lastmodified:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    audioListAdapter.sortByLastModified();
                }
                else
                {
                    Toast.makeText(getContext(), "Sorting is supported only from Nougat", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_sort_by_size:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    audioListAdapter.sortBySize();
                }
                else
                {
                    Toast.makeText(getContext(), "Sorting is supported only from Nougat", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case 101:
                if (audioListAdapter.removeAudio(item.getGroupId()))
                {
                    Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(), "This item cannot be deleted", Toast.LENGTH_SHORT).show();
                }
                return true;
            case 102:

                RenameDialog dialog = new RenameDialog();

                Bundle data = new Bundle();
                data.putString("current_adapter_name", allFiles.get(item.getGroupId()).getName());
                data.putInt("current_adapter_position", item.getGroupId());

                dialog.setArguments(data);

                dialog.setTargetFragment(this, 11);

                dialog.show(getFragmentManager(), "rename_tag");

                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = view.findViewById(R.id.audio_list_view);

        playerHeader = view.findViewById(R.id.player_header_title);
        playerFileName = view.findViewById(R.id.player_filename);

        playerPlayButton = view.findViewById(R.id.player_play_button);
        playerBackButton = view.findViewById(R.id.player_back_button);
        playerForwardButton = view.findViewById(R.id.player_forward_button);


        playerSeekBar = view.findViewById(R.id.player_seekbar);

        playerFileName.setSelected(true);


        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        directory = new File(path);
        allFiles = new ArrayList<File>(Arrays.asList(directory.listFiles()));

        audioListAdapter = new AudioListAdapter(allFiles, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        audioList.setAdapter(audioListAdapter);



        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        playerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (fileToPlay != null && fileToPlayPosition > 0)
                {
                    if (!isPlayerCompleted) {
                        stopAudio();

                    }
                    fileToPlayPosition = fileToPlayPosition - 1;
                    fileToPlay = allFiles.get(fileToPlayPosition);
                    playAudio(fileToPlay);
                }

            }
        });


        playerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying)
                {
                    if (fileToPlay != null) {
                        pauseAudio();
                    }
                }
                else
                {
                    if (fileToPlay != null)
                    {
                        if(!isPlayerCompleted) {
                            resumeAudio();
                        }
                        else
                        {
                            playAudio(fileToPlay);
                        }
                    }
                }
            }
        });

        playerForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fileToPlay != null && fileToPlayPosition < allFiles.size()-1)
                {

                    if (!isPlayerCompleted) {
                        stopAudio();

                    }
                    fileToPlayPosition = fileToPlayPosition + 1;
                    fileToPlay = allFiles.get(fileToPlayPosition);
                    playAudio(fileToPlay);

                }


            }
        });

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (fileToPlay != null)
                {
                    pauseAudio();
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(fileToPlay != null)
                {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }

            }
        });


    }

    @Override
    public void onClickListener(File file, int position) {

        fileToPlay = file;
        fileToPlayPosition = position;
        if (isPlaying)
        {
            stopAudio();
        }
        playAudio(fileToPlay);

    }

    private void pauseAudio()
    {
        mediaPlayer.pause();
        playerPlayButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn));
        isPlaying = false;
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void resumeAudio()
    {
        mediaPlayer.start();
        playerPlayButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn));
        isPlaying = true;

        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar, 0);

    }


    private void stopAudio() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();

        playerPlayButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn));
        playerHeader.setText("Stopped");
        isPlaying = false;
        isPlayerCompleted = true;

        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void playAudio(File fileToPlay) {
        //Play the audio

        mediaPlayer = new MediaPlayer();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playerPlayButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn));
        playerFileName.setText(fileToPlay.getName());
        playerHeader.setText("Playing");


        isPlaying = true;
        isPlayerCompleted = false;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());

                stopAudio();
                playerHeader.setText("Finished");
            }
        });


        playerSeekBar.setMax(mediaPlayer.getDuration());

        seekBarHandler = new Handler();

        updateRunnable();

        //First initialize the thread and starts the seekbar
        seekBarHandler.postDelayed(updateSeekBar, 0);

    }

    private void updateRunnable() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {

                playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                //updates the seekbar by calling updateseekbar at 500 millis
                seekBarHandler.postDelayed(this, 500);

            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();

        if (isPlaying)
        {
            stopAudio();
        }
        else if (mediaPlayer != null)
        {
            mediaPlayer.release();
        }

    }


    @Override
    public void onRenameText(String renameText, int position) {
        if (!audioListAdapter.renameAudio(directory, renameText, position))
        {
            Toast.makeText(getContext(), "In your Mobile Storage permission is not granted", Toast.LENGTH_SHORT).show();
        }
    }
}