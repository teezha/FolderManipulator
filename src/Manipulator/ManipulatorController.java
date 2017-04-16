package Manipulator;

/**=====================================================================================================================
 * FolderManipulator.java
 * Made By: Toby Zhang
 * This is a simple GUI program that allows me to pack and unpack files into multiple directories based on their name.
 * The second unpack function works for any folders.
 *
 * Limitations: Cannot overwrite files with the same name. To pack files, file names require dividing symbols to split
 * correctly.
 *
 */

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ManipulatorController extends Application {



    //UI hooks
    @FXML
    Pane pane;
    @FXML
    TextField warnings;
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
    @FXML
    TextField fCounter;
    @FXML
    TextField regexID;

    //load the folder method
    public void onLoad() {

        try {
            //sets the drive
            String driveLetter = drive.getText() + ":\\";
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose Folder");
            directoryChooser.setInitialDirectory(new File(driveLetter));
            File selectedDirectory = directoryChooser.showDialog(new Stage());

            //saves path to the textfield
            //allows bypass if users wish to type out
            selPath.setText(selectedDirectory.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            warnings.setText("Enter a drive letter first!");
        }

    }

    //takes in the user input and returns it as a string
    private String userRegex() {
        String regExp = "(.*)"+regexID.getText()+"(.*)";
        if (!regexChecker()) {
            regExp = ".*";
        }
        warnings.setText(regExp);
        return regExp;
    }

    private boolean regexChecker() {
        String regExp = regexID.getText();
        try {
            Pattern.compile(regExp);
        } catch (PatternSyntaxException e) {
            System.err.println(e.getDescription());
            return false;
        }
        return true;
    }

    //This function creates and moves files into folders
    public void createFolder() {
        //Wipe out results from previous runs
        results.setText("");
        //create new File with the path and stores the symbol divider
        File folder = new File(selPath.getText());
        File[] listOfFiles = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(userRegex());
            }
        });
        String div = divider.getText();


        //for loop for each file in the list
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()&&(!div.equals(""))) {

                //splits file name with a divider token Only want the name before the token so we call for 0.
                //This is the default way of my shows are sorted.
                // also adds a divider token in the string in case there is no token in filename (unlikely due to regex now)
                String fileName = listOfFiles[i].getName() + div;
                String[] names = fileName.split(div);
                //System.out.print(names[0]+"\n");
                Path folderPath = Paths.get(folder +"\\"+ names[0].trim() + "\\");
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
                    results.appendText("Move successful\n");
                } else {
                    results.appendText("Failed\n");
                }
            } else {
                results.setText("Divider was empty.\nOperation cancelled!");
            }
        }

    }

    public void dirUpOneFolder() {
        //wipes results output
        results.setText("");

        //stores file names as a list
        ArrayList<File> fileNames = new ArrayList<>();
        listAll(selPath.getText(), fileNames);
        warnings.setText(String.valueOf(fileNames.size()));

            //loops recursively through the directories to split the paths into an array
            for (int i = 0; i < fileNames.size(); i++) {
                tmpOut.setText("");
                //gets the filename from the list
                String fileStr = String.valueOf(fileNames.get(i));
                //appends the result to output and sets the file to point to the file listed in the list
                results.appendText(fileStr + "\n");
                File fileOrig = fileNames.get(i);
                //splits the paths into variables as elements through system
                String pattern = Pattern.quote(System.getProperty("file.separator"));
                String[] fileParts = fileStr.split(pattern);
                //over write the paths, then moves all files up 1 directory
                fileParts[fileParts.length - 2] = fileParts[fileParts.length - 1];
                fileParts[fileParts.length - 1] = null;
                //rewrites the paths in a field
                for (int j = 0; j < fileParts.length - 2; j++) {
                    tmpOut.appendText(fileParts[j]);
                    tmpOut.appendText("\\");
                }
                //the actual movement of the files
                //this actually uses the text field to hold the new file names as it moves through
                tmpOut.appendText(fileParts[fileParts.length - 2]);
                String newFile = tmpOut.getText();
                fileNames.set(i, new File(newFile));
                fileOrig.renameTo(fileNames.get(i));
            }
        }

    //This method stores all the files paths recursively into the list
    private void listAll(String dirName, ArrayList<File> files) {
        results.setText("");
        warnings.setText("Listing Files...\n");
        //sets variables
        File folder = new File(dirName);
        File[] listOfFiles = folder.listFiles();

        //loops through directory to get all files
        for (File file : listOfFiles) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listAll(file.getAbsolutePath(), files);
            }
        }
        //sets the number of items plus directories (amount found)
        fCounter.setText(String.valueOf(files.size()));
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
