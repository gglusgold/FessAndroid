package nofuemagia.fess.fragmentos;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import nofuemagia.fess.otros.ComunicacionClient;
import nofuemagia.fess.R;
import nofuemagia.fess.modelo.Categorias;
import nofuemagia.fess.modelo.Productos;

/**
 * Created by Tano on 22/1/2017.
 * No Fue Magia
 */
public class ProductosFragment extends Fragment {

    ComunicacionClient client;
    private OnProductosListener mOnProductosListener;
    private RecyclerView rvProductos;

    public ProductosFragment() {
        client = new ComunicacionClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rvProductos = (RecyclerView) inflater.inflate(R.layout.fragment_producos, container, false);
        rvProductos.setHasFixedSize(true);
        rvProductos.setItemViewCacheSize(20);
        rvProductos.setDrawingCacheEnabled(true);
        rvProductos.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        return rvProductos;
    }

    public void mostrarProductos(List<Categorias> productos) {
        if (rvProductos != null && productos != null) {

            RecyclerViewExpandableItemManager expMgr = new RecyclerViewExpandableItemManager(null);

            GridLayoutManager lm = new GridLayoutManager(getContext(), 2);
            ProductosAdapter adapter = new ProductosAdapter(productos);
            lm.setSpanSizeLookup(new MySpanSizeLookup(expMgr, lm, adapter));

            rvProductos.setLayoutManager(new LinearLayoutManager(getContext()));
            rvProductos.setAdapter(expMgr.createWrappedAdapter(adapter));

            expMgr.attachRecyclerView(rvProductos);
        }
    }

    public void buscarProductos(int idLocal) {

        client.cancelAllRequests(true);

        RequestParams params = new RequestParams();
        params.put("idLocal", idLocal);

        client.get(ComunicacionClient.PRODUCTOS, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                System.out.println(response);
                Type listType = new TypeToken<List<Categorias>>() {
                }.getType();
                List<Categorias> lista = new Gson().fromJson(response.toString(), listType);

                mostrarProductos(lista);
                mOnProductosListener.terminoCargar(lista);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Snackbar.make(rvProductos, R.string.error_comunicacion, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void setOnProductosListener(OnProductosListener mOnProductosListener) {
        this.mOnProductosListener = mOnProductosListener;
    }

    public void actualizarLista(List<Categorias> lista, List<Productos> productos) {

        for (Categorias cat : lista) {
            for (Productos prodCat : cat.getProductos()) {
                for (Productos prod : productos) {
                    if (prodCat.getIdProducto() == prod.getIdProducto()) {
                        prodCat.setPedidos(prod.getCantidad());
                        mOnProductosListener.añadirProductos(prodCat);
                    }
                }
            }
        }

        mostrarProductos(lista);
    }

    public interface OnProductosListener {
        void añadirProductos(Productos productos);

        void sacarProductos(Productos productos);

        void terminoCargar(List<Categorias> lista);
    }

    private class ProductosAdapter extends AbstractExpandableItemAdapter<ProductosAdapter.CategoriaViewHolder, ProductosAdapter.GeneralViewHolder> {
        private final List<Categorias> mDataset;

        ProductosAdapter(List<Categorias> productos) {
            setHasStableIds(true);
            mDataset = productos;
        }

        @Override
        public int getGroupCount() {
            return mDataset.size();
        }

        @Override
        public int getChildCount(int groupPosition) {
            return mDataset.get(groupPosition).getProductos().size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return mDataset.get(groupPosition).getProductos().get(childPosition).getIdProducto();
        }

        @Override
        public CategoriaViewHolder onCreateGroupViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_productos_categoria, parent, false);
            return new CategoriaViewHolder(v);
        }

        @Override
        public GeneralViewHolder onCreateChildViewHolder(ViewGroup parent, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_productos_general, parent, false);
            return new GeneralViewHolder(v);
        }

        @Override
        public void onBindGroupViewHolder(CategoriaViewHolder holder, int groupPosition, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            Categorias categorias = mDataset.get(groupPosition);
            holder.tvCategoria.setText(categorias.getNombre());
        }

        @Override
        public void onBindChildViewHolder(final GeneralViewHolder holder, int groupPosition, int childPosition, @IntRange(from = -8388608L, to = 8388607L) int viewType) {
            Categorias categorias = mDataset.get(groupPosition);
            final Productos productos = categorias.getProductos().get(childPosition);

            String url = ComunicacionClient.URL_IMAGNES + productos.getIdProducto() + ".jpg";

            Glide.with(getContext()).load(url).thumbnail(0.6f).centerCrop().placeholder(R.mipmap.logo_redondo).into(holder.ivProducto);

            Locale def = Locale.getDefault();
            holder.tvNombre.setText(productos.getNombre());
            holder.tvPresentacion.setText(productos.getPresentacion());
            holder.tvPrecio.setText(String.format(def, "$ %1$.0f", productos.getPrecio()));
            String marca = productos.getMarca();
            if (marca == null)
                holder.tvMarca.setVisibility(View.GONE);
            else
                holder.tvMarca.setText(marca);

            if (productos.getPedidos() > 0) {
                mostrarPedidos(holder, productos);
            } else {
                sacarPedidos(holder);
            }

            holder.btnPedir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pedidos = productos.getPedidos() + 1;
                    productos.setPedidos(pedidos);
                    mostrarPedidos(holder, productos);
                    mOnProductosListener.añadirProductos(productos);

                }
            });

            holder.btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productos.setPedidos(0);
                    sacarPedidos(holder);
                    mOnProductosListener.sacarProductos(productos);
