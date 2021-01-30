package green.snake;
import java.util.* ;

public class Obstacle extends Ball {

    public Obstacle ( int x, int y, int radius, int color ) {
        super( x, y, radius, color);
    }

    public void avoid (Snake snake) {
        ArrayList<Ball> snakeBody = snake.getSnakeBody();
        Ball foundBall = null;

        if ( this.touchesBall( snake.getSnakeHead() ) ) {
            foundBall = snake.getSnakeHead();
        }else{
            for ( int i = 0; i < snakeBody.size(); ++i ) {
                if ( this.touchesBall( snakeBody.get(i) ) ) {
                    foundBall = snakeBody.get(i);
                    break;
                }
            }
        }
        if ( foundBall != null ) this.avoid(foundBall);
    }

    public void moveToScreen (int viewWidth, int viewHeight) {
        if ( this.getCenterX() < this.getRadius() ) {
            this.moveBallBy(1,0);
        }
        if ( this.getCenterX() > viewWidth - this.getRadius() ) {
            this.moveBallBy(-1,0);
        }
        if ( this.getCenterY() < this.getRadius() ) {
            this.moveBallBy(0,1);
        }
        if ( this.getCenterY() > viewHeight - this.getRadius() ) {
            this.moveBallBy(0,-1);
        }
    }

    public void unstuck( Ball otherObstacle ) {
        if ( this.touchesBall(otherObstacle)) {
            if( this.getCenterX() > otherObstacle.getCenterX() ){
                this.moveBallBy(1,0);
            }else{
                this.moveBallBy(-1,0);
            }
            if( this.getCenterY() > otherObstacle.getCenterY() ){
                this.moveBallBy(0,1);
            }else{
                this.moveBallBy(0,-1);
            }
        }
    }
}
