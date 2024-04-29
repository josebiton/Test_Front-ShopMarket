package com.task.shopmarket.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.task.shopmarket.R;

public class CustomToast {

    public static void showError(Context context, String message) {
        showToast(context, message, R.drawable.ic_close, "#cc3c3c", R.color.white, R.drawable.background_red);
    }

    public static void showSuccess(Context context, String message) {
        showToast(context, message, R.drawable.ic_check, "#2ecc71", R.color.white, R.drawable.background_blue);
    }


    public static void showWarning(Context context, String message) {
        showToast(context, message, android.R.drawable.ic_dialog_alert, "#f1c40f", R.color.white, R.drawable.background_yellow);
    }

    private static void showToast(Context context, String message, int iconRes, String backgroundColor, int textColor, int backgroundDrawable) {
        try {
            // Inflar el diseño personalizado
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.custom_toast, null);

            // Configurar el mensaje y el icono
            TextView textView = layout.findViewById(R.id.ErrorMessage);
            textView.setText(message);
            textView.setTextColor(ContextCompat.getColor(context, textColor));

            ImageView imageView = layout.findViewById(R.id.imageView);
            imageView.setImageResource(iconRes);
            imageView.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

            // Configurar el fondo
            LinearLayout toastLayout = layout.findViewById(R.id.toast_layout_root);
            toastLayout.setBackgroundResource(backgroundDrawable);

            // Crear y mostrar el Toast personalizado
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.TOP | Gravity.END, 0, 100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