//                    listaProductos.remove(productos);
//                    mostarChango();
                }
            });

        }

        private void sacarPedidos(GeneralViewHolder holder) {
            holder.tvPedidos.setVisibility(View.INVISIBLE);
            holder.btnCancelar.setVisibility(View.INVISIBLE);
        }

        private void mostrarPedidos(GeneralViewHolder holder, Productos productos) {
            holder.tvPedidos.setVisibility(View.VISIBLE);
            holder.tvPedidos.setText(String.valueOf(productos.getPedidos()));
            holder.btnCancelar.setVisibility(View.VISIBLE);
        }

//        private void mostarChango() {
//            if (mOnProductosListener != null) {
//                mOnProductosListener.mostrarChango(listaProductos.size() != 0);
//            }
//        }

        @Override
        public boolean onCheckCanExpandOrCollapseGroup(CategoriaViewHolder holder, int groupPosition, int x, int y, boolean expand) {
            return true;
        }

        class CategoriaViewHolder extends AbstractExpandableItemViewHolder {

            private final TextView tvCategoria;

            CategoriaViewHolder(View view) {
                super(view);
                tvCategoria = (TextView) view.findViewById(R.id.tv_categoria);
            }
        }

        class GeneralViewHolder extends AbstractExpandableItemViewHolder {

            private final ImageView ivProducto;
            private final TextView tvNombre;
            private final TextView tvPrecio;
            private final TextView tvPresentacion;
            private final TextView tvMarca;

            private final TextView tvPedidos;
            private final ImageButton btnCancelar;
            private final ImageButton btnPedir;


            GeneralViewHolder(View view) {
                super(view);

                ivProducto = (ImageView) view.findViewById(R.id.iv_producto);
                tvNombre = (TextView) view.findViewById(R.id.tv_nombre);
                tvPrecio = (TextView) view.findViewById(R.id.tv_precio);
                tvPresentacion = (TextView) view.findViewById(R.id.tv_presentacion);
                tvMarca = (TextView) view.findViewById(R.id.tv_marca);

                tvPedidos = (TextView) view.findViewById(R.id.tv_pedidos);
                btnCancelar = (ImageButton) view.findViewById(R.id.btn_cancelar);
                btnPedir = (ImageButton) view.findViewById(R.id.btn_pedir);

                Resources res = getContext().getResources();
                btnCancelar.setImageDrawable(VectorDrawableCompat.create(res, R.drawable.ic_cancelar, getContext().getTheme()));
                btnPedir.setImageDrawable(VectorDrawableCompat.create(res, R.drawable.ic_pedir, getContext().getTheme()));
            }
        }
    }


    private class MySpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        RecyclerViewExpandableItemManager expMgr;
        ProductosAdapter adapter;
        int spanCount;

        MySpanSizeLookup(RecyclerViewExpandableItemManager expMgr, GridLayoutManager lm, ProductosAdapter adapter) {
            this.expMgr = expMgr;
            this.adapter = adapter;
            this.spanCount = lm.getSpanCount();
        }

        @Override
        public int getSpanSize(int position) {
            long packedPos = expMgr.getExpandablePosition(position);
            int childPos = RecyclerViewExpandableItemManager.getPackedPositionChild(packedPos);

            if (childPos == RecyclerView.NO_POSITION) {
                return spanCount;
            } else {
                return 1;
            }
        }
    }
}