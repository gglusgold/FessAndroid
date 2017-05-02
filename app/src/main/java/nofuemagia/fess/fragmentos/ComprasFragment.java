package nofuemagia.fess.fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;
import nofuemagia.fess.otros.ComprasPagerAdapter;
import nofuemagia.fess.actividades.PantallaPrincipal;
import nofuemagia.fess.R;
import nofuemagia.fess.modelo.Compras;

/**
 * Created by jlionti on 14/02/2017. No Fue Magia
 */
public class ComprasFragment extends Fragment {

    private ScrollerViewPager viewPager;
    private ComprasPagerAdapter adapter;
    private boolean editando;
    private Compras compras;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compra, container, false);

        Bundle args = getArguments();
        if (args != null) {
            editando = true;
            compras = args.getParcelable("Compra");
        }

        viewPager = (ScrollerViewPager) v.findViewById(R.id.view_pager);
        SpringIndicator springIndicator = (SpringIndicator) v.findViewById(R.id.indicator);

        adapter = new ComprasPagerAdapter(getContext(), getChildFragmentManager(), editando ? compras : null);
        adapter.setOnAdapter(new ComprasPagerAdapter.OnAdapter() {
            @Override
            public void mostrarConfimar(boolean visible) {
                PantallaPrincipal.confirmar = visible;
                getActivity().invalidateOptionsMenu();
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.fixScrollSpeed();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        springIndicator.setViewPager(viewPager);

        if (editando)
            viewPager.setCurrentItem(2, false);

        return v;
    }

    public int getCurrentItem() {
        if (viewPager == null)
            return 0;
        return viewPager.getCurrentItem();
    }

    public ComprasPagerAdapter getAdapter() {
        return adapter;
    }

    public int getIdCompra() {
        if (editando)
            return compras.getIdCompra();
        else
            return -1;
    }
}
