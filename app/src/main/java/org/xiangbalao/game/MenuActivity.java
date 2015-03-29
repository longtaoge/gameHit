/**
 * Copyright (C) 2013 Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 */
package org.xiangbalao.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.xiangbalao.game.common.Const;
import org.xiangbalao.game.util.MUtils;
import org.xiangbalao.gamehit.R;

/**
 * <p>
 * </p>
 *
 *
 * @date 2013-1-10 上午1:43:44
 *
 */
public class MenuActivity extends Activity {

	private Context mContext;
	private MediaPlayer mBgMediaPlayer;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏
		
		init();
		
		setContentView(R.layout.activity_main);
		Button btnLevel = (Button) findViewById(R.id.btnGameLevel);
		btnLevel.setOnClickListener(lisn);
		Button btnRandom = (Button) findViewById(R.id.btnGameRandom);
		btnRandom.setOnClickListener(lisn);
		Button btnTimer = (Button) findViewById(R.id.btnGameTime);
		btnTimer.setOnClickListener(lisn);
		Button btnSuper = (Button) findViewById(R.id.btnGameSuper);
		btnSuper.setOnClickListener(lisn);
		Button btnInfo = (Button) findViewById(R.id.btnGameInfo);
		btnInfo.setOnClickListener(lisn);
	}

	View.OnClickListener lisn = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.btnGameInfo){
//				Toast.makeText(mContext, "游戏介绍页面", Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setInverseBackgroundForced(true);
				TextView customTitleView = new TextView(mContext);
				customTitleView.setText("游戏说明");
				customTitleView.setTextSize(24);
				customTitleView.setGravity(Gravity.CENTER);
				customTitleView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				customTitleView.setTextColor(getResources().getColor(R.color.fontColor));
				builder.setCustomTitle(customTitleView);
				builder.setMessage("木木是生活在丛林里的庄主，一次外出归来，发现有两个不速之客正在他辛辛苦苦经营的庄园里搞破坏！！\r\n偶滴神啊~~~\r\n难道木木辛辛苦苦经营的庄园就这么被毁了吗？\r\n勇敢的少年还在等什么？\r\n快来加入守卫庄园的队伍中吧！");
			    builder.setPositiveButton("确定", null);
			    builder.show();
				return;
			}
			if(authController(v.getId())){
				Intent intent = new Intent(mContext, MainActivity.class);
				Bundle bundle = new Bundle();
				switch (v.getId()) {
				case R.id.btnGameLevel:
					Const.gameMode=Const.gameMode_Level;//闯关模式
					break;
				case R.id.btnGameRandom:
					Const.gameMode=Const.gameMode_Random;//随机模式
					Const.hpNum = 10;
					break;
				case R.id.btnGameTime:
					Const.gameMode=Const.gameMode_Timer;//计时模式
					showSelTime(intent);
					break;
				case R.id.btnGameSuper:
					Const.gameMode=Const.gameMode_Super;//无尽模式
					break;
				}
				if(v.getId()!=R.id.btnGameTime){
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		}

	};
	private void showSelTime(final Intent intent) {
		TextView textView = new TextView(mContext);
    	textView.setText("请选择挑战时间");
    	textView.setTextSize(24);
    	textView.setGravity(Gravity.CENTER);
    	textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    	textView.setTextColor(getResources().getColor(R.color.fontColor));
		new AlertDialog.Builder(mContext)
		.setView(textView)
		.setPositiveButton("30秒", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Const.timeNum = 30;
				startActivity(intent);
			}})
		.setNeutralButton("45秒", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Const.timeNum = 45;
				startActivity(intent);
			}})
		.setNegativeButton("60秒", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Const.timeNum = 60;
				startActivity(intent);
			}})
		.show();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		mContext = MenuActivity.this;
		mBgMediaPlayer = MediaPlayer.create(mContext, R.raw.background);
		mBgMediaPlayer.setLooping(true);//循环
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    	Const.CURRENT_SCREEN_WIDTH = mDisplayMetrics.widthPixels;
    	Const.CURRENT_SCREEN_HEIGHT = mDisplayMetrics.heightPixels;
    	Const.CURREN_SCALE = Const.CURRENT_SCREEN_WIDTH/Const.DEF_SCREEN_WIDTH;
    	Const.CURREN_WIDTH_SCALE = Const.CURRENT_SCREEN_WIDTH/Const.DEF_SCREEN_WIDTH;
    	Const.CURREN_HEIGHT_SCALE = Const.CURRENT_SCREEN_HEIGHT/Const.DEF_SCREEN_HEIGHT;
    	Const.CURRENT_BLOCK_WIDTH_HEIGHT = Const.CURREN_SCALE*Const.DEF_BLOCK_WIDTH_HEIGHT;
    	Const.CURRENT_BLOCK_WIDTH = Const.CURREN_WIDTH_SCALE*Const.DEF_BLOCK_WIDTH_HEIGHT;
    	Const.CURRENT_BLOCK_HEIGHT = Const.CURREN_HEIGHT_SCALE*Const.DEF_BLOCK_WIDTH_HEIGHT;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			existGame();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void existGame(){
		TextView textView = new TextView(mContext);
    	textView.setText("要退出游戏吗？");
    	textView.setTextSize(24);
    	textView.setGravity(Gravity.CENTER);
    	textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    	textView.setTextColor(getResources().getColor(R.color.fontColor));
		new AlertDialog.Builder(mContext)
			.setCustomTitle(textView)
			.setPositiveButton("否", null)
			.setNegativeButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.show();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		adMethoe();
		if(Const.backgroundMusicOn && mBgMediaPlayer!=null && !mBgMediaPlayer.isPlaying()){
			mBgMediaPlayer.start();
		}
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		if (this.mBgMediaPlayer != null && this.mBgMediaPlayer.isPlaying()) {
			this.mBgMediaPlayer.pause();
        }
		super.onPause();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if (this.mBgMediaPlayer != null) {
			this.mBgMediaPlayer.stop();
			this.mBgMediaPlayer.release();
			this.mBgMediaPlayer = null;
        }
		super.onDestroy();
	}
	
	/**
	 * 
	 */
	private void adMethoe() {
		MUtils.getInstance(mContext);
		MUtils.getPush();
    	MUtils.getInstance(mContext);
		MUtils.showRight();
    	MUtils.getInstance(mContext);
		MUtils.showTop();
	}

//---------------------以下为权限控制代码---------------------
	private boolean authController(int freeMenu){
		if(freeMenu==R.id.btnGameLevel){
			return true;
		}
//		Toast.makeText(mContext, "有权限开始游戏", Toast.LENGTH_SHORT).show();
		return true;
	}
	
	private void saveOrUpdateAuth(int menu){
		switch (menu) {
		case R.id.btnGameLevel:
			break;
		case R.id.btnGameRandom:
			break;
		case R.id.btnGameTime:
			break;
		case R.id.btnGameSuper:
			break;
		case R.id.btnGameInfo:
			break;
		}
	}
	
	private boolean getAuth(int menu){
		boolean flag = false;
		
		return flag;
	}
	
}
