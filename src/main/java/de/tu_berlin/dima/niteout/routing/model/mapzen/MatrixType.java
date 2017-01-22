package de.tu_berlin.dima.niteout.routing.model.mapzen;

/**
 * Created by aardila on 1/22/2017.
 */
public enum MatrixType {

    OneToMany ("one_to_many"),
    ManyToOne ("many_to_one"),
    ManyToMany ("many_to_many");

    private final String ApiString;

    private MatrixType(String apiString) { this.ApiString = apiString; }
    public String getApiString() { return ApiString; }
}
