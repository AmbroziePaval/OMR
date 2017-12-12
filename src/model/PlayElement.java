package model;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 * TODO Description!
 * <p>
 * Creator: Ambrozie
 * Info: PlayElement.class
 * Date: 12/12/2017 22:49
 */
public class PlayElement extends Element {

    private NodeDuration duration;
    private Point center;

    public PlayElement(Rect rectangle, Mat mat, ElementType type, NodeDuration duration, Point center) {
        super(rectangle, mat, type);
        this.duration = duration;
        this.center = center;
    }

    public NodeDuration getDuration() {
        return duration;
    }

    public Point getCenter() {
        return center;
    }

    @Override
    public String toString() {
        if (ElementType.NOTE.equals(type)) {
            return super.toString() + "\t" + duration.name().toLowerCase() + "\t" + " TODO(note type) ";
        }
        return super.toString() + "\t" + duration.name().toLowerCase();
    }
}
