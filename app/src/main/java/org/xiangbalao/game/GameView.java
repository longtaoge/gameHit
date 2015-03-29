/**
 * Copyright (C) 2013 
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 */
package org.xiangbalao.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import org.xiangbalao.game.anima.UpDownAnima;
import org.xiangbalao.game.common.Const;
import org.xiangbalao.game.util.BitmapUtil;
import org.xiangbalao.game.util.GameUtil;
import org.xiangbalao.gamehit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * </p>
 *
 *
 * @date 2013-1-4 下午2:48:17
 *
 */
public class GameView extends View implements Runnable{

	public MainActivity mContext;
	public boolean running;
	public UpDownAnima uda;
	public List<UpDownAnima> udaList = new ArrayList<UpDownAnima>();
	private long sleepTime = 50;
	private long showDishuTime = 500;
	private int touchX;
	private int touchY;
	private Bitmap imgBackground;
	private Bitmap imgDishu;
	private Bitmap imgDishu2;
	private Bitmap imgHit;
	private Bitmap imgDidong;
	private int[][] didongArray;
//	private int rowSize = 20;
//	private int colSize = 12;
	private int rowSize = 17;
	private int colSize = 10;
	private int count;
	private int dishuCount;
	private int curDishuCount;
	private int curLevel = 0;
	private int runAwayNum=0;
	private int killNum=0;
	private int totalDishuNum;
	private Bitmap imgLevel;
	private Bitmap imgNumber;
	private Bitmap imgGo;
	private Bitmap imgSmallDishu40;
	private Bitmap imgX;
	private Bitmap imgMuchui;
	private boolean hitting;
//	private HammerAnima hammer;
	public Lock lock;
	private Bitmap imgMe;
	private int score;
	private Bitmap imgScore;
	private boolean linkHit;
	private int linkHitScore=-5;
	private Bitmap imgLinkHit;
	private Bitmap imgTimer;
	private int timeCount;
	private int timeNum;
	private int hpNum=-1;
	private Bitmap imgHp;
	private Bitmap imgMusic;
	
	/**
	 * @param context
	 */
	public GameView(MainActivity context) {
		super(context);
		mContext = context;
		running = true;
		mContext.isPause = false;
		lock = new ReentrantLock();
		initBitmap();
		initGameInfo();
		playBackMusic();
//		hammer = new HammerAnima(imgMuchui, imgHit, 300, 700);
		new Thread(this).start();
	}
	
	/**
	 * 
	 */
	private void playBackMusic() {
		mContext.playBackgroundMusic();
	}

	/**
	 * 
	 */
	public void initGameInfo() {
		lock.lock();
		udaList.clear();
		lock.unlock();
		runAwayNum = 0;
		curLevel = 0;
		score = 0;
		linkHit = false;
		linkHitScore = -5;
		if(Const.gameMode==Const.gameMode_Timer){
			timeNum = Const.timeNum;
			killNum = 0;
		}
		if(Const.gameMode==Const.gameMode_Random){
			hpNum = Const.hpNum;
			killNum = 0;
		}
		if(Const.gameMode==Const.gameMode_Super){
			hpNum = 10;
			killNum = 0;
		}
		startGame();
		mContext.isPause = false;
	}

	public void gameOver(){
		mContext.isPause = true;
		mContext.gameOver();
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(imgBackground, 0, 0, null);
		topMenu(canvas);
		drawDisong(canvas);
		playAnima(canvas);
//		drawHammer(canvas);
		drawLinkHitInfo(canvas);
	}

	/**
	 * @param canvas
	 */
	private void drawLinkHitInfo(Canvas canvas) {
		if(linkHit && linkHitScore>=5){
			linkHit = false;
			int marginLeft = touchX+imgLinkHit.getWidth();
			int marginTop = touchY-imgLinkHit.getHeight();
			canvas.drawBitmap(imgLinkHit, marginLeft, marginTop, null);
			marginLeft += imgLinkHit.getWidth();
			canvas.drawBitmap(imgX, marginLeft, marginTop+imgX.getHeight()/4, null);
			marginLeft += imgX.getWidth();
			drawNum(canvas, linkHitScore/5, marginLeft, marginTop+2);
		}
//		if(linkHitScore==10){
//			gameOver();
//		}
	}

