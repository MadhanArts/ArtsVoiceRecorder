package com.madhanarts.artsvoicerecorder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_CODE = 21;
    private NavController navController;
    private TextView fileNameText;

    private ImageButton listButton;
    private ImageButton recordButton;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;



    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listButton = view.findViewById(R.id.record_list_button);
        recordButton = view.findViewById(R.id.record_button);
        timer = view.findViewById(R.id.record_timer);
        fileNameText = view.findViewById(R.id.record_filename);

        listButton.setOnClickListener(this);
        recordButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.record_list_button:

                if (isRecording)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                    alertDialog.setTitle("Audio still recording");
                    alertDialog.setMessage("Are you sure, you want to stop the recording?");

                    alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            stopRecording();
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment2);

                        }
                    });

                    alertDialog.setNegativeButton("CANCEL", null);

                    alertDialog.create().show();

                }
                else
                {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment2);
                }

                break;

            case R.id.record_button:

                if (isRecording)
                {
                    //stop recording
                    stopRecording();

                }
                else
                {
                    //start recording
                    if (checkPermission())
                    {
                        startRecording();
                    }
                }
                break;

        }

    }

    private void stopRecording() {
        timer.stop();


        recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped));
        isRecording = false;

        fileNameText.setText("Recording Stopped, File saved : " + recordFile);

        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;

    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();


        recordFile = "Recording_" + formatter.format(now) + ".3gp";
        fileNameText.setText("Recording, File Name : " + recordFile);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

        recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording));
        isRecording = true;

    }

    private boolean checkPermission() {

        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }


    }

    @Override
    public void onStop() {
        super.onStop();

        if (isRecording)
        {

            stopRecording();

        }
    }
}