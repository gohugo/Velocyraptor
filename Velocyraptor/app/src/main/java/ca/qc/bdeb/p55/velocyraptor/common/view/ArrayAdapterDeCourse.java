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
import java.util.List;


import ca.qc.bdeb.p55.velocyraptor.R;
import ca.qc.bdeb.p55.velocyraptor.model.Course;
import ca.qc.bdeb.p55.velocyraptor.model.CustomChronometer;
import ca.qc.bdeb.p55.velocyraptor.model.HistoriqueDeCourse;


public class ArrayAdapterDeCourse extends ArrayAdapter<HistoriqueDeCourse> {
    Context context;


    public ArrayAdapterDeCourse(Context context, int resource, List<HistoriqueDeCourse> item) {
        super(context, resource, item);
        this.context = context;


    }

    private class CourseHistoryHolder {
        TextView lblDate;
        TextView lblTypedeCourse;
        TextView lblTemps;
        TextView lblDistance;
        TextView lblCalorie;
        TextView lblNbStep;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        CourseHistoryHolder holder;
        final HistoriqueDeCourse ancienneCourse = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.historique_une_course, null);
            holder = new CourseHistoryHolder();
            // ici initialiser les component de la listview
            holder.lblDate = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_datevalue);
            holder.lblTypedeCourse = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_typevalue);
            holder.lblTemps = (TextView) convertView.findViewById(R.id.textView5historiquecourse_lbl_tempvalue);
            holder.lblDistance = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_distancevalue);
            holder.lblCalorie = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_calorievalue);
            holder.lblNbStep = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_nbpasvalue);

            holder.lblTypedeCourse.setText(ancienneCourse.getTypeCourse().toString());
            holder.lblTemps.setText(ancienneCourse.getTime());
            holder.lblDistance.setText(Integer.toString(ancienneCourse.getTotalDistance()));
            holder.lblCalorie.setText(Integer.toString(ancienneCourse.getNbCalorieBurn()));

            if (ancienneCourse.getTypeCourse() == Course.TypeCourse.APIED) {
                holder.lblNbStep.setVisibility(View.VISIBLE);
                holder.lblNbStep.setText(Integer.toString(ancienneCourse.getNbStep()));
            }

            convertView.setTag(holder);
        } else {
            holder = (CourseHistoryHolder) convertView.getTag();
        }

        // do set text therre


        return convertView;

    }
}
