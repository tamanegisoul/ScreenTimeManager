package tamanegisoul.screentime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ListAdapter of installed application list.
 * The layout of list item is defined as res/layout/list_item.xml.
 *
 */
public class AppListAdapter extends ArrayAdapter<ApplicationInfo>{



    private LayoutInflater mLayoutInflater;
    private PackageManager mPackageManager;
    private CheckBoxClickListener mCheckBoxClickListener;
    private Map<CheckBox, ApplicationInfo> mDataMap;

    public AppListAdapter(Context context, int resourceId, List<ApplicationInfo> applicationInfoList){
        super(context, resourceId, applicationInfoList);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = context.getPackageManager();
        mDataMap = new HashMap<>();
        mCheckBoxClickListener = new CheckBoxClickListener(mDataMap);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定行(position)のデータを得る
        ApplicationInfo applicationInfo = getItem(position);
        // convertViewは使いまわされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) convertView = mLayoutInflater.inflate(R.layout.list_item, null);
        // set the icon image
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_imageView);
        imageView.setImageDrawable(mPackageManager.getApplicationIcon(applicationInfo));
        // set the application label
        TextView textView = (TextView)convertView.findViewById(R.id.list_item_textView);
        textView.setText(applicationInfo.loadLabel(mPackageManager));
        // set checked if it is an restricted application
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_item_checkBox);
        mDataMap.put(checkBox, applicationInfo);
        boolean isRestricted = PreferenceHelper.isRestrictedApp(getContext(), applicationInfo.packageName);
        checkBox.setChecked(isRestricted);
        checkBox.setOnClickListener(mCheckBoxClickListener);
        return convertView;
    }

    public class CheckBoxClickListener implements View.OnClickListener {

        private Map<CheckBox, ApplicationInfo> mDataMap;

        public CheckBoxClickListener(Map<CheckBox, ApplicationInfo> keyMap){
            super();
            mDataMap = keyMap;
        }

        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;
            Logger.d(this, mDataMap.get(checkBox).packageName + " is clicked.");
            PreferenceHelper.setRestrictedApp(getContext(), mDataMap.get(checkBox).packageName, checkBox.isChecked());
        }
    }

}
