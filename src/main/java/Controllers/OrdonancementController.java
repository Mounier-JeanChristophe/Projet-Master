package Controllers;

import Algorithme.Ordonnancement.SchedulingResult;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class OrdonancementController {

    private SchedulingResult resultats;
    
    @FXML
    Slider u, l, ucons, lcons;

    @FXML
    TextField nbEquipe;

    @FXML
    public void initialize(){
        u = new Slider();
        l = new Slider();
        ucons = new Slider();
        lcons = new Slider();
    }


    @FXML //fonction recuperant les param donnes
    public void onButtonCLicked(){
        int param_nbEquipe = Integer.parseInt(nbEquipe.getText());
        int param_u = (int) u.getValue();
        int param_l = (int) l.getValue();
        int param_ucons = (int) ucons.getValue();
        int param_lcons = (int) lcons.getValue();

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


    }
    
}
