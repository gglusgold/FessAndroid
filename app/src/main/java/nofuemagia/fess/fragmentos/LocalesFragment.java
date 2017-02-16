package nofuemagia.fess.fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nofuemagia.fess.R;
import nofuemagia.fess.modelo.Locales;

/**
 * Created by Tano on 22/1/2017.
 */
public class LocalesFragment extends Fragment {

    private LocalListener onLocalListener;
    private RecyclerView rvLocales;
    private boolean editando;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rvLocales = (RecyclerView) inflater.inflate(R.layout.fragment_locales, container, false);
        rvLocales.setHasFixedSize(true);

        return rvLocales;
    }

    public void setOnLocalListener(LocalListener onLocalListener) {
        this.onLocalListener = onLocalListener;
    }

    public void mostrarLocales(List<Locales> locales, int idLocal) {
        if (rvLocales != null && locales != null) {

            if (idLocal != -1) {
                editando = true;
                onLocalListener.localSeleccionado(idLocal);
            }

            rvLocales.setAdapter(new LocalesAdapter(locales, idLocal));
            rvLocales.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    public int getLocalActual() {
        List<Locales> locales = ((LocalesAdapter) rvLocales.getAdapter()).getDataset();
        for (int x = 0; x < locales.size(); x++) {
            Locales ubi = locales.get(x);
            if (ubi.seleccionado())
                return ubi.getIdLocal();
        }

        return -1;
    }

    public interface LocalListener {
        void localSeleccionado(int idLocal);
    }

    private class LocalesAdapter extends RecyclerView.Adapter<LocalesAdapter.LocalesViewHolder> {

        private final List<Locales> mDataset;
        private boolean onBind;

        LocalesAdapter(List<Locales> locales, int idLocal) {
            mDataset = locales;
            if (idLocal != -1)
                for (Locales local : locales) {
                    if (local.getIdLocal() == idLocal)
                        local.setSeleccionado(true);
                }
        }

        @Override
        public LocalesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LocalesViewHolder(View.inflate(getContext(), R.layout.item_locales, null));
        }

        public List<Locales> getDataset() {
            return mDataset;
        }

        @Override
        public void onBindViewHolder(LocalesViewHolder holder, int position) {
            Locales local = mDataset.get(position);

            holder.tvNombre.setText(local.getNombre());
            holder.tvNombre.setVisibility(local.getNombre() == null ? View.GONE : View.VISIBLE);

            holder.tvDireccion.setText(local.getDireccion());
            holder.tvLugar.setText("Comuna " + local.getComuna() + "\n(" + local.getBarrio() + ")");
            holder.tvHorario.setText("Horario:\n" + local.getHorario());
            holder.itemView.setTag(local);

            onBind = true;
            holder.cardView.setCardBackgroundColor(local.seleccionado() ? ContextCompat.getColor(getContext(), R.color.colorAccent) : ContextCompat.getColor(getContext(), R.color.partido));
            onBind = false;
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        class LocalesViewHolder extends RecyclerView.ViewHolder {

            private final CardView cardView;

            private final TextView tvDireccion;
            private final TextView tvLugar;
            private final TextView tvNombre;
            private final TextView tvHorario;


            LocalesViewHolder(final View itemView) {
                super(itemView);

                cardView = (CardView) itemView;

                tvDireccion = (TextView) itemView.findViewById(R.id.tv_direccion);
                tvLugar = (TextView) itemView.findViewById(R.id.tv_lugar);
                tvNombre = (TextView) itemView.findViewById(R.id.tv_nombre);
                tvHorario = (TextView) itemView.findViewById(R.id.tv_horario);

                if (!editando)
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Locales actual = mDataset.get(getAdapterPosition());
                            actual.setSeleccionado(!actual.seleccionado());

                            for (int x = 0; x < mDataset.size(); x++) {
                                if (x != getAdapterPosition()) {
                                    Locales ubi = mDataset.get(x);
                                    ubi.setSeleccionado(false);
                                }
                            }

                            if (actual.seleccionado() && onLocalListener != null)
                                onLocalListener.localSeleccionado(actual.getIdLocal());

                            if (!onBind)
                                rvLocales.getAdapter().notifyDataSetChanged();
                        }
                    });


            }
        }
    }
}
