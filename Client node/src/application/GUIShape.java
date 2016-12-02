/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.geom.Path2D;

/**
 *
 * @author vegard Tegner en enkel todimensjonal
 * strekfigur
 */
public class GUIShape extends Path2D.Float {

    /**
     *
     */
    public enum ShapeType {

        RIGHT_ARROW, UP_ARROW, BOAT;
    }

    public GUIShape(ShapeType type) {
        switch (type) {
            case BOAT:
                moveTo(10, 0);
                lineTo(20, 15);
                lineTo(20, 60);
                lineTo(0, 60);
                lineTo(0, 15);
                lineTo(10, 0);
                break;
            case RIGHT_ARROW:
                moveTo(498,250);
                lineTo(488,255);
                lineTo(488,245);
                lineTo(498,250);
            case UP_ARROW:
                moveTo(250,4);
                lineTo(255,14);
                lineTo(245,14);
                lineTo(250,4);
        }

    }

}
