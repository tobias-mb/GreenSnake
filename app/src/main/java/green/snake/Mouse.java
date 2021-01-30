package green.snake;
import android.graphics.*;
import java.util.* ;


public class Mouse {
    Ball mouseBody;
    // movement as rad from 0 to 2*pi
    double moveDirection;
    int mouseRadius;
    int color;

    public Mouse(int x, int y, int radius, String type) {
        switch (type) {
            case "bonus":
                color = Color.BLUE;
                break;
            case "punisher":
                color = 0xFFFFC800;
                break;
            case "killer":
                color = Color.RED;
                break;
            default:
                color = Color.GRAY;
                break;
        }
        mouseRadius = radius;
        mouseBody = new Ball(x, y, mouseRadius, color);
        moveDirection = Math.random() * 2 * Math.PI;
    }

    // return timeSinceCreated, so I can delete old ones
    public void makeStep(int speed, int viewWidth, int viewHeight, Snake snake ) {
        mouseBody.moveBallBy( (int)(speed * Math.cos(moveDirection)), (int)(speed * Math.sin(moveDirection)) );

        // check for screen borders
        int positionX = mouseBody.getCenterX();
        int positionY = mouseBody.getCenterY();
        if( positionY >= viewHeight - mouseRadius ) {
            mouseBody.moveBallBy(0, viewHeight - mouseRadius - positionY);
            moveDirection = -moveDirection;
        }else if (positionY <= mouseRadius) {
            mouseBody.moveBallBy(0, mouseRadius - positionY);
            moveDirection = -moveDirection;
        }else if ( positionX >= viewWidth - mouseRadius ) {
            mouseBody.moveBallBy( viewWidth - mouseRadius - positionX, 0);
            moveDirection = Math.PI - moveDirection;
        }else if (positionX <= 10){
            mouseBody.moveBallBy( mouseRadius - positionX, 0);
            moveDirection = Math.PI - moveDirection;
        }else{
            moveDirection = moveDirection + Math.random() * 0.6 - 0.3;
        }

        // change direction when touching snake body
        ArrayList<Ball> snakeBody = snake.getSnakeBody();
        for ( int i = 0; i < snakeBody.size(); i++ ){
            if ( mouseBody.touchesBall( snakeBody.get(i) )) {
                moveDirection += Math.PI;
                break;
            }
        }

        // make sure mouse can never run out of bounds
        if( positionY > viewHeight + 20 || positionY < -20 || positionX > viewWidth + 20 || positionX < -20 ){
            mouseBody.moveBallTo(100,100);
        }
    }

    public boolean touchesSnakeHead(Snake snake) {
        return ( mouseBody.touchesBall( snake.getSnakeHead() ) ) ;
    }
    public boolean touchesSnake(Snake snake) {

        ArrayList<Ball> snakeBody = snake.getSnakeBody();

        if ( mouseBody.touchesBall( snake.getSnakeHead() ) ) {
            return true;
        }else{
            for ( int i = 0; i < snakeBody.size(); ++i ) {
                if ( mouseBody.touchesBall( snakeBody.get(i) ) ) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean touchesObstacle(Ball obstacle) {
        return ( mouseBody.touchesBall( obstacle ) ) ;
    }

    public void activate () {
        mouseBody.activateBall();
        mouseRadius += 5;
        mouseBody.setRadius(mouseRadius);
    }
    public boolean isActive() {
        return mouseBody.isActivated();
    }

    public void draw(Canvas canvas) {
        mouseBody.draw(canvas);
    }
}
