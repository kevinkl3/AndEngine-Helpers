package com.kevinkl3.classes;

import java.io.IOException;
//import java.util.ArrayList;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MusicPlayer {
	//private ArrayList<Music> songs;
	private String songsPaths[];
	private MusicManager musicMan;
	private Context context;
	private int currentSongIndex;
	private Music currentSong;
	
	public MusicPlayer(MusicManager mm, Context pContext, String rutas[]){
		this(mm,pContext);
		songsPaths = rutas;
		/*for(String path : rutas){
			try{
				final Music song = MusicFactory.createMusicFromAsset(musicMan, context, path);
				songs.add(song);
			}catch(IOException e){
				
			}
		}*/
	}
	
	private Music loadSong(int i){
		try{
			final Music song = MusicFactory.createMusicFromAsset(musicMan, context, songsPaths[i]);
			return song;
		}catch(IOException e){
			
		}
		return null;
	}
	
	public MusicPlayer(MusicManager mm, Context pContext){
		this.musicMan = mm;
		this.context = pContext;
		//songs = new ArrayList<Music>();
		currentSongIndex = 0;
	}
	
	public boolean isPlaying(){
		if(currentSong == null)return false;
		return currentSong.isPlaying();
	}
	
	public void stop(){
		if(currentSong != null){
			if(currentSong.isPlaying())currentSong.stop();
			currentSong.release();
			currentSong = null;
		}
		currentSongIndex++;
		if(currentSongIndex >= songsPaths.length){
			currentSongIndex = 0;
		}
	}
	
	public void play(){
		if(currentSong == null){
			currentSong = loadSong(currentSongIndex);
			currentSong.setOnCompletionListener(new OnCompletionListener(){
				public void onCompletion(MediaPlayer arg0) {
					MusicPlayer.this.nextSong();
				}
			});
		}
		if(!currentSong.isPlaying()){
			currentSong.play();
		}
	}
	
	public void pause(){
		if(currentSong == null)return;
		if(currentSong.isPlaying())currentSong.pause();
	}
	
	public void resume(){
		if(currentSong == null)return;
		if(currentSong.isPlaying())return;
		currentSong.resume();
	}
	
	public void softResume(){
		if(currentSong == null)return;
		if(currentSong.isPlaying())return;
		currentSong.setVolume(0.0f);
		currentSong.resume();
		TimerHandler volumeHandler = new TimerHandler(0.4f, true, new ITimerCallback() {
			public void onTimePassed(TimerHandler pTimerHandler) {
				if(currentSong==null){
					((BaseGameActivity)MusicPlayer.this.context).getEngine().unregisterUpdateHandler(pTimerHandler);
					return;
				}
				currentSong.setVolume(currentSong.getVolume() + 0.1f);
				if(currentSong.getVolume() >= 1.0f){
					((BaseGameActivity)MusicPlayer.this.context).getEngine().unregisterUpdateHandler(pTimerHandler);
				}
			}
		});
		
		((BaseGameActivity)MusicPlayer.this.context).getEngine().registerUpdateHandler(volumeHandler);
	}
	
	public void softStop(){
		TimerHandler volumeHandler = new TimerHandler(0.15f, true, new ITimerCallback() {
			public void onTimePassed(TimerHandler pTimerHandler) {
				if(currentSong==null){
					((BaseGameActivity)MusicPlayer.this.context).getEngine().unregisterUpdateHandler(pTimerHandler);
					return;
				}
				currentSong.setVolume(currentSong.getVolume() - 0.1f);
				if(currentSong.getVolume() <= 0){
					((BaseGameActivity)MusicPlayer.this.context).getEngine().unregisterUpdateHandler(pTimerHandler);
					MusicPlayer.this.stop();
				}
			}
		});
		((BaseGameActivity)MusicPlayer.this.context).getEngine().registerUpdateHandler(volumeHandler);
	}
	
	public void nextSong(){
		stop();
		play();
	}
	
}
