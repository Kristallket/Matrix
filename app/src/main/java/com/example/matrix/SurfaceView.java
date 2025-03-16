package com.example.matrix;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class SurfaceView extends android.view.SurfaceView implements SurfaceHolder.Callback {

    Bitmap image;
    Bitmap imageleft;
    float x, y, touchX, touchY; //координаты рисунка и точки касания
    float dx, dy;// изменения координат при движении
    Paint paint;
    float speed;
    Resources resources;
    SurfaceHolder holder;
    DrawThread drawThread;//поток рисования

    ArrayList<PlayerSprite> sprites = new ArrayList<>();
    PlayerSprite character;

    public SurfaceView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);//"активируем" интерфейс SurfaceHolder.Callback
        paint = new Paint();
        x = 400;
        y = 1100;
        resources = getResources();
        image = BitmapFactory.decodeResource(resources, R.drawable.sprites);
        imageleft = BitmapFactory.decodeResource(resources, R.drawable.spritesleft);
        speed = 20;//коэффициент скорости
        character = new PlayerSprite(image,imageleft, this, 400, 400);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            touchX = event.getX();
            touchY = event.getY();
            //calculate();
            for (PlayerSprite s: sprites) {
                s.setTouchX(touchX);
                s.setTouchY(touchY);
            }
            character.setTouchX(touchX);
            character.setTouchY(touchY);
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //canvas.drawBitmap(image, x, y, paint);
        //x += dx;
        //y += dy;
        for (PlayerSprite s : sprites) {
            s.draw(canvas);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        PlayerSprite sprite = new PlayerSprite(image,imageleft, this, 400, 400);
        sprites.add(sprite);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        drawThread = new DrawThread(holder, this);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
