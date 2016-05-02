package in.codehex.emotion;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.List;

import in.codehex.emotion.util.Const;
import in.codehex.emotion.util.DrawingView;

public class EmotionDetectionActivity extends AppCompatActivity implements Detector.ImageListener,
        Detector.FaceListener, CameraDetector.CameraEventListener {
    int cameraPreviewWidth = 0;
    int cameraPreviewHeight = 0;
    private CameraDetector detector;
    private SurfaceView surfaceView;
    private Toolbar toolbar;
    private DrawingView drawingView;
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_detection);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        surfaceView = (SurfaceView) findViewById(R.id.camera);
        drawingView = (DrawingView) findViewById(R.id.drawing_view);

        drawingView.setZOrderMediaOverlay(true);
        surfaceView.setZOrderMediaOverlay(false);

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, surfaceView);
        detector.setLicensePath("sdk_gladwin.codehex@gmail.com.license");
        detector.setMaxProcessRate(20);
        detector.setDetectJoy(true);
        detector.setDetectSadness(true);
        detector.setDetectFear(true);
        detector.setDetectAnger(true);
        detector.setDetectSurprise(true);
        detector.setDetectDisgust(true);
        detector.setImageListener(this);
        detector.setFaceListener(this);
        detector.setOnCameraEventListener(this);

        startCamera();
    }

    @Override
    public void onFaceDetectionStarted() {

    }

    @Override
    public void onFaceDetectionStopped() {
        drawingView.invalidatePoints();
    }

    private void startCamera() {
        if (!detector.isRunning()) {
            try {
                detector.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopCamera() {
        if (detector.isRunning()) {
            try {
                detector.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onImageResults(List<Face> faces, Frame image, float timeStamp) {

        if (faces == null) {
            return;
        }

        if (faces.size() == 0) {
            drawingView.updatePoints(null, true);
            return;
        }

        Face face = faces.get(0);
        if (drawingView.getDrawPointsEnabled() || drawingView.getDrawMeasurementsEnabled()) {
            drawingView.setMetrics(face.measurements.orientation.getRoll(),
                    face.measurements.orientation.getYaw(), face.measurements.orientation.getPitch(),
                    face.measurements.getInterocularDistance(), face.emotions.getValence());
            drawingView.updatePoints(face.getFacePoints(), true);
        }

        if (face.emotions.getJoy() > 30) {
            stopCamera();
            Intent intent = new Intent(EmotionDetectionActivity.this, MusicPlayerActivity.class);
            intent.putExtra("mood", Const.EMOTIONS[0]);
            startActivity(intent);
        } else if (face.emotions.getSadness() > 30) {
            stopCamera();
            Intent intent = new Intent(EmotionDetectionActivity.this, MusicPlayerActivity.class);
            intent.putExtra("mood", Const.EMOTIONS[1]);
            startActivity(intent);
        } else if (face.emotions.getAnger() > 30) {
            stopCamera();
            Intent intent = new Intent(EmotionDetectionActivity.this, MusicPlayerActivity.class);
            intent.putExtra("mood", Const.EMOTIONS[2]);
            startActivity(intent);
        } else if (face.emotions.getFear() > 30) {
            stopCamera();
            Intent intent = new Intent(EmotionDetectionActivity.this, MusicPlayerActivity.class);
            intent.putExtra("mood", Const.EMOTIONS[3]);
            startActivity(intent);
        } else if (face.emotions.getSurprise() > 30) {
            stopCamera();
            Intent intent = new Intent(EmotionDetectionActivity.this, MusicPlayerActivity.class);
            intent.putExtra("mood", Const.EMOTIONS[4]);
            startActivity(intent);
        } else if (face.emotions.getDisgust() > 30) {
            stopCamera();
            Intent intent = new Intent(EmotionDetectionActivity.this, MusicPlayerActivity.class);
            intent.putExtra("mood", Const.EMOTIONS[5]);
            startActivity(intent);
        }
    }

    @Override
    public void onCameraSizeSelected(int cameraWidth, int cameraHeight, Frame.ROTATE rotation) {
        if (rotation == Frame.ROTATE.BY_90_CCW || rotation == Frame.ROTATE.BY_90_CW) {
            cameraPreviewWidth = cameraHeight;
            cameraPreviewHeight = cameraWidth;
        } else {
            cameraPreviewWidth = cameraWidth;
            cameraPreviewHeight = cameraHeight;
        }
        drawingView.setThickness((int) (cameraPreviewWidth / 100f));

        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get the screen width and height, and calculate the new app width/height based on the surfaceview aspect ratio.
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int layoutWidth = displaymetrics.widthPixels;
                int layoutHeight = displaymetrics.heightPixels;

                if (cameraPreviewWidth == 0 || cameraPreviewHeight == 0 || layoutWidth == 0 || layoutHeight == 0)
                    return;

                float layoutAspectRatio = (float) layoutWidth / layoutHeight;
                float cameraPreviewAspectRatio = (float) cameraPreviewWidth / cameraPreviewHeight;

                int newWidth;
                int newHeight;

                if (cameraPreviewAspectRatio > layoutAspectRatio) {
                    newWidth = layoutWidth;
                    newHeight = (int) (layoutWidth / cameraPreviewAspectRatio);
                } else {
                    newWidth = (int) (layoutHeight * cameraPreviewAspectRatio);
                    newHeight = layoutHeight;
                }

                drawingView.updateViewDimensions(newWidth, newHeight, cameraPreviewWidth, cameraPreviewHeight);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        mainLayout.getLayoutParams();
                params.height = newHeight;
                params.width = newWidth;
                mainLayout.setLayoutParams(params);
            }
        });

    }
}
