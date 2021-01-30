package green.snake;
import android.graphics.*;
import java.util.* ;

public class Snake {
    int headCenterX;
    int headCenterY;
    //get snake direction from this
    int lastHeadCenterX;
    int lastHeadCenterY;

    int headRadius;
    double ballShift;

    Ball snakeHead;
    ArrayList<Ball> snakeBody;
    Ball rightEye;
    Ball leftEye;

    public Snake ( int x, int y, int radius, double shift){
        headCenterX = x;
        headCenterY = y;
        lastHeadCenterX = x;
        lastHeadCenterY = y+1;
        headRadius = radius;
        ballShift = shift;

        snakeHead = new Ball(x, y, headRadius, Color.GREEN);
        rightEye = new Ball(x+5, y-5, (int)(headRadius/5), 0xFF800000);
        leftEye = new Ball(x-5, y-5, (int)(headRadius/5), 0xFF800000);

        snakeBody = new ArrayList<Ball>();
        snakeBody.add( new Ball(x, y, (int)(headRadius * 3 / 4), Color.GREEN) );
        snakeBody.add( new Ball(x, y, (int)(headRadius * 3 / 4), Color.GREEN) );
    }

    // get, set
    public int getRadius() {
        return headRadius;
    }
    public int getHeadX() {
        return headCenterX;
    }
    public int getHeadY() {
        return headCenterY;
    }
    public Ball getSnakeHead() {
        return snakeHead;
    }
    public ArrayList<Ball> getSnakeBody() {
        return snakeBody;
    }

    // moves the head to given point
    public void moveSnakeTo(int x, int y) {
        if(headCenterX != x || headCenterY != y){ // snake needs to move

            lastHeadCenterX = headCenterX;
            lastHeadCenterY = headCenterY;
            headCenterX = x;
            headCenterY = y;
        }

        int snakeDirectionX = headCenterX - lastHeadCenterX ;
        int snakeDirectionY = headCenterY - lastHeadCenterY ;

        //calculate position of the eyes.        
        double alpha = Math.atan2(snakeDirectionY, snakeDirectionX);
        double alpha2 = alpha + Math.PI / 4;
        double alpha3 = alpha - Math.PI / 4;

        // move snake head
        snakeHead.moveBallTo(x, y);
        
        rightEye.moveBallTo( (int) ( x + Math.cos(alpha2) * headRadius / 2 ) , (int)( y + Math.sin(alpha2) * headRadius / 2 ) );
        leftEye.moveBallTo( (int) ( x + Math.cos(alpha3) * headRadius / 2 ) , (int)( y + Math.sin(alpha3) * headRadius / 2 ) );

        // move snake tail
        for(int i = 0; i < snakeBody.size(); ++i){
            Ball ballToMove = snakeBody.get(i);
            if (i == 0) {
                ballToMove.followBall(snakeHead, ballShift);
            }else{
                Ball previousBall = snakeBody.get(i-1);
                ballToMove.followBall(previousBall, ballShift);
            }
        }
    }

    // takes {speed} steps toward a point
    public void moveSnakeTowards( int speed, int x, int y ) {
        int xToMove = x - headCenterX;
        int yToMove = y - headCenterY;
        int distanceToPoint = (int) Math.sqrt( Math.pow( xToMove, 2 ) + Math.pow( yToMove, 2 ) );
        if (distanceToPoint > speed){ //don't move so far in a single step
            xToMove = (int) ( xToMove * speed / distanceToPoint ) ;
            yToMove = (int) ( yToMove * speed / distanceToPoint ) ;
        }
        this.moveSnakeTo( headCenterX + xToMove, headCenterY + yToMove ) ;
    }

    public void grow( int amount ) {
        Ball lastSegment;
        for ( int i = 0; i < amount; ++i ){
            if(snakeBody.size() >= 1) {
                lastSegment = snakeBody.get( snakeBody.size() - 1 );
            }else{
                lastSegment = snakeHead;
            }
            int x = lastSegment.getCenterX();
            int y = lastSegment.getCenterY();
            snakeBody.add( new Ball(x, y, (int)(headRadius * 3 / 4), Color.GREEN) );
        }
    }

    public void shrink( int amount ) {
        for ( int i = 0; snakeBody.size() >= 2 && i < amount; ++i) {
            snakeBody.remove( snakeBody.size() - 1 );
        }
    }

    public void draw(Canvas canvas){
        for(int i = 0; i < snakeBody.size(); ++i){
            snakeBody.get(i).draw(canvas);
        }
        snakeHead.draw(canvas);
        rightEye.draw(canvas);
        leftEye.draw(canvas);
    }
}
