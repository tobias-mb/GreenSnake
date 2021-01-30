package green.snake;

import android.graphics.* ;
import android.view.View ;
import android.content.Context ;
import java.util.ArrayList ;
import android.util.AttributeSet ;


public class GreenSnakeView extends View {
    int viewWidth;
    int viewHeight;
    boolean gameRunning = false;
    boolean gameLost = false;
    int touchedPointX = 100;
    int touchedPointY = 100;

    int timerForMice = 1;
    boolean delayForKillerActivation = false;

    int score = 0;

    Snake snake;
    ArrayList<Mouse> miceOnScreen;
    ArrayList<Mouse> bonusMice;
    ArrayList<Mouse> punishers;
    ArrayList<Mouse> killers;
    ArrayList<Obstacle> obstacles;

    Snake demoSnake;
    ArrayList<Ball> demoBalls;

    public void createInstructions() {
        demoSnake = new Snake(0, 100, 20, 0.9);
        demoSnake.moveSnakeTo(100,100);
        demoBalls = new ArrayList<Ball>();
        demoBalls.add( new Ball(80,200,10, Color.GRAY ) );
        demoBalls.add( new Ball(80,300,10, 0xFFFFC800 ) );
        demoBalls.add( new Ball(80,400,10, Color.RED ) );
        demoBalls.add( new Ball(80,500,10, Color.BLUE ) );
        demoBalls.add( new Ball(80,600,40, Color.BLACK ) );
    }

