package ca.qc.bdeb.p55.velocyraptor.model;

/**
 * Created by hugo on 2015-11-28.
 */
public class Setting {
    public enum TypeDeCourse {
        APIED, VELO
    }

    private TypeDeCourse typeDeCourse;

    private boolean courseEnCour;

    public Setting() {
        this.typeDeCourse = TypeDeCourse.APIED;
        this.courseEnCour = false;
    }

    public TypeDeCourse getTypeDeCourse() {
        return typeDeCourse;
    }

    public void setTypeDeCourse(TypeDeCourse typeDeCourse) {
        this.typeDeCourse = typeDeCourse;
    }
}
