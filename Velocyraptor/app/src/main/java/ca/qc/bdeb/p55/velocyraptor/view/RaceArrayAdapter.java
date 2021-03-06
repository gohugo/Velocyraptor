package ca.qc.bdeb.p55.velocyraptor.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.qc.bdeb.p55.velocyraptor.R;
import ca.qc.bdeb.p55.velocyraptor.common.Formatting;
import ca.qc.bdeb.p55.velocyraptor.model.Course;
import ca.qc.bdeb.p55.velocyraptor.model.ItemCourse;


public class RaceArrayAdapter extends ArrayAdapter<ItemCourse> {
    public RaceArrayAdapter(Context context, int resource, List<ItemCourse> item) {
        super(context, resource, item);
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
        final ItemCourse ancienneCourse = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_course, null);
            holder = new CourseHistoryHolder();

            holder.lblDate = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_datevalue);
            holder.lblTypedeCourse = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_typevalue);
            holder.lblTemps = (TextView) convertView.findViewById(R.id.textView5historiquecourse_lbl_tempvalue);
            holder.lblDistance = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_distancevalue);
            holder.lblCalorie = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_calorievalue);
            holder.lblNbStep = (TextView) convertView.findViewById(R.id.historiquecourse_lbl_nbpasvalue);

            convertView.setTag(holder);
        } else {
            holder = (CourseHistoryHolder) convertView.getTag();
        }

        String nomCourse = getContext().getString(ancienneCourse.getTypeCourse()
                == Course.TypeCourse.APIED ? R.string.footrace : R.string.bikerace);

        holder.lblTypedeCourse.setText(nomCourse);
        holder.lblDate.setText(Formatting.formatDate(ancienneCourse.getTimestamp(), getContext().getResources()));
        holder.lblTemps.setText(Formatting.formatExactDuration(ancienneCourse.getDurationInMilliseconds()));
        holder.lblDistance.setText(Formatting.formatDistance(ancienneCourse.getTotalDistance()));
        holder.lblCalorie.setText(Integer.toString(ancienneCourse.getNbCalorieBurn()) + getContext().getString(R.string.calories));

        if (ancienneCourse.getTypeCourse() == Course.TypeCourse.APIED) {
            holder.lblNbStep.setVisibility(View.VISIBLE);
            holder.lblNbStep.setText(Integer.toString(ancienneCourse.getNbStep()) + getContext().getString(R.string.steps));
        }

        return convertView;
    }
}
