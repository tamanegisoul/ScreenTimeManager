package tamanegisoul.screentime;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        List<ApplicationInfo> applicationInfoList = view.getContext().getPackageManager().getInstalledApplications(0);
        final PackageManager packageManager = view.getContext().getPackageManager();
        Collections.sort(applicationInfoList, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                return lhs.loadLabel(packageManager).toString().compareTo(rhs.loadLabel(packageManager).toString());
            }
        });
        listView.setAdapter(new AppListAdapter(view.getContext(), R.layout.list_item, applicationInfoList));

        return view;
    }
}
