package codingalecr.cr.asteroides.views.GameView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import codingalecr.cr.asteroides.R;
import codingalecr.cr.asteroides.utils.Graphic;

import java.util.ArrayList;
import java.util.List;

public class GameView extends View implements SensorEventListener {
    // //// NAVE //////
    private Graphic spaceship; // Gráfico de la spaceship
    private int shipRotation; // Incremento de dirección
    private double shipAcceleration; // aumento de velocidad
    private static final int MAX_SHIP_SPEED = 20;
    // Incremento estándar de giro y aceleración
    private static final int STEP_SHIP_ROTATION = 5;
    private static final float STEP_SHIP_ACCELERATION = 0.5f;

    // //// ASTEROIDES //////
    private List<Graphic> asteroids; // Lista con los Asteroides
    private int asteroidsQty = 5; // Número inicial de asteroids
    private int fragmentQty; // Fragmentos en que se divide

    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private GameThread thread = new GameThread();
    // Cada cuanto queremos procesar cambios (ms)
    private static int REFRESH_RATE = 50;
    // Cuando se realizó el último proceso
    private long lastUpdate = 0;

    // //// SPACECRAFT CONTROLS //////
    private String mControlType;

    // //// MISSILE //////
    private List<Graphic> missiles;
    private static int STEP_MISSILE_SPEED = 12;
    private List<Integer> missileTimes;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableSpaceship, drawableAsteroid, drawableMissile;

        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        try {
            fragmentQty = Integer.parseInt(pref.getString(
                    "fragments",
                    String.valueOf(getResources().getInteger(R.integer.default_fragments))
            ));
        } catch (Exception ex) {
            ex.printStackTrace();
            fragmentQty = getResources().getInteger(R.integer.default_fragments);
        }
        mControlType = pref.getString("controls", getResources().getString(R.string.default_controls));

