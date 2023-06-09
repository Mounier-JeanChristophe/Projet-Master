package Controllers;

import API.CitiesDistanceAPI;
import Algorithme.Clustering;
import Algorithme.ClusteringManagerMinMaxCluster;
import Algorithme.ClusteringManagerTotalDistances;
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
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import static java.lang.Math.round;

public class ResultatsPoulesController implements Initializable {

    @FXML
    private Button exportCsvButton;

    @FXML
    private Button visualizePoolsButton;

    @FXML
    private Button retour;

    @FXML
    private TableView<Equipe> equipesSelectedTable;

    @FXML
    private TableColumn<Equipe, String> ageColumn;

    @FXML
    private TableColumn<Equipe, String> pouleColumn;

    @FXML
    private TableColumn<Equipe, String> codePostalColumn;

    @FXML
    private TableColumn<Equipe, String> niveauColumn;

    @FXML
    private TableColumn<Equipe, String> nomColumn;


    @FXML
    private TableColumn<Equipe, String> sportColumn;

    @FXML
    private Button launchButton;

    ArrayList<ArrayList<Integer>> pools=new ArrayList<>();
    ArrayList<Algorithme.Equipe> equipeClusteringListe = new ArrayList<Algorithme.Equipe>();
    ArrayList<Equipe> selectedEquipeListe=new ArrayList<>();
    ObservableList<Equipe> equipes;
    ParametersController popUp;

    ArrayList<ArrayList<Double>> getDistanceMatrix(ArrayList<DB.Equipe.Equipe> equipeSelected) throws IOException {
        ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<>();
        for(int i = 0; i < equipeSelected.size(); i++){
            ArrayList<Double> distanceRow = new ArrayList<>();
            VilleDAO villeDAO = new VilleDAO();
            Ville v = villeDAO.getVilleByCodePostal(equipeSelected.get(i).getCodePostal());
            String coordinates = v.getLongitude()+","+v.getLatitude();
            for(int j = 0; j< equipeSelected.size(); j++){
                VilleDAO villeDAO2 = new VilleDAO();
                Ville v2 = villeDAO2.getVilleByCodePostal(equipeSelected.get(j).getCodePostal());
                String coordinates2 = v2.getLongitude()+","+v2.getLatitude();

                CitiesDistanceAPI api = new CitiesDistanceAPI(coordinates, coordinates2);
                distanceRow.add(api.getDistanceBetweenTwoCities());
            }
            distanceMatrix.add(distanceRow);
        }
        //System.out.println(distanceMatrix);
        printDistance(distanceMatrix,"distance matrix");
        return distanceMatrix;
    }

