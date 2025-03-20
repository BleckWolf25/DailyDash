module com.bleckwolf.tmapp {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires java.sql;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    
    opens com.bleckwolf.tmapp.app.controllers to javafx.fxml;
    opens com.bleckwolf.tmapp.app to javafx.graphics, javafx.fxml;
    opens com.bleckwolf.tmapp.model to javafx.base;
    
    exports com.bleckwolf.tmapp.app;
    exports com.bleckwolf.tmapp.model;
    exports com.bleckwolf.tmapp.service;
}