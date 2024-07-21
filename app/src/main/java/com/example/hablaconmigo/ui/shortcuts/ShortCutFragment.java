package com.example.hablaconmigo.ui.shortcuts;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.hablaconmigo.R;
import com.example.hablaconmigo.databinding.FragmentShortCutBinding;
import com.example.hablaconmigo.entities.Carpeta;

import java.util.ArrayList;

public class ShortCutFragment extends Fragment {
    private FragmentShortCutBinding binding;
    private AtajosAdapter adapter;
    private ShortCutViewModel shortCutViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        shortCutViewModel = new ViewModelProvider(this).get(ShortCutViewModel.class);
        binding = FragmentShortCutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar RecyclerView
        adapter = new AtajosAdapter(new ArrayList<>(), new AtajosAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Carpeta carpeta, View view) {
                // Lógica para manejar el clic en un ítem de la carpeta
                // Por ejemplo, navegar al fragmento de la carpeta seleccionada
                NavController navController = Navigation.findNavController(view);
                Bundle args = new Bundle();
                args.putString("carpetaTitulo", carpeta.getNombreCarpeta());
                navController.navigate(R.id.listShortCutsFragment, args);
            }

            @Override
            public void onDeleteClick(Carpeta carpeta, int position) {
                // Eliminar la carpeta de la base de datos
                shortCutViewModel.eliminarCarpeta(carpeta);
                // Eliminar la carpeta de la lista y actualizar el adaptador
                adapter.eliminarCarpeta(position);
            }
        });
        RecyclerView recyclerView = binding.recyclerShortcut;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        // Observar cambios en los datos del ViewModel
        shortCutViewModel.getCarpetasLiveData().observe(getViewLifecycleOwner(), listaCarpetas -> {
            adapter.setCarpetas(listaCarpetas);
            adapter.notifyDataSetChanged();
        });

        // Configurar SearchView
        SearchView searchView = binding.searchView;

        searchView.setIconifiedByDefault(false);
        // Obtener el EditText de SearchView
        int searchEditTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = searchView.findViewById(searchEditTextId);

        // Verificar que searchEditText no sea nulo antes de agregar los listeners
        if (searchEditText != null) {
            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Acción a tomar cuando el usuario confirma el texto
                        // Puedes ocultar el teclado si es necesario
                        // InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        // imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        // Ocultar el teclado
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        // Remover foco del SearchView
                        searchView.clearFocus();
                        return true;
                    }
                    return false;
                }
            });




            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // No hacer nada
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Cambiar el botón de la lupa a botón de confirmar cuando el usuario comienza a escribir
                    searchEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    searchEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // No hacer nada
                }
            });

            // Configurar el teclado para que siempre muestre "Done"
            searchEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        } else {
            // Manejo del error cuando no se encuentra el EditText
            Log.e("ShortCutFragment", "No se pudo encontrar el EditText en el SearchView");
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtrar datos
                adapter.filtrar(newText);
                return false;
            }
        });

        // Configurar FloatingActionButton
        binding.fabAdd.setOnClickListener(v -> {
            NewFolderDialogFragment dialog = new NewFolderDialogFragment();
            dialog.setShortCutViewModel(shortCutViewModel); // Pasar el ViewModel
            dialog.show(getParentFragmentManager());
        });

        // Configurar editIcon para alternar el modo de edición
        binding.editIcon.setOnClickListener(v -> {
            boolean isEditMode = !adapter.isEditMode(); // Alternar modo de edición
            adapter.setEditMode(isEditMode);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
