module org.example.oopca5jfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.json;


    exports org.example.oopca5jfx.DAOs;
    exports org.example.oopca5jfx.DTOs;
    exports org.example.oopca5jfx.Database;
    exports org.example.oopca5jfx.Utils;
    exports org.example.oopca5jfx.Networking;

    opens org.example.oopca5jfx.Networking to javafx.fxml;
    opens org.example.oopca5jfx.DAOs to javafx.fxml;

}