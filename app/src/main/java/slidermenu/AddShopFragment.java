package slidermenu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import waldo.bike.bikeshops.R;

/**
 * Created by Narcis11 on 23.12.2014.
 */
public class AddShopFragment extends Fragment {
    public AddShopFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slider_add_shop,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
