module com.example.optimisationcompetitionssportives {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires javafx.graphics;
    requires cplex;
    requires java.sql;


    opens com.example.optimisationcompetitionssportives to javafx.fxml;
    exports com.example.optimisationcompetitionssportives;
    exports DB.Equipe;
    opens DB.Equipe to javafx.fxml;
}