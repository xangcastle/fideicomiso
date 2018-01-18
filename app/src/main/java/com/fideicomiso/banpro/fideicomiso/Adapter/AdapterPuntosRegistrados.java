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
import com.fideicomiso.banpro.fideicomiso.R;

import java.util.ArrayList;

/**
 * Created by root on 18/01/18.
 */

public class AdapterPuntosRegistrados extends BaseAdapter implements Filterable {
    /**
     * Es el arreglo que se genera cuando se copia en el filtrador
     */
    private ArrayList<Punto> puntosCopia;
    /**
     * Es el arreglo original de las gestiones
     */
    private ArrayList<Punto> puntosOriginal;
    /**
     * Contexto de la aplicación
     */
    private Context context;

    public AdapterPuntosRegistrados(ArrayList<Punto> puntosOriginal, Context context) {
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

        final Punto g = puntosCopia.get(i);
        ImageButton suvecion = (ImageButton) view.findViewById(R.id.suvecion);
        if (g.getSuvecion() == "1") {
            suvecion.setImageResource(R.drawable.star2);
        }

        TextView id = (TextView) view.findViewById(R.id.textID);
        id.setText(g.getId());

        TextView contactos = (TextView) view.findViewById(R.id.textContactos);
        contactos.setText(g.getContactos());

        TextView direccion = (TextView) view.findViewById(R.id.textDireccion);
        direccion.setText(g.getContactos());

        TextView comuna = (TextView) view.findViewById(R.id.textComuna);
        comuna.setText(g.getComunidad());

        TextView comarca = (TextView) view.findViewById(R.id.textComarca);
        direccion.setText(g.getComarca());

        TextView barrio = (TextView) view.findViewById(R.id.textBarrio);
        direccion.setText(g.getBarrio());

        TextView estado = (TextView) view.findViewById(R.id.textEstado);

        if(g.getEstado().equals("1")||g.getEstado().equals("PENDIENTE"))
        {
            estado.setText("PENDIENTE");
            g.setEStado("PENDIENTE");
        }

        if(g.getEstado().equals("3")||g.getEstado().equals("ENVIADO"))
        {
            estado.setText("ENVIADO");
            g.setEStado("ENVIADO");
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
                    final ArrayList<Punto> results = new ArrayList<Punto>();
                    if (puntosOriginal == null)
                        puntosOriginal = puntosCopia;
                    if (constraint != null) {
                        if (puntosOriginal != null && puntosOriginal.size() > 0) {
                            for (final Punto g : puntosOriginal) {
                                if (g.getId().contains(constraint.toString().toLowerCase()) ||
                                        g.getDireccion().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                        g.getContactos().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getSuvecion().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getComarca().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getComunidad().toLowerCase().contains(constraint.toString().toLowerCase())
                                        || g.getBarrio().toLowerCase().contains(constraint.toString().toLowerCase())
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
                puntosCopia = (ArrayList<Punto>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
