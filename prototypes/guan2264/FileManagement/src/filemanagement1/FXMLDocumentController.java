/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filemanagement1;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;



/**
 *
 * @author Feng
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML private Label label;
    @FXML TextArea file_content;
    @FXML TreeView<String> directory_content;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        file_content.setText("Hello World!");

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String desktop = System.getProperty ("user.home");
        File currentDir = new File("C:\\Users\\Feng\\Documents\\uWebKit temp2");
        findFiles(currentDir,null, null);
    }    
    public void findFiles(File dir, TreeItem<File> parent, TreeItem<String> parentt){
        TreeItem<String> fileName = new TreeItem<>(dir.getName());
        if(parentt == null){ fileName.setExpanded(true);}
        TreeItem<File> root = new TreeItem<>(dir);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
//                System.out.println("directory:" + file.getCanonicalPath());
                findFiles(file,root, fileName);
            } 
            if(parent==null){ directory_content.setRoot(fileName);}
        }
        for (File file : files) {
            if(file.isFile()){
 //           System.out.println("     file:" + file.getCanonicalPath());
                root.getChildren().add(new TreeItem<>(file));
                fileName.getChildren().add(new TreeItem<>(file.getName()));
            }
        }
        if(parentt!=null){ parentt.getChildren().add(fileName);}
    }
}
