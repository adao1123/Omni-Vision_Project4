package com.example.omni_vision;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    /**
     * Camera related declarations.
     */
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera camera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;

    /**
     * Main menu button declarations.
     */
    Button leftMainButton;
    Button wikiPediaButton;
    Button gmailButton;
    Button rightMainButton;
    Button youTubeButton;
    Button mapsButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the camera.
        preview = (SurfaceView) findViewById(R.id.surfaceview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        camera = Camera.open();

        // Getting the main menu button on top (in front of) camera preview
        leftMainButton = (Button) findViewById(R.id.leftMainButton);
        leftMainButton.bringToFront();
        preview.invalidate();

        //Instantiating and setting up main menu buttons.
        wikiPediaButton = (Button) findViewById(R.id.wikiPediaButton);
        wikiPediaButton.setVisibility(View.GONE);
        gmailButton = (Button) findViewById(R.id.gmailButton);
        gmailButton.setVisibility(View.GONE);
        rightMainButton = (Button) findViewById(R.id.rightMainButton);
        youTubeButton = (Button) findViewById(R.id.youTubeButton);
        youTubeButton.setVisibility(View.GONE);
        mapsButton = (Button) findViewById(R.id.mapsButton);
        mapsButton.setVisibility(View.GONE);

    }

    /**
     * ----------------End OnCreate-----------------------------------------------------------------
     * <p/>
     * ----------------Start Methods----------------------------------------------------------------
     */


    @Override
    public void onResume() {
        super.onResume();

        releaseCameraAndPreview();
        camera = Camera.open();
        startPreview();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera = null;
        inPreview = false;

        super.onPause();
    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /**
     * Hides the Status bar and soft keys.
     * Causes non-fatal errors.
     *
     * @param hasFocus
     */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }

    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            } catch (Throwable t) {
                Log.e("MainActivity",
                        "Exception in setPreviewDisplay()", t);
                Toast
                        .makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height,
                        parameters);
                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };

}
