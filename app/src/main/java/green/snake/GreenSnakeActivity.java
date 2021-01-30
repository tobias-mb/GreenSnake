package green.snake;

import android.os.Bundle;
import android.app.Activity ;
import android.view.* ;
import android.os.Handler;
import android.widget.TextView;

public class GreenSnakeActivity extends Activity {
    GreenSnakeView greenSnakeView;
    TextView scoreTextfield;
    int currentScore;
    private final Handler handler = new Handler();

    // last touched position
    int lastTouchDownX = 100;
    int lastTouchDownY = 100;

    // this is executed periodically
    private void doTheGameloop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //this function is running the game
                currentScore = greenSnakeView.gameStep( lastTouchDownX, lastTouchDownY );
                setScoreTextfield("Score: " + currentScore);
                doTheGameloop();
            }
        }, 20);
    }

    private void setScoreTextfield (String text) {
        scoreTextfield.setText(text);
    }

    // This is just saving the last touched position
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            lastTouchDownX = (int) event.getX();
            lastTouchDownY = (int) event.getY();

            // so the touch event is passed on (if someone else needs it)
            return false;
        }
    };

    // in case something should happen on touch
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_snake);

        greenSnakeView = findViewById(R.id.greenSnakeView);
        greenSnakeView.setOnTouchListener(touchListener);
        greenSnakeView.setOnClickListener(clickListener);

        scoreTextfield = findViewById(R.id.scoreTextfield);

        doTheGameloop();
    }

    public void onStart(View view) {
        greenSnakeView.startGame();
    }
    public void onReset(View view) {
        greenSnakeView.resetGame();
    }
}