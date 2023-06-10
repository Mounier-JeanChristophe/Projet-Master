package Controllers;

import Algorithme.Graphe.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.graphstream.stream.GraphParseException;

import java.io.IOException;

public class CalculGrapheController {

    @FXML
    private TextField fichierField;

    @FXML
    private Button validerButton;

    @FXML
    private Button construireGrapheButton;

    @FXML
    private Button afficherModeleButton;

    @FXML
    private Button sauvegarderProjetButton;

    @FXML
    private Button supprimerProjetButton;

    Main mainInstance = new Main();

    public void initialize() {
    }

    @FXML
    void onValiderButtonClick(ActionEvent event) throws IOException, GraphParseException, ClassNotFoundException {
        String nomFichier = fichierField.getText();

        // Vérifier si le nom de fichier est valide et effectuer les opérations nécessaires
        if (nomFichier != null && !nomFichier.isEmpty()) {

            try {
                mainInstance.main(new String[0]);
            } catch (IOException | ClassNotFoundException | GraphParseException e) {
                e.printStackTrace();
            }
            mainInstance.init(nomFichier);
        }
    }

    @FXML
    void onConstruireGrapheButtonClick(ActionEvent event) {
        mainInstance.construireGraphe();
    }

    @FXML
    void onAfficherModeleButtonClick(ActionEvent event) {
        mainInstance.afficherModele();
    }

    @FXML
    void onSauvegarderProjetButtonClick(ActionEvent event) {
        mainInstance.sauvegarderProjet();
    }

    @FXML
    void onSupprimerProjetButtonClick(ActionEvent event) {
        mainInstance.supprimerProjet();
    }
}