        if (pref.getString("graphics", getResources().getString(R.string.default_graphics)).equals("0")) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setBackgroundResource(R.drawable.ic_background);
            drawableAsteroid = AppCompatResources.getDrawable(context, R.drawable.ic_large_asteroid);
            drawableSpaceship = AppCompatResources.getDrawable(context, R.drawable.ic_spaceship);
        } else {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            drawableAsteroid = AppCompatResources.getDrawable(context, R.drawable.asteroide1);
            drawableSpaceship = AppCompatResources.getDrawable(context, R.drawable.nave);
        }

        assert drawableSpaceship != null;
        spaceship = new Graphic(this, drawableSpaceship);

        missiles = new ArrayList<>();
        missileTimes = new ArrayList<>();

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

        if (mControlType.equals("sensors")) {
            //Sensors
            SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);
            if (!sensorList.isEmpty()) {
                Sensor orientationSensor = sensorList.get(0);
                mSensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @NonNull
    private ShapeDrawable getVectorMissile() {
        ShapeDrawable dMissile = new ShapeDrawable(new RectShape());
        dMissile.getPaint().setColor(Color.MAGENTA);
        dMissile.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        dMissile.setIntrinsicWidth(15);
        dMissile.setIntrinsicHeight(3);
        return dMissile;
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
        synchronized (asteroids) {
            for (Graphic asteroid : asteroids) {
                asteroid.drawGraphic(canvas);
            }
        }
        spaceship.drawGraphic(canvas);

        synchronized (missiles) {
            for (Graphic missile : missiles) {
                missile.drawGraphic(canvas);
            }
        }
    }

    public class GameThread extends Thread {
        private boolean paused, running;

        public synchronized void pauseGame() {
            paused = true;
        }

        public synchronized void resumeGame() {
            paused = true;
            GameThread.this.notify();
        }

        public void stopGame() {
            running = false;
            if (paused) resumeGame();
        }

        @Override
        public void run() {
            running = true;
            while (running) {
                updateMovement();
                synchronized (this) {
                    while (paused) {
                        try {
                            GameView.this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
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
        // shipRotation y aceleracionNave (según la entrada del jugador)
        spaceship.setAngle((int) (spaceship.getAngle() + shipRotation * movFactor));
        double nIncX = spaceship.getIncX() + shipAcceleration *
                Math.cos(Math.toRadians(spaceship.getAngle())) * movFactor;
        double nIncY = spaceship.getIncY() + shipAcceleration *
                Math.sin(Math.toRadians(spaceship.getAngle())) * movFactor;
        // Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX, nIncY) <= MAX_SHIP_SPEED) {
            spaceship.setIncX(nIncX);
            spaceship.setIncY(nIncY);
        }
        spaceship.increasePos(movFactor);
        // Actualizamos posición
        for (Graphic asteroid : asteroids) {
            asteroid.increasePos(movFactor);
        }

        if (!missiles.isEmpty()) {
            for (int m = 0; m < missiles.size(); m++) {
                Graphic missile = missiles.get(m);
                Integer missileTime = missileTimes.get(m);

                missile.increasePos(movFactor);
                if (missileTime < 0) {
                    missiles.remove(m);
                    missileTimes.remove(m);
                    break;
                } else {
                    for (int a = 0; a < asteroids.size(); a++) {
                        if (missile.verifyCollision(asteroids.get(a))) {
                            destroyAsteroid(a);
                            removeMissile(m);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void shootMissile(Context context) {
        Drawable drawableMissile;
        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        if (pref.getString("graphics", getResources().getString(R.string.default_graphics)).equals("0")) {
            drawableMissile = getVectorMissile();
        } else {
            drawableMissile = AppCompatResources.getDrawable(context, R.drawable.missile_animation);

            AnimationDrawable animMissile = (AnimationDrawable) drawableMissile;
            final View me = this;
            animMissile.setCallback(new Drawable.Callback() {
                @Override
                public void unscheduleDrawable(Drawable who, Runnable what) {
                    me.removeCallbacks(what);
                }

                @Override
                public void scheduleDrawable(Drawable who, Runnable what, long when) {
                    me.postDelayed(what, when - SystemClock.uptimeMillis());
                }

                @Override
                public void invalidateDrawable(Drawable who) {
                    me.postInvalidate();
                }
            });
            animMissile.start();
        }
        assert drawableMissile != null;
        Graphic missile = new Graphic(this, drawableMissile);

        missile.setCenX(spaceship.getCenX());
        missile.setCenY(spaceship.getCenY());
        missile.setAngle(spaceship.getAngle());
        missile.setIncX(Math.cos(Math.toRadians(missile.getAngle())) * STEP_MISSILE_SPEED);
        missile.setIncY(Math.sin(Math.toRadians(missile.getAngle())) * STEP_MISSILE_SPEED);
        Integer missileTime = (int) Math.min(this.getWidth() / Math.abs(missile.getIncX()),
                this.getHeight() / Math.abs(missile.getIncY())) - 2;
        Log.d("GameView", "shootMissile: " + missileTime);

        missiles.add(missile);
        missileTimes.add(missileTime);
    }

    private void destroyAsteroid(int i) {
        synchronized (asteroids) {
            asteroids.remove(i);
            this.postInvalidate();
        }
    }

    private void removeMissile(int i) {
        synchronized (missiles) {
            missiles.remove(i);
            synchronized (missileTimes) {
                missileTimes.remove(i);
                this.postInvalidate();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (!mControlType.equals("keyboard")) return false;

        boolean processed = true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                shipAcceleration = +STEP_SHIP_ACCELERATION;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                shipRotation = -STEP_SHIP_ROTATION;
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                shipRotation = +STEP_SHIP_ROTATION;
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                shootMissile(getContext());
                break;

            default:
                processed = false;
                break;
        }
        return processed;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if (!mControlType.equals("keyboard")) return false;

        boolean processed = true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                shipAcceleration = 0;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                shipRotation = 0;
                break;

            default:
                processed = false;
                break;
        }
        return processed;
    }

    private float mX, mY = 0;
    private boolean shooting = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        boolean processed = true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                shooting = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy < 6 && dx > 6) {
                    shooting = false;
                    if (mControlType.equals("touchscreen")) shipRotation = Math.round((x - mX) / 2);
                } else if (dx < 6 && dy > 6) {
                    shooting = false;
                    if (mControlType.equals("touchscreen")) shipAcceleration = Math.round((mY - y) / 25);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mControlType.equals("touchscreen")) {
                    shipRotation = 0;
                    shipAcceleration = 0;
                    processed = false;
                }
                if (shooting) {
                    shootMissile(getContext());
                }
                break;
        }
        mX = x;
        mY = y;
        return processed;
    }

    private boolean hasInitialRotationValue = false;
    private float initialRotationValue;
    private boolean hasInitialAccelerationValue = false;
    private float initialAccelerationValue;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float accelerationValue = sensorEvent.values[0];
        float rotationValue = sensorEvent.values[1];
        if (!hasInitialAccelerationValue) {
            initialAccelerationValue = accelerationValue;
            hasInitialAccelerationValue = true;
        }
        if (!hasInitialRotationValue) {
            initialRotationValue = rotationValue;
            hasInitialRotationValue = true;
        }
        shipRotation = (int) ((rotationValue - initialRotationValue) / 2);
        shipAcceleration = (accelerationValue - initialAccelerationValue) / 20;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public GameThread getThread() {
        return thread;
    }
}
