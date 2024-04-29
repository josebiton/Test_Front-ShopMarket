package com.task.shopmarket.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.task.shopmarket.R;
import com.task.shopmarket.models.Carrito;
import com.task.shopmarket.models.Producto;
import com.task.shopmarket.utils.CustomToast;
import com.task.shopmarket.utils.EscuchaCarrito;
import com.task.shopmarket.utils.LocalStorage;
import com.task.shopmarket.views.VerProductoActivity;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> listaProductos;
    private Context context;
    TextView contadorCarrito;
    private EscuchaCarrito escuchaCarrito;
    String Tag;

    public ProductoAdapter(List<Producto> listaProductos, Context context, TextView contadorCarrito, String tag) {
        this.listaProductos = listaProductos;
        this.context = context;
        this.contadorCarrito = contadorCarrito;
        this.Tag = tag;
    }

    public void setProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
        notifyDataSetChanged();
    }
    public void setEscuchaCarrito(EscuchaCarrito escuchaCarrito) {
        this.escuchaCarrito = escuchaCarrito;
    }

    private void actualizarContadorCarrito(List<Carrito> carrito, TextView contadorCarrito) {
        int contCarrito = 0;
        for (Carrito item : carrito) {
            contCarrito += 1;
        }
        if (contadorCarrito != null) {
            contadorCarrito.setText(String.valueOf(contCarrito));
            contadorCarrito.setVisibility(contCarrito > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @NonNull
    @Override
    public ProductoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        if (Tag.equalsIgnoreCase("Inicio")) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_producto_inicio, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ProductoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (listaProductos != null && position < listaProductos.size()) {
            Producto producto = listaProductos.get(position);
            holder.producto_nombre.setText(producto.getNombre());
            holder.producto_moneda.setText("S/ ");
            holder.producto_precio.setText(String.valueOf(producto.getPrecio()));

            String primeraImagen = producto.getImagenes().get(0);
            Picasso.get().load(primeraImagen).error(R.drawable.no_imagen).into(holder.producto_imagen, new Callback() {
                @Override
                public void onSuccess() {
                    if (holder.progressBar != null) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.d("Error : ", e.getMessage());
                }
            });

            holder.agregar_carrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomToast.showSuccess(context, "Producto agregado al carrito");
                    agregarAlCarrito(producto);
                }
            });
        }  else {
           Log.e("ProductoAdapter", "La lista de productos o el producto en la posición " + position + " es null.");
        }

        holder.ver_producto_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productoId = listaProductos.get(position).getId();
                Intent intent = new Intent(context, VerProductoActivity.class);
                intent.putExtra("producto_id", productoId);
                intent.putExtra("producto_nombre", listaProductos.get(position).getNombre());
                context.startActivity(intent);

            }
        });
    }

    public void agregarAlCarrito(Producto producto) {
        List<Carrito> carrito = LocalStorage.obtenerCarrito(context);
        boolean productoExistente = false;

        for (Carrito item : carrito) {
            if (item.getId() == producto.getId()) {
                item.setCantidad(item.getCantidad() + 1);
                productoExistente = true;
                break;
            }
        }

        if (!productoExistente) {
            String imagenProducto = producto.getImagenes().isEmpty() ? "" : producto.getImagenes().get(0);

            Carrito nuevoItem = new Carrito(producto.getId(), producto.getNombre(), producto.getPrecio(), 1, imagenProducto);
            carrito.add(nuevoItem);

            LocalStorage.guardarCarrito(context, carrito);

            if (escuchaCarrito != null) {
                escuchaCarrito.onCarritoChange(carrito);
            }

            actualizarContadorCarrito(carrito, contadorCarrito);
        }
    }


    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

   /* public List<Producto> getListaProductos() {
        return listaProductos;
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView producto_imagen;
        ProgressBar progressBar;
        TextView producto_nombre, producto_precio, producto_moneda;
        Button agregar_carrito;
        LinearLayout ver_producto_ll;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
            producto_imagen = itemView.findViewById(R.id.producto_imagen);
            producto_nombre = itemView.findViewById(R.id.producto_nombre);
            producto_precio = itemView.findViewById(R.id.producto_precio);
            producto_moneda = itemView.findViewById(R.id.producto_moneda);
            agregar_carrito = itemView.findViewById(R.id.agregar_carrito);
            ver_producto_ll = itemView.findViewById(R.id.ver_producto_ll);


        }
    }
}
