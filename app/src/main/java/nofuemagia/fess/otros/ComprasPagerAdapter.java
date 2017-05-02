package nofuemagia.fess.otros;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArraySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nofuemagia.fess.fragmentos.ConfirmarFragment;
import nofuemagia.fess.fragmentos.InstructivoFragment;
import nofuemagia.fess.fragmentos.LocalesFragment;
import nofuemagia.fess.fragmentos.ProductosFragment;
import nofuemagia.fess.modelo.Categorias;
import nofuemagia.fess.modelo.Compras;
import nofuemagia.fess.modelo.Locales;
import nofuemagia.fess.modelo.Productos;

/**
 * Created by Tano on 22/1/2017.
 * No Fue Magia
 */
public class ComprasPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> listadoFragments;
    private final Compras compras;
    public int idLocal;
    private Set<Productos> listaProductos;
    private InstructivoFragment instructivoFragment;
    private LocalesFragment localesFragment;
    private ProductosFragment productosFragment;
    private ConfirmarFragment confirmarFragment;
    private OnAdapter mOnAdapter;


    public ComprasPagerAdapter(Context c, FragmentManager fm, Compras compras) {
        super(fm);

        listadoFragments = new ArrayList<>();
        crearLista(c);

        this.compras = compras;
        idLocal = compras != null ? compras.getIdLocal() : -1;
    }

    private void crearLista(Context c) {
        instructivoFragment = (InstructivoFragment) Fragment.instantiate(c, InstructivoFragment.class.getName(), null);
        localesFragment = (LocalesFragment) Fragment.instantiate(c, LocalesFragment.class.getName(), null);
        productosFragment = (ProductosFragment) Fragment.instantiate(c, ProductosFragment.class.getName(), null);
        confirmarFragment = (ConfirmarFragment) Fragment.instantiate(c, ConfirmarFragment.class.getName(), null);

        instructivoFragment.traerLocales();
        instructivoFragment.setOnInstructivoListener(new InstructivoFragment.OnInstructivoListener() {
            @Override
            public void terminoLocales(List<Locales> locales) {
                localesFragment.mostrarLocales(locales, idLocal);
            }
        });

        localesFragment.setOnLocalListener(new LocalesFragment.LocalListener() {
            @Override
            public void localSeleccionado(int idLocal) {
                productosFragment.buscarProductos(idLocal);

                listaProductos = new ArraySet<>();
                mOnAdapter.mostrarConfimar(listaProductos.size() != 0);
                confirmarFragment.vaciar();
                ComprasPagerAdapter.this.idLocal = idLocal;
                mOnAdapter.localSeleccionado();
            }
        });

        productosFragment.setOnProductosListener(new ProductosFragment.OnProductosListener() {
            @Override
            public void añadirProductos(Productos productos) {
                listaProductos.add(productos);
                confirmarFragment.agregar(productos);
                mOnAdapter.mostrarConfimar(listaProductos.size() != 0);
            }

            @Override
            public void sacarProductos(Productos productos) {
                listaProductos.remove(productos);
                confirmarFragment.sacar(productos);
                mOnAdapter.mostrarConfimar(listaProductos.size() != 0);
            }

            @Override
            public void terminoCargar(List<Categorias> lista) {
                if ( compras != null )
                    productosFragment.actualizarLista(lista, compras.getProductos());
            }
        });

        listadoFragments.clear();
        listadoFragments.add(instructivoFragment);
        listadoFragments.add(localesFragment);
        listadoFragments.add(productosFragment);
        listadoFragments.add(confirmarFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return listadoFragments.get(position);
    }

    @Override
    public int getCount() {
        return listadoFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            default:
                return "";
        }
    }

    public void setOnAdapter(OnAdapter mOnAdapter) {
        this.mOnAdapter = mOnAdapter;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public Set<Productos> listaProductos() {
        return listaProductos;
    }

    public interface OnAdapter {
        void mostrarConfimar(boolean visible);

        void localSeleccionado();
    }
}
