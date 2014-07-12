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
import org.andengine.util.debug.Debug;

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
    private int mMode;
    
	private float mMinX = 0;
    private float mMinY = 0;
	private float mMaxX = 793;
	private float mMaxY = 480;
    private float mCurrentX = 0;
    private float mCurrentY = 0;
    
    private long mTouchStartTime;
    private float mTouchSumX,mTouchSumY;
    
	public ScrollableScene(Camera pCamera, float pMinX, float pMinY,float pMaxX,float pMaxY,int pMode){
		mCamera = pCamera;
		mMinX = pMinX;
		mMinY = pMinY;
		mMaxX = pMaxX - mCamera.getWidth();
		mMaxY = pMaxY - mCamera.getHeight();
		
		mCurrentX = mCamera.getCenterX();
		mCurrentY = mCamera.getCenterY();
		mMode = pMode;
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
		mTouchStartTime = System.currentTimeMillis();
		mTouchSumX = mTouchSumY= 0;
		if(mScrollHandler != null)
			mCamera.unregisterUpdateHandler(mScrollHandler);
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {

		Debug.d("onScroll ( " + pDistanceX + " , " + pDistanceY + " )");
		mTouchSumX += pDistanceX;
		mTouchSumY += pDistanceY;

        if ( !((mCurrentX - pDistanceX) < mMinX) && !((mCurrentX - pDistanceX) > mMaxX ) ){//move X
        	 this.mCamera.offsetCenter(-pDistanceX, 0 );
             mCurrentX -= pDistanceX;
        }
        /*
        
        if ( !((mCurrentY + pDistanceY) < mMinY) && !((mCurrentY + pDistanceY) > mMaxY ) ){//move X
       	 this.mCamera.offsetCenter(0, pDistanceY );
            mCurrentY += pDistanceY;
        	if(this.mCamera.getCenterY()<0 ){
        		this.mCamera.offsetCenter(mCurrentX,0 );
        		mCurrentY=0;
        	}
       }*/
        
       
        
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		long timeElapse = System.currentTimeMillis() - mTouchStartTime;
		Debug.d("Scroll Finished at: " + System.currentTimeMillis());
		Debug.d("Touch Time: " + timeElapse);
		Debug.d("values ( " + pDistanceX + " , " + pDistanceY + " )");
		Debug.d("SUM x: " + mTouchSumX +"  y: " + mTouchSumY);
		final long velX = (long) ( ((long)mTouchSumX*1000)/timeElapse);//V = d/t
		Debug.d("VelX: " + velX);
		final float deaccelarationX = Math.abs(velX*0.6f);
		final boolean direction = velX > 0;
		
		mScrollHandler = new IUpdateHandler(){
			float factorX = velX;
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
        	 	//d = v*t
				float dx = factorX*pSecondsElapsed;
				if ( !((mCurrentX - dx) < mMinX) && !((mCurrentX - dx) > mMaxX ) ){
						mCamera.offsetCenter(-dx, 0 );
						mCurrentX -= dx;
				}
				
				factorX += (direction ? -1 : 1)*(deaccelarationX*pSecondsElapsed);//V = Vo + a*t
				if(direction){
					if(factorX < 0){
						mCamera.unregisterUpdateHandler(this);
					}
				}else if(factorX > 0){
					mCamera.unregisterUpdateHandler(this);
				}
			}

			@Override
			public void reset() {
				
			}
		};
		
		mCamera.registerUpdateHandler( mScrollHandler );
	}
	
}
