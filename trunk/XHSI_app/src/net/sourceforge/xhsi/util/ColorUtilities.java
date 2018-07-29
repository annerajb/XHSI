/**
 * ColorUtilities.java is a class for color calculations
 * It is an helper class for GraphicsConfig and its child
 *
 *
 * Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2016  Nicolas Carel
 * Copyright (C) 2017  Patrick Burkart (pburkartpublic@gmail.com) (Technische Hochschule Ingolstadt)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sourceforge.xhsi.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

/**
 * @author Cameron Behar
 * @author Patrick Burkart
 *
 *
 *
 */
public class ColorUtilities {

    public static final int AIRBUS_NULL = 0;
    public static final int AIRBUS_AVAIL = 1;
    public static final int AIRBUS_ON = 2;
    public static final int AIRBUS_OFF = 3;
    public static final int AIRBUS_FAULT = 4;
    public static final int AIRBUS_ALTN = 5;
    public static final int AIRBUS_MAN = 6;
    public static final int AIRBUS_OPEN = 7;
    public static final int AIRBUS_SQUIB = 8;
    public static final int AIRBUS_DISCH = 9;

    public static final int AIRBUS_PANEL = 1;
    public static final int AIRBUS_BACK_PANEL = 2;
    public static final int AIRBUS_FRONT_PANEL = 3;

