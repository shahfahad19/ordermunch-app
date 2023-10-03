package com.app.ordermunch.Utils;

import com.app.ordermunch.R;
import android.app.AlertDialog;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAlert {



    public static void showCustomDialog(Context context, int image, String text) {
        // Create a custom layout view for the dialog
        View customView = LayoutInflater.from(context).inflate(R.layout.custom_alert, null);


        ImageView alertImageView = customView.findViewById(R.id.alertImage);
        TextView alertTextView = customView.findViewById(R.id.alertText);
        Button alertButton = customView.findViewById(R.id.alertBtn);

        // Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RoundedDialogTheme);
        builder.setView(customView);
        builder.setCancelable(true);

        // Customize the dialog's appearance or behavior here if needed
        alertImageView.setImageResource(image);
        alertTextView.setText(text);

        // Apply animation to the ImageView
        Animation splashAnimation = AnimationUtils.loadAnimation(context, R.anim.appear_animation);
        alertImageView.startAnimation(splashAnimation);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        alertButton.setOnClickListener(v->{
            dialog.hide();
        });
    }
}