	/**
	 * @param canvas
	 */
	private void drawDisong(Canvas canvas) {
		if(Const.gameMode==Const.gameMode_Level){
			for(int row=0; row<rowSize; row++){
				for(int col=0; col<colSize; col++){
					if(didongArray[row][col]!=0){
						canvas.drawBitmap(imgDidong, (int) (col*Const.CURRENT_BLOCK_WIDTH), (int) (row*Const.CURRENT_BLOCK_HEIGHT), null);
					}
				}
			}
		}
	}

	/**
	 * @param canvas
	 */
//	private void drawHammer(Canvas canvas) {
//		if(hitting){
//			hammer.drawHammer(canvas, touchX, touchY);
//			hammer.setTemp(hammer.getTemp()+1);
//			if(hammer.getTemp()>=5){
//				hitting = false;
//				hammer.setTemp(0);
//			}
//		}
//	}

	/**
	 * @param canvas
	 */
	private void topMenu(Canvas canvas) {
		int marginLeft = 5;
		musicInfo(canvas, marginLeft);
		if(Const.gameMode==Const.gameMode_Level){
			marginLeft = levelInfo(canvas, marginLeft);
		}
		marginLeft = dishuInfo(canvas, marginLeft);
		if(Const.gameMode!=Const.gameMode_Level){
			marginLeft = scoreInfo(canvas, marginLeft);
		}
		if(Const.gameMode==Const.gameMode_Timer){
			marginLeft = timeInfo(canvas, marginLeft);
		}
		if(Const.gameMode!=Const.gameMode_Timer){
			marginLeft = hpInfo(canvas, marginLeft);
			if(hpNum<=0){
				gameOver();
			}
		}
		
	}

	/**
	 * @param canvas
	 * @param marginLeft
	 */
	private void musicInfo(Canvas canvas, int marginLeft) {
		Bitmap tem = null;
		if(Const.backgroundMusicOn){
			tem = Bitmap.createBitmap(imgMusic, 0, 0, imgMusic.getWidth()/2, imgMusic.getHeight());
		}else{
			tem = Bitmap.createBitmap(imgMusic, imgMusic.getWidth()/2, 0, imgMusic.getWidth()/2, imgMusic.getHeight());
		}
		canvas.drawBitmap(tem, Const.CURRENT_SCREEN_WIDTH-imgMusic.getWidth()*2/3, tem.getHeight(), null);
	}

	/**
	 * @param canvas
	 * @param marginLeft
	 * @return
	 */
	private int hpInfo(Canvas canvas, int marginLeft) {
		marginLeft += 10;
		Bitmap hp = Bitmap.createBitmap(imgHp, 0, 0, imgHp.getWidth()/3, imgHp.getHeight());
		canvas.drawBitmap(hp, marginLeft, 5, null);
		marginLeft += hp.getWidth()+3;
		marginLeft = drawNum(canvas, hpNum, marginLeft, 7);
		return marginLeft;
	}

	/**
	 * @param canvas
	 * @param marginLeft
	 * @return
	 */
	private int timeInfo(Canvas canvas, int marginLeft) {
		if(timeNum<=0){
			mContext.isPause=true;
			mContext.showTimerGrade(score, killNum);
		}
		marginLeft += 10;
		canvas.drawBitmap(imgTimer, marginLeft, 5, null);
		marginLeft += imgTimer.getWidth();
		drawNum(canvas, timeNum, marginLeft, 7);
		return marginLeft;
	}

	/**
	 * @param canvas
	 * @param marginLeft
	 */
	private int scoreInfo(Canvas canvas, int marginLeft) {
		marginLeft += 10;
		canvas.drawBitmap(imgScore, marginLeft, 5, null);
		marginLeft += imgScore.getWidth() + 3;
		marginLeft = drawNum(canvas, score, marginLeft, 7);
		return marginLeft;
	}

	/**
	 * @param canvas
	 */
	private int dishuInfo(Canvas canvas, int marginLeft) {
		marginLeft += 10;
		canvas.drawBitmap(imgSmallDishu40, marginLeft, 5, null);
		marginLeft += imgSmallDishu40.getWidth() + 2;
		canvas.drawBitmap(imgX, marginLeft, 15, null);
		marginLeft += imgX.getWidth() + 2;
		if(Const.gameMode==Const.gameMode_Level){
			marginLeft = drawNum(canvas, totalDishuNum-killNum, marginLeft, 7);
		}else{
			marginLeft = drawNum(canvas, killNum, marginLeft, 7);
		}
		return marginLeft;
	}

