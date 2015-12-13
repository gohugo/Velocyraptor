package ca.qc.bdeb.p55.velocyraptor.view;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ca.qc.bdeb.p55.velocyraptor.R;
import ca.qc.bdeb.p55.velocyraptor.common.Formatting;
import ca.qc.bdeb.p55.velocyraptor.model.Course;

/**
 * Permet le partage sur les réseaux sociaux.
 */
public final class ShareUtil {
    private ShareUtil() throws InstantiationException {
        throw new InstantiationException("Cette classe n'est pas instanciable.");
    }

    /**
     * Partage les résultats de l'utilisateur sur d'autres applications.
     */
    public static void shareResults(Context context, Course course){
        if(course != null) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(context.getString(R.string.share1))
                    .append(context.getString(course.getTypeCourse() == Course.TypeCourse.APIED ? R.string.share_ran : R.string.share_cycled))
                    .append(Formatting.formatDistanceWithoutSuffix(course.getDistanceInMeters()))
                    .append(context.getString(R.string.share2))
                    .append(course.getElapsedMinutes())
                    .append(context.getString(R.string.share3));
            if (course.getElapsedSeconds() % 60 != 0) {
                messageBuilder.append(course.getElapsedSeconds() % 60)
                        .append(context.getString(R.string.share4_opt));
            }
            messageBuilder.append(context.getString(R.string.share5))
                    .append(course.getCalories())
                    .append(context.getString(R.string.share6));

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, messageBuilder.toString());
            context.startActivity(Intent.createChooser(intent, "Share"));
        } else {
            Toast.makeText(context, context.getString(R.string.no_race), Toast.LENGTH_LONG).show();
        }
    }
}
