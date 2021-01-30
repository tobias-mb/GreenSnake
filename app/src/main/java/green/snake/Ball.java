package green.snake;

import android.graphics.*;


public class Ball {
    int ballCenterX;
    int ballCenterY;
    int ballRadius;
    int ballColor;
    boolean isActivated;
    
    //constructor
    public Ball(int x, int y, int rad, int col){
        ballCenterX = x;
        ballCenterY = y;
        ballRadius = rad;
        ballColor = col;
    }

    //getter & setter
    public int getCenterX() {
        return ballCenterX;
    }
    public int getCenterY() {
        return ballCenterY;
    }
    public int getRadius() {
        return ballRadius;
    }
    public int getColor() {
        return ballColor;
    }
    public boolean isActivated() {
        return isActivated;
    }
    public void setCenterX(int x) {
        ballCenterX = x;
    }
    public void setCenterY(int y) {
        ballCenterY = y;
    }
    public void setRadius(int radius) {
        ballRadius = radius;
    }
    public void setColor(int color) {
        ballColor = color;
    }
    public void activateBall() {
        isActivated = true;
    }
    public void deactivateBall() {
        isActivated = false;
    }

    //more functions
    public void moveBallTo(int x, int y){
        ballCenterX = x;
        ballCenterY = y;
    }
    public void moveBallBy(int x, int y) {
        ballCenterX += x;
        ballCenterY += y;
    }

    // is the given point inside the ball?
    public boolean containsPoint(int pointX, int pointY){
        int distanceGivenPointToBallCenter = 
            (int) Math.sqrt(
                Math.pow( ballCenterX - pointX, 2 ) +
                Math.pow( ballCenterY - pointY, 2 ) );

        return ( distanceGivenPointToBallCenter <= ballRadius );
    }

    // do these 2 balls touch?
    public boolean touchesBall(Ball otherBall){
        int otherBallX = otherBall.getCenterX();
        int otherBallY = otherBall.getCenterY();
        int otherBallRadius = otherBall.getRadius();

        int distanceGivenPointToBallCenter = 
            (int) Math.sqrt(
                Math.pow( ballCenterX - otherBallX, 2 ) +
                Math.pow( ballCenterY - otherBallY, 2 ) );

        return ( distanceGivenPointToBallCenter <= ballRadius + otherBallRadius);
    }

    // attach to another ball. shift is between 0 to 1. 0 = balls have same center, 1= only borders touch
    public void followBall(Ball otherBall, double shift ) {
        if (shift < 0) shift = 0;
        if (shift > 1) shift = 1;
        int radiusOtherBall = otherBall.getRadius();
        int xToOtherBall = ballCenterX - otherBall.getCenterX();
        int yToOtherBall = ballCenterY - otherBall.getCenterY();
        int distanceBalls = (int) Math.sqrt( Math.pow( xToOtherBall, 2 ) + Math.pow( yToOtherBall, 2 ) );
        
        if( distanceBalls > shift * (radiusOtherBall + ballRadius) ) {    // no longer touching
            int xToMove = (int) ( xToOtherBall * (distanceBalls - shift * (ballRadius + radiusOtherBall)) / distanceBalls )  ;
            int yToMove = (int) ( yToOtherBall * (distanceBalls - shift * (ballRadius + radiusOtherBall)) / distanceBalls )  ;
            
            ballCenterX -= xToMove;
            ballCenterY -= yToMove;
        }

    }

    //default is only borders touch
    public void followBall( Ball otherBall ) {
        followBall(otherBall, 1);
    }

    // move ball out of the way of this point
    public void avoid( Ball otherBall ){
        int otherBallRadius = otherBall.getRadius();
        int distanceX = ballCenterX - otherBall.getCenterX();
        int distanceY = ballCenterY - otherBall.getCenterY();

        int distanceBalls = (int) Math.sqrt( Math.pow( distanceX, 2 ) + Math.pow( distanceY, 2 )  ) ;

        if ( distanceBalls  <=  ballRadius + otherBallRadius ){ // overlap
            int xToMove = distanceX * (ballRadius + otherBallRadius - distanceBalls) / distanceBalls;
            int yToMove = distanceY * (ballRadius + otherBallRadius - distanceBalls) / distanceBalls;

            ballCenterX += xToMove;
            ballCenterY += yToMove;
        }
    }

    public void draw(Canvas canvas){
        Paint fillerPaint = new Paint() ;
        fillerPaint.setStyle( Paint.Style.FILL ) ;
        fillerPaint.setColor( ballColor ) ;
        Paint borderPaint = new Paint() ;
        borderPaint.setStyle( Paint.Style.STROKE ) ;
        borderPaint.setStrokeWidth( 1 ) ;
        borderPaint.setColor( Color.BLACK ) ;

        canvas.drawCircle( ballCenterX, ballCenterY, ballRadius, fillerPaint ) ;                 
        if (isActivated) {
            canvas.drawCircle( ballCenterX, ballCenterY, ballRadius, borderPaint ) ;
        }
    }
}
