package tamanegisoul.screentime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 休日扱いする日付一覧のListAdapter。
 */
public class HolidayListAdapter extends ArrayAdapter<String> {

    private LayoutInflater mLayoutInflater;
    private PackageManager mPackageManager;

    public HolidayListAdapter(Context context, int resourceId, List<String> holidayList) {
        super(context, resourceId, holidayList);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = context.getPackageManager();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convertViewは使いまわされている可能性があるのでnullの時だけ新しく作る
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_holiday_list, null);
        }

        String value = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.list_item_textView);
        textView.setText(value);
        return convertView;
    }

}
