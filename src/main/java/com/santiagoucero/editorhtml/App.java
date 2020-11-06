package com.santiagoucero.editorhtml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.image.Image;

/**
 * @author Pablo Martínez y Santiago Ucero. Cesur 2ªDAM, Diseño de Interfaces
 */
public class App extends Application {
    
    private static Scene scene;
    private static Stage stageAux; // Copia auxiliar de Stage, para uso en métodos menores

    @Override
    public void start(Stage stage) throws IOException {
        stageAux = stage;
        scene = new Scene(loadFXML("primaryWindow"), 1200, 720);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("HTML Editor - New File");
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icon.png")));
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
    
    public void setStageTitle(String newTitle){
        stageAux.setTitle(newTitle);
    }
    
    public boolean fullscreen(){
        stageAux.setFullScreen(!stageAux.isFullScreen());
        return stageAux.isFullScreen();
    }
}
