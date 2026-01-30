package IStore.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;

/**
 * Utilitaires pour afficher des alertes JavaFX.
 *
 * @author IStore Team
 * @version 1.0
 */
public class AlertUtil {

    /**
     * Affiche une alerte d'information
     * @param title Le titre
     * @param message Le message
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'erreur
     * @param title Le titre
     * @param message Le message
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'avertissement
     * @param title Le titre
     * @param message Le message
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de confirmation
     * @param title Le titre
     * @param message Le message
     * @return true si l'utilisateur confirme
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Affiche un message de succès
     * @param message Le message
     */
    public static void showSuccess(String message) {
        showInfo("Succès", message);
    }
}
