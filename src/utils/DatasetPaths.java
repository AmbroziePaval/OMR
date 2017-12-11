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
    FULL_HEAD_TEMPLATE(DATASET_FOLDER.getPath() + "\\full_head.png"),
    QUARTERS_DATASET(DATASET_FOLDER.getPath() + "\\patrimi.png"),
    OCTETS_DATASET(DATASET_FOLDER.getPath() + "\\optimi.png"),
    DOTS_DATASET(DATASET_FOLDER.getPath() + "\\puncte.png"),
    DIEZ_DATASET(DATASET_FOLDER.getPath() + "\\diez.png"),
    CLEFF_DATASET(DATASET_FOLDER.getPath() + "\\g_cleff.png"),
    BARS_DATASET(DATASET_FOLDER.getPath() + "\\bars.png");

    private final String path;

    DatasetPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
