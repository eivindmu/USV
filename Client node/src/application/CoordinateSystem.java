/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import static java.lang.Math.atan;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author vegard
 * Klasse for koordinatsystemet. Tegner alle komponenter, og flytter de rundt
 * basert på data fra USV
 */
public class CoordinateSystem extends JPanel {

    private float yawRef;
    private float yawAct;
    private float distanceNorth;
    private float distanceEast;
    private float[] data;
    private GUIShape actual;
    private GUIShape desired;
    private GUIShape upArrow;
    private GUIShape rightArrow;
    private GUIShape forceVectorArrow;

    public CoordinateSystem(int width, int height) {
        setSize(width, height);
        actual = new GUIShape(GUIShape.ShapeType.BOAT);
        desired = new GUIShape(GUIShape.ShapeType.BOAT);
        upArrow = new GUIShape(GUIShape.ShapeType.UP_ARROW);
        rightArrow = new GUIShape(GUIShape.ShapeType.RIGHT_ARROW);
        forceVectorArrow = new GUIShape(GUIShape.ShapeType.UP_ARROW);
    }

    @Override
    public void paintComponent(Graphics g) {
//        long time1 = System.currentTimeMillis();
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        // Tegner koordinatsystem først
        int i;

        int height = this.getBounds().height;
        int width = this.getBounds().width;
        // tegn radene

        int rowHt = (height / 10);
        for (i = 0; i < 10; i++) {
            g.drawLine(0, i * rowHt, width, i * rowHt);
        }
        g.drawLine(0, 501, 501, 501);
        // tegn kolonnene
        int rowWid = (width / 10);
        for (i = 0; i < 10; i++) {
            g.drawLine(i * rowWid, 0, i * rowWid, height);
        }
        g.drawLine(501, 0, 501, 500);
        // Sett navn på aksene
        g.setFont(new Font("Serif", Font.BOLD, 15));
        g.setColor(Color.red);

        g.drawLine(250, 0, 250, 500);
        g.drawLine(0, 250, 500, 250);
        g.drawChars(new char[]{'N', 'o', 'r', 't', 'h'}, 0, 5, 260, 15);
        g.drawChars(new char[]{'E', 'a', 's', 't'}, 0, 4, 465, 270);

        yawRef = data[0];
        yawAct = data[1];
        distanceNorth = data[2]*100;
        distanceEast = data[3]*100;
        float forceX = data[4]*3;
        float forceY = data[5]*3;
        

        // Trenger tre lineære transformasjoner
        AffineTransform at1 = new AffineTransform();
        AffineTransform at2 = new AffineTransform();
        AffineTransform at3 = new AffineTransform();

        // Lineær transformasjon av "skipsboksene"
        at1.translate(((width / 2) + (distanceEast - 10)), 
                ((height / 2) - (distanceNorth + 30)));
        // Tegn kraftvektoren fra PIDene
        g.setColor(Color.RED);
        
        // parameter i drawline: x1,y1,x2,y2
        g.drawLine((int) ((width / 2) + distanceEast), 
                (int) ((height / 2) - distanceNorth),
                (int) ((width / 2) + distanceEast + forceY), 
                (int) ((height / 2) - distanceNorth - forceX));
        
        // Translasjon av pilen
        at3.translate(distanceEast + forceY, 
                (height/2)-(distanceNorth + forceX)-3);
        float arrowDir = (float) atan(forceX/forceY);
      
        // atan() returnerer -pi/2 til pi/2
        // sjekk om rotasjonen er utenfor dette området
        // Pilen roterer om spissen
        if(forceY < 0f) {
            // Pilen starter med vinkel pi/2
        at3.rotate(-arrowDir + 3*Math.PI/2,250,4);
        }
        else at3.rotate(-arrowDir+Math.PI/2, 250, 4);

        Shape force = new Path2D.Float(forceVectorArrow, at3);
        
//         Rotasjon om senter
        at1.rotate(Math.toRadians(yawAct), 10, 30);
        Shape actShape = new Path2D.Float(actual, at1);

        at2.translate((width / 2) - 10, (height / 2) - 30);
        at2.rotate(Math.toRadians(yawRef), 10, 30);
        Shape desShape = new Path2D.Float(desired, at2);

        // Tegne skipene i koordinatsystemet

        g2d.setStroke(new BasicStroke(3));

        g2d.setColor(Color.RED);
        g2d.draw(force);
        g2d.fill(force);
        g2d.draw(upArrow);
        g2d.fill(upArrow);
        g2d.draw(rightArrow);
        g2d.fill(rightArrow);
        g2d.fill(actShape);
        g2d.draw(actShape);
        
        g2d.setColor(Color.BLUE);
        
        g2d.draw(desShape);

        g2d.dispose();
    }

    /*
     data = Yaw referanse, Reell yaw, avstand fra referanse nord, 
    avstand fra referanse øst
     */
    public void dataUpdated(float[] data) {
        this.data = data;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });

    }

}
