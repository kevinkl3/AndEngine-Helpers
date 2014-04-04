package com.kevinkl3.classes;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;

public class SlideBoxMessage extends Rectangle{
	BaseGameActivity parent;
	Scene scene;
	float WIDTH;
	static float HEIGHT = 35;
	String message;
	Font font;
	float secsDuration;
	float timeOut;
	float initTextY;
	Text texto;
	ITexture fontTexture;
	
	/**
	 * 
	 * @param parent Contexto
	 * @param msg Mensaje a mostrar
	 * @param waitTime Tiempo de espera para la entrada
	 */
	public SlideBoxMessage(BaseGameActivity parent,String msg,int waitTime){
		this(parent,parent.getEngine().getCamera().getWidth(),msg,waitTime,0.5f,2.0f);
	}
	
	/**
	 * 
	 * @param parent Contexto
	 * @param msg Mensaje a Mostrar
	 * @param col Color del Box
	 */
	public SlideBoxMessage(BaseGameActivity parent,String msg, org.andengine.util.adt.color.Color col){
		this(parent,parent.getEngine().getCamera().getWidth(),msg,0.5f,2.0f);
		this.setColor(col);
	}
	
	/**
	 * SlideBoxMessage
	 * @param parent Context
	 * @param width Ancho del Box
	 * @param msg Mensaje a mostrar
	 */
	public SlideBoxMessage(BaseGameActivity parent,float width,String msg){
		this(parent,width,msg,0.5f,2.0f);
	}
	
	/**
	 * SlideBoxMessage
	 * @param parent Context
	 * @param width Ancho del box
	 * @param msg Mensaje a mostrar
	 * @param duration Tiempo de entrada/salida
	 * @param timeOut Tiempo de espera para salida
	 */
	public SlideBoxMessage(BaseGameActivity parent,float width,String msg,float duration,float timeOut){
		this(parent,width,msg,0,duration,timeOut);
	}
	
	public SlideBoxMessage(BaseGameActivity parent,float width,String msg,int waitTime,float duration,float timeOut){
		super(0,-HEIGHT,width,HEIGHT,parent.getEngine().getVertexBufferObjectManager());
		this.setColor(0.0f, 0.0f, 0.0f, 0.5f);
		this.parent = parent;
		this.scene = parent.getEngine().getScene();
		this.WIDTH = width;
		this.message = msg;
		this.secsDuration = duration;
		this.timeOut = timeOut;
		fontTexture = new BitmapTextureAtlas(parent.getTextureManager(), 800, 480, TextureOptions.BILINEAR);
		fontTexture.load();

		font = FontFactory.createFromAsset(parent.getFontManager(),fontTexture,parent.getAssets(),"gfx/aerial.ttf",20f,true,Color.WHITE);

		font.load();
		
		if(waitTime <= 0){
			animar();
		}else{
			final int waitSecs = waitTime;
			scene.registerUpdateHandler(new TimerHandler(1.0f, true, new ITimerCallback() {
	            int loops = 0;
				@Override
	            public void onTimePassed(final TimerHandler pTimerHandler) {
					loops++;
					if(loops >= waitSecs){
						SlideBoxMessage.this.animar();
						scene.unregisterUpdateHandler(pTimerHandler);
					}
	            }
	        }));
		}
	}
	
	private void animar(){
		
		texto = new Text(0, 0, font, message, parent.getEngine().getVertexBufferObjectManager());
		texto.setX((float)(WIDTH*0.5 - texto.getWidth()*0.5));
		initTextY= (float)(-HEIGHT*0.5-texto.getHeight()*0.5);
		texto.setY(initTextY);
		texto.setAlpha(0.5f);
		
		scene.attachChild(this);
		scene.attachChild(texto);
		
		float toY = this.getHeight()*0.5f - texto.getHeight()*0.5f; 
		
		this.registerEntityModifier(new MoveYModifier(secsDuration,this.getY(),0));
		texto.registerEntityModifier(new MoveYModifier(secsDuration,texto.getY(),toY));
		
		scene.registerUpdateHandler(new TimerHandler(1.0f, true, new ITimerCallback() {
            int loops = 0;
			@Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
				loops++;
				if(loops >= timeOut){
					limpiar();
					scene.unregisterUpdateHandler(pTimerHandler);
				}
            }
        }));
	}
	
	private void limpiar(){
		//loops++;
		MoveYModifier boxModifier = new MoveYModifier(secsDuration,SlideBoxMessage.this.getY(),-HEIGHT){				       
	        @Override
	        protected void onModifierFinished(final IEntity pItem){
	        	parent.runOnUpdateThread(new Runnable() {
	    		    @Override
	    		    public void run() {
	    				pItem.detachSelf();
	    				pItem.dispose();
	    			}
	    		});
	        }
		};
		
		MoveYModifier textModifier = new MoveYModifier(secsDuration,texto.getY(),initTextY){				       
	        @Override
	        protected void onModifierFinished(final IEntity pItem){
	        	parent.runOnUpdateThread(new Runnable() {
	    		    @Override
	    		    public void run() {
	    				pItem.detachSelf();
	    				pItem.dispose();
	    				fontTexture.unload();
	    				font.unload();
	    				//System.gc();
	    			}
	    		});
	        }
		};
		
		SlideBoxMessage.this.registerEntityModifier(boxModifier);
		texto.registerEntityModifier(textModifier);
	}
}
