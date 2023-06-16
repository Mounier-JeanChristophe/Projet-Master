package Controllers;

import Algorithme.Ordonnancement.DataGenerator;
import Algorithme.Ordonnancement.Scheduling;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdonancementController {

    private Map<String,String> equipes;
    private Map<Integer, String> indexEquipes;

    @FXML
    Slider u, l, ucons, lcons;

    @FXML
    TextField nbEquipe;

    @FXML
    TextField nomEquipe;

    @FXML
    TextField adresseEquipe;

    @FXML
    public void initialize(){
        u = new Slider();
        l = new Slider();
        ucons = new Slider();
        lcons = new Slider();
        equipes = new HashMap<>();
        indexEquipes = new HashMap<>();
    }

    @FXML
    void onRetourButtonClick(ActionEvent event) throws IOException {
        Context.getApp().back();
    }
    @FXML //fonction recuperant les param donnes
    public void onButtonCLicked(){
        int param_nbEquipe = Integer.parseInt(nbEquipe.getText());
        int param_u = (int) u.getValue();
        int param_l = (int) l.getValue();
        int param_ucons = (int) ucons.getValue();
        int param_lcons = (int) lcons.getValue();

        int i = 1;
        for(String nom : equipes.keySet()){
            indexEquipes.put(i, nom);
            i++;
        }

        ArrayList<String> adresseList = new ArrayList<>(equipes.values());
        DataGenerator dataGenerator = new DataGenerator(param_nbEquipe, param_l, param_u, param_lcons, param_ucons,adresseList);
        dataGenerator.generateIntputFile();
        Scheduling scheduling = new Scheduling(Scheduling.modele.MINZ);
        scheduling.getSchedulingResult();
        System.out.println(scheduling);
    }

    @FXML
    public void onNbEquipeChanged(){
        int nbe = Integer.parseInt(nbEquipe.getText());
        if(nbe%2 != 0){
            nbe++;
        }
        u.setMax(nbe-1);
        u.setMin(nbe/2);
        l.setMin(0);
        l.setMax(nbe/2-1);

        ucons.setMax(u.getValue());
        ucons.setMin(nbe/2);
        lcons.setMax(nbe/2-1);
        lcons.setMin(Math.min(1,l.getValue()));
    }

    @FXML
    public void onAddAdresseClick(){
        equipes.put(nomEquipe.getText(),adresseEquipe.getText());
    }

    public Map<Integer, String> getIndexEquipes(){
        return this.indexEquipes;
    }

}
