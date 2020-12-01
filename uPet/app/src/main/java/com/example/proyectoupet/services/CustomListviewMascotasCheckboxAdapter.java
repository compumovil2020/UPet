package com.example.proyectoupet.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoupet.R;

import java.util.List;

public class CustomListviewMascotasCheckboxAdapter extends BaseAdapter {
    Context context;
    List<String> idMascotas;
    List<String> nombreMascotas;
    List<Bitmap> imagenesMascotas;
    boolean[] seleccionados;
    LayoutInflater inflater;


    public CustomListviewMascotasCheckboxAdapter(Context context, List<String> idMascotas, List<String> nombreMascotas, List<Bitmap> imagenesMascotas) {
        this.context = context;
        this.idMascotas = idMascotas;
        this.nombreMascotas = nombreMascotas;
        this.seleccionados = new  boolean[nombreMascotas.size()];
        this.imagenesMascotas = imagenesMascotas;
        inflater = (LayoutInflater.from(context));
    }

    static class ViewHolder {
        protected TextView nombreMascota;
        protected CheckBox check;
        protected ImageView imagenMascota;
    }

    @Override
    public int getCount() {
        return idMascotas.size();
    }

    public boolean isSelected(int i)
    {
        return seleccionados[i];
    }

    @Override
    public Object getItem(int i) {
        return idMascotas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_mascotas_checkbox, null);
            holder.nombreMascota = (TextView) view.findViewById(R.id.nombreMascotaCheck);
            holder.check = (CheckBox) view.findViewById(R.id.checkBoxItemMascotas);
            holder.imagenMascota = (ImageView) view.findViewById(R.id.imagenMascotaCheck);
            view.setTag(holder);
            view.setTag(R.id.nombreMascotaCheck, holder.nombreMascota);
            view.setTag(R.id.checkBoxItemMascotas, holder.check);
            holder.check
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton vw,
                                                     boolean isChecked) {
                            int getPosition = (Integer) vw.getTag();
                            seleccionados[getPosition] = true;
                        }
                    });
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.check.setTag(i);

        holder.nombreMascota.setText(nombreMascotas.get(i));
        holder.nombreMascota.setTextColor(Color.BLACK);
        Bitmap imagen = imagenesMascotas.get(i);
        if( imagen != null)
            holder.imagenMascota.setImageBitmap(imagen);
        else
            holder.imagenMascota.setImageResource(R.drawable.profile);

        if(seleccionados[i] == true)
        {
            holder.check.setChecked(true);
        }
        else {
            holder.check.setChecked(false);
        }
        return view;
    }

}
