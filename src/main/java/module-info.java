module com.bleckwolf.tmapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    
    opens com.bleckwolf.tmapp.app.controllers to javafx.fxml;
    opens com.bleckwolf.tmapp.app to javafx.fxml;
    
    exports com.bleckwolf.tmapp.app;
    exports com.bleckwolf.tmapp.model;
    exports com.bleckwolf.tmapp.service;
}