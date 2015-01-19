package jp.fkmsoft.libs.inputtextdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Dialog fragment for displaying Progress
 */
public class InputTextDialogFragment extends DialogFragment {
    public static final String EXTRA_DATA = "extra";

    private static final String ARGS_TITLE = "title";
    private static final String ARGS_MESSAGE = "message";
    private static final String ARGS_INIT_TEXT = "initText";
    private static final String ARGS_EXTRA = "extra";
    private static final String ARGS_INPUT_TYPE = "inputType";

    public static InputTextDialogFragment newInstance(Fragment target, int requestCode,
                                                     String title, String message, String initText, Bundle extra) {
        return newInstance(target, requestCode, title, message, initText, InputType.TYPE_CLASS_TEXT, extra);
    }

    public static InputTextDialogFragment newInstance(Fragment target, int requestCode,
        String title, String message, String initText, int inputType, Bundle extra) {
        InputTextDialogFragment fragment = new InputTextDialogFragment();
        fragment.setTargetFragment(target, requestCode);
        
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        args.putString(ARGS_MESSAGE, message);
        args.putString(ARGS_INIT_TEXT, initText);
        args.putInt(ARGS_INPUT_TYPE, inputType);
        args.putBundle(ARGS_EXTRA, extra);
        fragment.setArguments(args);
        
        return fragment;
    }

    private EditText mEdit;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity == null) { return super.onCreateDialog(savedInstanceState); }

        Bundle args = getArguments();
        String title = args.getString(ARGS_TITLE);
        String message = args.getString(ARGS_MESSAGE);
        String initText = args.getString(ARGS_INIT_TEXT);
        int inputType = args.getInt(ARGS_INPUT_TYPE);

        LayoutInflater inflater = LayoutInflater.from(activity);
        View root = inflater.inflate(R.layout.lib_dialog_input_text, null);
        TextView messageText = (TextView) root.findViewById(R.id.text_message);
        messageText.setText(message);
        mEdit = (EditText) root.findViewById(R.id.edit);
        mEdit.setText(initText);
        mEdit.setInputType(inputType);
        mEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setView(root);
        builder.setPositiveButton(android.R.string.ok, mClickListener);
        builder.setNegativeButton(android.R.string.cancel, mClickListener);

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        Fragment target = getTargetFragment();
        if (target == null) { return; }

        Bundle args = getArguments();
        Bundle extra = args.getBundle(ARGS_EXTRA);

        Intent data = new Intent();
        data.putExtra(EXTRA_DATA, extra);

        target.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, data);
    }

    private final DialogInterface.OnClickListener mClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                dialog.cancel();
                return;
            }

            Fragment target = getTargetFragment();
            if (target == null) {
                return;
            }

            Bundle args = getArguments();
            Bundle extra = args.getBundle(ARGS_EXTRA);
            String text = mEdit.getText().toString();

            Intent it = new Intent();
            it.putExtra(Intent.EXTRA_TEXT, text);
            it.putExtra(EXTRA_DATA, extra);
            target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, it);
        }
    };
}
