package Controllers;

import DB.Equipe.Equipe;
import DB.Equipe.EquipeDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class CreerEquipeController {

    @FXML
    private Label errorNumber;

    @FXML
    private Label errorEmpty;

    EquipeDAO equipeDAO = new EquipeDAO();

    @FXML
    private TextField ageInput;


    @FXML
    private Button creerEquipe;

    @FXML
    private TextField niveauInput;

    @FXML
    private TextField nomInput;

    @FXML
    private TextField codePostalInput;


    @FXML
    private Button retour;

    @FXML
    private TextField sportInput;

    public void init(){
        codePostalInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                keyNotNumberEvent(newValue, codePostalInput);
            }
        });
    }

    private void keyNotNumberEvent(String newValue, TextField textField) {
        textField.setText(newValue.replaceAll("[^\\d]", ""));
        errorNumber.setText("Les champs doivent être des nombres");
        errorNumber.setVisible(true);
    }


    @FXML
    void onCreerEquipeButtonClick(ActionEvent event) throws IOException {
        if(nomInput.getText().isEmpty() || nomInput.getText().isEmpty() || nomInput.getText().isEmpty() || nomInput.getText().isEmpty() || nomInput.getText().isEmpty()){
            errorEmpty.setText("Tout les champs doivent être remplis.");
            errorEmpty.setVisible(true);
        } else {
            Equipe newEquipe = new Equipe(nomInput.getText(), sportInput.getText(), niveauInput.getText(), ageInput.getText(), codePostalInput.getText());
            equipeDAO.addEquipe(newEquipe);
            Parent tableViewParent = FXMLLoader.load(getClass().getResource("gerer-equipes-view.fxml"));
            Scene tableViewScene = new Scene(tableViewParent);

            // Get the stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(tableViewScene);
            window.show();
        }

    }

    @FXML
    void onRetourButtonClick(ActionEvent event) throws IOException {
        Context.getApp().showFxml("gerer-equipes-view.fxml");
    }

}
