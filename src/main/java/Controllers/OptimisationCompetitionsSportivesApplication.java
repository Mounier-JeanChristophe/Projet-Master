package Controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Application
 */
public class OptimisationCompetitionsSportivesApplication extends Application {

    Stage stage;

    /**
     * Lance l application.
     * Enregistre l application dans la classe context
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage=stage;
        Context.setApp(this);
        showHome();

    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Affiche la page d acceuil
     */
    public void showHome(){
        try {
            Context.addFXML("accueil-view.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(OptimisationCompetitionsSportivesApplication.class.getResource("accueil-view.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            scene.getStylesheets().add("style.css");
            stage.setTitle("Optimisation de Compétitions Sportives");
            stage.setScene(scene);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Revient a la page d acceuil
     */
    public void backToHome(){
        showHome();
    }

    /**
     * Revient a la page precendente
     */
    public void back() {
        String lastfxml=Context.getLastFXML();
        if (lastfxml.isEmpty()) {
            showHome();
        }
        else{
            showFxml(lastfxml);
        }
    }

    /**
     * Affiche une Pop up en fonction de son fxml
     * @param fxml
     * @return
     */
    public FXMLLoader showPopUp(String fxml){
        try {
            //Context.addFXML(fxml);
            //Charger le fichier fxml associé
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(OptimisationCompetitionsSportivesApplication.class.getResource(fxml)); //On charge la vue souhaitée
            Stage connectionStage = loader.load();

            connectionStage.show(); //Affichage de la fenêtre
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
        }
         return null;
    }

    /**
     * Affiche une page en fonction de son fxml
     * @param fxml
     */
    public void showFxml(String fxml){
        try {
            Context.addFXML(fxml);
            FXMLLoader fxmlLoader = new FXMLLoader(OptimisationCompetitionsSportivesApplication.class.getResource(fxml));

            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            scene.getStylesheets().add("style.css");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}