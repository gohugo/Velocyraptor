package ca.qc.bdeb.p55.velocyraptor.common.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.qc.bdeb.p55.velocyraptor.R;
import ca.qc.bdeb.p55.velocyraptor.model.Achievement;

/**
 * Crée les items affichés dans la liste des accomplissements.
 */
public class ArrayAdapterAchievement extends ArrayAdapter<Achievement> {
    public ArrayAdapterAchievement(Context context, int resource, ArrayList<Achievement> item) {
        super(context, resource, item);
    }

    private class AchievementHolder {
        TextView lblDescription;
        ImageView imgReached;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        AchievementHolder holder = null;
        final Achievement achievement = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.accomplissement_un_accomplissement, null);
            holder = new AchievementHolder();

            holder.lblDescription = (TextView) convertView.findViewById(R.id.accomplissement_lbl_description);
            holder.imgReached = (ImageView) convertView.findViewById(R.id.accomplissement_img_atteint);

            int achievementDescriptionResourceId = getContext().getResources()
                    .getIdentifier("achievement" + achievement.getId(), "string", getContext().getPackageName());
            holder.lblDescription.setText(getContext().getString(achievementDescriptionResourceId));

            Bitmap reachedImage = BitmapFactory.decodeResource(getContext().getResources(),
                    achievement.isReached() || Math.random() > 0.5 ? R.mipmap.ic_reached_achievement : R.mipmap.ic_unreached_achievement);
            holder.imgReached.setImageBitmap(reachedImage);

            convertView.setTag(holder);
        } else {
            holder = (AchievementHolder) convertView.getTag();
        }

        //do set text therre


        return convertView;

    }
}
