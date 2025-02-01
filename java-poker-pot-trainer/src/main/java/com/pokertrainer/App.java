package com.pokertrainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {

  private Table table;
  private Pane chipGrid;
  private Label feedbackLabel;
  private TextField userInput;
  private int chipCount = 0;
  private long startTime;
  private List<Long> calculationTimes = new ArrayList<>();
  private int correctAnswers = 0;
  private int incorrectAnswers = 0;
  private HashMap<Double, Image> chipImages;
  private final Random random = new Random();
  private Stage statsStage;

  @Override
  public void start(Stage primaryStage) {
    table = new Table();
    loadChipImages();

    feedbackLabel = new Label();
    feedbackLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

    userInput = new TextField();
    userInput.setPromptText("enter your total (e.g., 2.35)");
    userInput.setOnAction(e -> checkUserInput());

    chipGrid = new Pane();
    chipGrid.setPrefSize(600, 400);
    chipGrid.setStyle("-fx-background-color: lightgray;");

    Button viewStatsButton = new Button("view stats");
    viewStatsButton.setOnAction(e -> displayStats());

    VBox root = new VBox(
      10,
      chipGrid,
      userInput,
      feedbackLabel,
      viewStatsButton
    );
    root.setPadding(new Insets(20));
    root.setStyle("-fx-alignment: center;");

    Scene scene = new Scene(root, 600, 800);

    scene.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case S -> {
          if (statsStage == null || !statsStage.isShowing()) {
            displayStats();
          } else {
            statsStage.close();
            generateRandomChips();
          }
        }
        default -> System.out.println("unhandled key: " + event.getCode());
      }
    });

    primaryStage.setTitle("poker pot trainer");
    primaryStage.setScene(scene);
    primaryStage.show();

    generateRandomChips();
  }

  private void loadChipImages() {
    chipImages = new HashMap<>();
    chipImages.put(
      0.05,
      new Image(getClass().getResourceAsStream("/images/chip_5.png"))
    );
    chipImages.put(
      0.25,
      new Image(getClass().getResourceAsStream("/images/chip_25.png"))
    );
    chipImages.put(
      1.00,
      new Image(getClass().getResourceAsStream("/images/chip_1.png"))
    );
  }

  private void generateRandomChips() {
    clearTable();

    int numChips = random.nextInt(12) + 4;
    double[] chipValues = { 0.05, 0.25, 1.00 };

    for (int i = 0; i < numChips; i++) {
      double value = chipValues[random.nextInt(chipValues.length)];
      Chip chip = new Chip(value, getColorForValue(value));
      table.addChip(chip);
      addChipToStack(chip);
    }

    startTime = System.currentTimeMillis();

    userInput.setEditable(true);
    userInput.clear();
    userInput.requestFocus();
  }

  private void checkUserInput() {
    if (!userInput.isEditable()) return;

    String input = userInput.getText().trim();
    try {
      double userTotal = Double.parseDouble(input);
      double actualTotal = table.calculatePot();

      long endTime = System.currentTimeMillis();
      long timeTaken = endTime - startTime;
      calculationTimes.add(timeTaken);

      if (Math.abs(userTotal - actualTotal) < 0.01) {
        feedbackLabel.setText(
          "✅ correct! time: " + (timeTaken / 1000.0) + " seconds"
        );
        feedbackLabel.setTextFill(Color.GREEN);
        correctAnswers++;

        userInput.setEditable(false);

        new Thread(() -> {
          try {
            Thread.sleep(1000);
            javafx.application.Platform.runLater(this::generateRandomChips);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        })
          .start();
      } else {
        feedbackLabel.setText("❌ incorrect. keep trying!");
        feedbackLabel.setTextFill(Color.RED);
      }
    } catch (NumberFormatException e) {
      feedbackLabel.setText("❌ invalid input. please enter a valid number.");
      feedbackLabel.setTextFill(Color.RED);
    }
  }

  private void displayStats() {
    if (statsStage != null && statsStage.isShowing()) {
      return;
    }

    statsStage = new Stage();
    statsStage.setTitle("performance stats");

    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("attempt number");
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("time (ms)");

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("calculation time");

    XYChart.Series<Number, Number> timeSeries = new XYChart.Series<>();
    timeSeries.setName("calculation time");
    for (int i = 0; i < calculationTimes.size(); i++) {
      timeSeries
        .getData()
        .add(new XYChart.Data<>(i + 1, calculationTimes.get(i)));
    }
    lineChart.getData().add(timeSeries);

    VBox statsLayout = new VBox(10, lineChart);
    statsLayout.setPadding(new Insets(20));

    Scene statsScene = new Scene(statsLayout, 800, 600);
    statsStage.setScene(statsScene);
    statsStage.show();

    statsScene.setOnKeyPressed(event -> {
      if (event.getCode() == javafx.scene.input.KeyCode.S) {
        statsStage.close();
        generateRandomChips();
      }
    });

    statsStage.setOnCloseRequest(event -> generateRandomChips());
  }

  private void clearTable() {
    table.clearTable();
    chipGrid.getChildren().clear();
    feedbackLabel.setText("");
    userInput.clear();
    chipCount = 0;
  }

  private String getColorForValue(double value) {
    return switch ((int) (value * 100)) {
      case 5 -> "yellow";
      case 25 -> "pink";
      case 100 -> "blue";
      default -> {
        System.out.println("warning: unknown chip value " + value);
        yield "white";
      }
    };
  }

  private void addChipToStack(Chip chip) {
    Image baseImage = chipImages.get(chip.getValue());
    Color chipColor =
      switch (chip.getColor()) {
        case "yellow" -> Color.YELLOW;
        case "pink" -> Color.PINK;
        case "blue" -> Color.BLUE;
        default -> Color.WHITE;
      };

    Image recoloredImage = recolorChip(baseImage, chipColor);
    ImageView chipImage = new ImageView(recoloredImage);

    chipImage.setFitWidth(50);
    chipImage.setFitHeight(50);

    int column = chipCount / 10;
    int positionInColumn = chipCount % 10;

    double xOffset = column * 60;
    double yOffset = positionInColumn * 25;

    chipImage.setTranslateX(xOffset);
    chipImage.setTranslateY(yOffset);

    chipGrid.getChildren().add(chipImage);
    chipImage.toFront();

    chipCount++;
  }

  private Image recolorChip(Image baseImage, Color newColor) {
    int width = (int) baseImage.getWidth();
    int height = (int) baseImage.getHeight();

    WritableImage writableImage = new WritableImage(width, height);
    PixelReader pixelReader = baseImage.getPixelReader();
    PixelWriter pixelWriter = writableImage.getPixelWriter();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Color originalColor = pixelReader.getColor(x, y);
        pixelWriter.setColor(
          x,
          y,
          isPreservedColor(originalColor)
            ? originalColor
            : newColor.deriveColor(
              0,
              1,
              originalColor.getBrightness(),
              originalColor.getOpacity()
            )
        );
      }
    }
    return writableImage;
  }

  private boolean isPreservedColor(Color color) {
    return (
      Math.abs(color.getRed() - color.getGreen()) < 0.1 &&
      Math.abs(color.getGreen() - color.getBlue()) < 0.1 ||
      color.getOpacity() < 0.1
    );
  }

  public static void main(String[] args) {
    launch(args);
  }
}
