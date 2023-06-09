package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ParametersController {
    @FXML
    private Label error;

    @FXML
    private CheckBox timeLimitActivate;

    @FXML
    private TextField timeLimit;

    @FXML
    private TextField clusterSize;

    @FXML
    private TextField numberInitializations;

    @FXML
    private TextField worseningPercentageField;

    @FXML
    private Button closeButton;

    @FXML
    private Button launchButton;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private HBox progressBox;

    @FXML
    private HBox verticalFirstPartLinear;

    @FXML
    HBox verticalFirstPartGluttonous;

    @FXML
    HBox verticalSecondPartGluttonous;

    @FXML
    HBox improveWorstCluster;

    @FXML
    CheckBox improveWorstClusterBool;

    @FXML
    HBox worseningPercentage;

    ResultatsPoulesController controller;

    public void setController(ResultatsPoulesController controller) {
        this.controller = controller;
    }

    public void init(){
        if (Context.getMode() == "lineaire")
        {
            verticalFirstPartGluttonous.setVisible(false);
            verticalFirstPartGluttonous.setManaged(false);
            verticalSecondPartGluttonous.setVisible(false);
            verticalSecondPartGluttonous.setManaged(false);

            timeLimit.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    keyNotNumberEvent(newValue, timeLimit);
                }
                else
                {
                    error.setVisible(false);
                }
            });
        }
        else if (Context.getMode() == "glouton")
        {
            verticalFirstPartLinear.setVisible(false);
            verticalFirstPartLinear.setManaged(false);
            worseningPercentage.setVisible(false);
            worseningPercentage.setManaged(false);

            numberInitializations.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    keyNotNumberEvent(newValue, numberInitializations);
                }
                else
                {
                    error.setVisible(false);
                }
            });

            worseningPercentageField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    keyNotNumberEvent(newValue, worseningPercentageField);
                }
                else
                {
                    error.setVisible(false);
                }
            });
        }

        clusterSize.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                keyNotNumberEvent(newValue, clusterSize);
            }
            else
            {
                error.setVisible(false);
            }
        });
    }

    private void keyNotNumberEvent(String newValue, TextField textField) {
        textField.setText(newValue.replaceAll("[^\\d]", ""));
        error.setText("Les champs doivent être des nombres");
        error.setVisible(true);
    }

    @FXML
    void onClose(ActionEvent event) throws IOException {
        close();
    }

    void close(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onLaunch(){
        // int teamsNumber = 100;
        int teamsNumber = Context.getEquipeSelected().size();
        try {
            error.setVisible(false);
            int clusterSizeInt = Integer.parseInt(clusterSize.getText());
            if (teamsNumber % clusterSizeInt != 0) {
                error.setText("Division : Nombre d'équipes (" + teamsNumber + ")/Taille d'une poule (" + clusterSizeInt + ") ne tombe pas pile !");
                error.setVisible(true);
            }
            else if(clusterSizeInt < 2){
                error.setText("Le nombre d'équpes par poules doit être au moins égal à 2");
                error.setVisible(true);
            }
            else {
                System.out.println("launch");
                if (Context.getMode() == "lineaire") {
                    if (timeLimitActivate.isSelected()) {
                        Integer.parseInt(timeLimit.getText());
                        Context.addToResolutionParameters("timeLimit", timeLimit.getText());
                    }
                    Context.addToResolutionParameters("clusterSize", String.valueOf(clusterSizeInt));
                    Context.addToResolutionParameters("timeLimitActivated", String.valueOf(timeLimitActivate.isSelected()));
                } else if (Context.getMode() == "glouton") {
                    int nombreEssais = Integer.parseInt(numberInitializations.getText());
                    if (nombreEssais < 1) {
                        error.setText("Le nombre d'essai doit être au moins de 1");
                        error.setVisible(true);
                    }
                    if (nombreEssais >= 1) {
                        Context.addToResolutionParameters("clusterSize", String.valueOf(clusterSizeInt));
                        Context.addToResolutionParameters("numberTests", String.valueOf(nombreEssais));
                        Context.addToResolutionParameters("improveWorstCluster", String.valueOf(improveWorstClusterBool.isSelected()));
                        if (improveWorstClusterBool.isSelected()) {
                            Context.addToResolutionParameters("worseningPercentage", String.valueOf(worseningPercentageField.getText()));
                        }
                    }
                }
                launchButton.setVisible(false);
                progress.setProgress(-1);
                progressBox.setVisible(true);
                controller.launchResolution();
            }
        } catch (NumberFormatException nfe) {
            error.setText("L'un des champs est vide");
            error.setVisible(true);
        }
    }

    @FXML
    public void showPercentageWorsening(){
        if (improveWorstClusterBool.isSelected())
        {
            worseningPercentage.setVisible(true);
            worseningPercentage.setManaged(true);
        }
        else
        {
            worseningPercentage.setVisible(false);
            worseningPercentage.setManaged(false);
        }
    }

    @FXML
    protected void onKeyEvent(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            this.onLaunch();
        }
    }

}