    public static Color blend(Color c0, Color c1) {
        double totalAlpha = c0.getAlpha() + c1.getAlpha();
        double weight0 = c0.getAlpha() / totalAlpha;
        double weight1 = c1.getAlpha() / totalAlpha;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    public static Color blend(Color c0, Color c1, double alpha) {
        double weight0 = alpha;
        double weight1 = 1 - alpha;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = weight0 * c0.getAlpha() + weight1 * c1.getAlpha();

        try {
            return new Color((int) r, (int) g, (int) b, (int) a);
        } catch (IllegalArgumentException e) {
            System.err.println("Blending unsuccesfull: Alpha = " + alpha + ".");
            throw e;
        }
    }

    public static Color multiply(Color c0, Color c1) {
        int r = (c0.getRed() * c1.getRed()) / 255;
        int g = (c0.getGreen() * c1.getGreen()) / 255;
        int b = (c0.getBlue() * c1.getBlue()) / 255;
        int a = (c0.getAlpha() * c1.getAlpha()) / 255;

        return new Color(r, g, b, a);
    }

    public static Color multiply(Color c0, double intensity) {
        int value = (int) (intensity * 255);
        Color c1 = new Color(value, value, value, value);
        int r = (c0.getRed() * c1.getRed()) / 255;
        int g = (c0.getGreen() * c1.getGreen()) / 255;
        int b = (c0.getBlue() * c1.getBlue()) / 255;
        int a = (c0.getAlpha() * c1.getAlpha()) / 255;

        return new Color(r, g, b, a);
    }

    public static GradientPaint multiply(GradientPaint gradient, Color cockpit_light_level) {
        return new GradientPaint(
                gradient.getPoint1(),
                multiply(gradient.getColor1(), cockpit_light_level),
                gradient.getPoint2(),
                multiply(gradient.getColor2(), cockpit_light_level),
                gradient.isCyclic());
    }

    public static GradientPaint multiply(GradientPaint gradient, double cockpit_light_level) {
        return new GradientPaint(
                gradient.getPoint1(),
                multiply(gradient.getColor1(), cockpit_light_level),
                gradient.getPoint2(),
                multiply(gradient.getColor2(), cockpit_light_level),
                gradient.isCyclic());
    }

    /**
     * Darken a color by a factor. If the factor is 1, then this will result in
     * a black return color.
     *
     * @param inputColor the color that the factor will be applied to
     * @param factor the factor, by which the brightness should decrease. Must
     * be between 0 and 1!
     * @return The Color darkened down by the factor
     */
    public static Color darker(Color inputColor, double factor) {
        if (factor < 0) {
            factor = 0;
        }
        if (factor > 1) {
            factor = 1;
        }

        int r = (int) (inputColor.getRed() - (factor * inputColor.getRed()));
        int g = (int) (inputColor.getGreen() - (factor * inputColor.getGreen()));
        int b = (int) (inputColor.getBlue() - (factor * inputColor.getBlue()));
        return new Color(r, g, b, inputColor.getAlpha());
    }

    /**
     * Increases the brightness of a color by a factor. If the factor is 1, the
     * color would be twice as bright. Brightness is increased by increasing each
     * value (red, green and blue) by the factor. If this would exceed the
     * maximum of one of these values, the highest possible factor is chosen.
     *
     * @param inputColor the color that the factor will be applied to
     * @param factor the factor, by which the brightness should increase. Must
     * be between 0 and 1!
     * @return The Color lightened up by the factor
     */
    public static Color lighter(Color inputColor, double factor) {
        if (factor < 0) {
            factor = 0;
        }
        if (factor > 1) {
            factor = 1;
        }

        int maxFactorR = (int) (255.0 / inputColor.getRed() - 1);
        int maxFactorG = (int) (255.0 / inputColor.getGreen() - 1);
        int maxFactorB = (int) (255.0 / inputColor.getBlue() - 1);

        factor = Math.min(factor, Math.min(maxFactorR, Math.min(maxFactorG, maxFactorB)));

        int r = (int) (inputColor.getRed() + (factor * inputColor.getRed()));
        int g = (int) (inputColor.getRed() + (factor * inputColor.getGreen()));
        int b = (int) (inputColor.getRed() + (factor * inputColor.getBlue()));
        return new Color(r, g, b, inputColor.getAlpha());
    }

    /**
     *
     * @param data a three dimensional array, with
     * [y-sample-index][x-sample-index][value for x-y-sample combination]
     * @param x a value between 0 and 100
     * @param y a value between 0 and 100
     * @return the interpolated color based on the input data array
     */
    public static Color interpolate_color(int[][][] data, int x, int y) {
        int[] colorChannel = new int[3];
        int xSteps = data[0].length;
        int ySteps = data.length;

        //Now we can interpolate based on the parameters
        //First, lets make sure the background_setting is 0<background_setting<1!
        x = x < 0 ? 0 : x > 100 ? 100 : x;
        //Second, lets make sure the outside light is 0<outside_light<180!
        y = y < 0 ? 0 : y > 100 ? 100 : y;
        //Let's get to work:
        int x_interval_low;
        int x_increment = (int) (100.0 / (xSteps - 1));
        //Special case: What if x == 100? Then we need to manually set them, otherwise the calculation will do its job!
        if (x == 100) {
            x_interval_low = xSteps - 2;
        } else {
            x_interval_low = x / x_increment;
        }
        int y_interval_low;
        int y_increment = (int) (100.0 / (ySteps - 1));
        //Special case: What if y == 100? Then we need to manually set them, otherwise the calculation will do its job!
        if (y == 100) {
            y_interval_low = ySteps - 2;
        } else {
            y_interval_low = y / y_increment;
        }

        int x0 = x_interval_low * x_increment;
        int x1 = (x_interval_low + 1) * x_increment;
        int x2 = x;
        int y0 = y_interval_low * y_increment;
        int y1 = (y_interval_low + 1) * y_increment;
        int y2 = y;
        //Area of the whole rectangle (x1-x0)*(y1-y0)
        double area = (x1 - x0) * (y1 - y0);
        double na = ((x1 - x2) * (y2 - y0)) / area;
        double nb = ((x2 - x0) * (y2 - y0)) / area;
        double nc = ((x1 - x2) * (y1 - y2)) / area;
        double nd = ((x2 - x0) * (y1 - y2)) / area;

        //Now lets get the color values and calculate the result!
        for (int i = 0; i < 3; i++) {
            int z0 = data[y_interval_low + 1][x_interval_low][i];
            int z1 = data[y_interval_low + 1][x_interval_low + 1][i];
            int z2 = data[y_interval_low][x_interval_low][i];
            int z3 = data[y_interval_low][x_interval_low + 1][i];
            colorChannel[i] = (int) (z0 * na + z1 * nb + z2 * nc + z3 * nd);
        }

        //Finally, return the color:
        return new Color(colorChannel[0], colorChannel[1], colorChannel[2], 255);
    }

 
    public static Color get_button_inside_text_color(int type, double cockpit_level, boolean isActive) {

        switch (type) {
            case AIRBUS_OPEN:
            //Same as AVAIL as far as I know
            case AIRBUS_AVAIL:
                if (isActive) {
                    return blend(new Color(5, 200, 5), new Color(0, 247, 0), cockpit_level);
                } else {
                    return blend(new Color(21, 44, 20), new Color(2, 5, 2), cockpit_level);
                }
            case AIRBUS_ON:
                if (isActive) {
                    return blend(new Color(80, 161, 200), new Color(95, 197, 248), cockpit_level);
                } else {
                    return blend(new Color(29, 40, 40), new Color(3, 5, 4), cockpit_level);
                }
            case AIRBUS_SQUIB:
            //Same as OFF as far as I know
            case AIRBUS_ALTN:
            //Same as OFF as far as I know
            case AIRBUS_MAN:
            //Same as OFF as far as I know
            case AIRBUS_OFF:
                if (isActive) {
                    return blend(new Color(200, 201, 200), new Color(248, 248, 248), cockpit_level);
                } else {
                    return blend(new Color(41, 44, 40), new Color(4, 5, 4), cockpit_level);
                }
            case AIRBUS_DISCH:
            //Same as FAULT as far as I know
            case AIRBUS_FAULT:
                if (isActive) {
                    return blend(new Color(200, 152, 5), new Color(248, 186, 0), cockpit_level);
                } else {
                    return blend(new Color(41, 39, 20), new Color(4, 4, 2), cockpit_level);
                }
            default:
                return new Color(0, 0, 0, 0);
        }
    }

    public static Color get_color(int type, double cockpit_level) {
        switch (type) {
            case AIRBUS_PANEL:
                return blend(new Color(76, 96, 102), new Color(8, 10, 10), cockpit_level);
            case AIRBUS_BACK_PANEL:
                return blend(new Color(153, 176, 179), new Color(8, 10, 10), cockpit_level);
            case AIRBUS_FRONT_PANEL:
                return blend(new Color(81, 107, 125), new Color(8, 10, 10), cockpit_level);
            default:
                return new Color(0, 0, 0, 0);
        }
    }
}
