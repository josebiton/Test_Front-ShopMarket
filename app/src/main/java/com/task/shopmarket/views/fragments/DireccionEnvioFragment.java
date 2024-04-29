package com.task.shopmarket.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.task.shopmarket.R;
import com.task.shopmarket.adapters.DireccionAdapter;
import com.task.shopmarket.models.Direccion;
import com.task.shopmarket.services.ApiPersonas;
import com.task.shopmarket.services.ApiReniec;
import com.task.shopmarket.utils.CustomToast;
import com.task.shopmarket.utils.LocalStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DireccionEnvioFragment extends Fragment {
    Context context;
    Spinner spinerDireeciones;
    SharedPreferences sharedPreferences;
    String dni, nombres, apellidos, email, telefono, direccion, persona_id, referencia ;
    EditText dr_dni, dr_nombres, dr_apellidos, dr_email, dr_telefono, dr_direccion, dr_referencia;
    Button btn_buscar, btn_siguiente_pago;
    ProgressBar progressBar;
    LinearLayout linearReferencia, linearDireccion, linearListaDirecciones;
    LocalStorage localStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direccion_envio, container, false);
        sharedPreferences = requireContext().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        persona_id = sharedPreferences.getString("persona_id_usuario", null);

        localStorage = new LocalStorage(requireContext());
        dr_dni = view.findViewById(R.id.dr_dni);
        dr_nombres = view.findViewById(R.id.dr_nombres);
        dr_apellidos = view.findViewById(R.id.dr_apellidos);
        dr_email = view.findViewById(R.id.dr_email);
        dr_telefono = view.findViewById(R.id.dr_telefono);
        btn_siguiente_pago = view.findViewById(R.id.btn_siguiente_pago);
        btn_buscar = view.findViewById(R.id.btn_buscar);
        spinerDireeciones = view.findViewById(R.id.spinnerDirecciones);
        dr_direccion = view.findViewById(R.id.dr_direccion);
        dr_referencia = view.findViewById(R.id.dr_referencia);
        linearReferencia = view.findViewById(R.id.linearReferencia);
        linearDireccion = view.findViewById(R.id.linearDireccion);
        linearListaDirecciones = view.findViewById(R.id.linearListaDirecciones);
        progressBar = view.findViewById(R.id.progressBar);

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarDni();
            }
        });

        btn_siguiente_pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"0".equals(persona_id)) {
                    Direccion selectedDireccion = (Direccion) spinerDireeciones.getSelectedItem();
                    int idDireccion = selectedDireccion.getIdDireccion();

                    localStorage.capturarDireccionEnvio(idDireccion);
                   // Toast.makeText(requireContext(), "Direccion de envio guardada", Toast.LENGTH_SHORT).show();

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new MetodoPagoFragment()).commit();

                } else {
                    // Si no hay un id_persona, realizar una inserción de datos
                    nuevoRegistro();
                }
            }
        });

        verificarPersonaId();

        return view;
    }

    public void verificarPersonaId(){
        if (!"0".equals(persona_id)) {
            obtenerDatos(persona_id);
            email = sharedPreferences.getString("email_usuario", null);
            dr_email.setText(email);
            dr_email.setEnabled(false);
            linearDireccion.setVisibility(View.GONE);
            linearReferencia.setVisibility(View.GONE);
            linearListaDirecciones.setVisibility(View.VISIBLE);
            btn_siguiente_pago.setText("Siguiente");
           // btn_buscar.setVisibility(View.GONE);

        } else {
            btn_siguiente_pago.setText("Guardar Datos");
            btn_buscar.setVisibility(View.VISIBLE);
            email = sharedPreferences.getString("email_usuario", null);
            dr_email.setText(email);
            dr_email.setEnabled(false);

            telefono = sharedPreferences.getString("telefono_usuario", null);
            dr_telefono.setText(telefono);
            dr_telefono.setEnabled(false);

            linearListaDirecciones.setVisibility(View.GONE);
            linearDireccion.setVisibility(View.VISIBLE);
            linearReferencia.setVisibility(View.VISIBLE);
        }
    }

    private void nuevoRegistro() {

                dni = dr_dni.getText().toString();
                nombres = dr_nombres.getText().toString();
                apellidos = dr_apellidos.getText().toString();
                telefono = dr_telefono.getText().toString();
                direccion = dr_direccion.getText().toString();
                referencia = dr_referencia.getText().toString();

                if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() ||  telefono.isEmpty() || direccion.isEmpty() || referencia.isEmpty()) {
                    CustomToast.showWarning(requireContext(), "Complete todos los campos");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    ApiPersonas.guardarPersona(dni, nombres, apellidos, telefono, direccion, referencia, requireContext(), new ApiPersonas.PersonaCallback() {
                        @Override
                        public void onSuccess(JSONObject personaJson) {
                            try {
                                // Verifica si existe la clave "persona_id" en el objeto JSON
                                if (personaJson.has("persona_id")) {
                                    String personaId = personaJson.getString("persona_id");
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("persona_id_usuario", personaId);
                                    editor.apply();

                                    progressBar.setVisibility(View.GONE);

                                    cargarFragmento();

                                    CustomToast.showSuccess(requireContext(), "Persona y dirección agregadas correctamente");
                                } else {
                                    // Maneja el caso donde no se encuentra "persona_id" en el JSON
                                    CustomToast.showSuccess(requireContext(), "Persona agregada correctamente");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                CustomToast.showError(requireContext(), "Error al guardar persona");
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
    }

    private void cargarFragmento() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new DireccionEnvioFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void obtenerDatos(String personaId) {
        context = requireContext();
        ApiPersonas.obtenerPersonaPorId(personaId, context, new ApiPersonas.PersonaCallback() {
            @Override
            public void onSuccess(JSONObject personaJson) {
                try {
                    dni = personaJson.getString("ruc_dni");
                    nombres = personaJson.getString("nombres");
                    apellidos = personaJson.getString("apellidos");
                    telefono = personaJson.getString("telefono");

                    dr_dni.setText(dni);
                    dr_nombres.setText(nombres);
                    dr_apellidos.setText(apellidos);
                    dr_telefono.setText(telefono);
                    dr_email.setText(email);

                    dr_dni.setEnabled(false);
                    dr_nombres.setEnabled(false);
                    dr_apellidos.setEnabled(false);
                    dr_telefono.setEnabled(false);
                    dr_email.setEnabled(false);


                    JSONArray direccionesArray = personaJson.getJSONArray("direcciones");
                    List<Direccion> direccionesList  = new ArrayList<>();
                    for (int i = 0; i < direccionesArray.length(); i++) {
                        JSONObject direccionObj = direccionesArray.getJSONObject(i);
                        int idDireccion = direccionObj.getInt("direciones_id");
                        String direccion = direccionObj.getString("direccion");
                        String referencia = direccionObj.getString("referencia");

                        Direccion direccionModel = new Direccion(idDireccion, direccion, referencia);
                        direccionesList.add(direccionModel);
                    }

                    DireccionAdapter adapter = new DireccionAdapter(requireContext(), direccionesList);
                    spinerDireeciones.setAdapter(adapter);

                    spinerDireeciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            Direccion selectedDireccion = (Direccion) parentView.getSelectedItem();
                            int selectedIdDireccion = selectedDireccion.getIdDireccion();
                            //Toast.makeText(requireContext(), "Id: " + selectedIdDireccion, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Hacer algo cuando no se selecciona nada
                            CustomToast.showWarning(requireContext(), "Seleccione una dirección");
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void consultarDni() {
        dni = dr_dni.getText().toString();
        if (!TextUtils.isEmpty(dni) && dni.length() == 8) {
            // El DNI tiene exactamente 8 dígitos, proceder con la consulta
            ApiReniec.consultarDni(dni, requireContext(), new ApiReniec.DniCallback() {
                @Override
                public void onSuccess(String nombres, String apellidos) {
                    // Actualizar los EditText con los datos obtenidos
                    dr_nombres.setText(nombres);
                    dr_apellidos.setText(apellidos);

                    // Deshabilitar la edición de los EditText
                    dr_nombres.setEnabled(false);
                    dr_apellidos.setEnabled(false);
                }

                @Override
                public void onError(String error) {
                    CustomToast.showError(requireContext(), "Error al consultar DNI");
                }
            });
        } else {
            CustomToast.showWarning(requireContext(), "Ingrese un DNI válido, debe tener 8 dígitos");
        }
    }

}