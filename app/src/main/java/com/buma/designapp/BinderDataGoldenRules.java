package com.buma.designapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class BinderDataGoldenRules extends BaseAdapter {

    // XML node keys
    static final String KEY_TAG = "useritem"; // parent node
    static final String KEY_ID = "employeeid";
    static final String KEY_FULLNAME = "name";
    static final String KEY_POSITION = "position";
    static final String KEY_PHOTO = "photo";

    LayoutInflater inflater;
    ImageView thumb_image;
    List<HashMap<String, String>> UserDataCollection;
    ViewHolder holder;

    public BinderDataGoldenRules() {
        // TODO Auto-generated constructor stub
    }

    public BinderDataGoldenRules(Activity act, List<HashMap<String, String>> map) {

        this.UserDataCollection = map;

        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public int getCount() {
        // TODO Auto-generated method stub
//		return idlist.size();
        return UserDataCollection.size();
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

            vi = inflater.inflate(R.layout.activity_golden_rules_list_pattern, null);
            holder = new ViewHolder();

            holder.tvName = (TextView) vi.findViewById(R.id.gr_lst_fullname); // fullname
            holder.tvPosition = (TextView) vi.findViewById(R.id.gr_lst_position); // position
            holder.tvPhoto = (ImageView) vi.findViewById(R.id.gr_lst_photo); // thumb image

            vi.setTag(holder);
        } else {

            holder = (ViewHolder) vi.getTag();
        }

        // Setting all values in listview

        holder.tvName.setText(UserDataCollection.get(position).get(KEY_FULLNAME));
        holder.tvPosition.setText(UserDataCollection.get(position).get(KEY_POSITION));

        //Setting an image
        String uri = "drawable/" + UserDataCollection.get(position).get(KEY_PHOTO);
        int imageResource = vi.getContext().getApplicationContext().getResources().getIdentifier(uri, null, vi.getContext().getApplicationContext().getPackageName());
        Drawable image = vi.getContext().getResources().getDrawable(imageResource);
        holder.tvPhoto.setImageDrawable(image);

        return vi;
    }

    /*
     *
     * */
    static class ViewHolder {

        TextView tvName, tvPosition;
        ImageView tvPhoto;
    }

}
