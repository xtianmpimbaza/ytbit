package com.truiton.bnuwallet;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.truiton.bnuwallet.Activities.ScanActivity;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class SendFragment extends Fragment {
    String balance;
    ProgressDialog progressDialog;

    @BindView(R.id.input_address)
    EditText inputAddress;
    @BindView(R.id.amount)
    EditText inputAmount;

    @OnClick(R.id.btn_scaner)
    public void gotToScanner() {
        startActivity(new Intent(getActivity(), ScanActivity.class));
    }

    @OnClick(R.id.btn_send)
    public void submit() {
        String short_add = inputAddress.getText().toString();

        if (isValid()) {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity());
            builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
            builder.setTitle("SEND");
            builder.setMessage("Send " + inputAmount.getText().toString() + " to " + short_add);
            builder.addButton("Continue", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    onNetworkSuccessful();
                }
            });
            builder.addButton("Cancel", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    public static SendFragment newInstance() {
        SendFragment fragment = new SendFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send, container, false);
        ButterKnife.bind(this, view);

        Objects.requireNonNull(getActivity()).setTitle("Send Money");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending .....");
        Bundle arguments = getArguments();
        if (arguments != null){
            inputAddress.setText( arguments.getString("editTextValue"));
        }
        return view;
    }

    private boolean isValid() {
        return true;
    }

    void onNetworkSuccessful() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("Sent")
                .setIcon(getResources().getDrawable(R.drawable.ic_complete))
                .setMessage("Sent Successfully");
        builder.onDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        builder.show();
    }

}