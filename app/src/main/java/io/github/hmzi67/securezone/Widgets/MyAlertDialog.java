package io.github.hmzi67.securezone.Widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyAlertDialog {

    public MyAlertDialog(Context context) {}

    public static void showAlertDialog(Context context, String title, String message) {
        // Create an AlertDialog.Builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message of the dialog
        builder.setTitle(title);
        builder.setMessage(message);

        // Add buttons (optional). For example:
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}


