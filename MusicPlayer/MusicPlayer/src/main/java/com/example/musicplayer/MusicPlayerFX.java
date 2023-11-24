package com.example.musicplayer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerFX extends Application {
    private Stage stage;
    private MediaPlayer mediaPlayer;
    private Label songNameLabel;
    private Label currentTimeLabel;
    private Label totalTimeLabel;
    private int currentIndex;
    private boolean isPaused;
    private List<File> songs;
    private ListView<String> songListView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Music Player");
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(214, 197, 231), CornerRadii.EMPTY,Insets.EMPTY)));
        root.setPadding(new Insets(20, 20, 20, 20));

        // Create controls for playback
        HBox controlsBox = createControlsBox();
        root.setTop(controlsBox);
        controlsBox.setStyle("-fx-padding: 10px;");


        // Create a ListView to display the list of songs
        songListView = new ListView<>();
       // songListView.setPrefWidth(30);
        //songListView.setPrefHeight(40);
        songListView.setStyle("-fx-background-color: rgb(255, 218, 185);");
        songListView.setFixedCellSize(40);

        root.setCenter(songListView);


        // Create labels for song information and time display
        Font font = Font.font("Arial", 18);
        songNameLabel = new Label("Now Playing:");
        songNameLabel.setPadding(new Insets(10, 10, 10, 10));

        songNameLabel.setFont(font);
        currentTimeLabel = new Label("0:00");
        currentTimeLabel.setFont(font);
        totalTimeLabel = new Label("0:00");
        totalTimeLabel.setFont(font);
        DropShadow shadow = new DropShadow();

        HBox songInfoBox = new HBox(5);
        songInfoBox.setAlignment(Pos.CENTER);


        songInfoBox.getChildren().addAll(songNameLabel, currentTimeLabel, new Label("/"), totalTimeLabel);

        root.setBottom(songInfoBox);

        // Initialize the song list
        songs = new ArrayList<>();

        // Create a file chooser for selecting songs
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));

        // Add event listeners for the buttons
        Button uploadButton = new Button("Upload");