	/**
	 * @param canvas
	 */
	private int levelInfo(Canvas canvas, int marginLeft) {
		Bitmap di = Bitmap.createBitmap(imgLevel, 0, 0, imgLevel.getWidth()/2, imgLevel.getHeight());
		Bitmap guan = Bitmap.createBitmap(imgLevel, imgLevel.getWidth()/2, 0, imgLevel.getWidth()/2, imgLevel.getHeight());
		int marginTop = 5;
		canvas.drawBitmap(di, marginLeft, marginTop, null);
		marginLeft += di.getWidth() + 5;
		marginLeft = drawNum(canvas, curLevel, marginLeft, 7);
		canvas.drawBitmap(guan, marginLeft, marginTop, null);
		marginLeft += guan.getWidth();
		return marginLeft;
	}

	private int drawNum(Canvas canvas, int num, int marginLeft, int marginTop){
		String numStr = String.valueOf(num<0?0:num);
		Bitmap imgNum;
		for(int i=0; i<numStr.length(); i++){
			imgNum = getNumberImg(Integer.valueOf(numStr.substring(i, i+1)));
			canvas.drawBitmap(imgNum, marginLeft, marginTop, null);
			marginLeft += imgNum.getWidth() + 2;
		}
		return marginLeft;
	}
	
