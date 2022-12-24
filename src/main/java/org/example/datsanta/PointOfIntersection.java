package org.example.datsanta;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PointOfIntersection {
    public static void main(String[] args) {
        System.out.println(pointOfIntersection(new ChildDouble(0, 0), new ChildDouble(5, 0), new ChildDouble(2, 3), new ChildDouble(2, 0)));
    }

    public static ChildDouble pointOfIntersection(ChildDouble a, ChildDouble b, ChildDouble c, ChildDouble d) {

        Vector2 main = new Vector2(c, d);
        Vector2 v1 = new Vector2(c, a);
        Vector2 v2 = new Vector2(c, b);
        if (Vector2.fromDifferentSides(main, v1, v2)) {
            main = new Vector2(a, b);
            v1 = new Vector2(a, c);
            v2 = new Vector2(a, d);

            double product1 = Vector2.crs(main, v1), product2 = Vector2.crs(main, v2);
            if (product1 >= 0 && product2 <= 0 || product1 <= 0 && product2 >= 0) {
                //коэффициент подобия
                double k = Math.abs(product2 / product1);
                if (Float.isInfinite((float) k)) return c;

                //вектор DC
                Vector2 dc = new Vector2(d, c);

                //уменьшаем вектор
                dc.scl((float) (1 / (k + 1) * k));

                //добавляем вектор к точке
                return new ChildDouble(d.x() + dc.x, d.y() + dc.y);
            }

        }

        return null;
    }

    public static boolean linesIntersect(ChildDouble a, ChildDouble b, ChildDouble c, ChildDouble d) {
        Vector2 main = new Vector2(a, b);
        Vector2 v1 = new Vector2(a, c);
        Vector2 v2 = new Vector2(a, d);

        if (Vector2.fromDifferentSides(main, v1, v2)) {
            main = new Vector2(c, d);
            v1 = new Vector2(c, a);
            v2 = new Vector2(c, b);
            return Vector2.fromDifferentSides(main, v1, v2);
        }
        return false;
    }

    public static class Vector2 {
        double x, y;

        public Vector2(ChildDouble d1, ChildDouble d2) {
            this.x = d2.x() - d1.x();
            this.y = d2.y() - d1.y();
        }

        /**
         * Calculates the 2D cross product between this and the given vector.
         *
         * @param v2 the other vector
         * @return the cross product (Z vector)
         */
        public static double crs(Vector2 v1, Vector2 v2) {
            return v1.x * v2.y - v1.y * v2.x;
        }

        /**
         * Multiplies this vector by a scalar
         */
        public Vector2 scl(float scalar) {
            x *= scalar;
            y *= scalar;
            return this;
        }

        public static boolean fromDifferentSides(Vector2 main, Vector2 v1, Vector2 v2) {
            double product1 = crs(main, v1), product2 = crs(main, v2);
            return (product1 >= 0 && product2 <= 0 || product1 <= 0 && product2 >= 0);
        }

        //функция округления
        public static double round(double value, int places) {
            if (places < 0) throw new IllegalArgumentException();
            BigDecimal bd = new BigDecimal(Double.toString(value));
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }
}