package photoeditor.cutout.backgrounderaser.bg.remove.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import photoeditor.cutout.backgrounderaser.bg.remove.android.R;

public class ClipArt extends RelativeLayout {
    int baseh;
    int basew;
    int basex;
    int basey;
    ImageButton btndel;
    ImageButton btnrot;
    ImageButton btnscl;
    RelativeLayout clip;
    Context cntx;
    boolean freeze = false;
    int h;
    int i;
    ImageView image;
    EditText text;
    String imageUri;
    boolean isShadow;
    int iv;
    RelativeLayout layBg;
    RelativeLayout layGroup;
    RelativeLayout.LayoutParams layoutParams;
    public LayoutInflater mInflater;
    int margl;
    int margt;
    float opacity = 1.0F;
    Bitmap originalBitmap;
    int pivx;
    int pivy;
    int pos;
    Bitmap shadowBitmap;
    float startDegree;
    String[] v;

    public ClipArt(Context paramContext,int bitmap) {
        super(paramContext);
        cntx = paramContext;
        layGroup = this;

        basex = 0;
        basey = 0;
        pivx = 0;
        pivy = 0;

        mInflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        mInflater.inflate(R.layout.clipart, this, true);
        btndel = ((ImageButton) findViewById(R.id.del));
        btnrot = ((ImageButton) findViewById(R.id.rotate));
        btnscl = ((ImageButton) findViewById(R.id.sacle));

        layoutParams = new RelativeLayout.LayoutParams(250,250 );
        layGroup.setLayoutParams(layoutParams);
        image = ((ImageView) findViewById(R.id.clipart));
        image.setImageResource(bitmap);

        setOnTouchListener(new View.OnTouchListener() {
            final GestureDetector gestureDetector = new GestureDetector(ClipArt.this.cntx,
                    new GestureDetector.SimpleOnGestureListener() {
                        public boolean onDoubleTap(MotionEvent paramAnonymous2MotionEvent) {
                            return false;
                        }
                    });

            public boolean onTouch(View paramAnonymousView, MotionEvent event) {
                if (!ClipArt.this.freeze) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            layGroup.invalidate();
                            gestureDetector.onTouchEvent(event);

                            layGroup.performClick();
                            basex = ((int) (event.getRawX() - layoutParams.leftMargin));
                            basey = ((int) (event.getRawY() - layoutParams.topMargin));
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int i = (int) event.getRawX();
                            int j = (int) event.getRawY();
                            layBg = ((RelativeLayout) getParent());
                            if ((i - basex > -(layGroup.getWidth() * 2 / 3))
                                    && (i - basex < layBg.getWidth() - layGroup.getWidth() / 3)) {
                                layoutParams.leftMargin = (i - basex);
                            }
                            if ((j - basey > -(layGroup.getHeight() * 2 / 3))
                                    && (j - basey < layBg.getHeight() - layGroup.getHeight() / 3)) {
                                layoutParams.topMargin = (j - basey);
                            }
                            layoutParams.rightMargin = 0;
                            layoutParams.bottomMargin = 0;
                            layGroup.setLayoutParams(layoutParams);
                            break;

                    }

                    return true;
                }
                return true;
            }
        });
        this.btnscl.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({ "NewApi" })
            public boolean onTouch(View paramAnonymousView, MotionEvent event) {
                if (!ClipArt.this.freeze) {
                    int j = (int) event.getRawX();
                    int i = (int) event.getRawY();
                    layoutParams = (RelativeLayout.LayoutParams) layGroup.getLayoutParams();
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            ClipArt.this.layGroup.invalidate();
                            ClipArt.this.basex = j;
                            ClipArt.this.basey = i;
                            ClipArt.this.basew = ClipArt.this.layGroup.getWidth();
                            ClipArt.this.baseh = ClipArt.this.layGroup.getHeight();
                            int[] loaction = new int[2];
                            layGroup.getLocationOnScreen(loaction);
                            margl = layoutParams.leftMargin;
                            margt = layoutParams.topMargin;
                            break;
                        case MotionEvent.ACTION_MOVE:

                            float f2 = (float) Math.toDegrees(Math.atan2(i - ClipArt.this.basey, j - ClipArt.this.basex));
                            float f1 = f2;
                            if (f2 < 0.0F) {
                                f1 = f2 + 360.0F;
                            }
                            j -= ClipArt.this.basex;
                            int k = i - ClipArt.this.basey;
                            i = (int) (Math.sqrt(j * j + k * k)
                                    * Math.cos(Math.toRadians(f1 - ClipArt.this.layGroup.getRotation())));
                            j = (int) (Math.sqrt(i * i + k * k)
                                    * Math.sin(Math.toRadians(f1 - ClipArt.this.layGroup.getRotation())));
                            k = i * 2 + ClipArt.this.basew;
                            int m = j * 2 + ClipArt.this.baseh;
                            if (k > 150) {
                                layoutParams.width = k;
                                layoutParams.leftMargin = (ClipArt.this.margl - i);
                            }
                            if (m > 150) {
                                layoutParams.height = m;
                                layoutParams.topMargin = (ClipArt.this.margt - j);
                            }
                            ClipArt.this.layGroup.setLayoutParams(layoutParams);
                            ClipArt.this.layGroup.performLongClick();
                            break;
                    }
                    return true;

                }
                return ClipArt.this.freeze;
            }
        });
        this.btnrot.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({ "NewApi" })
            public boolean onTouch(View paramAnonymousView, MotionEvent event) {
                if (!ClipArt.this.freeze) {
                    layoutParams = (RelativeLayout.LayoutParams) ClipArt.this.layGroup.getLayoutParams();
                    ClipArt.this.layBg = ((RelativeLayout) ClipArt.this.getParent());
                    int[] arrayOfInt = new int[2];
                    layBg.getLocationOnScreen(arrayOfInt);
                    int i = (int) event.getRawX() - arrayOfInt[0];
                    int j = (int) event.getRawY() - arrayOfInt[1];
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            ClipArt.this.layGroup.invalidate();
                            ClipArt.this.startDegree = layGroup.getRotation();
                            ClipArt.this.pivx = (layoutParams.leftMargin + ClipArt.this.getWidth() / 2);
                            ClipArt.this.pivy = (layoutParams.topMargin + ClipArt.this.getHeight() / 2);
                            ClipArt.this.basex = (i - ClipArt.this.pivx);
                            ClipArt.this.basey = (ClipArt.this.pivy - j);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            int k = ClipArt.this.pivx;
                            int m = ClipArt.this.pivy;
                            j = (int) (Math.toDegrees(Math.atan2(ClipArt.this.basey, ClipArt.this.basex))
                                    - Math.toDegrees(Math.atan2(m - j, i - k)));
                            i = j;
                            if (j < 0) {
                                i = j + 360;
                            }
                            ClipArt.this.layGroup.setRotation((ClipArt.this.startDegree + i) % 360.0F);
                            break;
                    }

                    return true;
                }
                return ClipArt.this.freeze;
            }
        });
        this.btndel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (!ClipArt.this.freeze) {
                    layBg = ((RelativeLayout) ClipArt.this.getParent());
                    layBg.performClick();
                    layBg.removeView(ClipArt.this.layGroup);
                }
            }
        });
    }
    public ClipArt(Context paramContext,String text) {
        super(paramContext);
        cntx = paramContext;
        layGroup = this;

        basex = 0;
        basey = 0;
        pivx = 0;
        pivy = 0;

        mInflater = ((LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        mInflater.inflate(R.layout.clipart_text, this, true);
        btndel = ((ImageButton) findViewById(R.id.del));
        btnrot = ((ImageButton) findViewById(R.id.rotate));
        btnscl = ((ImageButton) findViewById(R.id.sacle));

        layoutParams = new RelativeLayout.LayoutParams(250,250 );
        layGroup.setLayoutParams(layoutParams);
        this.text = ((EditText) findViewById(R.id.text));
        this.text.setTextColor(R.color.theme_color);

      //  image.setImageResource(bitmap);
        this.text.setText(text);

        layoutParams = new RelativeLayout.LayoutParams(this.text.getWidth(),this.text.getHeight() );


        this.text.

        setOnTouchListener(new View.OnTouchListener() {
            final GestureDetector gestureDetector = new GestureDetector(ClipArt.this.cntx,
                    new GestureDetector.SimpleOnGestureListener() {
                        public boolean onDoubleTap(MotionEvent paramAnonymous2MotionEvent) {
                            return false;
                        }
                    });

            public boolean onTouch(View paramAnonymousView, MotionEvent event) {
                if (!ClipArt.this.freeze) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            layGroup.invalidate();
                            gestureDetector.onTouchEvent(event);

                            layGroup.performClick();
                            basex = ((int) (event.getRawX() - layoutParams.leftMargin));
                            basey = ((int) (event.getRawY() - layoutParams.topMargin));
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int i = (int) event.getRawX();
                            int j = (int) event.getRawY();
                            layBg = ((RelativeLayout) getParent());
                            if ((i - basex > -(layGroup.getWidth() * 2 / 3))
                                    && (i - basex < layBg.getWidth() - layGroup.getWidth() / 3)) {
                                layoutParams.leftMargin = (i - basex);
                            }
                            if ((j - basey > -(layGroup.getHeight() * 2 / 3))
                                    && (j - basey < layBg.getHeight() - layGroup.getHeight() / 3)) {
                                layoutParams.topMargin = (j - basey);
                            }
                            layoutParams.rightMargin = 0;
                            layoutParams.bottomMargin = 0;
                            layGroup.setLayoutParams(layoutParams);
                            break;

                    }

                    return true;
                }
                return true;
            }
        });
        this.btnscl.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({ "NewApi" })
            public boolean onTouch(View paramAnonymousView, MotionEvent event) {
                if (!ClipArt.this.freeze) {
                    int j = (int) event.getRawX();
                    int i = (int) event.getRawY();
                    layoutParams = (RelativeLayout.LayoutParams) layGroup.getLayoutParams();
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            ClipArt.this.layGroup.invalidate();
                            ClipArt.this.basex = j;
                            ClipArt.this.basey = i;
                            ClipArt.this.basew = ClipArt.this.layGroup.getWidth();
                            ClipArt.this.baseh = ClipArt.this.layGroup.getHeight();
                            int[] loaction = new int[2];
                            layGroup.getLocationOnScreen(loaction);
                            margl = layoutParams.leftMargin;
                            margt = layoutParams.topMargin;
                            break;
                        case MotionEvent.ACTION_MOVE:

                            float f2 = (float) Math.toDegrees(Math.atan2(i - ClipArt.this.basey, j - ClipArt.this.basex));
                            float f1 = f2;
                            if (f2 < 0.0F) {
                                f1 = f2 + 360.0F;
                            }
                            j -= ClipArt.this.basex;
                            int k = i - ClipArt.this.basey;
                            i = (int) (Math.sqrt(j * j + k * k)
                                    * Math.cos(Math.toRadians(f1 - ClipArt.this.layGroup.getRotation())));
                            j = (int) (Math.sqrt(i * i + k * k)
                                    * Math.sin(Math.toRadians(f1 - ClipArt.this.layGroup.getRotation())));
                            k = i * 2 + ClipArt.this.basew;
                            int m = j * 2 + ClipArt.this.baseh;
                            if (k > 150) {
                                layoutParams.width = k;
                                layoutParams.leftMargin = (ClipArt.this.margl - i);
                            }
                            if (m > 150) {
                                layoutParams.height = m;
                                layoutParams.topMargin = (ClipArt.this.margt - j);
                            }
                            ClipArt.this.layGroup.setLayoutParams(layoutParams);
                            ClipArt.this.layGroup.performLongClick();
                            break;
                    }
                    return true;

                }
                return ClipArt.this.freeze;
            }
        });
        this.btnrot.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({ "NewApi" })
            public boolean onTouch(View paramAnonymousView, MotionEvent event) {
                if (!ClipArt.this.freeze) {
                    layoutParams = (RelativeLayout.LayoutParams) ClipArt.this.layGroup.getLayoutParams();
                    ClipArt.this.layBg = ((RelativeLayout) ClipArt.this.getParent());
                    int[] arrayOfInt = new int[2];
                    layBg.getLocationOnScreen(arrayOfInt);
                    int i = (int) event.getRawX() - arrayOfInt[0];
                    int j = (int) event.getRawY() - arrayOfInt[1];
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            ClipArt.this.layGroup.invalidate();
                            ClipArt.this.startDegree = layGroup.getRotation();
                            ClipArt.this.pivx = (layoutParams.leftMargin + ClipArt.this.getWidth() / 2);
                            ClipArt.this.pivy = (layoutParams.topMargin + ClipArt.this.getHeight() / 2);
                            ClipArt.this.basex = (i - ClipArt.this.pivx);
                            ClipArt.this.basey = (ClipArt.this.pivy - j);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            int k = ClipArt.this.pivx;
                            int m = ClipArt.this.pivy;
                            j = (int) (Math.toDegrees(Math.atan2(ClipArt.this.basey, ClipArt.this.basex))
                                    - Math.toDegrees(Math.atan2(m - j, i - k)));
                            i = j;
                            if (j < 0) {
                                i = j + 360;
                            }
                            ClipArt.this.layGroup.setRotation((ClipArt.this.startDegree + i) % 360.0F);
                            break;
                    }

                    return true;
                }
                return ClipArt.this.freeze;
            }
        });
        this.btndel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (!ClipArt.this.freeze) {
                    layBg = ((RelativeLayout) ClipArt.this.getParent());
                    layBg.performClick();
                    layBg.removeView(ClipArt.this.layGroup);
                }
            }
        });
    }


    public void disableAll() {
        this.btndel.setVisibility(View.INVISIBLE);
        this.btnrot.setVisibility(View.INVISIBLE);
        this.btnscl.setVisibility(View.INVISIBLE);
    }

    public void enableAll() {
        this.btndel.setVisibility(View.VISIBLE);
        this.btnrot.setVisibility(View.VISIBLE);
        this.btnscl.setVisibility(View.VISIBLE);
    }

    public ImageView getImageView() {
        return this.image;
    }

    public void setFreeze(boolean paramBoolean) {
        this.freeze = paramBoolean;
    }
}
