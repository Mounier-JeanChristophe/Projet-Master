package Graphe;


import org.graphstream.graph.Graph;
import org.graphstream.stream.file.FileSinkDGS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileManager {

    public static boolean isProjectExist(String projectName) {
        File f = new File("project/"+projectName);
        return f.exists();
    }

    public static boolean isDataFileExist(String filename){
        File f = new File("data/"+filename);
        return f.exists();
    }

    public static void saveData(GraphePoids graphePoids, Graph graph, String projectName) {

        try {
            //creation du projet s'il n'existe pas deja
            if(!isProjectExist(projectName)){
                Path path = Paths.get("project/"+projectName);
                Files.createDirectories(path);
            }
            ObjectOutputStream oos;

            //serialisation du graphe
            FileSinkDGS fs = new FileSinkDGS();
            fs.writeAll(graph, "project/"+projectName+"/graph.dgs");

            //serialisation du graphePoids
            FileOutputStream file = new FileOutputStream("project/"+projectName+"/graphePoids.ser");
            oos = new ObjectOutputStream(file);
            oos.writeObject(graphePoids);
            oos.flush();
            oos.close();
            file.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteProject(String projectName){
        //si le projet n'existe pas
        if(!isProjectExist(projectName)){
            System.out.println("Projet introuvanble");
            return;
        }

        //sinon on supprime les fichiers du projet avant de supprimer le repertoire
        String[] fileList;
        File f = new File("project/"+projectName);
        fileList = f.list();

        if(fileList != null) {
            int i =1;
            for (String filename : fileList) {
                filename = "project/"+projectName+"/"+filename;
                File file = new File(filename);
                if(file.delete()){
                    System.out.println("SUPPRESSION "+i+"/2");
                    i++;
                }
                else{
                    System.out.println("ERREUR SUPPRESSION");
                }
            }
        }
        if(f.delete()){
            System.out.println("SUPPRESSION OK");
        }
    }


}
