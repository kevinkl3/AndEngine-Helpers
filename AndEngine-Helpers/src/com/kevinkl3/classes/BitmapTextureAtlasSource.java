package com.kevinkl3.classes;

import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.source.BaseTextureAtlasSource;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * 
 * @author edbartley 
 *	https://github.com/edbartley/AndEngine---Texture-Source-From-Bitmap/blob/master/source.java
 */

public class BitmapTextureAtlasSource extends BaseTextureAtlasSource implements IBitmapTextureAtlasSource {
    private final int[] mColors;
 
    public BitmapTextureAtlasSource(Bitmap pBitmap)
    {
    	super(0,0, pBitmap.getWidth(), pBitmap.getHeight());
        
        mColors = new int[mTextureWidth * mTextureHeight];
        
        for(int y = 0; y < mTextureHeight; ++y)
        {
        	for( int x = 0; x < mTextureWidth; ++x)
        	{
        		mColors[x + y * mTextureWidth] = pBitmap.getPixel(x, y);
        	}
        }
    }

	@Override
	public Bitmap onLoadBitmap(Config pBitmapConfig){
		return this.onLoadBitmap(pBitmapConfig,false);
	}

	@Override
	public IBitmapTextureAtlasSource deepCopy()
	{
		return new BitmapTextureAtlasSource(Bitmap.createBitmap(mColors, mTextureWidth, mTextureHeight, Bitmap.Config.ARGB_8888));
	}

	@Override
	public Bitmap onLoadBitmap(Config pBitmapConfig, boolean pMutable) {
		return Bitmap.createBitmap(mColors, mTextureWidth, mTextureHeight, Bitmap.Config.ARGB_8888);
	}

}