package com.example.optimisationcompetitionssportives;

import API.CitiesDistanceAPI;
import Algorithme.Clustering;
import Algorithme.ClusteringManagerMinMaxCluster;
import Algorithme.ClusteringManagerTotalDistances;
import DB.Equipe.Equipe;
import DB.Equipe.EquipeDAO;
import DB.Ville.Ville;
import DB.Ville.VilleDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class CalculerPoulesController implements Initializable {

    EquipeDAO equipeDAO = new EquipeDAO();

    @FXML
    private TextField ageInput;

    @FXML
    private TextField departementInput;

    @FXML
    private Button launchGluton;

    @FXML
    private Button launchModele;

    @FXML
    private TextField niveauInput;

    @FXML
    private TextField regionInput;

    @FXML
    private Button retour;

    @FXML
    private Button addTeam;

    @FXML
    private TextField sportInput;

    @FXML
    private Button searchWithFilter;

    @FXML
    private TableView<Equipe> equipesSelectedTable;

    @FXML
    private TableColumn<Equipe, String> ageColumn;


    @FXML
    private TableColumn<Equipe, String> niveauColumn;

    @FXML
    private TableColumn<Equipe, String> nomColumn;


    @FXML
    private TableColumn<Equipe, String> sportColumn;


    @FXML
    private TableColumn<Equipe, Integer> villeIdColumn;

    @FXML
    private TableColumn<Equipe, CheckBox> selectedColumn;

    @FXML
    private Label error;


    @FXML
            private Button selectAll;

    Service<Void> service = new Service<Void>()
    {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(1200);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            error.setVisible(false);
                        }
                    });
                    return null;
                }
            };
        }
    };

    @FXML
    void selectAllButtonClick(ActionEvent event) throws IOException {
        if (selectAll.getId()=="unselect") {
            for (int i = 0; i < equipesSelectedTable.getItems().size(); i++) {
                equipesSelectedTable.getItems().get(i).getSelected().setSelected(false);
            }
            selectAll.setText("Selectionner toutes les équipes");
            selectAll.setId("select");
        }
        else{
            for(int i = 0; i< equipesSelectedTable.getItems().size(); i++){
                equipesSelectedTable.getItems().get(i).getSelected().setSelected(true);
            }
            selectAll.setText("Déselectionner toutes les équipes");
            selectAll.setId("unselect");
        }
    }

    @FXML
    void onRetourButtonClick(ActionEvent event) throws IOException {
        Context.getApp().back();
    }

    @FXML
    void onCreerEquipeButtonClick(ActionEvent event) throws IOException {
        Context.getApp().showFxml("creer-equipe-view.fxml");
    }

    boolean getSelectedEquipesListe() throws IOException {
        // Récupération des équipes sélectionnées
        ArrayList<Equipe> equipeSelected = new ArrayList<Equipe>();
        for(int i = 0; i< equipesSelectedTable.getItems().size(); i++){
            if(equipesSelectedTable.getItems().get(i).getSelected().isSelected()){
                equipeSelected.add(equipesSelectedTable.getItems().get(i));
            }
        }
        // equipeSelected contient toutes les équipes sélectionnées depuis l'interface
        // distanceMatrix contient la matrice des distances
        Context.setEquipeSelected(equipeSelected);
        return equipeSelected.isEmpty();

    }

    @FXML
    void onLaunchModeleButtonClick(ActionEvent event) throws IOException {
        if (!(getSelectedEquipesListe())){
            Context.setMode("lineaire");
            Context.getApp().showFxml("resultats-poules-view.fxml");
        }
        else {
            error.setVisible(true);
            service.restart();
        }
    }

    @FXML
    void onlaunchGlutonButtonClick(ActionEvent event) throws IOException {
        if (!(getSelectedEquipesListe())){
            Context.setMode("glouton");
            Context.getApp().showFxml("resultats-poules-view.fxml");
        }
        else {
            error.setVisible(true);
            service.restart();
        }
    }



    @FXML
    void onSearchWithFilterButtonClick(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nomColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("nom"));
        sportColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("sport"));
        niveauColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("niveau"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("age"));
        villeIdColumn.setCellValueFactory(new PropertyValueFactory<Equipe, Integer>("ville"));
        selectedColumn.setCellValueFactory(new PropertyValueFactory<Equipe, CheckBox>("selected"));

        equipesSelectedTable.setItems(getEquipes());
    }

    private ObservableList<Equipe> getEquipes() {
        ArrayList<Equipe> equipeFromDB = new ArrayList<Equipe>(equipeDAO.getEquipes());
        ObservableList<Equipe> equipe = FXCollections.observableArrayList();
        for(int i = 0 ; i<equipeFromDB.size(); i++){
            CheckBox ch = new CheckBox("");
            equipe.add(new Equipe(equipeFromDB.get(i).getNom(), equipeFromDB.get(i).getSport(), equipeFromDB.get(i).getNiveau(), equipeFromDB.get(i).getAge(), parseInt(equipeFromDB.get(i).getVille()), equipeFromDB.get(i).getEquipeId(), ch));
        }
        return equipe;
    }
}
