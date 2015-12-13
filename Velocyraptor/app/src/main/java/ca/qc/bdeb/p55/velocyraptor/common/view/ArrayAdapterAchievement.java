package ca.qc.bdeb.p55.velocyraptor.common.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ca.qc.bdeb.p55.velocyraptor.R;
import ca.qc.bdeb.p55.velocyraptor.model.Achievement;
import ca.qc.bdeb.p55.velocyraptor.model.Course;
import ca.qc.bdeb.p55.velocyraptor.model.HistoriqueDeCourse;

/**
 * Created by hugo on 2015-12-12.
 */
public class ArrayAdapterAchievement extends ArrayAdapter<Achievement> {
    private Context context;

    public ArrayAdapterAchievement(Context context, int resource, ArrayList<Achievement> item) {
        super(context, resource, item);
        this.context = context;
    }

    private class CourseHistoryHolder {
//        TextView lblDate;
//        TextView lblTypedeCourse;
//        TextView lblTemps;
//        TextView lblDistance;
//        TextView lblCalorie;
//        TextView lblNbStep;
//        LinearLayout layoutNbPAS;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        CourseHistoryHolder holder = null;
        Button button = null;
        final Achievement rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.accomplissement_un_accomplissement, null);
            holder = new CourseHistoryHolder();
            //ici initialiser les component de la listview
//            holder.txtNom = (TextView) convertView.findViewById(R.id.unclient_lbl_nom);



            convertView.setTag(holder);
        } else {
            holder = (CourseHistoryHolder) convertView.getTag();

        }

        //do set text therre


        return convertView;

    }
}
