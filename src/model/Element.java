package model;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * TODO Description!
 * <p>
 * Creator: Ambrozie
 * Info: Element.class
 * Date: 12/12/2017 22:44
 */
public class Element {
    protected Rect rectangle;
    protected Mat mat;
    protected ElementType type;

    public Element(Rect rectangle, Mat mat, ElementType type) {
        this.rectangle = rectangle;
        this.mat = mat;
        this.type = type;
    }

    public Rect getRectangle() {
        return rectangle;
    }

    public Mat getMat() {
        return mat;
    }

    public ElementType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.name();
    }
}
