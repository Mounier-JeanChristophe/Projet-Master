package Controllers;

import DB.Equipe.Equipe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import DB.Equipe.EquipeDAO;

import static java.lang.Integer.parseInt;

public class GererEquipesController implements Initializable{

    EquipeDAO equipeDAO = new EquipeDAO();

    @FXML
    private Button creerEquipe;

    @FXML
    private Button retour;

    @FXML
    private TableView<Equipe> equipesTable;

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

    public GererEquipesController() {
    }

//    @FXML
//    private TableColumn<Equipe, String> villeColumn;



    @FXML
    void onCreerEquipeButtonClick(ActionEvent event) throws IOException {
        Context.getApp().showFxml("creer-equipe-view.fxml");
    }

    @FXML
    void onRetourButtonClick(ActionEvent event) throws IOException {
        Context.getApp().showHome();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nomColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("nom"));
        sportColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("sport"));
        niveauColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("niveau"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("age"));
        villeIdColumn.setCellValueFactory(new PropertyValueFactory<Equipe, Integer>("ville"));
        selectedColumn.setCellValueFactory(new PropertyValueFactory<Equipe, CheckBox>("selected"));
        equipesTable.setItems(getEquipes());
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

    @FXML
    void onDeleteEquipe(ActionEvent event) throws IOException {
        // Récupération des équipes sélectionnées
        ArrayList<Equipe> equipeSelected = new ArrayList<Equipe>();
        for(int i = 0; i< equipesTable.getItems().size(); i++){
            if(equipesTable.getItems().get(i).getSelected().isSelected()){
                equipeDAO.deleteEquipe(equipesTable.getItems().get(i));
            }
        }
        Context.getApp().showFxml("gerer-equipes-view.fxml");
    }
}
