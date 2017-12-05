import org.opencv.core.Core;

/**
 * Class with the soul purpose of the Computer Vision presentation.
 * <p>
 * Creator: Ambrozie
 * Info: OmrOpenCV.class
 * Date: 12/06/2017 01:13
 */
public class OmrOpenCV {
    public static void main(String[] args) {
        System.out.println("OMR PRESENTATION Paval Ambrozie");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new StaveUtils().run(args[0]);
    }
}
