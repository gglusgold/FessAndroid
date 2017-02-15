package nofuemagia.fess.fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import nofuemagia.fess.R;
import nofuemagia.fess.modelo.Productos;

/**
 * Created by Tano on 22/1/2017.
 */
public class ConfirmarFragment extends Fragment {

    private ArraySet<Productos> prod = new ArraySet<>();
    private TotalAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_confirmar, container, false);

        RecyclerView rvTotal = (RecyclerView) v.findViewById(R.id.rv_total_productos);
        rvTotal.setHasFixedSize(true);

        adapter = new TotalAdapter(prod);
        rvTotal.setAdapter(adapter);
        rvTotal.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    public void agregar(Productos productos) {
        prod.add(productos);
        adapter.notifyDataSetChanged();
    }

    public void sacar(Productos productos) {
        prod.remove(productos);
        adapter.notifyDataSetChanged();
    }

    public void vaciar() {
        prod.clear();
        adapter.notifyDataSetChanged();
    }

    public class TotalAdapter extends RecyclerView.Adapter<TotalAdapter.TotalViewHolder> {

        private final Locale def;
        ArraySet<Productos> mDataset;

        TotalAdapter(ArraySet<Productos> locales) {
            mDataset = locales;
            def = Locale.getDefault();
        }

        @Override
        public TotalAdapter.TotalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TotalAdapter.TotalViewHolder(View.inflate(getContext(), R.layout.item_totales, null));
        }

        @Override
        public void onBindViewHolder(TotalAdapter.TotalViewHolder holder, int position) {
            try {
                Productos producto = mDataset.valueAt(position);

                holder.tvNombre.setGravity(Gravity.START);
                holder.tvNombre.setText(producto.getNombre() + " - " + producto.getMarca() + " - " + producto.getPresentacion());
                holder.tvCantidad.setText(String.valueOf(producto.getPedidos()));
                holder.tvPrecio.setText(String.format(def, "$ %1$.0f", (producto.getPrecio() * producto.getPedidos())));

                holder.itemView.setTag(producto);
            } catch (Exception ex) {
                double total = 0;
                for (Productos prod : mDataset)
                    total += (prod.getPrecio() * prod.getPedidos());
                holder.tvNombre.setText(R.string.total);
                holder.tvNombre.setGravity(Gravity.END);
                holder.tvPrecio.setText(String.format(def, "$ %1$.0f", total));
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size() + 1;
        }

        class TotalViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvCantidad;
            private final TextView tvNombre;
            private final TextView tvPrecio;


            TotalViewHolder(final View itemView) {
                super(itemView);

                tvCantidad = (TextView) itemView.findViewById(R.id.tv_cantidad_total);
                tvNombre = (TextView) itemView.findViewById(R.id.tv_nombre_total);
                tvPrecio = (TextView) itemView.findViewById(R.id.tv_precio_total);

            }
        }
    }
}