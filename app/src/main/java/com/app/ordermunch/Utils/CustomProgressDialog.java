package com.app.ordermunch.Utils;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.ordermunch.R;

public class CustomProgressDialog extends Dialog {

    private Dialog dialog;
    private Context context;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void showProgressDialog(String message) {
        dialog = new Dialog(this.context, R.style.RoundedDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.setCancelable(false);


        TextView textView = dialog.findViewById(R.id.textViewMessage);
        textView.setText(message);

        ImageView imageView = dialog.findViewById(R.id.imageView);

        // Load the animation from the XML resource
        Animation bounceAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_animation);
        // Apply the animation to the ImageView
        imageView.startAnimation(bounceAnimation);

        dialog.show();
    }

    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
