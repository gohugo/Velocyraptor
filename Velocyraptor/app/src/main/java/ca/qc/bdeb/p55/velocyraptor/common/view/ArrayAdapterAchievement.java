package ca.qc.bdeb.p55.velocyraptor.common.view;

import android.app.Activity;
import android.content.Context;
import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ca.qc.bdeb.p55.velocyraptor.R;
import ca.qc.bdeb.p55.velocyraptor.model.Achievement;

/**
 * Created by hugo on 2015-12-12.
 */
public class ArrayAdapterAchievement extends ArrayAdapter<Achievement> {
    private Context context;

    public ArrayAdapterAchievement(Context context, int resource, ArrayList<Achievement> item) {
        super(context, resource, item);
        this.context = context;
    }

    private class AchievementHolder {
        TextView lblDescription;
        TextView lblReached;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        AchievementHolder holder = null;
        Button button = null;
        final Achievement rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.accomplissement_un_accomplissement, null);
            holder = new AchievementHolder();
            //ici initialiser les component de la listview
//            holder.txtNom = (TextView) convertView.findViewById(R.id.unclient_lbl_nom);
            holder.lblDescription = (TextView) convertView.findViewById(R.id.accomplissement_lbl_description);
            holder.lblReached = (TextView) convertView.findViewById(R.id.accomplissement_lbl_reached);

            holder.lblDescription.setText(rowItem.getName());
            holder.lblReached.setText(rowItem.isReached()?"done":"in progres");


            convertView.setTag(holder);
        } else {
            holder = (AchievementHolder) convertView.getTag();

        }

        //do set text therre


        return convertView;

    }
}
