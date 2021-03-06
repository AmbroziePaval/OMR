package utils;

/**
 * Enum for image path where to save part of the steps from the detection.
 * <p>
 * Creator: Ambrozie
 * Info: OutputPaths.class
 * Date: 12/06/2017 23:28
 */
public enum OutputPaths {
    OUTPUT_FOLDER_NAME("C:\\Users\\Ambrozie\\IdeaProjects\\OpenCVStart\\outputs"),
    ELEMENTS_OUTPUT_FOLDER_NAME("C:\\Users\\Ambrozie\\IdeaProjects\\OpenCVStart\\outputs\\elements\\"),
    DEFAULT_OUTPUT(OUTPUT_FOLDER_NAME.path + "\\output.png"),
    DEFAULT_OUTPUT2(OUTPUT_FOLDER_NAME.path + "\\output2.png"),
    ALL_ELEMENTS(OUTPUT_FOLDER_NAME.path + "\\allRectangles.png"),
    GRAY_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\0_gray.png"),
    BITWISE_NOT_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\1_bitwise_not.png"),
    BINARY_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\2_binary.png"),
    HORIZONTAL_OBJ_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\3_horizontal.png"),
    VERTICAL_OBJ_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\4_vertical.png"),
    EDGES_VERTICAL_OBJ_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\5_edges.png"),
    DIL_EDGES_VERTICAL_OBJ_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\6_dil_edges.png"),
    SMOOTH_VERTICAL_OBJ_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\7_smooth.png"),
    REFINED_VERTICAL_OBJ_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\8_vert_refined.png"),
    REFINED_HORIZONTAL_OBJ_IMAGE_PATH(OUTPUT_FOLDER_NAME.path + "\\9_horiz_refined.png"),
    SORTED_RECOGNISED_ELEMENTS(OUTPUT_FOLDER_NAME.path + "\\sorted_recognised_elements.txt");

    private final String path;

    OutputPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
