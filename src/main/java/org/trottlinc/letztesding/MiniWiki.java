package org.trottlinc.letztesding;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MiniWiki extends Application {

    private final BorderPane bPane = new BorderPane();

    private final GridPane gPane = new GridPane();
    private final TextArea input = new TextArea();
    private final WebView htmlView = new WebView();

    private final HBox hbox = new HBox();
    private final Button compile = new Button("compile to HTML");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Mini Wiki");
        stage.setWidth(800);
        stage.setHeight(600);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        gPane.getColumnConstraints().addAll(column1, column2);
        gPane.add(input, 0, 0);
        gPane.add(htmlView, 1, 0);
        bPane.setCenter(gPane);
        compile.setOnAction(_ -> {
            String inputText = input.getText();
            String html = WikiConverter.convertToHtml(inputText);
            htmlView.getEngine().loadContent(html);
        });
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(compile);
        bPane.setBottom(hbox);
        Scene scene = new Scene(bPane);
        stage.setScene(scene);
        stage.show();
    }
}
