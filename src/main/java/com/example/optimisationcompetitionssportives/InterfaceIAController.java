package com.example.optimisationcompetitionssportives;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

public class InterfaceIAController {

    @FXML
    private Pane rootPane;

    @FXML
    private Text titleText;

    @FXML
    private Text projectText;

    public void initialize() {
        // Code de l'initialisation
    }

    @FXML
    void onRetourButtonClick(ActionEvent event) throws IOException {
        Context.getApp().back();
    }

}