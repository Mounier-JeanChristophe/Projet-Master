module Optimisation.Competitions.Sportives {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires javafx.graphics;
    requires cplex;
    requires java.sql;
    requires decimal4j;
    requires com.google.common;
    requires gs.core;
    requires gs.algo;


    opens com.example.optimisationcompetitionssportives to javafx.fxml;
    exports com.example.optimisationcompetitionssportives;
    exports DB.Equipe;
    opens DB.Equipe to javafx.fxml;
}