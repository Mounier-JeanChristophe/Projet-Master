package com.example.optimisationcompetitionssportives;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller pour la page d acceuil
 */
public class AccueilController {
    @FXML
    private Button gererEquipes;

    @FXML
    private Button calculerPoules;


    /**
     * Affiche la page permettant de gerer les equipes
     */
    @FXML
    void onGererEquipesButtonClick(ActionEvent event) throws IOException {
        Context.getApp().showFxml("gerer-equipes-view.fxml");
    }

    /**
     * Affiche la page permettant de selectionner les equipes pour creer des poules
     */
    @FXML
    void onCalculerPoulesClickButton(ActionEvent event) throws IOException {
        Context.getApp().showFxml("calculer-poules-view.fxml");
    }

    @FXML
    void onInterfaceIAClickButton(ActionEvent event) throws IOException {
        Context.getApp().showFxml("interfaceIA.fxml");
    }
}