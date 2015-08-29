package tamanegisoul.screentime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
 * アプリ一覧のListAdapter。
 */
public class AppListAdapter extends ArrayAdapter<ApplicationInfo> implements View.OnClickListener {

    private LayoutInflater mLayoutInflater;
    private PackageManager mPackageManager;
    // CheckBoxの選択イベントでCheckBoxに対応するアプリケーションを取得するための入れ物
    private Map<CheckBox, ApplicationInfo> mDataMap;

    public AppListAdapter(Context context, int resourceId, List<ApplicationInfo> applicationInfoList){
        super(context, resourceId, applicationInfoList);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = context.getPackageManager();
        mDataMap = new HashMap<>();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convertViewは使いまわされている可能性があるのでnullの時だけ新しく作る
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);
        }

        ApplicationInfo applicationInfo = getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_imageView);
        imageView.setImageDrawable(mPackageManager.getApplicationIcon(applicationInfo));
        TextView textView = (TextView)convertView.findViewById(R.id.list_item_textView);
        textView.setText(applicationInfo.loadLabel(mPackageManager));
        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_item_checkBox);
        mDataMap.put(checkBox, applicationInfo);
        boolean isRestricted = PreferenceHelper.isRestrictedApp(getContext(), applicationInfo.packageName);
        checkBox.setChecked(isRestricted);
        checkBox.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View view) {
        CheckBox checkBox = (CheckBox) view;
        Logger.d(this, mDataMap.get(checkBox).packageName + " is clicked. isChecked() is " + checkBox.isChecked());
        PreferenceHelper.setRestrictedApp(getContext(), mDataMap.get(checkBox).packageName, checkBox.isChecked());
    }

}
