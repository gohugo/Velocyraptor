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
 * Crée les items affichés dans la liste des accomplissements.
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
            holder.lblDescription = (TextView) convertView.findViewById(R.id.accomplissement_lbl_description);
            holder.lblReached = (TextView) convertView.findViewById(R.id.accomplissement_lbl_reached);

            int achievementDescriptionResourceId = getContext().getResources()
                    .getIdentifier("achievement" + rowItem.getId(), "string", getContext().getPackageName());
            holder.lblDescription.setText(getContext().getString(achievementDescriptionResourceId));
            holder.lblReached.setText(rowItem.isReached()?"done":"in progres");


            convertView.setTag(holder);
        } else {
            holder = (AchievementHolder) convertView.getTag();

        }

        //do set text therre


        return convertView;

    }
}
