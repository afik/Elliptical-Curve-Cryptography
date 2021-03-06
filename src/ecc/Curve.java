package ecc;

import java.util.ArrayList;
import com.google.common.math.LongMath;
import java.math.RoundingMode;


/**
 *
 * @author Afik & Hayyu
 */
public class Curve {
    //Elliptic curves we used is short Weierstrass equation
    //y^2 = x^3+ax+b
    
    private long a = 1;
    private long b = 6;
    private long p ;
    public ArrayList<Point> ellipticGroup;
    
    public void setP (long L){
        this.p = L;
    }

    public void setA (long L){
        this.a = L;
    }
    
    public void setB (long L){
        this.b = L;
    }
    /**
     * Find all elliptic group of equation y^2 = x^3+ax+b
     */
    public void setEllipticGrup(){
       ellipticGroup = new ArrayList<>();
       long y2, aCongruence, y, y3;
       long x = 0;
       
       while (x < p){
            y2 = (x*x*x + a*x + b);
            aCongruence = LongMath.mod(y2, p);
            for (int j = 1; j<p-1; j++){
                y3 = p*j + aCongruence;
                if (isPerfectSquare(y3)) {
                    y = LongMath.sqrt(y3, RoundingMode.UP);
                    Point po = new Point(x,y);
                    if (!isPointInGroup(po)){
                        ellipticGroup.add(po);
                    }
                    Point pp = new Point(x,p-y);
                    if (!isPointInGroup(pp)) {
                        ellipticGroup.add(pp);
                    }
                    
                }
            }
            x++;
       }
       ellipticGroup.add(Point.O);
//       System.out.println("Elliptic grup : " + ellipticGroup.size());
//       for (int i =0; i<ellipticGroup.size(); i++){
//           System.out.print("(" + ellipticGroup.get(i).getX() + " " +ellipticGroup.get(i).getY() + ") ");
//       }
    }
    
    /**
     * Get value Y in curve, known x
     */
    public long getY(long x) {
        long y2, aCongruence, y = 0, y3;
        y2 = (x*x*x + a*x + b);
        aCongruence = LongMath.mod(y2, p);
        boolean found = false;
        int j = 0;
        while (!found && j<p-1){
            y3 = p*j + aCongruence;
            if (isPerfectSquare(y3)) {
                y = LongMath.sqrt(y3, RoundingMode.UP);
                found = true;
            }
            j++;
        }
        return y;
    }
    
    /**
     * Check if a long integer is a perfect square
     * @param n, number to be checked
     * @return true if n is perfect square
     */
    public final static boolean isPerfectSquare(long n)
    {
        if (n < 0)
            return false;

        switch((int)(n & 0xF))
        {
        case 0: case 1: case 4: case 9:
            long tst = (long)Math.sqrt(n);
            return tst*tst == n;

        default:
            return false;
        }
    }
    
    
    /**
     * Check is point exist in elliptic group
     */
    public boolean isPointInGroup(Point p){
        boolean found = false;
        int i = 0;
        while (!found && i<ellipticGroup.size()) {
            if (p.getX()==ellipticGroup.get(i).getX() && p.getY()==ellipticGroup.get(i).getY()) {
                found = true;
            }
            i++;
        }
        return found;
    }
    
    /**
     * Point operation on elliptic curve
     */
    public Point penjumlahan(Point q, Point t){
        Point r = new Point();
        if (q == Point.O){
            r.setX(t.getX());
            r.setY(t.getY());
        } else if (t == Point.O){
            r.setX(q.getX());
            r.setY(q.getY());
        } else if (t.inverse() == q){
            r = Point.O;
        } else if (t.getX() == q.getX()){
            r = Point.O;
        } else {
            long lambda = (t.getY() - q.getY())/(t.getX() - q.getX());  //Menghitung gradien garis
            long _x = lambda * lambda - t.getX() - q.getX();
            long _y = lambda * (t.getX() - _x) - t.getY();
            r.setX(_x);
            r.setY(_y);
        }
        return r;
    }
    
    public Point penggandaan(Point p){
        if (p.getY() == 0){
            return Point.O;
        } else {
            Point r = new Point();
            long lambda = (3 * p.getX() * p.getX() + this.a)/(2 * p.getY());  //Menghitung gradien garis
            long _x = lambda * lambda - 2 * p.getX();
            long _y = lambda * (p.getX() - _x) - p.getY();
            r.setX(_x);
            r.setY(_y);            
            return r;
        }
    }
    
    public Point pelelaran(Point p, long k){
        Point r = new Point(p.getX(), p.getY());
        for (long i=1; i<k-1; i++){
            r = penjumlahan(r,p);
        }
        return r;
    }
    
    public Point perkalian(Point p, long k){
        Point r = new Point();
        if (k==0) {
            r = Point.O;
        }
        else if (k==1){
            r = p;
        } 
        else if (k%2==1)
        {
            r = penjumlahan(p, perkalian(p, k-1));
        }
        else {
            r = perkalian(penggandaan(p), k/2);
        }
        return r;
    }
    
}
