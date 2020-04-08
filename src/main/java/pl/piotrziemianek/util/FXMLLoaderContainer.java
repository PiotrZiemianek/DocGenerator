package pl.piotrziemianek.util;

import javafx.fxml.FXMLLoader;

public class FXMLLoaderContainer {
    private static FXMLLoader addTherapistPopupLoader = new FXMLLoader(FXMLLoaderContainer.class
            .getResource("/view/addTherapistPopup_view.fxml"));

    private static FXMLLoader addPatientPopupLoader = new FXMLLoader(FXMLLoaderContainer.class
            .getResource("/view/addPatientPopup_view.fxml"));

    private static FXMLLoader mainViewLoader = new FXMLLoader(FXMLLoaderContainer.class
            .getResource("/view/main_view.fxml"));


    public static FXMLLoader getAddTherapistPopupLoader() {
        return addTherapistPopupLoader;
    }

    public static FXMLLoader getMainViewLoader() {
        return mainViewLoader;
    }

    public static FXMLLoader getAddPatientPopupLoader() {
        return addPatientPopupLoader;
    }
}
