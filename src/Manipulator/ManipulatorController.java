package Manipulator;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ManipulatorController extends Application{


    @FXML
    Pane pane;
    @FXML
    Stage stage;
    @FXML
    TextField drive;
    @FXML
    TextField selPath;
    @FXML
    TextField divider;
    @FXML
    TextArea results;
    @FXML
    TextField tmpOut;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String driveLetter = drive.getText() + ":\\";
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Folder");
        directoryChooser.setInitialDirectory(new File(driveLetter));

    }

    //This function creates and moves files into folders
    public void creatFolder() {
        results.setText("");

        File folder = new File(selPath.getText());
        File[] listOfFiles = folder.listFiles();
        String div = divider.getText();


        //for loop for each file in the list
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {

                //splits file name with - Only want the name before - so we call for 0.
                //This is the deafult way of show my shows are sorted.
                // also adds a - in the string in case there is no - in file
                String fileName = listOfFiles[i].getName() + div;
                String[] names = fileName.split(div);
                //System.out.print(names[0]+"\n");
                Path folderPath = Paths.get(folder + names[0].trim() + "\\");
                results.appendText(folderPath + "\n");

                File directory = new File(String.valueOf(folderPath));

                //checks if directory exists. if does not exist, make new directory.
                if (!directory.exists()) {
                    //makes a directory if not exists
                    directory.mkdir();
                }
                //sets initial file directory
                File dirA = listOfFiles[i];
                //renames the file path of the file in the if loop
                //returns a user response if successful or not
                if (dirA.renameTo(new File(String.valueOf(folderPath) + "\\" + dirA.getName()))) {
                    System.out.print("Move success");
                } else {
                    System.out.print("Failed");
                }
            }
        }

    }

    public void deleteFolder() {
        results.setText("");
        tmpOut.setText("");

        ArrayList<File> fileNames = new ArrayList<>();
        listAll(selPath.getText(),fileNames);

        for (int i =0; i < fileNames.size(); i++) {
            String fileStr = String.valueOf(fileNames.get(i));
            File fileOrig = fileNames.get(i);

            String[] fileParts = fileStr.split("/");
            fileParts[fileParts.length-2]=fileParts[fileParts.length-1];
            fileParts[fileParts.length-1] = null;

            for (int j=0; j < fileParts.length-2;j++) {
                tmpOut.appendText(fileParts[j]);
                tmpOut.appendText("//");
            }
            tmpOut.appendText(fileParts[fileParts.length-2]);
            String newFile = tmpOut.getText();
            fileNames.set(i,new File(newFile));

            fileOrig.renameTo(fileNames.get(i));

        }



    }

    public void listAll(String dirName, ArrayList<File> files) {
        results.setText("");

        File folder = new File(dirName);
        File[] listOfFiles = folder.listFiles();

        for (File file: listOfFiles) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listAll(file.getAbsolutePath(),files);
            }
        }
    }


}
