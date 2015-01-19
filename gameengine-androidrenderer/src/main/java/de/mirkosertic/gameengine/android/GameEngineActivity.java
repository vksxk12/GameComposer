package de.mirkosertic.gameengine.android;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import de.mirkosertic.gameengine.camera.CameraBehavior;
import de.mirkosertic.gameengine.camera.SetScreenResolution;
import de.mirkosertic.gameengine.core.*;
import de.mirkosertic.gameengine.type.Size;
import de.mirkosertic.gameengine.type.TouchIdentifier;
import de.mirkosertic.gameengine.type.TouchPosition;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GameEngineActivity extends Activity {

    private AndroidGameSoundSystemFactory gameSoundSystemFactory;
    private final AndroidGameRuntimeFactory gameRuntimeFactory;
    private final GameLoopFactory gameLoopFactory;
    private AndroidCanvas androidCanvas;

    private Game game;
    private PlaySceneStrategy playSceneStrategy;

    public GameEngineActivity() {
        gameRuntimeFactory = new AndroidGameRuntimeFactory();
        gameLoopFactory = new GameLoopFactory();

        playSceneStrategy = new PlaySceneStrategy(gameRuntimeFactory, gameLoopFactory) {
            @Override
            protected void loadOtherScene(String aSceneId) {
                loadScene(aSceneId);
            }

            @Override
            protected Size getScreenSize() {
                return new Size(androidCanvas.getWidth(), androidCanvas.getHeight());
            }

            @Override
            protected GameView getOrCreateCurrentGameView(GameRuntime aGameRuntime, CameraBehavior aCamera, GestureDetector aGestureDetector) {
                return new AndroidGameView(androidCanvas, aCamera, aGameRuntime, aGestureDetector);
            }

            @Override
            public void handleResize() {
                Size theCurrentSize = getScreenSize();
                getRunningGameLoop().getScene().getRuntime().getEventManager().fire(new SetScreenResolution(theCurrentSize));
            }
        };
    }

    /**
     * Called when the activity is first created.
     *
     * @param aSavedState If the activity is being re-initialized after
     *                    previously being shut down then this Bundle contains the data it most
     *                    recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle aSavedState) {
        super.onCreate(aSavedState);

        gameSoundSystemFactory = new AndroidGameSoundSystemFactory(getAssets());

        setContentView(R.layout.activity_main);

        androidCanvas = (AndroidCanvas) findViewById(R.id.canvasView);
        androidCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View aView, MotionEvent aEvent) {
                handleTouchEvent(aEvent);
                return true;
            }
        });

        // Load the game, this is done in another thread
        Runnable theRunnable = new Runnable() {
            @Override
            public void run() {
                loadGame();
            }
        };
        new Thread(theRunnable).start();
    }

    private TouchPosition[] toArray(MotionEvent aEvent) {
        TouchPosition[] thePosition = new TouchPosition[aEvent.getPointerCount()];
        for (int i = 0; i < aEvent.getPointerCount(); i++) {
            thePosition[i] = new TouchPosition(new TouchIdentifier(aEvent.getPointerId(i)), (int) aEvent.getX(i), (int) aEvent.getY(i));
        }
        return thePosition;
    }

    private void handleTouchEvent(MotionEvent aEvent) {
        if (playSceneStrategy.hasGameLoop()) {
            GestureDetector theGestureDetector = playSceneStrategy.getRunningGameLoop().getHumanGameView().getGestureDetector();

            switch (aEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    theGestureDetector.touchStarted(toArray(aEvent));
                    break;
                case MotionEvent.ACTION_UP:
                    theGestureDetector.touchEnded(toArray(aEvent));
                    break;
                case MotionEvent.ACTION_MOVE:
                    theGestureDetector.touchMoved(toArray(aEvent));
                    break;
                case MotionEvent.ACTION_CANCEL:
                    theGestureDetector.touchCanceled(toArray(aEvent));
                    break;
            }
        }
    }

    private void loadGame() {
        AssetManager theAssetManager = getAssets();
        try (InputStream theStream = theAssetManager.open("game.json")) {
            JSONObject theGameJSON = new JSONObject(IOUtils.toString(theStream));
            Map<String, Object> theGameData = JSONUtils.toMap(theGameJSON);

            game = Game.deserialize(theGameData);

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GameLoop theLoop = playSceneStrategy.getRunningGameLoop();
                if (theLoop != null && !theLoop.isShutdown()) {
                    theLoop.singleRun();
                }
            }
        }, 0, 16);

        String theDefaultScene = game.defaultSceneProperty().get();
        loadScene(theDefaultScene);
    }

    private void loadScene(String aSceneID) {
        AssetManager theAssetManager = getAssets();

        try (InputStream theStream = theAssetManager.open(aSceneID + "/scene.json")) {
            JSONObject theSceneJSON = new JSONObject(IOUtils.toString(theStream));
            Map<String, Object> theGameData = JSONUtils.toMap(theSceneJSON);

            AndroidGameResourceLoader theResourceLoader = new AndroidGameResourceLoader(theAssetManager, getCacheDir(), aSceneID);

            GameRuntime theRuntime = gameRuntimeFactory.create(theResourceLoader, gameSoundSystemFactory);
            GameScene theGameScene = GameScene.deserialize(game, theRuntime, theGameData);

            playScene(theGameScene);

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void playScene(GameScene aGameScene) {
        playSceneStrategy.playScene(aGameScene);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu aMenu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, aMenu);
        return true;
    }
}