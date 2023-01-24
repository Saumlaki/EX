package ru.saumlaki.filemanager;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {

    @FXML
    public TableView<FileInfo> filesTable;
    @FXML
    public ComboBox<String> disksBox;
    @FXML
    public TextField pathField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        creatColumn(filesTable);
        completeListOfDisks(disksBox);

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFileName());
                    if(Files.isDirectory(path)){
                    updateList(path);}
                }
            }
        });
    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    //Метод создает колонки списка
    private void creatColumn(TableView<FileInfo> table) {

        //!Колонка типа файла
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param->new SimpleStringProperty(param.getValue().getFileType().getName()));
        fileTypeColumn.setPrefWidth(24);

        //!Колонка имени файла
        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        fileNameColumn.setCellValueFactory(param->new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(240);

        //!Колонка размера файла
        //Необходимо переопределить вывод данных.
        //Данные должны разделяться на 1000(например 25 000 012)
        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param->new SimpleObjectProperty(param.getValue().getSize()));

        fileSizeColumn.setCellFactory(column->{
            //Эта часть отвечает за то как выглядит ячейка в столбце_начало
            return new TableCell<FileInfo, Long>(){
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    }else{
                        String text  = String.format("%,d байт",item);
                        if (item == -1) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
            //Эта часть отвечает за то как выглядит ячейка в столбце_конец
        });
        fileSizeColumn.setPrefWidth(240);


        //!Колонка даты последнего изменения
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        TableColumn<FileInfo, String> fileDataColumn = new TableColumn<>("Дата изменения");
        fileDataColumn.setCellValueFactory(param->new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDataColumn.setPrefWidth(240);


        filesTable.getColumns().add(fileTypeColumn);
        filesTable.getColumns().add(fileNameColumn);
        filesTable.getColumns().add(fileSizeColumn);
        filesTable.getColumns().add(fileDataColumn);

        //Сортировка по умолчанию
        filesTable.getSortOrder().addAll(fileTypeColumn);

        //Обновление списка файлов
        updateList(Paths.get("C:"));
    }

    //Метод заполняет список дисков
    private void completeListOfDisks(ComboBox<String> comboBox) {
        comboBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            comboBox.getItems().add(p.toString());
        }
        comboBox.getSelectionModel().select(0);
    }

    //Метод читает файлы в переданной директории и заполняет по ним таблицу
    public void updateList(Path path) {

        pathField.setText(path.normalize().toAbsolutePath().toString());
        filesTable.getItems().clear();
        try {
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,"Не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();

        }

    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path path = Paths.get(pathField.getText()).getParent();
        if (path != null) {
            updateList(path);
        }
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        String item = element.getSelectionModel().getSelectedItem();
        updateList(Paths.get(item));
    }

    public String getSelectedFileName() {
        if(filesTable.isFocused()) {return null;}
        return filesTable.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath() {
        return pathField.getText();

    }
}
