module org.example.assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.assignment1 to javafx.fxml;
    exports org.example.assignment1;
}