    public GreenSnakeView (Context context) {
        super(context);
        setBackgroundColor( 0xFFFFFFE0 ) ; // Light Yellow
        createInstructions();
    }
    public GreenSnakeView (Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor( 0xFFFFFFE0 ) ; // Light Yellow
        createInstructions();
    }
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
    }

    public void startGame() {
        if ( !gameRunning ){
            snake = new Snake(0, 0, 20, 0.9);
            miceOnScreen = new ArrayList<Mouse>();
            bonusMice = new ArrayList<Mouse>();
            punishers = new ArrayList<Mouse>();
            killers = new ArrayList<Mouse>();
            obstacles = new ArrayList<Obstacle>();
            obstacles.add( new Obstacle( viewWidth / 2 -45, viewHeight / 2, 40, Color.BLACK) );
            obstacles.add( new Obstacle( viewWidth / 2 +45, viewHeight / 2, 40, Color.BLACK) );

            gameRunning = true;
        }        
        invalidate();
    }
    public void resetGame() {
        // delete all
        gameRunning = false;
        gameLost = false;
        snake = new Snake(0, 0, 20, 0.9);
        miceOnScreen = new ArrayList<Mouse>();
        bonusMice = new ArrayList<Mouse>();
        punishers = new ArrayList<Mouse>();
        killers = new ArrayList<Mouse>();
        obstacles = new ArrayList<Obstacle>();
        timerForMice = 1;
        delayForKillerActivation = false;
        score = 0;
        invalidate();
    }
    //adds a new mouse to the mice array
    private void addMouse() {
        int mousePositionX = (int) (Math.random() * viewWidth);
        int mousePositionY = (int) (Math.random() * viewHeight);
        miceOnScreen.add(new Mouse(mousePositionX, mousePositionY, 10, "normal"));
    }
    private void addBonusMouse() {
        int mousePositionX = (int) (Math.random() * viewWidth);
        int mousePositionY = (int) (Math.random() * viewHeight);
        bonusMice.add(new Mouse(mousePositionX, mousePositionY, 10, "bonus"));
    }
    private void addPunisher() {
        int mousePositionX = (int) (Math.random() * viewWidth);
        int mousePositionY = (int) (Math.random() * viewHeight);
        punishers.add(new Mouse(mousePositionX, mousePositionY, 10, "punisher"));
    }
    private void addKiller() {
        int mousePositionX = (int) (Math.random() * viewWidth);
        int mousePositionY = (int) (Math.random() * viewHeight);
        killers.add(new Mouse(mousePositionX, mousePositionY, 10, "killer"));
    }
    // touched Point handed down by the Activity. Returns current score, so the activity can display it.
    public int gameStep(int touchedX, int touchedY) {
        touchedPointX = touchedX;
        touchedPointY = touchedY;
        if(gameRunning && !gameLost){
            // ----------- move stuff -----------
            // snake moves a bit in direction of touched position
            snake.moveSnakeTowards( 6, touchedX, touchedY);
            for ( int i = 0; i < obstacles.size(); ++i ) {
                // snake can push obstcles away
                obstacles.get(i).avoid(snake); 
                // obstacles move back into screen, if pushed out
                obstacles.get(i).moveToScreen(viewWidth, viewHeight);
                // move obstacles apart, if they're on top of each other
                for ( int j = 0; j < obstacles.size(); ++j ) {
                    if ( i != j && obstacles.get(i).touchesBall(obstacles.get(j)) ) {
                        obstacles.get(i).unstuck(obstacles.get(j));
                    }
                }
            }
            // move mice
            for ( int i = 0; i < miceOnScreen.size(); ++i ){
                miceOnScreen.get(i).makeStep( 3, viewWidth, viewHeight, snake );
            }
            for ( int i = 0; i < bonusMice.size(); ++i ){
                bonusMice.get(i).makeStep( 6, viewWidth, viewHeight, snake );
            }
            for ( int i = 0; i < punishers.size(); ++i ){
                punishers.get(i).makeStep( 3, viewWidth, viewHeight, snake );
            }
            for ( int i = 0; i < killers.size(); ++i ){
                killers.get(i).makeStep( 2, viewWidth, viewHeight, snake );
            }
            // check if a mouse is eaten = get points & grow snake
            for ( int i = 0; i < miceOnScreen.size(); ++i ){
                if( miceOnScreen.get(i).touchesSnakeHead(snake) ) {
                    miceOnScreen.remove(i);
                    snake.grow(1);
                    ++score;
                    break;
                }
            }
            // check if a bonus mouse is eaten = shrink snake
            for ( int i = 0; i < bonusMice.size(); ++i ){
                if( bonusMice.get(i).touchesSnakeHead(snake) ) {
                    bonusMice.remove(i);
                    snake.shrink(2);
                    break;
                }
            }
            // check if a punisher touches the snake = remove point & shrink snake
            for ( int i = 0; i < punishers.size(); ++i ){
                if( punishers.get(i).touchesSnake(snake) ) {
                    punishers.remove(i);
                    snake.shrink(2);
                    score -= 5;
                    break;
                }
            }
            // check if a killer touches the snake = game over
            for ( int i = 0; i < killers.size(); ++i ){
                if( killers.get(i).touchesSnake(snake) && killers.get(i).isActive() ) {
                    gameLost = true;
                }
            }
            //check if a mouse is touching the blockers = remove that mouse
            for ( int j = 0; j < obstacles.size() ; ++j ){
                for ( int i = 0; i < miceOnScreen.size(); ++i ){
                    if ( miceOnScreen.get(i).touchesObstacle(obstacles.get(j)) ){
                        miceOnScreen.remove(i);
                        break;
                    }
                }
            }
            for ( int j = 0; j < obstacles.size() ; ++j ){
                for ( int i = 0; i < bonusMice.size(); ++i ){
                    if ( bonusMice.get(i).touchesObstacle(obstacles.get(j)) ){
                        bonusMice.remove(i);
                        break;
                    }
                }
            }
            for ( int j = 0; j < obstacles.size() ; ++j ){
                for ( int i = 0; i < punishers.size(); ++i ){
                    if ( punishers.get(i).touchesObstacle(obstacles.get(j)) ){
                        punishers.remove(i);
                        break;
                    }
                }
            }
            for ( int j = 0; j < obstacles.size() ; ++j ){
                for ( int i = 0; i < killers.size(); ++i ){
                    if ( killers.get(i).touchesObstacle(obstacles.get(j)) ){
                        killers.remove(i);
                        break;
                    }
                }
            }
            // ----- add new mice -----
            if(miceOnScreen == null || miceOnScreen.size() == 0 ) {
                for ( int i = 0; i < 3; ++i ){
                    this.addMouse();
                }
            }
            if(timerForMice % 100 == 0){
                this.addMouse();
            }
            if(timerForMice % 200 == 0){
                this.addBonusMouse();
            }
            if(timerForMice % 400 == 0){
                this.addPunisher();
            }
            if(timerForMice % 600 == 0){
                this.addKiller();
            }
            ++timerForMice;
            if(timerForMice > 1000){
                for ( int i = 0; i < killers.size(); ++i) {
                    killers.get(i).activate();
                }
                timerForMice = 1;
            }
        }
        invalidate();
        return score;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(gameRunning){
            snake.draw(canvas);
            for ( int i = 0; i < miceOnScreen.size(); ++i ){
                miceOnScreen.get(i).draw(canvas);
            }
            for ( int i = 0; i < bonusMice.size(); ++i ){
                bonusMice.get(i).draw(canvas);
            }
            for ( int i = 0; i < punishers.size(); ++i ){
                punishers.get(i).draw(canvas);
            } 
            for ( int i = 0; i < killers.size(); ++i ){
                killers.get(i).draw(canvas);
            }
            for ( int i = 0; i < obstacles.size(); ++i ){
                obstacles.get(i).draw(canvas);
            }
            if(gameLost){
                Paint finalPaint = new Paint() ;
                finalPaint.setStyle( Paint.Style.STROKE ) ;
                finalPaint.setTextSize( 40 ) ;
                finalPaint.setColor( Color.RED ) ;
                Paint shadowPaint = new Paint() ;
                shadowPaint.setStyle( Paint.Style.FILL ) ;
                shadowPaint.setColor( 0xAA000000 ) ;

                canvas.drawRect(0, 0, viewWidth, viewHeight, shadowPaint);
                canvas.drawText( "Final Score: " + score, viewWidth / 2 -150, viewHeight / 2, finalPaint ) ;
            }
        }else{ // display instructions
            Paint textPaint = new Paint() ;
            textPaint.setStyle( Paint.Style.STROKE ) ;
            textPaint.setTextSize( 18 ) ;
            textPaint.setColor( Color.BLACK ) ;

            demoSnake.draw(canvas);
            for ( int i = 0; i < demoBalls.size(); ++i ){
                demoBalls.get(i).draw(canvas);
            }
            canvas.drawText( "This is your snake.", 150,  100, textPaint ) ;
            canvas.drawText( "Your goal is to eat mice.", 150,  200, textPaint ) ;
            canvas.drawText( "Touching a poisonous bug", 150, 300, textPaint ) ;
            canvas.drawText( "will give you stomachache", 150, 320, textPaint ) ;
            canvas.drawText( "and cost points.", 150, 340, textPaint ) ;
            canvas.drawText( "Poisonous frogs will kill your snake", 150, 400, textPaint ) ;
            canvas.drawText( "and end the game.", 150, 420, textPaint ) ;
            canvas.drawText( "Eating a bird doesn't give point", 150, 500, textPaint ) ;
            canvas.drawText( "but it will shrink your snake.", 150, 520, textPaint ) ;
            canvas.drawText( "Boulders can be used to crush", 150, 600, textPaint ) ;
            canvas.drawText( "bugs and frogs.", 150, 620, textPaint ) ;
        }
    }
}
