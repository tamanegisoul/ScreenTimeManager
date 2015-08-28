package tamanegisoul.screentime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AppListActivityFragment extends Fragment {

    public AppListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        ListView listView = (ListView)view.findViewById(R.id.listView_app_list);
        listView.setAdapter(new AppListAdapter(view.getContext(), R.layout.list_item, StatsUtils.getApplicationInfoSortedList(view.getContext())));

        return view;
    }
}
