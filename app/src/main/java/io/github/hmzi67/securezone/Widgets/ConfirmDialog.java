package io.github.hmzi67.securezone.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.hmzi67.securezone.R;

public class ConfirmDialog {
    private Dialog dialog;
    private Context context;
    private ImageView dialog_logo;
    private TextView dialog_headline;
    private TextView dialog_body;
    private Button yes_btn;
    private Button no_btn;

    public ConfirmDialog(Context context) {
        this.context = context;
        init();
        findView();
    }

    private void init() {
        // setting up dialog
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.confirm_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
    }

    private void findView() {
        // finding views
        dialog_logo = (ImageView) dialog.findViewById(R.id.dialog_logo);
        dialog_headline = (TextView) dialog.findViewById(R.id.dialog_headline);
        dialog_body = (TextView) dialog.findViewById(R.id.dialog_body);
        yes_btn = (Button) dialog.findViewById(R.id.yes_btn);
        no_btn = (Button) dialog.findViewById(R.id.no_btn);
    }

    // getter and setter methods
    public void setDialog_headline(String headline) {
        dialog_headline.setText(headline);
    }

    public void setDialog_body(String body) {
        dialog_body.setText(body);
    }

    public void setYes_btn_text(String btn_text) {
        yes_btn.setText(btn_text);
    }

    public void setNo_btn_text(String btn_text) {
        no_btn.setText(btn_text);
    }

    public Button getYes_btn() {
        return yes_btn;
    }

    public Button getNo_btn() {
        return no_btn;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setCanceledOnTouchOutside(boolean value) {
        dialog.setCanceledOnTouchOutside(value);
    }
}
