package com.example.proyectoupet.services;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoupet.R;

public class CustomSpinnerMascotasAdapter extends BaseAdapter {
    Context context;
    int perros[];
    String[] nombresPerros;
    LayoutInflater inflater;

    public CustomSpinnerMascotasAdapter(Context applicationContext, int[] perros, String[] nombresPerros) {
        this.context = applicationContext;
        this.perros =  perros;
        this.nombresPerros = nombresPerros;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return perros.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_mascotas, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imagenMascota);
        TextView names = (TextView) view.findViewById(R.id.nombreMascota);
        Resources resources = context.getResources();
        icon.setImageResource(R.drawable.perfil);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),"fonts/playfair_display_black");
        names.setText(nombresPerros[i]);
        names.setTextColor(Color.BLACK);
        names.setTypeface(tf);
        return view;
    }
}