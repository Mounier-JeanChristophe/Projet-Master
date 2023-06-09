package Controllers;

import DB.Equipe.Equipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe statique permettant le stockage d information.
 */
public class Context {

    /**
     * Permet d avoir une reference a l objet application n import ou dans l application.
     */
    private static OptimisationCompetitionsSportivesApplication app=null;

    /**
     * Historique des pages affichees a l utilisateur. Utilise pour le bouton retour.
     */
    private static ArrayList<String> fxml=new ArrayList<>();

    /**
     * Dictionnaire contenant les parametres permettant la resolution du probleme de clustering des poules
     */
    private static Map<String, String> resolutionParameters = new HashMap<String, String>();

    private static ArrayList<Equipe> equipeSelected=new ArrayList<>();

    public static ArrayList<Equipe> getEquipeSelected() {
        return equipeSelected;
    }

    public static void setEquipeSelected(ArrayList<Equipe> equipeSelected) {
        Context.equipeSelected = equipeSelected;
    }

    public static String getMode() {
        return resolutionParameters.get("mode");
    }

    public static void setMode(String smode) {
        resolutionParameters.put("mode",smode);
    }

    public static Map<String, String> getResolutionParameters() {
        return resolutionParameters;
    }

    public static void addToResolutionParameters(String paramName,String value) {
        resolutionParameters.put(paramName,value);
    }

    public static OptimisationCompetitionsSportivesApplication getApp() {
        return app;
    }

    public static void setApp(OptimisationCompetitionsSportivesApplication app) {
        Context.app = app;
    }

    /**
     * Enleve le fxml de la page actuel. Enleve et renvoie le fxml de la page precedente
     * @return
     */
    public static String getLastFXML() {
        if (fxml.size()>=2) {
            fxml.remove(fxml.size() - 1);
            return fxml.remove(fxml.size() - 1);
        }
        else{
            return "acceuil-view.fxml";
        }
    }

    public static void addFXML(String sfxml) {
        fxml.add(sfxml);
    }


}
