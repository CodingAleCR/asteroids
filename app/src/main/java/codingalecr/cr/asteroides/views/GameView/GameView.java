package codingalecr.cr.asteroides.views.GameView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.View;
import codingalecr.cr.asteroides.R;
import codingalecr.cr.asteroides.utils.Graphic;

import java.util.ArrayList;
import java.util.List;

public class GameView extends View {
    // //// NAVE //////
    private Graphic spaceship; // Gráfico de la spaceship
    private int giroNave; // Incremento de dirección
    private double shipAcceleration; // aumento de velocidad
    private static final int MAX_SHIP_SPEED = 20;
    // Incremento estándar de giro y aceleración
    private static final int STEP_SHIP_ROTATION = 5;
    private static final float STEP_SHIP_ACCELERATION = 0.5f;

    // //// ASTEROIDES //////
    private List<Graphic> asteroids; // Lista con los Asteroides
    private int asteroidsQty = 5; // Número inicial de asteroids
    private int fragmentQty = 3; // Fragmentos en que se divide

    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private GameThread thread = new GameThread();
    // Cada cuanto queremos procesar cambios (ms)
    private static int REFRESH_RATE = 50;
    // Cuando se realizó el último proceso
    private long lastUpdate = 0;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableNave, drawableAsteroid, drawableMisil;

        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        try {
            fragmentQty = Integer.parseInt(pref.getString("fragments", "3"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fragmentQty = getResources().getInteger(R.integer.default_fragments);
        }


        if (pref.getString("graphics", "1").equals("0")) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setBackgroundColor(Color.BLACK);

            drawableAsteroid = AppCompatResources.getDrawable(context, R.drawable.ic_large_asteroid);
            drawableNave = AppCompatResources.getDrawable(context, R.drawable.ic_spacecraft);
        } else {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            drawableAsteroid = AppCompatResources.getDrawable(context, R.drawable.asteroide1);
            drawableNave = AppCompatResources.getDrawable(context, R.drawable.nave);
        }

        assert drawableNave != null;
        spaceship = new Graphic(this, drawableNave);

        asteroids = new ArrayList<>();
        for (int i = 0; i < asteroidsQty; i++) {
            assert drawableAsteroid != null;
            Graphic asteroid = new Graphic(this, drawableAsteroid);
            asteroid.setIncY(Math.random() * 4 - 2);
            asteroid.setIncX(Math.random() * 4 - 2);
            asteroid.setAngle((int) (Math.random() * 360));
            asteroid.setRotation((int) (Math.random() * 8 - 4));
            asteroids.add(asteroid);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int prevWidth, int prevHeight) {
        super.onSizeChanged(width, height, prevWidth, prevHeight);

        // Ponemos la nave.
        spaceship.setCenX(width / 2);
        spaceship.setCenY(height / 2);

        // Ponemos los asteroides sin que peguen con la nave.
        for (Graphic asteroid : asteroids) {
            do {
                asteroid.setCenX((int) (Math.random() * width));
                asteroid.setCenY((int) (Math.random() * height));
            } while (asteroid.distance(spaceship) < (width + height) / 5);
        }

        lastUpdate = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Graphic asteroid : asteroids) {
            asteroid.drawGraphic(canvas);
        }
        spaceship.drawGraphic(canvas);
    }

    protected void updateMovement() {
        long now = System.currentTimeMillis();
        if (lastUpdate + REFRESH_RATE > now) {
            return; // Salir si el período de proceso no se ha cumplido.
        }
        // Para una ejecución en tiempo real calculamos el factor de movimiento
        double movFactor = (now - lastUpdate) / REFRESH_RATE;
        lastUpdate = now;
        // Para la próxima vez
        // Actualizamos velocidad y dirección de la nave a partir de
        // giroNave y aceleracionNave (según la entrada del jugador)
        spaceship.setAngle((int) (spaceship.getAngle() + giroNave * movFactor));
        double nIncX = spaceship.getIncX() + shipAcceleration *
                Math.cos(Math.toRadians(spaceship.getAngle())) * movFactor;
        double nIncY = spaceship.getIncY() + shipAcceleration *
                Math.sin(Math.toRadians(spaceship.getAngle())) * movFactor;
        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX, nIncY) <= MAX_SHIP_SPEED) {
            spaceship.setIncX(nIncX);
            spaceship.setIncY(nIncY);
        }
        spaceship.increasePos(movFactor); // Actualizamos posición
        for (Graphic asteroid : asteroids) {
            asteroid.increasePos(movFactor);
        }
    }

    class GameThread extends Thread {
        @Override
        public void run() {
            while (true) {
                updateMovement();
            }
        }
    }
}
