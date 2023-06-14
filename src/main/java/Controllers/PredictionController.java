package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

public class PredictionController {
    @FXML
    private TextField am1Field;

    @FXML
    private TextField am2Field;

    @FXML
    private TextField am3Field;

    @FXML
    private TextField am4Field;

    @FXML
    private TextField am5Field;

    @FXML
    private TextField atLossStreak3Field;

    @FXML
    private TextField atLossStreak5Field;

    @FXML
    private TextField atWinStreak3Field;

    @FXML
    private TextField atWinStreak5Field;

    @FXML
    private TextField atgcField;

    @FXML
    private TextField atgsField;

    @FXML
    private TextField atpField;

    @FXML
    private TextField awayTeamField;

    @FXML
    private TextField hm1Field;

    @FXML
    private TextField hm2Field;

    @FXML
    private TextField hm3Field;

    @FXML
    private TextField hm4Field;

    @FXML
    private TextField hm5Field;

    @FXML
    private TextField homeTeamField;

    @FXML
    private TextField htLossStreak3Field;

    @FXML
    private TextField htLossStreak5Field;

    @FXML
    private TextField htWinStreak3Field;

    @FXML
    private TextField htWinStreak5Field;

    @FXML
    private TextField htgcField;

    @FXML
    private TextField htgsField;

    @FXML
    private TextField htpField;

    @FXML
    private TextField monthField;

    @FXML
    private Button predictButton;

    @FXML
    private Label resultLabel;

    @FXML
    private TextField seasonField;

    @FXML
    private TextField weekField;

    @FXML
    private TextField yearField;

    @FXML
    void predict(ActionEvent event) {
        //String modelPath = "C:\\Users\\Niang\\Desktop\\M1IA\\projet semestre\\Notebook\\code";
        String modelPath = "src/main/java/Algorithme/Donnees/ModelIASaved";
        int season = Integer.parseInt(seasonField.getText());
        int homeTeam = Integer.parseInt(homeTeamField.getText());
        int awayTeam = Integer.parseInt(awayTeamField.getText());
        int week = Integer.parseInt(weekField.getText());
        int htgs = Integer.parseInt(htgsField.getText());
        int atgs = Integer.parseInt(atgsField.getText());
        int htgc = Integer.parseInt(htgcField.getText());
        int atgc = Integer.parseInt(atgcField.getText());
        int htp = Integer.parseInt(htpField.getText());
        int atp = Integer.parseInt(atpField.getText());
        int am1 = Integer.parseInt(am1Field.getText());
        int am2 = Integer.parseInt(am2Field.getText());
        int hm1 = Integer.parseInt(hm1Field.getText());
        int hm2 = Integer.parseInt(hm2Field.getText());
        int hm3 = Integer.parseInt(hm3Field.getText());
        int am3 = Integer.parseInt(am3Field.getText());
        int am4 = Integer.parseInt(am4Field.getText());
        int hm4 = Integer.parseInt(hm4Field.getText());
        int hm5 = Integer.parseInt(hm5Field.getText());
        int am5 = Integer.parseInt(am5Field.getText());
        int htWinStreak3 = Integer.parseInt(htWinStreak3Field.getText());
        int htWinStreak5 = Integer.parseInt(htWinStreak5Field.getText());
        int htLossStreak3 = Integer.parseInt(htLossStreak3Field.getText());
        int htLossStreak5 = Integer.parseInt(htLossStreak5Field.getText());
        int atWinStreak3 = Integer.parseInt(atWinStreak3Field.getText());
        int atWinStreak5 = Integer.parseInt(atWinStreak5Field.getText());
        int atLossStreak3 = Integer.parseInt(atLossStreak3Field.getText());
        int atLossStreak5 = Integer.parseInt(atLossStreak5Field.getText());
        int year = Integer.parseInt(yearField.getText());
        int month = Integer.parseInt(monthField.getText());

        try (SavedModelBundle model = SavedModelBundle.load(modelPath, "serve")){
            float[][] input = {
                    {season, homeTeam, awayTeam, week, htgs, atgs, htgc, atgc, htp, atp, am1, am2, hm1, hm2, hm3, am3, am4, hm4, hm5, am5,
                            htWinStreak3, htWinStreak5, htLossStreak3, htLossStreak5, atWinStreak3, atWinStreak5, atLossStreak3, atLossStreak5,
                            year, month}
            };
            // Création d'un tenseur avec les données d'entrée
            Tensor<Float> inputTensor = Tensor.create(input, Float.class);

            try (Session sess = model.session()) {
                // Obtention des prédictions
                Tensor<?> result = sess.runner()
                        .feed("Season", inputTensor)  // Remplacez "Season" par le nom correct de l'input "Season" dans votre modèle
                        .feed("HomeTeam", inputTensor)  // Remplacez "HomeTeam" par le nom correct de l'input "HomeTeam" dans votre modèle
                        .feed("AwayTeam", inputTensor)  // Remplacez "AwayTeam" par le nom correct de l'input "AwayTeam" dans votre modèle

                        .feed("week", inputTensor)
                        .feed("HTGS", inputTensor)
                        .feed("ATGS", inputTensor)
                        .feed("HTGC", inputTensor)
                        .feed("ATGC", inputTensor)
                        .feed("HTP", inputTensor)
                        .feed("ATP", inputTensor)
                        .feed("AM1", inputTensor)
                        .feed("AM2", inputTensor)
                        .feed("HM1", inputTensor)
                        .feed("HM2", inputTensor)
                        .feed("HM3", inputTensor)
                        .feed("AM3", inputTensor)
                        .feed("AM4", inputTensor)
                        .feed("HM4", inputTensor)
                        .feed("HM5", inputTensor)
                        .feed("AM5", inputTensor)
                        .feed("HTWinStreak3", inputTensor)
                        .feed("HTWinStreak5", inputTensor)
                        .feed("HTLossStreak3", inputTensor)
                        .feed("HTLossStreak5", inputTensor)
                        .feed("ATWinStreak3", inputTensor)
                        .feed("ATWinStreak5", inputTensor)
                        .feed("ATLossStreak3", inputTensor)
                        .feed("ATLossStreak5", inputTensor)
                        .feed("year", inputTensor)
                        .feed("month", inputTensor)
                        .fetch("FTR")  // Remplacez "FTR" par le nom correct de la sortie "FTR" dans votre modèle
                        .run()
                        .get(0);

                // Traitement des résultats
                float[][] output = new float[1][2];  // Remplacez 2 avec le nombre correct de classes de votre modèle
                result.copyTo(output);
                float[] predictions = output[0];

                // Affichage des prédictions
                float homeWinProbability = predictions[0];
                float nonHomeWinProbability = predictions[1];

                // Utilisez les probabilités pour afficher ou prendre d'autres actions en fonction de votre application
                System.out.println("Probabilité de victoire de l'équipe à domicile : " + homeWinProbability);
                System.out.println("Probabilité de non-victoire de l'équipe à domicile : " + nonHomeWinProbability);
            }
        }

    }
}
