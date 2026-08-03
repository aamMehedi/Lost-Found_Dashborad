module com.lostandfound {
    requires javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    requires java.sql;

    opens com.lostandfound to javafx.fxml;
    exports com.lostandfound;
}