package com.santiagoucero.editorhtml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Pablo Martínez y Santiago Ucero 
 */
public class PrimaryController {
    
    boolean isBeingSaved; // Auxiliar para abrirFileChooser
    String filePath = "New File"; // Usado para SaveFunction y el Título del programa
    
    private final App app = new App();
    public WebViewerController webViewer;
    
    @FXML
    private VBox mainWindow;
    @FXML
    private MenuItem newButton;
    @FXML
    private MenuItem openButton;
    @FXML
    private MenuItem saveButton;
    @FXML
    private MenuItem saveAsButton;
    @FXML
    private MenuItem exitButton;
    @FXML
    private HTMLEditor visualEditor;
    @FXML
    private TextArea sourceEditor;
    @FXML
    private TabPane editorTabPane;
    @FXML
    private MenuItem openBrowserButton;
    @FXML
    private Tab visualTab;
    @FXML
    private Tab sourceTab;
    @FXML
    private MenuItem fullscreenButton;
    @FXML
    private AnchorPane sourcePane;
    @FXML
    private AnchorPane visualPane;

    // Método para que el código fuente en HTML se autoajuste con los espacios 
    // necesarios para que quede bonito usando la librería JSoup
    public void parseHTML(File f1) throws IOException{
        
        // Convierte la File que tengamos abierta en el programa en un Document
        // de esta librería, que permite parsear el texto automáticamente
        Document doc = Jsoup.parse(f1, "UTF-8", "http://example.com/");
        String parsedText = doc.outerHtml();
        sourceEditor.setText(parsedText);
    }
    
    public File abrirFileChooser(){
        
        File f1;
        FileChooser fc = new FileChooser();
        
        // Empezará a buscar por la capeta Escritorio del usuario
        String homeDir = System.getProperty("user.home");
        fc.setInitialDirectory(new File(homeDir + "/Desktop"));
        
        // Filtra por archivos .html
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML Files", "*.htm", "*.html")
        );
        
        // Cambia el título y botones dependiendo de si se está abriendo un archivo o guardandolo
        if(isBeingSaved){
            fc.setTitle("Save As");
            f1 = fc.showSaveDialog(null);
        } else {
            fc.setTitle("Open File");
            f1 = fc.showOpenDialog(null);
        }
        
        return f1;
    }
    
    public void saveFunction(int i, File f1){
        
        String text;
        
        // Guarda los cambios hechos dependiendo de en que Tab se hayan hecho los mismos
        // 0 = Visual Editor, 1 = Source Code Editor
        if(i == 0){
            text = visualEditor.getHtmlText();
        } else {
            text = sourceEditor.getText();
            visualEditor.setHtmlText(text);
        }
        
        FileWriter fileWriter = null;
        
        try {
            
            // Sobreescribe el texto anterior con los cambios que se hayan hecho
            fileWriter = new FileWriter(f1);
            fileWriter.write(text);
            fileWriter.close();
            
            parseHTML(f1);
            
            // Si la ventana Mobile View está abierta, la actualiza al guardar
            if(webViewer!=null){
                webViewer.setWeb(filePath);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void newFile(ActionEvent event) throws IOException {
        
        filePath = "New File";
        
        visualEditor.setHtmlText("");
        sourceEditor.setText("");
        
        app.setStageTitle("HTML Editor - "+filePath);
    }

    @FXML
    private void openFile(ActionEvent event) throws IOException {
        
        isBeingSaved = false;

        // Pone el texto del archivo en textArea línea a línea hasta que completa el archivo
        File f1 = abrirFileChooser();
        BufferedReader br1 = new BufferedReader(new FileReader(f1.getPath(), StandardCharsets.UTF_8));
        String allText = "";
        String currentLine;

        while ((currentLine=br1.readLine()) != null) {
            allText = allText + (currentLine + "\n");
        }

        br1.close();
        
        visualEditor.setHtmlText(allText);
        
        parseHTML(f1);
        
        filePath = f1.getPath();
        app.setStageTitle("HTML Editor - "+filePath);

    }

    @FXML
    private void saveFile(ActionEvent event) throws IOException {
        
        File f1 = new File(filePath);
        
        if(f1.exists()){
            
            // GetSelectedIndex pilla el tab en el que se han producido los cambios
            saveFunction(editorTabPane.getSelectionModel().getSelectedIndex(), f1);
            app.setStageTitle("HTML Editor - "+filePath);
        } else {
            saveAsButton.fire();
        }
    }

    @FXML
    private void saveAsFile(ActionEvent event) throws IOException {
        
        isBeingSaved = true;

        File f1 = abrirFileChooser();
        
        // Si el nombre puesto al archivo no termina en .html o .htm, se lo pone
        if (!(f1.getName().endsWith(".html") | f1.getName().endsWith(".htm"))) {
            f1 = new File(f1.getParentFile(), f1.getName() + ".html");
        }
        
        filePath = f1.getPath();
        app.setStageTitle("HTML Editor - "+filePath);
        saveFunction(editorTabPane.getSelectionModel().getSelectedIndex(), f1);
    }

    @FXML
    private void exit(ActionEvent event) {
        System.exit(0);
    }
    
    // Guarda los cambios hechos al cambiar de una tab a otra
    // (No lo hace si el archivo actual no se ha guardado aún en el PC)
    @FXML
    private void closingTab(Event event) {
        
        File f1 = new File(filePath);
        if(f1.exists()){
            saveButton.fire();
        }
    }
    
    @FXML
    private void goFullscreen(ActionEvent event) {
        
        // Cambia el tamaño de los dos canvas dependiendo si está en pantalla completa o no
        if(app.fullscreen()){
            visualEditor.setPrefSize(1920, 1030);
            sourceEditor.setPrefSize(1920, 1030);
        } else {
            visualEditor.setPrefSize(1200, 666);
            sourceEditor.setPrefSize(1200, 666);
        }
    }
    
    @FXML
    private void openBrowser(ActionEvent event) throws IOException {

        File f1 = new File(filePath);
        
        if(f1.exists()){

            // Abre la Mobile View en una ventana nueva
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(App.class.getResource("webViewer.fxml"));
            Parent root = fxmlLoader.load();
            webViewer = fxmlLoader.getController();
            
            webViewer.setWeb(filePath); 
           
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.setMinHeight(555);
            stage.setMinWidth(270);
            stage.setTitle("Mobile View");
            stage.getIcons().add(new Image(App.class.getResourceAsStream("icon.png")));
            stage.show(); 
        } else {
            
            // Si no detecta ningún archivo abierto en vez de abrirse la página
            // sale una alarma pidiendo que abras un archivo
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("Ningún archivo detectado");
            alert.setContentText(
                    "Para mostrar la Mobile View debe tener un archivo abierto.\n"+
                    "Si está trabajando con un archivo nuevo, asegúrese de haberlo guardado."
            );

            alert.showAndWait();
        }
    }
}
