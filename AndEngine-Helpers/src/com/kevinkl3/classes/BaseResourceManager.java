package com.kevinkl3.classes;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;

import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import android.content.Context;

public class BaseResourceManager {

	
	public BaseResourceManager(){
	}
	
	
	
	public static ITextureRegion loadTextureRegion(final Engine pEngine,final Context pContext, final String pRes){
		ITexture mTexture = null;
		try {
			mTexture = loadTexture(pEngine,pContext,pRes);
			pEngine.getTextureManager().loadTexture(mTexture);
			//mTexture.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(mTexture == null){
			return null;
		}
		return TextureRegionFactory.extractFromTexture(mTexture);
	}
	
	public static TiledTextureRegion loadTiledTextureRegion(final Engine pEngine,final Context pContext, final String pRes, int pRows, int pCols){
		ITexture mTexture = null;
		try {
			mTexture = loadTexture(pEngine,pContext,pRes);
			mTexture.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(mTexture == null){
			return null;
		}
		return TextureRegionFactory.extractTiledFromTexture(mTexture, pCols, pRows);
	}
	
	
	public static ITexture loadTexture(final Engine pEngine,final Context pContext, final String pRes) throws IOException {
		return new BitmapTexture(pEngine.getTextureManager(),
				new IInputStreamOpener() {
					public InputStream open() throws IOException {
						return pContext.getAssets().open(pRes);
					}
				}, TextureOptions.REPEATING_BILINEAR);
	}
	
}
