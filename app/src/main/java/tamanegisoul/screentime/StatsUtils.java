package tamanegisoul.screentime;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatsUtils {

    public static List<ApplicationInfo> getApplicationInfoSortedList(Context context) {
        List<ApplicationInfo> applicationInfoList = context.getPackageManager().getInstalledApplications(0);
        List<ApplicationInfo> list = new ArrayList<>();
        for (ApplicationInfo info : applicationInfoList) {
            //if((info.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM){
            if (((info.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM
                    || info.processName.equals("com.android.chrome")
                    || info.processName.equals("com.google.android.youtube")
                    || info.processName.equals("com.google.android.gm")
                    || info.processName.equals("com.android.settings")
                    || info.processName.equals("com.android.vending")
            ) && !info.processName.equals("tamanegisoul.screentime")) {
                list.add(info);
            }
        }
        final PackageManager packageManager = context.getPackageManager();
        Collections.sort(list, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                return lhs.loadLabel(packageManager).toString().compareTo(rhs.loadLabel(packageManager).toString());
            }
        });
        return list;
    }
}
