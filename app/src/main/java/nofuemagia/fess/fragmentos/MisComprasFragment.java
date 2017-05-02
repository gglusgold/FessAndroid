package nofuemagia.fess.fragmentos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import nofuemagia.fess.Aplicacion;
import nofuemagia.fess.otros.ComunicacionClient;
import nofuemagia.fess.R;
import nofuemagia.fess.modelo.Compras;
import nofuemagia.fess.modelo.Productos;

/**
 * Created by jlionti on 14/02/2017. No Fue Magia
 */
public class MisComprasFragment extends Fragment {

    public onMisCompras mOnMisCompras;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private RecyclerView rvMisEventos;
    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mis_compras, container, false);

        pref = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        mySwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.sf_mis_compras);
        rvMisEventos = (RecyclerView) v.findViewById(R.id.rv_mis_compras);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        buscarCompras();
                    }
                }
        );

        if (pref.getBoolean(Aplicacion.LOGUEADO, false)) {
            buscarCompras();
        }


        return v;
    }

    public void setOnMisCompras(onMisCompras omc) {
        mOnMisCompras = omc;
    }

    private void buscarCompras() {
        mySwipeRefreshLayout.setRefreshing(true);


        RequestParams params = new RequestParams();
        params.put("idVecino", pref.getInt(Aplicacion.IDVECINO, -1));

        ComunicacionClient client = new ComunicacionClient();
        client.post(ComunicacionClient.MIS_COMPRAS, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Aplicacion.mostrarSnack(mySwipeRefreshLayout, getResources().getString(R.string.error_comunicacion), null);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mySwipeRefreshLayout.setRefreshing(false);

                Type listType = new TypeToken<List<Compras>>() {
                }.getType();
                List<Compras> listaMisCompras = new Gson().fromJson(String.valueOf(response.optJSONArray("Historico")), listType);

                RecyclerViewExpandableItemManager expMgr = new RecyclerViewExpandableItemManager(null);

                MisComprasAdapter adapter = new MisComprasAdapter(listaMisCompras, getActivity(), expMgr);
                rvMisEventos.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvMisEventos.setAdapter(expMgr.createWrappedAdapter(adapter));

                expMgr.attachRecyclerView(rvMisEventos);
            }
        });
    }

    private void repetirPedido(Compras compra) {
        Toast.makeText(getContext(), "Metodo sin implementar", Toast.LENGTH_LONG).show();
    }

    private void comentarPedido(Compras compra) {
        Toast.makeText(getContext(), "Metodo sin implementar", Toast.LENGTH_LONG).show();
    }

    private void editarPedido(Compras compra) {
        mOnMisCompras.modificarCompra(compra);
    }

    private void cancelarPedido(final Compras compra) {
        AlertDialog builder = new AlertDialog.Builder(getContext())
                .setIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_cancelar_partido, null))
                .setTitle(R.string.cancelar_pedido)
                .setMessage(R.string.cancelar_confirmacion)
                .setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CancelarRemoto(compra);
                    }
                })
                .setNegativeButton(R.string.cancelar, null).create();
        builder.show();
    }

    private void CancelarRemoto(Compras compra) {
        mySwipeRefreshLayout.setRefreshing(true);

        RequestParams params = new RequestParams();
        params.put("idVecino", pref.getInt(Aplicacion.IDVECINO, -1));
        params.put("idCompra", compra.getIdCompra());

        ComunicacionClient client = new ComunicacionClient();
        client.post(ComunicacionClient.CANCELAR_PEDIDO, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Aplicacion.mostrarSnack(mySwipeRefreshLayout, getResources().getString(R.string.error_comunicacion), null);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mySwipeRefreshLayout.setRefreshing(false);

                String error = response.optString("error");
                if (TextUtils.isEmpty(error)) {
                    buscarCompras();
                } else {
                    Aplicacion.mostrarSnack(mySwipeRefreshLayout, error, null);
                }

            }
        });
    }

    public interface onMisCompras {
        void modificarCompra(Compras compra);
    }

    private class MisComprasAdapter extends AbstractExpandableItemAdapter<MisComprasAdapter.CompraViewHolder, MisComprasAdapter.ProductosViewHolder> {

        private final List<Compras> mDataset;
        private final Locale def;
        private final RecyclerViewExpandableItemManager expMgr;

        private int idUnico = 0x7;

        MisComprasAdapter(List<Compras> eventos, Context c, RecyclerViewExpandableItemManager expMgr) {
            setHasStableIds(true);
            mDataset = eventos;

            def = Locale.getDefault();
            this.expMgr = expMgr;
        }

        @Override
        public int getGroupCount() {
            return mDataset.size();
        }

        @Override
        public int getChildCount(int groupPosition) {
            return mDataset.get(groupPosition).getProductos().size() + 1;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            try {
                return mDataset.get(groupPosition).getProductos().get(childPosition).getIdProducto();
            } catch (Exception ex) {
                return idUnico++;
            }
        }

        @Override
        public CompraViewHolder onCreateGroupViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compra_producto, parent, false);
            return new CompraViewHolder(v, parent.getContext());
        }

        @Override
        public ProductosViewHolder onCreateChildViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_compra, parent, false);
            return new ProductosViewHolder(v);
        }

        @Override
        public void onBindGroupViewHolder(CompraViewHolder holder, final int groupPosition, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            final Compras compra = mDataset.get(groupPosition);

            holder.tvCantidad.setText(String.valueOf(compra.getProductos().size()));
            holder.tvEstado.setText(compra.getEstado());
            holder.tvFecha.setText(compra.getFecha());
            holder.tvLocal.setText(compra.getLocal() + " - " + compra.getBarrio());

            if (compra.editar())
                holder.llEditar.setVisibility(View.VISIBLE);
            else
                holder.llEditar.setVisibility(View.GONE);

            if (compra.comentar())
                holder.rlComentar.setVisibility(View.VISIBLE);
            else
                holder.rlComentar.setVisibility(View.GONE);

            if (compra.comentado())
                holder.rlPedir.setVisibility(View.VISIBLE);
            else
                holder.rlPedir.setVisibility(View.GONE);

            holder.llCompraProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (expMgr.isGroupExpanded(groupPosition))
                        expMgr.collapseGroup(groupPosition);
                    else
                        expMgr.expandGroup(groupPosition);
                }
            });

            holder.btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelarPedido(compra);
                }
            });

            holder.btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarPedido(compra);
                }
            });

            holder.btnComentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    comentarPedido(compra);
                }
            });

            holder.btnRepetir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    repetirPedido(compra);
                }
            });
        }

        @Override
        public void onBindChildViewHolder(ProductosViewHolder holder, int groupPosition, int childPosition, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            List<Productos> productos = mDataset.get(groupPosition).getProductos();
            try {
                Productos producto = productos.get(childPosition);

                holder.tvCantidad.setVisibility(View.VISIBLE);

                holder.tvNombre.setGravity(Gravity.START);
                holder.tvNombre.setText(producto.getNombre() + " - " + producto.getMarca() + " - " + producto.getPresentacion());
                holder.tvCantidad.setText(String.valueOf(producto.getCantidad()));
                holder.tvPrecio.setText(String.format(def, "$ %1$.0f", producto.getPrecioUnidad()));
            } catch (Exception ex) {
                double total = 0;

                for (Productos prod : productos)
                    total += (prod.getPrecioUnidad() * prod.getCantidad());
                holder.tvCantidad.setVisibility(View.GONE);
                holder.tvNombre.setText(R.string.total);
                holder.tvNombre.setGravity(Gravity.END);
                holder.tvPrecio.setText(String.format(def, "$ %1$.0f", total));
            }

        }


        @Override
        public boolean onCheckCanExpandOrCollapseGroup(CompraViewHolder holder, int groupPosition, int x, int y, boolean expand) {
            return false;
        }

        class CompraViewHolder extends AbstractExpandableItemViewHolder {

            private final LinearLayout llCompraProducto;

            private final TextView tvFecha;
            private final TextView tvEstado;
            private final TextView tvLocal;
            private final TextView tvCantidad;

            private final LinearLayout llEditar;
            private final LinearLayout rlComentar;
            private final LinearLayout rlPedir;

            private final Button btnEditar;
            private final Button btnCancelar;
            private final Button btnComentar;
            private final Button btnRepetir;


            CompraViewHolder(View view, Context c) {
                super(view);
                tvFecha = (TextView) view.findViewById(R.id.tv_fecha_compra);
                tvEstado = (TextView) view.findViewById(R.id.tv_estado_compra);
                tvLocal = (TextView) view.findViewById(R.id.tv_encargado_compra);
                tvCantidad = (TextView) view.findViewById(R.id.tv_cantidad_productos);

                llCompraProducto = (LinearLayout) view.findViewById(R.id.ll_compra_producto);

                llEditar = (LinearLayout) view.findViewById(R.id.ll_editar_compra);
                rlComentar = (LinearLayout) view.findViewById(R.id.rl_comentar_compra);
                rlPedir = (LinearLayout) view.findViewById(R.id.rl_repetir_compra);

                btnEditar = (Button) view.findViewById(R.id.btn_modificar_compra);
                btnEditar.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(c.getResources(), R.drawable.ic_edit, c.getTheme()), null, null, null);

                btnCancelar = (Button) view.findViewById(R.id.btn_cancelar_compra);
                btnCancelar.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(c.getResources(), R.drawable.ic_garbage, c.getTheme()), null, null, null);

                btnComentar = (Button) view.findViewById(R.id.btn_comentar_compra);
                btnComentar.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(c.getResources(), R.drawable.ic_comments, c.getTheme()), null, null, null);

                btnRepetir = (Button) view.findViewById(R.id.btn_repetir_compra);
                btnRepetir.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(c.getResources(), R.drawable.ic_repeat, c.getTheme()), null, null, null);
            }
        }

        class ProductosViewHolder extends AbstractExpandableItemViewHolder {

            private final TextView tvCantidad;
            private final TextView tvNombre;
            private final TextView tvPrecio;

            ProductosViewHolder(View view) {
                super(view);

                tvCantidad = (TextView) view.findViewById(R.id.tv_cantidad_total);
                tvNombre = (TextView) view.findViewById(R.id.tv_nombre_total);
                tvPrecio = (TextView) view.findViewById(R.id.tv_precio_total);

            }
        }


    }
}
