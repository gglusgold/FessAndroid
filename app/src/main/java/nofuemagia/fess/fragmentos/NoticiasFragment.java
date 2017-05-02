package nofuemagia.fess.fragmentos;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nofuemagia.fess.Aplicacion;
import nofuemagia.fess.otros.ComunicacionClient;
import nofuemagia.fess.R;

/**
 * Created by jlionti on 14/02/2017. No Fue Magia
 */
public class NoticiasFragment extends Fragment {

    private CustomTabsClient mClient;
    private CustomTabsIntent.Builder mBuilder;

    private SwipeRefreshLayout mySwipeRefreshLayout;
    private RecyclerView rvNoticias;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_noticias, container, false);

        mySwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.sf_noticias);
        rvNoticias = (RecyclerView) v.findViewById(R.id.rv_noticias);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        buscarNoticias();
                    }
                }
        );
        buscarNoticias();

        CustomTabsClient.bindCustomTabsService(getContext(), "com.android.chrome", new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient = customTabsClient;

                mBuilder = new CustomTabsIntent.Builder(getSession());
                mBuilder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                mBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                mBuilder.addDefaultShareMenuItem();
                mBuilder.setShowTitle(true);
                mBuilder.setStartAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                mBuilder.setExitAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                mBuilder.enableUrlBarHiding();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mClient = null;
            }
        });

        return v;
    }


    private CustomTabsSession getSession() {
        return mClient.newSession(new CustomTabsCallback() {
            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                super.onNavigationEvent(navigationEvent, extras);
            }
        });
    }


    private void buscarNoticias() {
        ComunicacionClient client = new ComunicacionClient();
        client.post(ComunicacionClient.NOTICIAS, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Aplicacion.mostrarSnack(mySwipeRefreshLayout, getResources().getString(R.string.error_noticias), null);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Aplicacion.mostrarSnack(mySwipeRefreshLayout, getResources().getString(R.string.error_noticias), null);
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mySwipeRefreshLayout.setRefreshing(false);

                Type listType = new TypeToken<List<Noticias>>() {
                }.getType();
                List<Noticias> listaNoticias = new Gson().fromJson(String.valueOf(response.optJSONArray("Noticias")), listType);

                NoticiasAdapter adapter = new NoticiasAdapter(listaNoticias, getContext());
                rvNoticias.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvNoticias.setAdapter(adapter);
            }
        });
    }

    private class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.NoticiasViewHolder> {

        private final List<Noticias> mDataset;
        private final Context mContext;

        NoticiasAdapter(List<Noticias> listaNoticias, Context c) {
            mDataset = listaNoticias;
            mContext = c;
        }

        @Override
        public NoticiasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NoticiasViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_noticias, null));
        }

        @Override
        public void onBindViewHolder(NoticiasViewHolder holder, int position) {
            final Noticias noticia = mDataset.get(position);

            Glide.with(mContext).load(noticia.getImagen()).into(holder.ivNoticia);
            holder.tvTitulo.setText(noticia.getTitulo());
            holder.tvFecha.setText(noticia.getFecha());
            holder.tvCopete.setText(noticia.getCopete());

            if (noticia.getAutor() != null) {
                holder.tvAutor.setText(noticia.getAutor());
            } else {
                holder.tvAutorAutor.setVisibility(View.GONE);
                holder.tvAutor.setVisibility(View.GONE);
            }

            prefetchContent(Uri.parse(noticia.getLink()));

            if (noticia.getLink() != null) {
                holder.tvLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomTabsIntent mIntent = mBuilder.build();
                        mIntent.launchUrl(getContext(), Uri.parse(noticia.getLink()));
                    }
                });
            } else {
                holder.tvAutor.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }


        class NoticiasViewHolder extends RecyclerView.ViewHolder {

            private final ImageView ivNoticia;
            private final TextView tvTitulo;
            private final TextView tvFecha;
            private final TextView tvCopete;
            private final TextView tvAutorAutor;
            private final TextView tvAutor;
            private final TextView tvLink;

            NoticiasViewHolder(View itemView) {
                super(itemView);

                ivNoticia = (ImageView) itemView.findViewById(R.id.iv_noticia);
                tvTitulo = (TextView) itemView.findViewById(R.id.tv_titulo_noticia);
                tvFecha = (TextView) itemView.findViewById(R.id.tv_fecha_noticia);
                tvCopete = (TextView) itemView.findViewById(R.id.tv_copete_noticia);
                tvAutorAutor = (TextView) itemView.findViewById(R.id.tv_autor_autor);
                tvAutor = (TextView) itemView.findViewById(R.id.tv_autor_noticia);
                tvLink = (TextView) itemView.findViewById(R.id.tv_link_noticia);
            }
        }

        void prefetchContent(Uri url) {
            if (mClient != null) {
                mClient.warmup(0);
                CustomTabsSession customTabsSession = getSession();
                customTabsSession.mayLaunchUrl(url, null, null);
            }
        }
    }
}
