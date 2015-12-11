package ca.qc.bdeb.p55.velocyraptor.common.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


import ca.qc.bdeb.p55.velocyraptor.R;
import ca.qc.bdeb.p55.velocyraptor.model.Course;


public class ArrayAdapterDeCourse extends ArrayAdapter<Course> {
    Context context;


    public ArrayAdapterDeCourse(Context context, int resource, ArrayList<Course> item) {
        super(context, resource, item);
        this.context = context;


    }

    private class CourseHistoryHolder {
        TextView txtNom;
        TextView txtAge;
        TextView txtAddresse;



    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        CourseHistoryHolder holder = null;
        Button button = null;
        final Course rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.historique_une_course, null);
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
