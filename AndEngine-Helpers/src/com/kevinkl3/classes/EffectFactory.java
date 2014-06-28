package com.kevinkl3.classes;

import org.andengine.engine.Engine;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.entity.IEntity;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.IModifier.IModifierListener;
/**
 * 
 * @author Kevin Lopez
 * kevinlopez@unitec.edu
 * Class to ease the use of effects, animations and trasitions for Entities
 */


public class EffectFactory {
	
	EffectFactory(){
		
	}
	
	public static void onClickScaleAnimation(ButtonSprite pButtonSprite, final float pToScale){
		final OnClickListener mListner = pButtonSprite.getOnClickListener();
		
		pButtonSprite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(ButtonSprite pButtonSprite,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				final float origScale  = pButtonSprite.getScaleX();
				EffectFactory.scaleAnimation(pButtonSprite, origScale, pToScale, 0.20f);
				mListner.onClick(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
				EffectFactory.scaleAnimation(pButtonSprite, pToScale, origScale, 0.20f);
			}
			
		});
	}
	
	public static ButtonSprite newScalableButton(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final float pScale) {
		ButtonSprite mButtonSprite = new ButtonSprite(pY, pY, pTiledTextureRegion, pTiledTextureRegion, pTiledTextureRegion, pVertexBufferObjectManager){
			
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				final float origScale  = this.getScaleX();
				
				if (!this.isEnabled()) {
				} else if (pSceneTouchEvent.isActionDown()) {
					EffectFactory.scaleAnimation(this, origScale, pScale, 0.20f);
				} else if (pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
					EffectFactory.scaleAnimation(this, pScale,origScale, 0.20f);
				} else if (pSceneTouchEvent.isActionUp() && this.getState() == State.PRESSED) {
					EffectFactory.scaleAnimation(this, pScale,origScale, 0.20f);
				}
				
				
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalY, pTouchAreaLocalY);
			}
		};
		
		
		return mButtonSprite;
	}
	
	public static void doFadeOutScaleText(float pX, float pY,Font pfont, String pText,Scene pScene, Engine pEngine){
		doFadeOutScaleText(pX, pY,pfont, pText, pScene, pEngine, 1.0f, 1.0f, 1.8f);
	}
	
	public static void doFadeOutScaleText(float pX, float pY,Font pfont, String pText,Scene pScene, Engine pEngine,float pDurationSecs){
		doFadeOutScaleText(pX, pY,pfont, pText, pScene, pEngine, pDurationSecs, 1.0f, 1.8f);
	}
	
	public static void doFadeOutScaleText(float pX, float pY,Font pFont, String pText,Scene pScene, Engine pEngine,float pDurationSecs,float pFromScale, float pToScale){
		Text mText = new Text(pX,pY,pFont,pText,pEngine.getVertexBufferObjectManager());
		pScene.attachChild(mText);
		mText.setCullingEnabled(true);
		fadeOutScale(mText,pFromScale,pToScale,pDurationSecs,false);
	}
	
	public static void fadeOutScale(IEntity pEntity, float pFromScale, float pToScale, float pDuration,final boolean pDisposeAfter){
		pEntity.registerEntityModifier(  new ScaleModifier(pDuration, pFromScale, pToScale));
		AlphaModifier mAlphaMod = new AlphaModifier(pDuration+0.5f,1.0f,0.0f);
		//TODO fix this
		/*mAlphaMod.addModifierListener(new IModifierListener<IEntity>(){

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,
					IEntity pItem) {
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier,
					IEntity pItem) {
					if(pDisposeAfter){
						pItem.detachSelf();
						pItem.dispose();
					}
			}
			
		});*/
		pEntity.registerEntityModifier( mAlphaMod);
	}
	
	public static void fadeOutScale(IEntity pEntity, float pFromScale, float pToScale, float pDuration){
		fadeOutScale(pEntity, pFromScale, pToScale, pDuration, false);
	}
	
	public static void scaleAnimation(IEntity pEntity, float pFromScale, float pToScale, float pDuration){
		pEntity.registerEntityModifier(  new ScaleModifier(pDuration, pFromScale, pToScale));
	}
	
	public static void scaleCycleAnimation(IEntity pEntity, float pFromScale, float pToScale, float pDuration,final boolean pRepeat){
		final ScaleModifier mScaleMod1 = new ScaleModifier(pDuration*0.5f, pFromScale, pToScale);
		final ScaleModifier mScaleMod2 = new ScaleModifier(pDuration*0.5f, pToScale, pFromScale);
		
		mScaleMod1.setAutoUnregisterWhenFinished(true);
		mScaleMod2.setAutoUnregisterWhenFinished(true);
		
		mScaleMod1.addModifierListener(new IModifierListener<IEntity>(){
			public void onModifierStarted(IModifier<IEntity> pModifier,IEntity pItem) {}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier,IEntity pItem) {
				mScaleMod2.reset();
				pItem.registerEntityModifier(mScaleMod2);
			}
			
		});
		
		mScaleMod2.addModifierListener(new IModifierListener<IEntity>(){
			public void onModifierStarted(IModifier<IEntity> pModifier,IEntity pItem) {}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier,IEntity pItem) {
				if(pRepeat){
					mScaleMod1.reset();
					pItem.registerEntityModifier(mScaleMod1);
				}
			}
		});
		
		
		pEntity.registerEntityModifier(mScaleMod1);
		
	}
	
	/**
	 * Fade transition between the current scene in the Engine and the Scene passed as pScene.
	 * @param pEngine	Engine
	 * @param pScene	Destiny Scene
	 * @param pTime		Time of the transition
	 */
	public static void fadeTransition(final Engine pEngine, final Scene pScene, final float pTime){
		EffectFactory.fadeTransition(pEngine, pScene, pTime,null);
	}
	
	public static void fadeTransition(final Engine pEngine, final Scene pScene, final float pTime, final SimpleCallBack pCallBack){
		Scene currentScene = pEngine.getScene();
		float width = pEngine.getCamera().getWidth();
		float height = pEngine.getCamera().getHeight();
		
		float mCameraCenterX = pEngine.getCamera().getCenterX();
		float mCameraCenterY = pEngine.getCamera().getCenterY();
		
		final Rectangle blackRect1 = new Rectangle(mCameraCenterX,mCameraCenterY,width,height,pEngine.getVertexBufferObjectManager());
		final Rectangle blackRect2 = new Rectangle(mCameraCenterX,mCameraCenterY,width,height,pEngine.getVertexBufferObjectManager());
		blackRect1.setColor(Color.BLACK);
		blackRect2.setColor(Color.BLACK);
		
		blackRect1.setAlpha(0.0f);
		
		currentScene.attachChild(blackRect1);
		pScene.attachChild(blackRect2);
		
		
		final AlphaModifier modificador1 = new AlphaModifier(pTime/2.0f,0.0f,1.0f);
		final AlphaModifier modificador2 = new AlphaModifier(pTime/2.0f,1.0f,0.0f);
		
		modificador2.addModifierListener(new IModifierListener<IEntity>(){

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,IEntity pItem) {}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier,IEntity pItem) {
				blackRect2.detachSelf();
				if(pCallBack != null)
					pCallBack.onAction();
			}
	
		});
		
		modificador1.addModifierListener(new IModifierListener<IEntity>(){
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				pEngine.setScene(pScene);
				blackRect1.detachSelf();
				blackRect2.registerEntityModifier(modificador2);
			}	
		});
		
		blackRect1.registerEntityModifier(modificador1);
		
		
	}
}
