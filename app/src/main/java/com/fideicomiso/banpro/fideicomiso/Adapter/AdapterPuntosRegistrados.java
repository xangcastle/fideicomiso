package com.fideicomiso.banpro.fideicomiso.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fideicomiso.banpro.fideicomiso.Clases.Punto;
import com.fideicomiso.banpro.fideicomiso.Clases.Registro;
import com.fideicomiso.banpro.fideicomiso.R;

import java.util.ArrayList;

/**
 * Created by root on 18/01/18.
 */

public class AdapterPuntosRegistrados extends BaseAdapter implements Filterable {
    /**
     * Es el arreglo que se genera cuando se copia en el filtrador
     */
    private ArrayList<Registro> puntosCopia;
    /**
     * Es el arreglo original de las gestiones
     */
    private ArrayList<Registro> puntosOriginal;
    /**
     * Contexto de la aplicación
     */
    private Context context;

    public AdapterPuntosRegistrados(ArrayList<Registro> puntosOriginal, Context context) {
        this.puntosOriginal = puntosOriginal;
        this.context = context;
        this.puntosCopia = puntosOriginal;
    }

    @Override
    public int getCount() {
        return puntosCopia.size();
    }

    @Override
    public Object getItem(int i) {
        return puntosCopia.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_punto_detalle_registrado, viewGroup, false);
        }

        final Registro g = puntosCopia.get(i);
        ImageButton estdo = (ImageButton) view.findViewById(R.id.estado);
        if (g.getEstado().equals("0")||g.getEstado().equals("1")||g.getEstado().equals("2") ||g.getEstado().equals("PENDIENTE")) {
            estdo.setImageResource(R.drawable.sincronizar);
        }
        else if (g.getEstado().equals("3") || g.getEstado().equals("ENVIADO"))
            {
                estdo.setImageResource(R.drawable.terminado);
            }

        TextView fecha = (TextView) view.findViewById(R.id.textFecha);
        fecha.setText(g.getFecha());

        TextView punto = (TextView) view.findViewById(R.id.textPunto);
        punto.setText(g.getPunto());

        TextView nombre = (TextView) view.findViewById(R.id.textNombre);
        nombre.setText(g.getNombre());

        TextView tipo = (TextView) view.findViewById(R.id.textTipo);
        if(g.getTipo().equals("1"))
            tipo.setText("Abre Cuenta");
        else if(g.getTipo().equals("0"))
            tipo.setText("No Abre cuenta");
        else if(g.getTipo().equals("2"))
            tipo.setText("No se puede Realizar visita");

        TextView cedula = (TextView) view.findViewById(R.id.textCedula);
        cedula.setText(g.getNcedula());

        TextView comentario = (TextView) view.findViewById(R.id.textComentario);
        comentario.setText(g.getComentarios());

        TextView imagenes = (TextView) view.findViewById(R.id.textImagenes);
        int cant = 0 ;
        if( g.getCedula1() != null&&!g.getCedula1().equals(""))
            cant++;
        if( g.getCedula2() != null&&!g.getCedula2().equals(""))
            cant++;

        if( g.getVivienda() != null&&!g.getVivienda().equals(""))
            cant++;

        imagenes.setText(cant+"");

        TextView audio = (TextView) view.findViewById(R.id.textAudio);

        if( g.getRuta() != null&&!g.getRuta().equals(""))
            audio.setText("SI");
        else
            audio.setText("NO");

        TextView estado = (TextView) view.findViewById(R.id.textEstado);
        if(g.getEstado().equals("0")||g.getEstado().equals("1")||g.getEstado().equals("2") ||g.getEstado().equals("PENDIENTE"))
        {
            estado.setText("PENDIENTE");
            g.setEstado("PENDIENTE");
        }

        if(g.getEstado().equals("3")||g.getEstado().equals("ENVIADO"))
        {
            estado.setText("ENVIADO");
            g.setEstado("ENVIADO");
        }
        return view;
    }

    /**
     * Filtrador de busqueda
     *
     * @return
     */
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                try {
                    final ArrayList<Registro> results = new ArrayList<Registro>();
                    if (puntosOriginal == null)
                        puntosOriginal = puntosCopia;
                    if (constraint != null) {
                        if (puntosOriginal != null && puntosOriginal.size() > 0) {
                            for (final Registro g : puntosOriginal) {
                                if (g.getId().contains(constraint.toString().toLowerCase()) ||
                                        g.getPunto().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                        g.getEstado().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getFecha().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getNombre().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getNcedula().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getEstado().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                    results.add(g);
                                }
                            }
                        }
                        oReturn.values = results;
                    }
                } catch (Exception e) {
                    oReturn.values = puntosOriginal;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                puntosCopia = (ArrayList<Registro>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
