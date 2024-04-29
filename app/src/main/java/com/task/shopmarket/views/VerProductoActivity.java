package com.task.shopmarket.views;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.task.shopmarket.R;
import com.task.shopmarket.adapters.ProductoAdapter;
import com.task.shopmarket.models.Carrito;
import com.task.shopmarket.models.Producto;
import com.task.shopmarket.services.ApiProducto;
import com.task.shopmarket.utils.CustomToast;
import com.task.shopmarket.utils.LocalStorage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VerProductoActivity extends AppCompatActivity  {
    Toolbar toolbar;
    Button p_addCart;
    TextView p_nombre, p_precio, p_descripcion, p_valor, tv_titulo;
    ImageSlider p_slider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_producto);
        Intent intent = getIntent();
        int productoId = intent.getIntExtra("producto_id", -1);
        String productoNombre = intent.getStringExtra("producto_nombre");

        tv_titulo = findViewById(R.id.tv_titulo);
        tv_titulo.setText(productoNombre);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Drawable flechaPersonalizada = obtenerDrawableConColor(R.drawable.ic_atras, Color.BLACK);
            actionBar.setHomeAsUpIndicator(flechaPersonalizada);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




        p_nombre = findViewById(R.id.p_nombre);
        p_precio = findViewById(R.id.p_precio);
        p_descripcion = findViewById(R.id.p_descripcion);
        p_valor = findViewById(R.id.p_valor);
        p_slider = findViewById(R.id.p_slider);


  /*      p_addCart = findViewById(R.id.p_addCart);
        p_addCart.setOnClickListener(v -> {
            agregarAlCarrito(productoId);

        });*/


        obtenerDetalles(productoId);

    }


    private void obtenerDetalles(int productoId) {
        ApiProducto.obtenerProducto(productoId, this, new ApiProducto.ProductoCallback() {
            @Override
            public void onSuccess(Producto producto) {
                double precio = Double.parseDouble(producto.getPrecio());
                if (!Double.isNaN(precio) && !Double.isInfinite(precio)) {
                    Locale localePeru = new Locale("es", "PE");
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localePeru);
                    String precioFormateado = currencyFormat.format(precio);
                    p_precio.setText(precioFormateado);
                } else {
                    // Manejar el caso en el que el precio no es un número válido
                    CustomToast.showError(VerProductoActivity.this, "El precio no es válido");
                }

                p_nombre.setText(producto.getNombre());
                p_descripcion.setText(producto.getDescripcion());
                // obterner imagenes
                List<String> imagenes = producto.getImagenes();
                 List<SlideModel> slideModels = new ArrayList<>();
                 for (String imagen : imagenes) {
                     slideModels.add(new SlideModel(imagen, ScaleTypes.FIT));

                 }
                    p_slider.setImageList(slideModels, ScaleTypes.FIT);

            }

            @Override
            public void onError(String error) {
                // Aquí manejas el error
                CustomToast.showError(VerProductoActivity.this, "Error al obtener el producto");
            }
        });
    }

    private Drawable obtenerDrawableConColor(@DrawableRes int resId, @ColorInt int color) {
        Drawable drawable = ContextCompat.getDrawable(this, resId);
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
        return drawable;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}