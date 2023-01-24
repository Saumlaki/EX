package ru.saumlaki.filemanager;

import com.sun.org.apache.xerces.internal.util.StAXLocationWrapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import javax.xml.xpath.XPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    @FXML
    public VBox leftPanel;
    @FXML
    public VBox rightPanel;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }


    public void copyBtnAction(ActionEvent actionEvent) {
        PanelController leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        PanelController rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if (leftPC.getSelectedFileName() == null && rightPC.getSelectedFileName() == null) {
            new Alert(Alert.AlertType.ERROR, "Ни один файл не выбран", ButtonType.OK).showAndWait();
            return;
        }

        PanelController srcPC = null, dstPC = null;
        if (leftPC.getSelectedFileName() != null) {
            srcPC = leftPC;
            dstPC = rightPC;
        }
        if (rightPC.getSelectedFileName() != null) {
            srcPC = rightPC;
            dstPC = leftPC;
        }

        Path srcPath = Paths.get(srcPC.getCurrentPath(), srcPC.getSelectedFileName());
        Path dstPath = Paths.get(dstPC.getCurrentPath()).resolve(srcPath.getFileName().toString());

        try {
            Files.copy(srcPath, dstPath);
            dstPC.updateList(Paths.get(dstPC.getCurrentPath()));
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Не удалось скопировать файл", ButtonType.OK).showAndWait();
        }

    }
}