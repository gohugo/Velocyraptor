package ca.qc.bdeb.p55.velocyraptor.model;

/**
 * Created by hugo on 2015-12-12.
 */
public class Achievement {

    private int id;
    private boolean reached;
    private String name;

    /*
    Constructeur pour quand on les insert dans la bd
     */
    public Achievement(String name, boolean reached) {
        this.name = name;
        this.reached = reached;
    }

    /*
    Constructeur pour quand on les recuper de la bd
     */
    public Achievement(int id, String name, boolean reached) {
        this.id = id;
        this.name = name;
        this.reached = reached;
    }

    public boolean isReached() {
        return reached;
    }

    public void setReached(boolean reached) {
        this.reached = reached;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
