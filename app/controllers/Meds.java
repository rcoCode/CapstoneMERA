package controllers;

import play.mvc.Result;

import java.util.List;

/**
 * Created by rebec on 11/17/2015.
 */
public class Meds {
    public Result index(){
        List<Meds> meds=Meds.find.all();
        return ok(views.html.Medication.index.render());
    }
}
