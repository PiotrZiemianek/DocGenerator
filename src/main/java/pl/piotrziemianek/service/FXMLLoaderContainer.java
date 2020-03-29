package pl.piotrziemianek.service;

import javafx.fxml.FXMLLoader;

public class FXMLLoaderContainer {
    private static FXMLLoader addTherapistPopupLoader = new FXMLLoader(FXMLLoaderContainer.class
            .getResource("/view/addTherapistPopup_view.fxml"));

    private static FXMLLoader selectionWindowLoader = new FXMLLoader(FXMLLoaderContainer.class
            .getResource("/view/therapist_view.fxml"));

    private static FXMLLoader mainWindowLoader = new FXMLLoader(FXMLLoaderContainer.class
            .getResource("/view/main_view.fxml"));

    public static FXMLLoader getSelectionWindowLoader() {
        return selectionWindowLoader;
    }

    public static FXMLLoader getAddTherapistPopupLoader() {
        return addTherapistPopupLoader;
    }

    public static FXMLLoader getMainWindowLoader() {
        return mainWindowLoader;
    }
}
