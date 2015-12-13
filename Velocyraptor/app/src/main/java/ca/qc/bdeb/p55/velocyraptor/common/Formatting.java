package ca.qc.bdeb.p55.velocyraptor.common;

import java.text.DecimalFormat;

/**
 * Méthodes qui permettent de formater des valeurs en chaîne de caractères.
 */
public final class Formatting {
    private Formatting() throws InstantiationException {
        throw new InstantiationException("Cette classe n'est pas instanciable.");
    }

    /**
     * Formate en kilomètres une distance en mètres.
     * @param distance Distance en mètres.
     * @return
     */
    public static String formatDistance(int distance){
        return formatDistanceWithoutSuffix(distance) + " km";
    }

    public static String formatDistanceWithoutSuffix(int distance){
        StringBuilder distanceBuilder = new StringBuilder();
        // TODO virgule vs. point (anglais et français)
        if (distance < 1000) {
            distanceBuilder.append("0,");
            if (distance < 100)
                distanceBuilder.append("0");
            if (distance < 10)
                distanceBuilder.append("0");
            distanceBuilder.append(distance);
        } else {
            distanceBuilder.append(distance).insert(distanceBuilder.length() - 3, ",");
        }

        return distanceBuilder.toString();
    }

    /**
     * À partir d'un nombre de secondes, crée une chaîne telle que «8 h 47 min 12 s».
     * @param seconds Nombre de secondes.
     * @return Résultat formaté.
     */
    public static String formatNiceDuration(int seconds){
        int minutes = seconds / 60;
        seconds %= 60;

        int hours = minutes / 60;
        minutes %= 60;

        int days = hours / 24;
        hours %= 24;

        // TODO langue (+ singulier/pluriel)
        if(days > 0)
            return days + " days, " + hours + " h " + minutes + " min " + seconds + " s";

        return hours + " h " + minutes + " min " + seconds + " s";
    }

    public static String formatExactDuration(int milliseconds){
        DecimalFormat df = new DecimalFormat("00");
        StringBuilder builder = new StringBuilder();

        int hours = milliseconds / (3600 * 1000);
        int remaining = milliseconds % (3600 * 1000);

        int minutes = remaining / (60 * 1000);
        remaining = remaining % (60 * 1000);

        int seconds = remaining / 1000;
        remaining = remaining % 1000;

        int hundredths = remaining / 10;

        if (hours > 0) {
            builder.append(df.format(hours))
                    .append(":");
        }

        builder.append(df.format(minutes))
                .append(":")
                .append(df.format(seconds))
                .append(".")
                .append(df.format(hundredths));

        return builder.toString();
    }
}
