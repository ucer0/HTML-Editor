package com.santiagoucero.editorhtml;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


/**
 * FXML Controller class
 *
 * @author Pablo Martinez y Santiago Ucero. Cesur 2ªDAM, Diseño de Interfaces
 */
public class WebViewerController implements Initializable {
    
    public WebEngine webEngine = new WebEngine();
    
    @FXML
    private WebView web;
    @FXML
    private VBox mainWindow;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        webEngine = web.getEngine();
        VBox.setVgrow(web, Priority.ALWAYS);
    }    
    
    public void setWeb(String web){
        File f = new File(web);
        webEngine.load(f.toURI().toString());
    }   
}
