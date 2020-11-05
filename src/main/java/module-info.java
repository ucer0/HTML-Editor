module com.santiagoucero.editorhtml {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.jsoup;
    requires java.base;
    
    opens com.santiagoucero.editorhtml to javafx.fxml;
    exports com.santiagoucero.editorhtml;
}
