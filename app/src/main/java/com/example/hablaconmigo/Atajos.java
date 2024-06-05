package com.example.hablaconmigo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Atajos extends AppCompatActivity {
                //Estas son las referencias a los elementos de la interfaz
    // Array de acciones
    String[] actions = {"Run","Jump","Clean","Cook","Eat","Exercise","Play","Read","Sleep","Work"};
    // Array de imágenes de cada acción
    int[] images = {R.drawable.run,R.drawable.run,R.drawable.run,R.drawable.run,R.drawable.run,
            R.drawable.run,R.drawable.run,R.drawable.run,R.drawable.run,R.drawable.run};

    //Referencia al elemento RecyclerView
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atajos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.rcvActions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new ActionsAdapter());
    }

    private class ActionsAdapter extends RecyclerView.Adapter<ActionsAdapter.ActionsAdapterHolder>{

        //Método que se encarga de crear los elementos del RecyclerView
        @NonNull //Indica que el método no puede retornar un valor nulo
        @Override //Indica que el método es una sobreescritura
        public ActionsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Se infla el layout de cada elemento del RecyclerView
            return new ActionsAdapterHolder(getLayoutInflater().inflate(R.layout.item_action, parent, false));
        }

        //Método que se encarga de mostrar los elementos en el RecyclerView
        @Override
        public void onBindViewHolder(@NonNull ActionsAdapterHolder holder, int position){
            holder.mostrarElementos(position);
        }

        //Método que retorna la cantidad de elementos que tiene el RecyclerView
        @Override
        public int getItemCount() {
            return actions.length;
        }

        private class ActionsAdapterHolder extends RecyclerView.ViewHolder{
            TextView tvAction;
            ImageView ivAction;
            public ActionsAdapterHolder(@NonNull View itemView) {
                super(itemView);
                ivAction = itemView.findViewById(R.id.imgvAccion);
                tvAction = itemView.findViewById(R.id.txtVnombre);
            }

            public void mostrarElementos(int position) { //Este método se llama por cada elemento del RecyclerView
                ivAction.setImageResource(images[position]);
                tvAction.setText(actions[position]);
            }
        }
    }
}