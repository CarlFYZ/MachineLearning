package demo.genaticalgorithm.bugs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MapOfFood{
	static int sizeOfMap = 128;
	static int maxFood = 255;
	static int[][] foodTable = new int[sizeOfMap][sizeOfMap];

	static int foodTime = 2;
	static int dayCounter = 0;
	static double foodWidth = 0.2;
	static int radius = 5;

	/*
	 * initial the map with food
	 */
	static void initial(){
		for(int i =0; i< 800; i++)
			drawDune((int)(Math.random()*(sizeOfMap+1)),(int)(Math.random()*(sizeOfMap+1)));		
	}

	/*
	 * one day passed
	 */
	static void oneDayPassed(){
		dayCounter ++;
		if(dayCounter >= foodTime){
			drawDune((int)(Math.random()*(sizeOfMap+1)),(int)(Math.random()*(sizeOfMap+1)));
			dayCounter = 0;
		}
	}

	/*
	 * draw the food in map
	 */
	static void drawDune(int x, int y){
		int amountOfFood = 0;
		for (int i = 0-radius; i <= radius ; i++){
			for (int j = 0-radius; j <= radius ; j++){
				double v = Math.round(Math.sqrt(Math.pow(i,2) + Math.pow(j,2)));
				amountOfFood = (int)((radius - Math.round(Math.sqrt(Math.pow(i,2) + Math.pow(j,2)))) * foodWidth * maxFood);

				if (amountOfFood > 0){
					foodTable[(x+i+sizeOfMap)%sizeOfMap][(y+j+sizeOfMap)%sizeOfMap] += amountOfFood;
					if (foodTable[(x+i+sizeOfMap)%sizeOfMap][(y+j+sizeOfMap)%sizeOfMap] > maxFood)
						foodTable[(x+i+sizeOfMap)%sizeOfMap][(y+j+sizeOfMap)%sizeOfMap] = maxFood;
				}
			}
		}

		
	}

	/*
	 * called when paint
	 */
	static void paint(Graphics g){
		for(int i =0; i<sizeOfMap; i++){
			for(int j =0; j<sizeOfMap; j++){
				if (foodTable[i][j]>0){
					g.setColor(new Color(0,foodTable[i][j],0));
					g.drawLine(i,j,i,j);
				}
			}
		}
	}
}
