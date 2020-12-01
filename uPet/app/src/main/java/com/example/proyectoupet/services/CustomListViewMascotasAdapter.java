package com.example.proyectoupet.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoupet.R;

import java.util.List;



public class CustomListViewMascotasAdapter extends BaseAdapter {

    Context context;
    List<String> idMascotas;
    List<String> nombreMascotas;
    List<Bitmap> imagenesMascotas;
    LayoutInflater inflater;

    public CustomListViewMascotasAdapter(Context context, List<String> idMascotas, List<String> nombreMascotas, List<Bitmap> imagenesMascotas) {
        this.context = context;
        this.idMascotas = idMascotas;
        this.nombreMascotas = nombreMascotas;
        this.imagenesMascotas = imagenesMascotas;
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return nombreMascotas.size();
    }

    @Override
    public Object getItem(int i) {
        return idMascotas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = inflater.inflate(R.layout.item_mascotas, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imagenMascota);
        TextView names = (TextView) view.findViewById(R.id.nombreMascota);
        Bitmap imagen = imagenesMascotas.get(i);
        if( imagen != null)
            icon.setImageBitmap(imagen);
        else
            icon.setImageResource(R.drawable.profile);
        names.setText(nombreMascotas.get(i));
        names.setTextColor(Color.BLACK);
        return view;

    }
}
