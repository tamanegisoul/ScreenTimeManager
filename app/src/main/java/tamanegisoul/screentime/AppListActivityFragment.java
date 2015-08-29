package tamanegisoul.screentime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 *
 */
public class AppListActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        ListView listView = (ListView)view.findViewById(R.id.listView_app_list);
        listView.setAdapter(new AppListAdapter(view.getContext(), R.layout.list_item, ApplicationUtils.getInstalledApplications(view.getContext())));

        return view;
    }
}