//        uploadButton.setStyle("-fx-padding: 0 10 0 0;"); // Adjust the left padding (10 pixels in this example)

        uploadButton.setCursor(Cursor.HAND);
        InputStream imageStream = getClass().getResourceAsStream("/upload.png");

        ImageView view = new ImageView(new Image(imageStream));
        uploadButton.setGraphic(view);
        uploadButton.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));
        uploadButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Mouse is hovering over the button
                uploadButton.setStyle("-fx-background-color: rgb(150, 250, 150);");
            } else {
                // Mouse is not hovering over the button
                uploadButton.setStyle("-fx-background-color: rgb(144, 238, 144);");

            }
        });
        uploadButton.setPrefWidth(150);
        uploadButton.setPrefHeight(50);
        uploadButton.setEffect(null); // Remove any existing effects

        // Add the shadow effect when the button is hovered over


        uploadButton.setOnMouseEntered(e -> uploadButton.setEffect(shadow));

        // Remove the shadow effect when the mouse exits the button
        uploadButton.setOnMouseExited(e -> uploadButton.setEffect(null));
        uploadButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                songs.add(selectedFile);
                songListView.getItems().add(selectedFile.getName());
            }
        });

        Button playButton = new Button("Play",new ImageView(new Image("play.png")));
        playButton.setCursor(Cursor.HAND);
        playButton.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));
        playButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Mouse is hovering over the button
                playButton.setStyle("-fx-background-color: rgb(150, 250, 150);");
            } else {
                // Mouse is not hovering over the button
                playButton.setStyle("-fx-background-color: rgb(144, 238, 144);");

            }
        });
        playButton.setPrefWidth(150);
        playButton.setPrefHeight(50);
        playButton.setEffect(null); // Remove any existing effects

        // Add the shadow effect when the button is hovered over


        playButton.setOnMouseEntered(e -> playButton.setEffect(shadow));

        // Remove the shadow effect when the mouse exits the button
        playButton.setOnMouseExited(e -> playButton.setEffect(null));
        playButton.setOnAction(e -> playSelectedSong());

        Button pauseButton = new Button("Pause", new ImageView(new Image("pause.png")));
        pauseButton.setCursor(Cursor.HAND);
        pauseButton.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));
        pauseButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Mouse is hovering over the button
                pauseButton.setStyle("-fx-background-color: rgb(150, 250, 150);");
            } else {
                // Mouse is not hovering over the button
                pauseButton.setStyle("-fx-background-color: rgb(144, 238, 144);");

            }
        });
        pauseButton.setPrefWidth(150);
        pauseButton.setPrefHeight(50);
        pauseButton.setEffect(null); // Remove any existing effects

        // Add the shadow effect when the button is hovered over


        pauseButton.setOnMouseEntered(e -> pauseButton.setEffect(shadow));

        // Remove the shadow effect when the mouse exits the button
        pauseButton.setOnMouseExited(e -> pauseButton.setEffect(null));
        pauseButton.setOnAction(e -> pauseSong());

        Button nextButton = new Button("Next", new ImageView(new Image("next.png")));
        nextButton.setCursor(Cursor.HAND);
        nextButton.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));
        nextButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Mouse is hovering over the button
                nextButton.setStyle("-fx-background-color: rgb(150, 250, 150);");
            } else {
                // Mouse is not hovering over the button
                nextButton.setStyle("-fx-background-color: rgb(144, 238, 144);");

            }
        });
        nextButton.setPrefWidth(150);
        nextButton.setPrefHeight(50);
        nextButton.setEffect(null); // Remove any existing effects

        // Add the shadow effect when the button is hovered over


        nextButton.setOnMouseEntered(e -> nextButton.setEffect(shadow));

        // Remove the shadow effect when the mouse exits the button
        nextButton.setOnMouseExited(e -> nextButton.setEffect(null));
        nextButton.setOnAction(e -> playNextSong());

        Button prevButton = new Button("Previous", new ImageView(new Image("rewind-button.png")));
        prevButton.setCursor(Cursor.HAND);
        prevButton.setPadding(new javafx.geometry.Insets(10, 20, 10, 20));
        prevButton.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Mouse is hovering over the button
                prevButton.setStyle("-fx-background-color: rgb(150, 250, 150);");
            } else {
                // Mouse is not hovering over the button
                prevButton.setStyle("-fx-background-color: rgb(144, 238, 144);");

            }
        });
        prevButton.setPrefWidth(150);
        prevButton.setPrefHeight(50);
        prevButton.setEffect(null); // Remove any existing effects

        // Add the shadow effect when the button is hovered over
        //DropShadow shadow = new DropShadow();

        prevButton.setOnMouseEntered(e -> prevButton.setEffect(shadow));

        // Remove the shadow effect when the mouse exits the button
        prevButton.setOnMouseExited(e -> prevButton.setEffect(null));
        prevButton.setOnAction(e -> playPreviousSong());

        VBox buttonBox = new VBox(35, uploadButton, playButton, pauseButton, prevButton, nextButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);
        root.setRight(buttonBox);

        // Create a scene and set it to the stage
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);

        // Set up the list view selection handling
        songListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    currentIndex = songListView.getSelectionModel().getSelectedIndex();
                    if (currentIndex >= 0) {
                        playSelectedSong();
                    }
                }
        );

        // Initialize the UI state
        currentIndex = -1;
        isPaused = false;

        // Show the stage
        stage.show();
    }

    private HBox createControlsBox() {
        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-padding: 10px;");
         //Replace the values as needed

        exitButton.setOnAction(e -> Platform.exit());

        HBox controlsBox = new HBox(10, exitButton);
        controlsBox.setAlignment(Pos.CENTER_RIGHT);
        return controlsBox;
    }

    private void playSelectedSong() {
        if (currentIndex >= 0 && currentIndex < songs.size()) {
            File selectedSong = songs.get(currentIndex);
            String mediaSource = selectedSong.toURI().toString();

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer = null; // Set it to null when stopping playback
            }

            Media media = new Media(mediaSource);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnError(() -> {
                // Handle media loading error
                System.err.println("Error loading media: " + mediaSource);
                songNameLabel.setText("Error loading media");
                currentTimeLabel.setText("0:00");
                totalTimeLabel.setText("0:00");
            });

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
                updateLabels();
                songNameLabel.setText("Now Playing: " + selectedSong.getName());
                isPaused = false;
            });

            mediaPlayer.setOnEndOfMedia(this::playNextSong);
        }
    }


    private void playNextSong() {
        if (!songs.isEmpty()) {
            currentIndex = (currentIndex + 1) % songs.size();
            songListView.getSelectionModel().select(currentIndex);
            playSelectedSong();
        }
    }

    private void playPreviousSong() {
        if (!songs.isEmpty()) {
            currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
            songListView.getSelectionModel().select(currentIndex);
            playSelectedSong();
        }
    }

    private void pauseSong() {
        if (mediaPlayer.getStatus() == Status.PLAYING) {
            mediaPlayer.pause();
            isPaused = true;
        } else if (isPaused) {
            mediaPlayer.play();
            isPaused = false;
        }
    }

    private void updateLabels() {
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                int currentTimeSeconds = (int) newValue.toSeconds();
                int currentMinutes = currentTimeSeconds / 60;
                int currentSeconds = currentTimeSeconds % 60;
                currentTimeLabel.setText(String.format("%d:%02d", currentMinutes, currentSeconds));

                int totalDurationSeconds = (int) mediaPlayer.getTotalDuration().toSeconds();
                int totalMinutes = totalDurationSeconds / 60;
                int totalSeconds = totalDurationSeconds % 60;
                totalTimeLabel.setText(String.format("%d:%02d", totalMinutes, totalSeconds));
            }
        });
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }
}

