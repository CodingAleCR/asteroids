package codingalecr.cr.asteroides.utils;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Graphic {
    private Drawable drawable;
    private int cenX, cenY;
    private int width, height;
    private double incX, incY;
    private double angle, rotation;
    private int collitionRadius; //Para determinar colisión
    private int lastX, lastY; // Posición anterior
    private int invalidationRadius; // Radio usado en invalidate()
    private View view; // Usada en view.invalidate()

    public Graphic(View view, Drawable drawable) {
        this.view = view;
        this.drawable = drawable;
        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();
        collitionRadius = (height + width) / 4;
        invalidationRadius = (int) Math.hypot(width / 2, height / 2);
    }

    public void drawGraphic(Canvas canvas) {
        int x = cenX - width / 2;
        int y = cenY - height / 2;
        drawable.setBounds(x, y, x + width, y + height);
        canvas.save();
        canvas.rotate((float) angle, cenX, cenY);
        drawable.draw(canvas);
        canvas.restore();
        lastX = cenX;
        lastY = cenY;
    }

    public void increasePos(double factor) {
        cenX += incX * factor;
        cenY += incY * factor;
        angle += rotation * factor;
        // Si salimos de la pantalla, corregimos posición
        if (cenX < 0) cenX = view.getWidth();
        if (cenX > view.getWidth()) cenX = 0;
        if (cenY < 0) cenY = view.getHeight();
        if (cenY > view.getHeight()) cenY = 0;


        view.postInvalidate(cenX - invalidationRadius, cenY - invalidationRadius, cenX + invalidationRadius, cenY + invalidationRadius);
        view.postInvalidate(lastX - invalidationRadius, lastY - invalidationRadius, lastX + invalidationRadius, lastY + invalidationRadius);

    }

    public double distance(Graphic g) {
        return Math.hypot(cenX - g.cenX, cenY - g.cenY);
    }

    public boolean verifyCollision(Graphic g) {
        return (distance(g) < (collitionRadius + g.collitionRadius));
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getCenX() {
        return cenX;
    }

    public void setCenX(int cenX) {
        this.cenX = cenX;
    }

    public int getCenY() {
        return cenY;
    }

    public void setCenY(int cenY) {
        this.cenY = cenY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getIncX() {
        return incX;
    }

    public void setIncX(double incX) {
        this.incX = incX;
    }

    public double getIncY() {
        return incY;
    }

    public void setIncY(double incY) {
        this.incY = incY;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public int getCollitionRadius() {
        return collitionRadius;
    }

    public void setCollitionRadius(int collitionRadius) {
        this.collitionRadius = collitionRadius;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    public int getInvalidationRadius() {
        return invalidationRadius;
    }

    public void setInvalidationRadius(int invalidationRadius) {
        this.invalidationRadius = invalidationRadius;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}