package controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StockAverageCalculatorController {

    @FXML private TableView<StockModel> tableView;
    @FXML private TableColumn<StockModel, Integer> numberOfSharesColumn;
    @FXML private TableColumn<StockModel, Double> costPerShareColumn;
    @FXML private TextField numberOfSharesTextField;
    @FXML private TextField costPerShareTextField;
    @FXML private TextField feesChargedPerTradeTextField;

    private final String DIGITS_ONLY_REGEX = "\\d*";
    private final String DECIMAL_NUMBER_ONLY_REGEX = "\\d*(\\.\\d*)?";
    private final String FIELD_DELIMITER = ",";

    @FXML
    public void initialize() {

        numberOfSharesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNumberOfSharesProperty()).asObject());
        costPerShareColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCostPerShareProperty()).asObject());

        numberOfSharesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(DIGITS_ONLY_REGEX)) {
                numberOfSharesTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        costPerShareTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(DECIMAL_NUMBER_ONLY_REGEX)) {
                costPerShareTextField.setText(newValue.replaceAll("[^\\d*(\\.\\d*)?]", ""));
            }
        });

        feesChargedPerTradeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(DECIMAL_NUMBER_ONLY_REGEX)) {
                feesChargedPerTradeTextField.setText(newValue.replaceAll("[^\\d*(\\.\\d*)?]", ""));
            }
        });
    }

    @FXML
    public void addRow() {
        if(!areTextFieldsEmpty()) {
            tableView.getItems().add(new StockModel(new SimpleIntegerProperty(Integer.parseInt(numberOfSharesTextField.getText())),
                    new SimpleDoubleProperty(Double.parseDouble(costPerShareTextField.getText()))));
            numberOfSharesTextField.clear();
            costPerShareTextField.clear();
        }
        else {
            String title = "Error";
            String message = "Cannot add a row with empty fields. Enter values in the fields.";
            int height = 100;
            int width = 400;
            displayAlertBox(title, message, height, width);
        }
    }

    @FXML
    public void removeRow() {
        if(tableView.getSelectionModel().getSelectedItem() != null) {
            tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
        }
        else {
            String title = "Error";
            String message = "There are no rows selected. Select a row to remove.";
            int height = 100;
            int width = 400;
            displayAlertBox(title, message, height, width);
        }
    }

    @FXML
    public void clear() {
        tableView.getItems().clear();
        numberOfSharesTextField.clear();
        costPerShareTextField.clear();
        feesChargedPerTradeTextField.clear();
    }

    @FXML
    public void calculate() {
        String title;
        String message;
        int height;
        int width;

        if(!tableView.getItems().isEmpty() && !feesChargedPerTradeTextField.getText().isEmpty()) {
            int totalNumberOfShares = 0;
            double totalBookCost = 0.0;

            for(StockModel model : tableView.getItems()) {
                totalNumberOfShares += model.getNumberOfShares();
                totalBookCost += model.getNumberOfShares() * model.getCostPerShare();
            }

            double finalAverage = (totalBookCost + (tableView.getItems().size() * Double.parseDouble(feesChargedPerTradeTextField.getText()))) / totalNumberOfShares;
            title = "Result";
            message = "Your average is $" + finalAverage;
            height = 100;
            width = 250;
        }
        else {
            title = "Error";
            message = "Error: Cannot calculate average without any elements in the table. " +
                    "\nMake sure that there are entries in the table, and that you enter a cost per trade.";
            height = 150;
            width = 500;
        }
        displayAlertBox(title, message, height, width);
    }

    @FXML
    private void saveFile() throws IOException {
        File file = showSaveFileDialog();
        if(file != null) {
            FileWriter fileWriter = new FileWriter(file, false);
            for(StockModel model : tableView.getItems()) {
                fileWriter.write(model.getNumberOfShares() + FIELD_DELIMITER + model.getCostPerShare() + "\n");
            }
            fileWriter.close();
        }
    }

    @FXML
    private void loadFile() throws IOException {
        File file = showOpenFileDialog();

        if(file != null) {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<StockModel> modelsToAdd = new ArrayList<>();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    modelsToAdd.add(createModel(line));
                }
                catch (Exception e) {
                    modelsToAdd.clear();
                    break;
                }
            }

            bufferedReader.close();
            fileReader.close();

            if(!modelsToAdd.isEmpty()) {
                tableView.getItems().clear();
                tableView.getItems().addAll(modelsToAdd);
            }
            else {
                String title = "Error";
                String message = "Error: The content in the file \"" + file.getName() + "\" is not in a valid format.";
                int height = 150;
                int width = 500;
                displayAlertBox(title, message, height, width);
            }
        }
    }

    private boolean areTextFieldsEmpty() {
        return numberOfSharesTextField.getText().isEmpty() || costPerShareTextField.getText().isEmpty();
    }

    private void displayAlertBox(String title, String message, int height, int width) {
        GenericAlertBox.displayAlertBox(title, message, height, width);
    }

    private File showSaveFileDialog() {
        return createFileChooser().showSaveDialog(tableView.getScene().getWindow());
    }

    private File showOpenFileDialog() {
        return createFileChooser().showOpenDialog(tableView.getScene().getWindow());
    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser;
    }

    private StockModel createModel(String line) throws Exception {
        String[] fields = line.split(FIELD_DELIMITER, -1);

        if(!checkValidityOfFields(fields)) {
            throw new Exception();
        }

        return new StockModel(new SimpleIntegerProperty(Integer.parseInt(fields[0])), new SimpleDoubleProperty(Double.parseDouble(fields[1])));
    }

    private boolean checkValidityOfFields(String[] fields) {
        return fields.length == 2 && fields[0].matches(DIGITS_ONLY_REGEX) && fields[1].matches(DECIMAL_NUMBER_ONLY_REGEX);
    }
}
