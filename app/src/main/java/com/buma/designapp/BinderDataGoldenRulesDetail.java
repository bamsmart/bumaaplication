package com.buma.designapp;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class BinderDataGoldenRulesDetail extends BaseAdapter {

    // XML node keys
    static final String KEY_TAG = "menuitem"; // parent node

    static final String KEY_DAY = "day";
    static final String KEY_DEPT = "dept";

    static final String KEY_SUB_TAG = "checklist"; // sub parent node
    static final String KEY_LIST_ITEM = "listitem1";

    LayoutInflater inflater;

    List<HashMap<String, String>> MenuDataCollection;
    ViewHolder holder;

    public BinderDataGoldenRulesDetail() {
        // TODO Auto-generated /[[constructor stub
    }

    public BinderDataGoldenRulesDetail(Activity act,
                                       List<HashMap<String, String>> map) {

        this.MenuDataCollection = map;

        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        // return idlist.size();
        return MenuDataCollection.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null) {

            vi = inflater.inflate(
                    R.layout.activity_golden_rules_list_pattern_detail, null);
            holder = new ViewHolder();

            holder.TvTopik = (TextView) vi.findViewById(R.id.gr_lst_topik_detail); //

            vi.setTag(holder);
        } else {

            holder = (ViewHolder) vi.getTag();
        }
        // Setting all values in listview

        holder.TvTopik.setText(Html.fromHtml(MenuDataCollection.get(position).get(
                KEY_LIST_ITEM)));

        return vi;
    }

    /*
     *
     * */
    static class ViewHolder {
        TextView TvTopik;
    }

}
