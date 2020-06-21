package com.madhanarts.artsvoicerecorder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.madhanarts.artsvoicerecorder.R;

public class RenameDialog extends DialogFragment {

    String input = "Sample";
    private EditText renameEditText;
    private Button mCancelButton, mRenameButton;


    private int adapterPosition;
    private String adapterName;

    public interface RenameDialogListener
    {
        void onRenameText(String renameText, int position);
    }

    public RenameDialogListener onRenameDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getLayoutInflater().inflate(R.layout.rename_dialog_layout, null);

        renameEditText = view.findViewById(R.id.rename_text);
        mCancelButton = view.findViewById(R.id.rename_layout_cancel);
        mRenameButton = view.findViewById(R.id.rename_layout_rename);

        renameEditText.setText(adapterName);
        int lastIndexOfDot = adapterName.lastIndexOf(".");
        renameEditText.setSelection(0, lastIndexOfDot);


        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input = renameEditText.getText().toString();

                if (!input.equals(""))
                {
                    onRenameDialogListener.onRenameText(input, adapterPosition);
                }

                getDialog().dismiss();
            }
        });

        builder.setView(view);

        return builder.create();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapterPosition = getArguments().getInt("current_adapter_position");
        adapterName = getArguments().getString("current_adapter_name");

    }
/*

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = inflater.inflate(R.layout.rename_dialog_layout, container, false);

        renameEditText = view.findViewById(R.id.rename_text);
        mCancelButton = view.findViewById(R.id.rename_layout_cancel);
        mRenameButton = view.findViewById(R.id.rename_layout_rename);

        renameEditText.setText(adapterName);
        int lastIndexOfDot = adapterName.lastIndexOf(".");
        renameEditText.setSelection(0, lastIndexOfDot);


        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                input = renameEditText.getText().toString();

                if (!input.equals(""))
                {
                    onRenameDialogListener.onRenameText(input, adapterPosition);
                }

                getDialog().dismiss();
            }
        });

        builder.setView(view);

        builder.create();
        builder.show();

        return view;
    }
*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //Activity activity = (Activity) context;
        try {

            onRenameDialogListener = (RenameDialogListener) getTargetFragment();

        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("must implement RenameListener method..");
        }
    }
}
