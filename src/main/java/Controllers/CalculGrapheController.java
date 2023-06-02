package Controllers;

import Graphe.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.graphstream.stream.GraphParseException;

import java.io.IOException;

public class CalculGrapheController {

    @FXML
    private TextField fichierField;

    @FXML
    private Button validerButton;

    public void initialize() {
    }

    @FXML
    void onValiderButtonClick(ActionEvent event) throws IOException, GraphParseException, ClassNotFoundException {
        String nomFichier = fichierField.getText();

        // Vérifier si le nom de fichier est valide et effectuer les opérations nécessaires
        if (nomFichier != null && !nomFichier.isEmpty()) {
            Main mainInstance = new Main();

            try {
                mainInstance.main(new String[0]);
            } catch (IOException | ClassNotFoundException | GraphParseException e) {
                e.printStackTrace();
            }
            mainInstance.init(nomFichier);
        }
    }

}