	public void playAnima(Canvas canvas){
		lock.lock();
		for(UpDownAnima uda : udaList){
			uda.drawFrame(canvas, null);
		}
		List<UpDownAnima> rm = new ArrayList<UpDownAnima>();
		for(UpDownAnima uda : udaList){
			if(uda.isHit()){
				rm.add(uda);
			}else if(!uda.isHitable()){
				rm.add(uda);
				if(uda.isAddFlag()){
					runAwayNum++;
					totalDishuNum--;
					if(Const.gameMode!=Const.gameMode_Super){
						hpNum--;
					}
				}
			}
		}
		udaList.removeAll(rm);
		curDishuCount = udaList.size();
		lock.unlock();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (running) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!mContext.isPause){
				postInvalidate();
				count++;
				if(count>=showDishuTime/sleepTime){//60*50=3000毫秒;即每秒随机出一个地鼠
					genDishu();
					count=0;
				}
				timeCount++;
				if(timeCount*sleepTime==1000){
					timeCount = 0;
					timeNum--;
				}
			}
		}
	}

	public void startGame(){
		curLevel++;
		if(Const.gameMode==Const.gameMode_Level){
			lock.lock();
			udaList.clear();
			lock.unlock();
			linkHitScore = -5;
			genDishu();
			killNum = 0;
			hpNum = 2*curLevel>10?10:2*curLevel;
		}
		totalDishuNum = GameUtil.getInstansce(getContext()).getDishuNumByLevel(curLevel);
		mContext.isPause = false;
	}
	
	/**
	 * 
	 */
	private void nextLevel() {
		mContext.isPause = true;
		if(Const.gameMode==Const.gameMode_Level){
			mContext.nextLevel();
		}else{
			startGame();
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN://按下时
			touchX = (int) event.getX();
			touchY = (int) event.getY();
			hitting = true;
			isHit();
			break;
		case MotionEvent.ACTION_MOVE://移动时
//			hitting = false;
			break;
		case MotionEvent.ACTION_UP://抬起时
//			hitting = false;
			break;
		}
		return true;
	}

	/**
	 * 
	 */
	private void isHit() {
		if(touchX>Const.CURRENT_SCREEN_WIDTH-imgMusic.getWidth()*2/3-5 && touchX<Const.CURRENT_SCREEN_WIDTH-imgMusic.getWidth()/4+5 && touchY>imgMusic.getHeight()-5 && touchY<imgMusic.getHeight()*2+5){
			if(Const.backgroundMusicOn){
				Const.backgroundMusicOn = false;
				Const.voiceMusicOn = false;
			}else{
				Const.backgroundMusicOn = true;
				Const.voiceMusicOn = true;
			}
			mContext.playBackgroundMusic();
			return ;
		}
		mContext.playVoice(Const.voiceShoot);
		lock.lock();
		for(UpDownAnima uda : udaList){
			if(hitting && uda.isHit(touchX, touchY)){
				hitting = false;
				score+=uda.getScore();
				if(uda.isAddFlag()){
					linkHit = true;
					linkHitScore += 5;
					mContext.playVoice(Const.voiceHit);
					killNum++;
					if(linkHitScore/5>65){
						score += 10;
					}else if(linkHitScore/5>45){
						score += 8;
					}else if(linkHitScore/5>30){
						score += 6;
					}else if(linkHitScore/5>15){
						score += 4;
					}else if(linkHitScore/5>5){
						score += 2;
					}else if(linkHitScore>0){
						score += 1;
					}
					if((totalDishuNum-killNum)<=0){
						nextLevel();
					}
				}else{
					mContext.playVoice(Const.voiceNo);
					linkHit = false;
					linkHitScore = -5;
					if(Const.gameMode!=Const.gameMode_Timer){
						hpNum--;
					}
				}
			}
		}
		lock.unlock();
		if(Const.gameMode==Const.gameMode_Timer){
			genDishu();
		}
	}
	
	private Bitmap getNumberImg(int num){
		int numW = imgNumber.getWidth()/10;
		return Bitmap.createBitmap(imgNumber, num*numW, 0, numW, imgNumber.getHeight());
	}
	
	private boolean randomShow(int row, int col){
		double random = Math.random();
		boolean flag = false;
		if(0.9>random){
			flag = true;
		}
		lock.lock();
		for(UpDownAnima uda : udaList){
			if(uda.getRow()==row && uda.getCol()==col){
				flag = false;
			}
		}
		lock.unlock();
//		if(dishuCount>=totalDishuNum){
//			flag = false;
//		}
//		dishuCount += runAwayNum;
//		runAwayNum = 0;
		return flag;
	}
	
	private void genDishu(){
		didongArray = GameUtil.getInstansce(getContext()).loadMapArrayByLevel(1, Const.gameArrayStrResid, rowSize, colSize);
		int rowSize = didongArray.length;
		int colSize = didongArray[0].length;
		int randomDishu = 0;
		Random random = new Random();
		randomDishu = random.nextInt(Const.randomMax);
		randomDishu = randomDishu==0?1:randomDishu;
		Random rand = new Random();
		double randShow = 0.0;
		lock.lock();
		for(int row=0; row<rowSize; row++){
			for(int col=0; col<colSize; col++){
				if(randomDishu==didongArray[row][col]){
//					System.out.println("============="+randomDishu);
					if(randomShow(row, col)){
						randShow = rand.nextDouble();
						if(randShow>0.6){
							uda = new UpDownAnima(imgDishu, imgHit, imgDidong, 100, 8, (int) (col*Const.CURRENT_BLOCK_WIDTH), (int) (row*Const.CURRENT_BLOCK_HEIGHT), true);
						}else if(randShow>0.2){
							uda = new UpDownAnima(imgDishu2, imgHit, imgDidong, 100, 8, (int) (col*Const.CURRENT_BLOCK_WIDTH), (int) (row*Const.CURRENT_BLOCK_HEIGHT), true);
						}else{
							uda = new UpDownAnima(imgMe, imgHit, imgDidong, 100, 8, (int) (col*Const.CURRENT_BLOCK_WIDTH), (int) (row*Const.CURRENT_BLOCK_HEIGHT), false);
						}
						uda.setRow(row);
						uda.setCol(col);
						dishuCount++;
						udaList.add(uda);
					}
				}
			}
		}
		lock.unlock();
	}
	
	private void initBitmap(){
		imgBackground = BitmapUtil.getInstansce(getContext()).getImgByResId(Const.backgroundImgResid);
		imgDishu = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.dishu_40x60);
		imgDishu2 = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.dishu2_40x60);
		imgHit = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.xuanyun_64);
		imgDidong = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.dd_dc);
		imgLevel = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.level69x35);
		imgNumber = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.num);
		imgSmallDishu40 = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.smalldishu_40);
		imgX = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.x);
		imgMuchui = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.muchui_002);
		imgMe = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.me_48);
		imgScore = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.score);
		imgLinkHit = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.linkhit_69x35);
		imgTimer = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.timer);
		imgHp = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.hp);
		imgMusic = BitmapUtil.getInstansce(getContext()).getImgByResId(R.drawable.music_96x48);
		
//		System.out.println("============bgw>"+imgBackground.getWidth());
//		System.out.println("============bgh>"+imgBackground.getHeight());
	}
	
}