    Service<Void> service = new Service<Void>()
    {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    equipeClusteringListe.clear();
                    pools.clear();
                    int clusterSize = Integer.parseInt(Context.getResolutionParameters().get("clusterSize")); //nombre d'équipes dans une poule


                    //for testing on the file, uncomment this:
//                    File file = new File(System.getProperty("user.dir") + "/src/main/java/Algorithme/Donnees/Donnees_1.txt");
//                    Scanner obj = null;
//                    try {
//                        obj = new Scanner(file);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    while (obj.hasNextLine()) {
//                        String[] coordinates = obj.nextLine().split(" ");
//                        Algorithme.Equipe equipe = new Algorithme.Equipe(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])); //coordonnées x et y
//                        equipeClusteringListe.add(equipe);
//                    }
//                    Clustering clustering = new Clustering(equipeClusteringListe, clusterSize, new ClusteringManagerTotalDistances(), "optimizedFurthestTeams"); //Initialisation du clustering
//                    ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<>(clustering.getDistanceMatrix());
                    // end testing


                    // comment when testing one file
                    ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<>(getDistanceMatrix(Context.getEquipeSelected()));
                    for (int i=0;i<Context.getEquipeSelected().size();i++){
                        Algorithme.Equipe equipe = new Algorithme.Equipe(Context.getEquipeSelected().get(i).getVilleOfEquipe().getLatitudeDouble(), Context.getEquipeSelected().get(i).getVilleOfEquipe().getLongitudeDouble()); //coordonnées x et y
                        equipeClusteringListe.add(equipe);
                    }
                    Clustering clustering = new Clustering(equipeClusteringListe, distanceMatrix, clusterSize, new ClusteringManagerTotalDistances(), "optimizedFurthestTeams"); //Initialisation du clustering
                    // end comment


                    //Si distances
                    if (distanceMatrix != null && !(distanceMatrix.isEmpty())) {
                        if (Context.getMode() == "lineaire") {
                            int tl;
                            if (Context.getResolutionParameters().get("timeLimitActivated") == "false") {
                                tl = 0;
                            } else {
                                tl = Integer.parseInt(Context.getResolutionParameters().get("timeLimit"));
                            }
                            LinearModel linearModel = new LinearModel(tl);
                            linearModel.launchModel(clustering.getDistanceMatrix(), clusterSize);
                            System.out.println("Coût : " + linearModel.getCost()); //Affichage de la distance totale parcourue
                            System.out.println("Poules : " + linearModel.getPools());
                            pools = linearModel.getPools();
                        } else if (Context.getMode() == "glouton"){
                            clustering.resolutionGloutonne(); //Resolution du clustering avec algorithme glouton
                            if (Context.getResolutionParameters().get("improveWorstCluster").equals("true"))
                            {
                                clustering.setClusteringManager(new ClusteringManagerMinMaxCluster(Integer.valueOf(Context.getResolutionParameters().get("worseningPercentage"))));
                                clustering.resolutionGloutonne();
                            }
                            double bestClusteringCost = clustering.getCost();
                            double newCost;
                            ArrayList<ArrayList<Integer>> bestPools = savePools(clustering.getPools());
                            int numberTests = Integer.parseInt(Context.getResolutionParameters().get("numberTests"));
                            if (numberTests >= 2)
                            {
                                clustering = new Clustering(equipeClusteringListe, distanceMatrix, clusterSize, new ClusteringManagerTotalDistances(), "optimizedRandom"); //Initialisation du clustering
                                clustering.resolutionGloutonne();
                                if (Context.getResolutionParameters().get("improveWorstCluster").equals("true"))
                                {
                                    clustering.setClusteringManager(new ClusteringManagerMinMaxCluster(Integer.valueOf(Context.getResolutionParameters().get("worseningPercentage"))));
                                    clustering.resolutionGloutonne();
                                }
                                newCost = clustering.getCost();
                                if (newCost < bestClusteringCost)
                                {
                                    bestPools = savePools(clustering.getPools());
                                    bestClusteringCost = newCost;
                                }

                                for (int i = 3; i <= numberTests; i++)
                                {
                                    System.out.println("itération : " + i);
                                    clustering = new Clustering(equipeClusteringListe, distanceMatrix, clusterSize, new ClusteringManagerTotalDistances(), "fullRandom"); //Initialisation du clustering
                                    clustering.resolutionGloutonne();
                                    if (Context.getResolutionParameters().get("improveWorstCluster").equals("true"))
                                    {
                                        //Enlever les commentaires pour voir les différentes valeurs des clusterings, avant et après résolution gloutonne, le total de distance parcourue dans le clustering ou la distance parcourue par la poule la moins bien lotie
                                        //System.out.println("Before total distance cost : " + clustering.getCost());
                                        clustering.setClusteringManager(new ClusteringManagerMinMaxCluster(Integer.valueOf(Context.getResolutionParameters().get("worseningPercentage"))));
                                        //System.out.println("Before worst pool cost : " + clustering.getCost());
                                        clustering.resolutionGloutonne();
                                        /*System.out.println("After worst pool cost : " + clustering.getCost());
                                        clustering.setClusteringManager(new ClusteringManagerTotalDistances());
                                        System.out.println("After total distance cost : " + clustering.getCost());
                                        clustering.setClusteringManager(new ClusteringManagerMinMaxCluster(Integer.valueOf(Context.getResolutionParameters().get("worseningPercentage"))));*/
                                    }
                                    newCost = clustering.getCost();
                                    if (newCost < bestClusteringCost)
                                    {
                                        bestPools = savePools(clustering.getPools());
                                        bestClusteringCost = newCost;
                                    }
                                }
                            }

                            System.out.println("Coût final : " + bestClusteringCost); //Affichage de la distance totale parcourue
                            System.out.println("Poules finales : " + bestPools);
                            pools = bestPools;
                        }

                        for (int i=0;i<pools.size();i++){
                            for (int j=0;j<pools.get(i).size();j++){
                                if (pools.get(i).get(j)<selectedEquipeListe.size()){
                                    equipes.get(pools.get(i).get(j)).setPoule(String.valueOf(i));
                                }
                            }
                        }
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (popUp!=null) {
                                popUp.close();
                            }
                            visualizePoolsButton.setVisible(true);
                            exportCsvButton.setVisible(true);
                            launchButton.setText("Relancer la résolution");
                            equipesSelectedTable.setItems(equipes);
                            pouleColumn.setVisible(false);
                            pouleColumn.setVisible(true);
                        }
                    });
                    return null;
                }
            };
        }
    };

    private ArrayList<ArrayList<Integer>> savePools(ArrayList<ArrayList<Integer>> pools)
    {
        ArrayList<ArrayList<Integer>> savedPools = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> pool: pools)
        {
            savedPools.add(new ArrayList<>(pool));
        }
        return savedPools;
    }


    @FXML
    void onExportCsvButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer l'image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File choosenFile=fileChooser.showSaveDialog(Context.getApp().stage);
        if (choosenFile!=null) {
            //File file1 = file.showSaveDialog(stage);
            //System.out.println(file1);

            FileWriter file = null;

            final String HEADER = "Poule, Nom equipe, Code Postal";
            final String SEPARATOR = "\n";
            final String DELIMITER = ", ";
            try {
                file = new FileWriter(choosenFile);
                //Ajouter l'en-tête
                file.append("Sport:"+equipes.get(0).getSport());
                file.append("  Age:"+equipes.get(0).getAge());
                file.append("  Niveau:"+equipes.get(0).getNiveau());
                file.append(SEPARATOR);
                file.append(HEADER);
                //Ajouter une nouvelle ligne après l'en-tête
                file.append(SEPARATOR);
                //Itérer bookList
                Iterator it = equipes.iterator();
                while (it.hasNext()) {
                    Equipe e = (Equipe) it.next();
                    file.append(e.toCSV(DELIMITER));
                    file.append(SEPARATOR);
                }

                file.close();
                System.out.println("Export effectue");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onVisualizePoolsClick(ActionEvent event) throws IOException {
        final ScatterChart<Number, Number> sc = plotResult(equipeClusteringListe, pools);
        //Ouvrir une nouvelle fenêtre avec le graphique
        Stage stage = new Stage();
        Scene scene = new Scene(sc, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void onClickLaunchResolution(ActionEvent event) throws IOException {
        FXMLLoader loader = Context.getApp().showPopUp("resolution-parameters.fxml");
        ParametersController controller = loader.getController();
        popUp = controller;
        controller.setController(this);
        controller.init();
    }

    public void launchResolution()
    {
        service.cancel();
        service.restart();
    }

    private ScatterChart<Number,Number> plotResult(ArrayList<Algorithme.Equipe> equipeListe, ArrayList<ArrayList<Integer>> pools) {
        //Création du Chart
        Double minX=100.0;
        Double maxX=0.0;
        Double minY=100.0;
        Double maxY=0.0;
        for (int i=0; i<equipeListe.size();i++){
            if (minX>equipeListe.get(i).getX()){
                minX=equipeListe.get(i).getX();
            }
            if (minY>equipeListe.get(i).getY()){
                minY=equipeListe.get(i).getY();
            }
            if (maxX<equipeListe.get(i).getX()){
                maxX=equipeListe.get(i).getX();
            }
            if (maxY<equipeListe.get(i).getY()){
                maxY=equipeListe.get(i).getY();
            }
        }
        minX=minX-0.5;
        maxX=maxX+0.5;
        minY=minY-0.5;
        maxY=maxY+0.5;
        Double stepX=(maxX-minX)/10;
        Double stepY=(maxY-minY)/10;
        final NumberAxis xAxis = new NumberAxis(minX, maxX, stepX);
        final NumberAxis yAxis = new NumberAxis(minY, maxY, stepY);
        final ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        xAxis.setLabel("Localisation x");
        yAxis.setLabel("Localisation y");
        sc.setTitle("Clustering");
        int k = 1;
        for (ArrayList<Integer> pool : pools) {
            //Pour chaque poule on prépare son affichage
            XYChart.Series series = new XYChart.Series();
            series.setName("Poule " + k);
            for (Integer teamNumber : pool) {
                System.out.println(equipeListe.get(teamNumber));
                series.getData().add(new XYChart.Data(equipeListe.get(teamNumber).getX(), equipeListe.get(teamNumber).getY()));
            }

            System.out.println(series);
            sc.getData().add(series);
            k++;
        }
        return sc;
    }

    @FXML
    void onRetourButtonClick(ActionEvent event) throws IOException {
        Context.getApp().back();
    }


    private ObservableList<Equipe> getSelectedEquipes() {
        equipes = FXCollections.observableArrayList();
        for (int i=0;i<Context.getEquipeSelected().size();i++){
            DB.Equipe.Equipe db_equipe=Context.getEquipeSelected().get(i);
            equipes.add(new Equipe(db_equipe.getNom(),db_equipe.getCodePostal(), db_equipe.getNiveau(), db_equipe.getAge(),db_equipe.getSport()));
        }
        return equipes;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pouleColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("poule"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("nom"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("age"));
        codePostalColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("codePostal"));
        niveauColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("niveau"));
        sportColumn.setCellValueFactory(new PropertyValueFactory<Equipe, String>("sport"));

        equipesSelectedTable.setItems(getSelectedEquipes());
        selectedEquipeListe=new ArrayList<Equipe>(equipesSelectedTable.getItems());

    }

    public static void printDistance(ArrayList<ArrayList<Double>> list, String listName){
        System.out.println(listName);
        for(int i=0;i<list.size();i++){
            System.out.print(i+":"+Context.getEquipeSelected().get(i).getNom()+" | ");
            for(int j=0;j<list.get(i).size();j++){
                System.out.print(Math.round(list.get(i).get(j)));
                System.out.print("; ");
            }
            System.out.println();
        }
    }

}
