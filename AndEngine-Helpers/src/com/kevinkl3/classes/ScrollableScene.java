package com.kevinkl3.classes;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ClickDetector;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;

/**
 * 
 * @author Kevin L—pez
 *	kevinlopez@unitec.edu
 * 	
 *	ScrollableScene:
 *	Class to permit scrolling with a physics effect
 */
public class ScrollableScene extends Scene implements  IScrollDetectorListener, IOnSceneTouchListener, IClickDetectorListener {

	public static final int MODE_X=1,MODE_Y=2,MODE_XY=3;//Modes, to Scroll X, Y or Both;
	
	Camera mCamera;
	private ScrollDetector mScrollDetector;
    private ClickDetector mClickDetector;
	IUpdateHandler mScrollHandler;
	private boolean mMoveX,mMoveY;
    
	private float mMinX = 0;
    private float mMinY = 0;
	private float mMaxX = 793;
	private float mMaxY = 480;
    private float mCurrentX = 0;
    private float mCurrentY = 0;
    private float mXVelocity,mYVelocity;
    
    private long mLastScroll = 0;
    
	public ScrollableScene(Camera pCamera, float pMinX, float pMinY,float pMaxX,float pMaxY,int pMode){
		mCamera = pCamera;
		mMinX = pMinX;
		mMinY = pMinY;
		mMaxX = pMaxX - mCamera.getWidth();
		mMaxY = pMaxY - mCamera.getHeight();
		
		mCurrentX = mCamera.getCenterX();
		mCurrentY = mCamera.getCenterY();
		mMoveX = (pMode == MODE_X || pMode == MODE_XY);
		mMoveY = (pMode == MODE_Y || pMode == MODE_XY);
		mScrollDetector = new SurfaceScrollDetector(this);
        mClickDetector = new ClickDetector(this);
        setOnSceneTouchListener(this);
        setTouchAreaBindingOnActionDownEnabled(true);
        setTouchAreaBindingOnActionMoveEnabled(true);	
	}
	
	public ScrollableScene(Camera pCamera,float pMaxX,float pMaxY,int pMode){
		this(pCamera,0,0,pMaxX,pMaxY,pMode);
	}
	
	public ScrollableScene(Camera pCamera,float pMaxX,float pMaxY){
		this(pCamera,0,0,pMaxX,pMaxY,ScrollableScene.MODE_XY);
	}
	
	public ScrollableScene(Camera pCamera){
		this(pCamera,0,0,pCamera.getWidth(),pCamera.getHeight(),ScrollableScene.MODE_XY);
	}
	
	
	
	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pTouchEvent) {
		 this.mClickDetector.onTouchEvent(pTouchEvent);
         this.mScrollDetector.onTouchEvent(pTouchEvent);
         return true;
	}

	@Override
	public void onClick(ClickDetector pClickDetector, int pPointerID,
			float pSceneX, float pSceneY) {
		if(mScrollHandler != null)
			mCamera.unregisterUpdateHandler(mScrollHandler);
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		if(mScrollHandler != null)
			mCamera.unregisterUpdateHandler(mScrollHandler);
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		
		//Debug.d("onScroll ( " + pDistanceX + " , " + pDistanceY + " )");

		//Move camera onSroll
		//Move X
        if ( !((mCurrentX - pDistanceX) < mMinX) && !((mCurrentX - pDistanceX) > mMaxX ) ){
        	 mCamera.offsetCenter(-pDistanceX, 0 );
             mCurrentX -= pDistanceX;
        }
        
        //Move Y
        if ( !((mCurrentY + pDistanceY) < mMinY) && !((mCurrentY + pDistanceY) > mMaxY ) ){
       	 	mCamera.offsetCenter(0, pDistanceY );
            mCurrentY += pDistanceY;
       }
        long dt = System.currentTimeMillis() - mLastScroll;
        mXVelocity = ( (pDistanceX*1000)/ dt );
        mYVelocity = ( (pDistanceY*1000)/ dt );
        mLastScroll = System.currentTimeMillis();
        //Debug.d("Instant velocity x: " + mXVelocity);
        //Debug.d("Instant velocity Y: " + mYVelocity);
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		//Debug.d("Scroll Finished at: " + System.currentTimeMillis());
		
		final float deaccelarationX = Math.abs(mXVelocity*0.6f);
		final float deaccelarationY = Math.abs(mYVelocity*0.6f);
		final boolean directionX = mXVelocity > 0;
		final boolean directionY = mYVelocity > 0;
		
		mScrollHandler = new IUpdateHandler(){
			float factorX = mXVelocity;
			float factorY = mYVelocity;
	
			@Override
			public void onUpdate(float pSecondsElapsed) {
				//MOVE X
				if(mMoveX){
					//d = v*t
					float dx = factorX*pSecondsElapsed;
					if ( !((mCurrentX - dx) < mMinX) && !((mCurrentX - dx) > mMaxX ) ){
							mCamera.offsetCenter(-dx, 0 );
							mCurrentX -= dx;
					}else{
						mCamera.unregisterUpdateHandler(this);
					}
					
					//Check if velocity reached 0
					if(directionX){
						if(factorX < 0){
							mCamera.unregisterUpdateHandler(this);
						}
					}else if(factorX > 0){
						mCamera.unregisterUpdateHandler(this);
					}
					
					factorX += (directionX ? -1 : 1)*(deaccelarationX*pSecondsElapsed);//V = Vo + a*t
				}
				
				//MOVE Y
				if(mMoveY){
					//d = v*t
					float dy = factorY*pSecondsElapsed;
					if ( !((mCurrentY + dy) < mMinY) && !((mCurrentY + dy) > mMaxY ) ){
							mCamera.offsetCenter(0, dy );
							mCurrentY += dy;
					}else{
						mCamera.unregisterUpdateHandler(this);
					}
					
					//Check if velocity reached 0
					if(directionY){
						if(factorY < 0){
							mCamera.unregisterUpdateHandler(this);
						}
					}else if(factorY > 0){
						mCamera.unregisterUpdateHandler(this);
					}
					
					factorY += (directionY ? -1 : 1)*(deaccelarationY*pSecondsElapsed);//V = Vo + a*t
				}
			}

			@Override
			public void reset() {
				
			}
		};
		
		mCamera.registerUpdateHandler( mScrollHandler );
		mLastScroll = 0;
	}
	
}
