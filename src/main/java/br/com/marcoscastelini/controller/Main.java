package br.com.marcoscastelini.controller;

import br.com.marcoscastelini.util.Configuracao;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;

    private Stage primaryStage;

    private StackPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        Configuracao.load();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Principal.fxml"));
        rootLayout = (StackPane) loader.load();

        primaryStage.setTitle("Impressão de Etiquetas");
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/imagens/icone.png"))));
        primaryStage.show();

//        primaryStage.setOnCloseRequest();

    }


    public void exit() {
        Platform.exit();
        System.exit(0);
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

}
