package ca.qc.bdeb.p55.velocyraptor.common;

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

        return distanceBuilder.append(" km").toString();
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
}
