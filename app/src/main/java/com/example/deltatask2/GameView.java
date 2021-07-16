package com.example.deltatask2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class GameView extends View {
    Context context;
    Velocity velocity = new Velocity(25,32);
    Handler handler;
    final long UPDATE_MILLI = 30;
    Runnable runnable;
    Dialog dialog;

    int ballx_cor,bally_cor;
    Paint textPaint = new Paint();
    float TEXT_SIZE = 60;
    float paddleX,paddleY;
    float old_x,oldpaddle_x;
    int point=0;
    Bitmap ball,paddle;
    int dWidth,dHeight;
    Random random = new Random();
    Rect rect = new Rect();

    public GameView(Context context) {
        super(context);
        this.context=context;
        ball= BitmapFactory.decodeResource(getResources(),R.drawable.ball);
        paddle= BitmapFactory.decodeResource(getResources(),R.drawable.paddle);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(TEXT_SIZE);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        ballx_cor=random.nextInt(dWidth);
        paddleY=(dHeight*4/5);
        paddleX=dWidth/2-paddle.getWidth()/2;
        dialog = new Dialog(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        ballx_cor +=velocity.getX();
        bally_cor+=velocity.getY();
        if((ballx_cor>=dWidth-ball.getWidth())|| ballx_cor<=0){
            velocity.setX(velocity.getX() * -1);
        }
        if(bally_cor<=0){
            velocity.setY(velocity.getY() * -1);
            point++;
        }
        if(bally_cor>(paddleY+paddle.getHeight())){
            ballx_cor= 1+ random.nextInt(dWidth-ball.getWidth() -1);
            bally_cor=0;
            velocity.setX(xVelocity());
            velocity.setY(32);
            openDialog();
            

        }
        if((ballx_cor+ball.getWidth() >=paddleX)&&(ballx_cor<=(paddleX+paddle.getWidth()))&&((bally_cor+ball.getHeight())>=paddleY)&&(bally_cor+ball.getHeight()<=(paddleY+paddle.getHeight()))){
            velocity.setX(velocity.getX()+1);
            velocity.setY((velocity.getY()+1)*-1);
        }
        canvas.drawBitmap(ball,ballx_cor,bally_cor,null);
        canvas.drawBitmap(paddle,paddleX,paddleY,null);
        canvas.drawText("POINTS: "+point,380,TEXT_SIZE,textPaint);
        handler.postDelayed(runnable,UPDATE_MILLI);
    }

    private void openDialog() {
    dialog.setContentView(R.layout.gameover_dialog);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageViewClose = dialog.findViewById(R.id.imageView);
        Button button=dialog.findViewById(R.id.button);
        dialog.show();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX=event.getX();
        float touchY=event.getX();
        if(touchY>=paddleY){
            int action=event.getAction();
            if(action==MotionEvent.ACTION_DOWN){
                old_x=event.getX();
                oldpaddle_x = paddleX;
            }
            if(action==MotionEvent.ACTION_MOVE){
                float shift = old_x-touchX;
                float newpaddle_x = oldpaddle_x - shift;
                if(newpaddle_x <=0)
                    paddleX=0;
                else if(newpaddle_x>dWidth-paddle.getWidth())
                    paddleX=dWidth-paddle.getWidth();
                else
                    paddleX=newpaddle_x;
            }
        }
        return true;
    }

    private int xVelocity() {
        int[] values = {-35,-30,-25,25,30,35};
        int index= random.nextInt(6);
        return values[index];
    }
}