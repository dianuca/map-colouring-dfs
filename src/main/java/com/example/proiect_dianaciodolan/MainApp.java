package com.example.proiect_dianaciodolan;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainApp extends Application {
    //pentru a detecta regiunile, construirea graficului si generarea imaginii colorate
    private final MapImageProcessor procesor = new MapImageProcessor();
    private final ImageView originalImageView = new ImageView(); //imaginea originala
    private final ImageView rezultatImageView = new ImageView(); //imaginea rezultata
    private final TextArea infoArea = new TextArea(); //zona cu detalii
    //variabila care imi pastreaza imaginea incarcata din  fisier
    private BufferedImage imagineIncarcata;

    @Override
    public void start(Stage stage) {
        Label title = new Label("Colorarea unei harti folosind DFS");
        title.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                -fx-text-fill: #1f2937;
                """);
        //definire buton
        Button incarcareButon = new Button("Incarca harta si coloreaza cu DFS");
        styleButton(incarcareButon); //stil pentru butoane
        //atribuire actiuni buton
        incarcareButon.setOnAction(e -> incarcareSiProcesare(stage));
        //cutie orizontala in care asez butoanele centrate spre stanga
        HBox controlere = new HBox(12, incarcareButon);
        controlere.setAlignment(Pos.CENTER_LEFT);
        //partea de sus a interfetei, pozitionez titlul si controlerele vertical
        VBox topBox = new VBox(10, title, controlere);
        topBox.setPadding(new Insets(15));
        //redimensionare poza si asezare in interfata
        originalImageView.setFitWidth(450);
        originalImageView.setFitHeight(450);
        originalImageView.setPreserveRatio(true);
        //poza rezultata
        rezultatImageView.setFitWidth(450);
        rezultatImageView.setFitHeight(450);
        rezultatImageView.setPreserveRatio(true);
        //creare box cu titlu+imagine
        VBox cutieStanga = new VBox(10, new Label("Imagine originala"), originalImageView);
        VBox cutieDreapta = new VBox(10, new Label("Imagine colorata"), rezultatImageView);
        //stilizare cutii
        cutieStanga.setPadding(new Insets(10));
        cutieDreapta.setPadding(new Insets(10));
        //pozitionare cutii si unirea loc intr o cutie orizontale
        HBox cutieImagini = new HBox(20, cutieStanga, cutieDreapta);
        cutieImagini.setPadding(new Insets(10));
        cutieImagini.setAlignment(Pos.CENTER);
        //setari sectiunea de detalii
        infoArea.setEditable(false);
        infoArea.setWrapText(true);
        infoArea.setPrefRowCount(10);
        infoArea.setStyle("""
                -fx-font-family: 'Consolas';
                -fx-font-size: 13px;
                """);
        //sectiunea de jos a interfetei
        VBox bottomBox = new VBox(10, new Label("Informatii"), infoArea);
        bottomBox.setPadding(new Insets(10));
        VBox.setVgrow(infoArea, Priority.ALWAYS);
        //asezarea in pagina
        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(cutieImagini);
        root.setBottom(bottomBox);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fafc, #e2e8f0);");
        Scene scene = new Scene(root, 1100, 800);
        stage.setTitle("Colorarea unei harti folosind DFS");
        stage.setScene(scene);
        stage.show();
    }
    //metoda folosita pt incarcare unei harti, detectarea zonelor
    private void incarcareSiProcesare(Stage stage) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Alege o harta");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg", "*.bmp")
            );
            File fisier = chooser.showOpenDialog(stage);
            if (fisier == null) {
                return;
            }
            imagineIncarcata = ImageIO.read(fisier);
            if (imagineIncarcata == null) {
                afisareEroare("Fisierul selectat nu este o imagine valida.");
                return;
            }
            procesor.intializare(imagineIncarcata); // initializez obiectul procesor cu imaginea originala
            originalImageView.setImage(FxImageUtils.toFxImage(imagineIncarcata)); // afisez imaginea originala
            procesor.procesareImagine(); // detecteaza regiunile + construieste vecinatatile + coloreaza regiunile
            if (procesor.getRegiuni().isEmpty()) {
                afisareEroare("Nu au fost detectate regiuni in imagine.");
                rezultatImageView.setImage(null);
                return;
            }
            BufferedImage imagineColorata = procesor.creareImagineColorata(); // creez si afisez imaginea colorata
            rezultatImageView.setImage(FxImageUtils.toFxImage(imagineColorata));
            File imagineDebug = salveazaImagine(fisier); // salvez automat o imagine PNG necolorata cu ID-urile regiunilor
            // afisez informatii
            infoArea.setText(
                    "Imagine incarcata: " + fisier.getName() + "\n"
                            + "Regiunile au fost detectate si colorate cu succes.\n"
                            + "Imaginea debug a fost salvata aici:\n"
                            + imagineDebug.getAbsolutePath() + "\n\n"
                            + procesor.buildRegionsInfo()
            );
        } catch (Exception ex) {
            afisareEroare("Eroare la procesarea hartii: " + ex.getMessage());
        }
    }

    //metoda folosita pentru a mi salva imaginea debug cu id ul regiunii si numarul de pixeli
    private File salveazaImagine(File fisierOriginal) throws IOException {
        BufferedImage imagineDebug = procesor.creareImagineDebug(); //creez imaginea
        String numeFisier = fisierOriginal.getName();
        int punct = numeFisier.lastIndexOf('.');
        String baza = punct > 0 ? numeFisier.substring(0, punct) : numeFisier;
        File output = new File(fisierOriginal.getParentFile(), baza + "_zone_id.png");
        ImageIO.write(imagineDebug, "png", output);
        return output;
    }

    private void styleButton(Button button) {
        button.setStyle("""
                -fx-background-color: linear-gradient(to right, #2563eb, #7c3aed);
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-radius: 12px;
                -fx-padding: 10 18 10 18;
                -fx-cursor: hand;
                """);
    }

    private void afisareEroare(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText("A apărut o problemă");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}