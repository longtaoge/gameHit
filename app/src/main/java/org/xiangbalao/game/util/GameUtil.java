/**
 * Copyright (C) 2012 Guangzhou JHComn Technologies Ltd.
 *
 * 本代码版权归源码发布者所有，且受到相关的法律保护。
 * 没有经过版权所有者的书面同意，
 * 任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。

 */
package org.xiangbalao.game.util;

import android.annotation.SuppressLint;
import android.content.Context;

import org.xiangbalao.game.common.Const;

/**
 * <p>
 * </p>
 *
 * @author ljmat
 * @date 2012-11-4 下午1:45:45
 *
 */
@SuppressLint("ParserError")
public class GameUtil {

	private static GameUtil gameUtil=null;
	private static Context mContext;
	private static int[][] mapArray = null;
	private static int RESID = 0;
	
	private GameUtil(Context context){mContext = context;}
	
	public static GameUtil getInstansce(Context context){
		if(null == gameUtil){
			gameUtil = new GameUtil(context);
		}
		return gameUtil;
	}
	
	private static int[][] getMapArray(int resid, int rowSize, int colSize){
		String str = "";
		if(RESID==0 || RESID!=resid){
			RESID = resid;
			str = mContext.getResources().getString(resid);
			mapArray = getMapArrayByStr(str, rowSize, colSize);
		}
		return mapArray;
	}
	
	/**
	 * 根据游戏关数加载地图数组
	 * @param level
	 * @return
	 */
	public int[][] loadMapArrayByLevel(int level, int strResid, int rowSize, int colSize){
		return getMapArray(strResid, rowSize, colSize);
		/*switch (level) {
		case 1:
			return getMapArray(strResid, rowSize, colSize);
		case Const.LEVEL_2:
			return getMapArrayByStr(mContext.getResources().getString(strResid), rowSize, colSize);
		default:
			return getMapArrayByStr(mContext.getResources().getString(strResid), rowSize, colSize);
		}*/
	}
	
	/**
	 * 根据字符串生成地图数组
	 * @param str
	 * @return
	 */
	private static int[][] getMapArrayByStr(String str, int rowSize, int colSize){
		mapArray = new int[rowSize][colSize];
		str = str.replace(" ", "");
		String[] arr = str.split(",");
		int temp = 0;
		int max = 0;
		for(int row=0; row<rowSize; row++){
			for(int col=0; col<colSize; col++){
				temp = row*colSize + col;
				mapArray[row][col] = Short.valueOf(arr[temp]);
				max = mapArray[row][col]>max?mapArray[row][col]:max;
			}
		}
		Const.randomMax = max;
		return mapArray;
	}

	public int getDishuNumByLevel(int level){
		int num = 10;
		if(level<=5){
			num = level*10;
		}else if(level<=10){
			num = level*10+5;
		}else if(level<=15){
			num = level*10+10;
		}else if(level<=20){
			num = level*15+15;
		}else {
			num = level*20;
		}
		return num;
	}
	
	/**
	 * @param level
	 * @return
	 *//*
	public List<EnemyData> loadEnemyDataByLevel(int level) {
		List<EnemyData> enemyData = new ArrayList<EnemyData>();
		switch (level) {
		case Const.LEVEL_1:
//			enemyData.add(new EnemyData("盗贼", 20, 2, 1, 200, 10, BitmapUtil.getInstansce(mContext).getImgByResId(R.drawable.daozei40)));
//			enemyData.add(new EnemyData("独眼龙", 20, 2, 1, 200, 15, BitmapUtil.getInstansce(mContext).getImgByResId(R.drawable.spirit001_40)));
//			enemyData.add(new EnemyData("巫婆", 20, 3, 2, 200, 15, BitmapUtil.getInstansce(mContext).getImgByResId(R.drawable.enemy16_40)));
			break;
		case Const.LEVEL_2:
			break;
			
		default:
			break;
		}
		return enemyData;
		
	}
	
	*//**
	 * @param level
	 * @return
	 *//*
	public List<TowerData> loadTowerDataByLevel(int level) {
		List<TowerData> towerData = new ArrayList<TowerData>();
		switch (level) {
		case Const.LEVEL_1:
			towerData.add(new TowerData("15金币炮塔", 2, (int)Const.CURRENT_BLOCK_WIDTH_HEIGHT, 1, 1000, false, 15, 5, 5, 5, 0, 0, true, "15金币炮塔", R.drawable.tower_001_40));
			towerData.add(new TowerData("25金币炮塔", 3, (int)Const.CURRENT_BLOCK_WIDTH_HEIGHT, 1, 1000, false, 15, 5, 5, 5, 0, 0, true, "25金币炮塔", R.drawable.tower_002_40));
		case Const.LEVEL_2:
			
		default:
			break;
		}
		return towerData;
	}

	*//**
	 * @param level
	 * @return
	 *//*
	public List<TowerSpirit> loadTowerSpiritByLevel(int level) {
		List<TowerSpirit> towerSpirit = new ArrayList<TowerSpirit>();
		switch (level) {
		case Const.LEVEL_1:
//			towerSpirit.add(new TowerSpirit("15金币炮塔", 2, (int)Const.CURRENT_BLOCK_WIDTH_HEIGHT, 1, 1000, false, 15, 5, 5, 5, 0, 0, true, "15金币炮塔", R.drawable.tower_001_40));
//			towerSpirit.add(new TowerSpirit("25金币炮塔", 3, (int)Const.CURRENT_BLOCK_WIDTH_HEIGHT, 1, 1000, false, 15, 5, 5, 5, 0, 0, true, "25金币炮塔", R.drawable.tower_002_40));
		case Const.LEVEL_2:
			
		default:
			break;
		}
		return towerSpirit;
	}*/
}
