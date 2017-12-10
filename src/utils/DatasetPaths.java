package utils;

/**
 * Enum for dataset path where we store images containing all templates for a certain classification of elements.
 * <p>
 * Creator: Ambrozie
 * Info: DatasetPaths.class
 * Date: 12/10/2017 23:50
 */
public enum DatasetPaths {

    DATASET_FOLDER("C:\\Users\\Ambrozie\\IdeaProjects\\OpenCVStart\\resources\\dataset"),
    QUARTERS_DATASET(DATASET_FOLDER.getPath() + "\\patrimi.png");

    private final String path;

    DatasetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
