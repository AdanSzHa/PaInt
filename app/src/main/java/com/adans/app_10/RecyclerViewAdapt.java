package com.adans.app_10;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapt extends RecyclerView.Adapter<RecyclerViewAdapt.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView codigo,curso,carrera,Gx,Gy,Gz,lat,log;


        public ViewHolder(View itemView) {
            super(itemView);
            codigo=(TextView)itemView.findViewById(R.id.tvCodigo);
            curso=(TextView)itemView.findViewById(R.id.tvCurso);
            carrera=(TextView)itemView.findViewById(R.id.tvCarrera);
            Gx=(TextView)itemView.findViewById(R.id.tvGx);
            Gy=(TextView)itemView.findViewById(R.id.tvGy);
            Gz=(TextView)itemView.findViewById(R.id.tvGz);
            lat=(TextView)itemView.findViewById(R.id.tvLoc);
            log=(TextView)itemView.findViewById(R.id.tvLoc2);

        }
    }

    public List<PerfModelo> cursoLista;

    public RecyclerViewAdapt(List<PerfModelo> cursoLista) {
        this.cursoLista = cursoLista;
    }

    @NonNull


    //Inflar los Layouts
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }


    //REaliza las modifics para cada item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.codigo.setText(cursoLista.get(position).getAx());
        holder.curso.setText(cursoLista.get(position).getAy());
        holder.carrera.setText(cursoLista.get(position).getAz());
        holder.Gx.setText(cursoLista.get(position).getGx());
        holder.Gy.setText(cursoLista.get(position).getGy());
        holder.Gz.setText(cursoLista.get(position).getGz());
        holder.lat.setText(cursoLista.get(position).getLat());
        holder.log.setText(cursoLista.get(position).getLog());
    }


    //La cantidad de elementos a precesar
    @Override
    public int getItemCount() {
        return cursoLista.size();
    }
}


