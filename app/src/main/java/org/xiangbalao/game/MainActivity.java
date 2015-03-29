package org.xiangbalao.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.xiangbalao.game.common.Const;
import org.xiangbalao.game.util.MUtils;
import org.xiangbalao.gamehit.R;

@SuppressLint("ResourceAsColor")
public class MainActivity extends Activity {

	private MediaPlayer mBgMediaPlayer;
	private boolean isMusic = true;
	private Context mContext;
	private static SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	/**0:打;1:打中;2死机*/
	private int[] soundIds = {-1,-1,-1,-1,-1};
	public boolean isPause;
	public GameView gameView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		init();
		gameView = new GameView(MainActivity.this);
        setContentView(gameView);

//        playBackgroundVoide();
    }

    /**
	 * 
	 */
	private void init() {
		mContext = MainActivity.this;
		initGameMode();
		mBgMediaPlayer = MediaPlayer.create(mContext, Const.voiceBackground);
		mBgMediaPlayer.setLooping(true);//循环
		soundIds[Const.voiceShoot] = mSoundPool.load(mContext, R.raw.shoot, 1);
		soundIds[Const.voiceHit] = mSoundPool.load(mContext, R.raw.hit, 1);
		soundIds[Const.voiceNo] = mSoundPool.load(mContext, R.raw.no, 1);
		soundIds[Const.voiceNextlevel] = mSoundPool.load(mContext, R.raw.nextlevel, 1);
		soundIds[Const.voiceGameover] = mSoundPool.load(mContext, R.raw.gameover, 1);
		
		adMethod();
	}

    /**
	 * 
	 */
	private void adMethod() {
		MUtils.getInstance(mContext);
		MUtils.showRight();
		MUtils.getInstance(mContext);
		MUtils.showBtoom();
	}

	/* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			return true;
		}
    	//super.onKeyDown(keyCode, event)
    	return false;
    }
    
    /**
	 * @return
	 */
	private boolean isGotomenu() {
		isPause = true;
		boolean flag = false;
		Builder gotoMenu = new Builder(mContext);
		TextView textView = new TextView(mContext);
    	textView.setText("要返回菜单吗？");
    	textView.setTextSize(24);
    	textView.setTextColor(getResources().getColor(R.color.fontColor));
    	textView.setGravity(Gravity.CENTER);
    	textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
		gotoMenu.setView(textView);
		gotoMenu.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		gotoMenu.setNegativeButton("否", null);
		gotoMenu.show();
		return flag;
	}

	public void nextLevel(){
    	playVoice(Const.voiceNextlevel);
    	TextView textView = new TextView(mContext);
    	textView.setText("成功过关！你真棒！");
    	textView.setTextSize(24);
    	textView.setTextColor(getResources().getColor(R.color.fontColor));
    	textView.setGravity(Gravity.CENTER);
    	textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    	Builder nextLevel = new Builder(mContext);
    	nextLevel.setView(textView);
    	nextLevel.setPositiveButton("下一关", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				gameView.startGame();
			}
		});
    	nextLevel.setNegativeButton("返回菜单", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
    	nextLevel.setCancelable(false);
    	nextLevel.show();
    }
    
    public void gameOver(){
    	playVoice(Const.voiceGameover);
    	ImageView imgView = new ImageView(mContext);
    	imgView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    	imgView.setBackgroundResource(R.drawable.gameover);
    	Builder gameOver = new Builder(mContext);
    	gameOver.setView(imgView);
    	gameOver.setPositiveButton("重新挑战", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				gameView.initGameInfo();
			}
		});
    	gameOver.setNegativeButton("返回菜单", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
    	gameOver.setCancelable(false);
    	gameOver.show();
    }
    
    @SuppressLint("ResourceAsColor")
	public void showTimerGrade(int grade, int killnum){
    	playVoice(Const.voiceNextlevel);
    	Builder timerGrade = new Builder(mContext);
    	TextView textView = new TextView(mContext);
    	textView.setText("本次"+Const.timeNum+"秒计时\r\n击中："+killnum+"个\r\n得分："+grade+"分");
    	textView.setTextColor(getResources().getColor(R.color.fontColor));
    	textView.setTextSize(24);
    	textView.setGravity(Gravity.CENTER);
    	textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    	timerGrade.setView(textView);
    	timerGrade.setPositiveButton("重新挑战", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				gameView.initGameInfo();
			}
		});
    	timerGrade.setNegativeButton("返回菜单", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
    	timerGrade.setCancelable(false);
    	timerGrade.show();
    }
    
    public void initGameMode(){
    	switch (Const.gameMode) {
		case Const.gameMode_Level://闯关模式
//			Const.backgroundImgResid = R.drawable.maplevel48_001;
			Const.backgroundImgResid = R.drawable.mapds48_001;
			Const.gameArrayStrResid = R.string.didong_level;
			Const.voiceBackground = R.raw.level;
			break;
		case Const.gameMode_Random://随机模式
			Const.backgroundImgResid = R.drawable.mapds48_001;
			Const.gameArrayStrResid = R.string.didong_001;
			Const.voiceBackground = R.raw.random;
			break;
		case Const.gameMode_Timer://计时模式
			Const.voiceBackground = R.raw.time;
			Const.backgroundImgResid = R.drawable.mapds48_001;
			Const.gameArrayStrResid = R.string.didong_level;
			break;
		case Const.gameMode_Super://无尽模式
			Const.voiceBackground = R.raw.supers;
			Const.backgroundImgResid = R.drawable.mapds48_001;
			Const.gameArrayStrResid = R.string.didong_001;
			break;
		}
    }
    
    /**播放背景音乐*/
    public void playBackgroundMusic(){
    	if(Const.backgroundMusicOn && mBgMediaPlayer!=null){
			mBgMediaPlayer.start();
    	}else{
    		if(mBgMediaPlayer.isPlaying()){
    			mBgMediaPlayer.pause();
    		}
    	}
    }
    
//    /**播放无效攻击音效*/
//    public void playHitVoice(){
//    	if(Const.voiceMusicOn && soundIds[0]!=-1){
//    		mSoundPool.play(soundIds[0], 1.0f, 1.0f, 1, 0, 1.0f);
//    	}
//    }
//    
//    /**播放击中音效*/
//    public void playDishuDeadVoice(){
//    	if(Const.voiceMusicOn && soundIds[1]!=-1){
//    		mSoundPool.play(soundIds[1], 1.0f, 1.0f, 1, 0, 1.0f);
//    	}
//    }
    
    /**播放指定音效*/
    public void playVoice(int idx){
    	if(Const.voiceMusicOn && soundIds[idx]!=-1){
    		mSoundPool.play(soundIds[idx], 1.0f, 1.0f, 1, 0, 1.0f);
    	}
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
    	if (this.mBgMediaPlayer != null) {
    		if (this.isMusic){
    			this.mBgMediaPlayer.stop();
    			this.mBgMediaPlayer.release();
    			this.mBgMediaPlayer = null;
    		}
        }
    	super.onDestroy();
    }
}
