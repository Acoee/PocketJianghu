package com.app.jianghu.common;

import java.util.Random;

/**
 * 随机数工具类
 * @author songqy
 * @version 1.0.0
 * @timer 015-04-27
 *
 */
public class RandomUtil {
	//随机码(数字+字母)
	private static String[] strLetterAndDigit = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", 
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", 
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", 
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
	//随机码(数字)
	private static String[] strDigit = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
	//随机数类
	private static Random random = new Random(); 
	
	/**
	 * 生成六位的随机验证码(数字+字母)
	 * @return
	 */
	public static String getRandomLetterAndDigit(){
		int[] nums = new int[6];
		for(int i=0; i<6; i++){
			nums[i] = random.nextInt(strLetterAndDigit.length); //生成6个随机数
		}
		return new StringBuffer().append(strLetterAndDigit[nums[0]]).append(strLetterAndDigit[nums[1]]).append(strLetterAndDigit[nums[2]])
				.append(strLetterAndDigit[nums[3]]).append(strLetterAndDigit[nums[4]]).append(strLetterAndDigit[nums[5]]).toString();
	}
	
	/**
	 * 生成六位的随机验证码（数字）
	 * @return
	 */
	public static String getRandomDigit(){
		int[] nums = new int[6];
		for(int i=0; i<6; i++){
			nums[i] = random.nextInt(strDigit.length); //生成6个随机数
		}
		return new StringBuffer().append(strDigit[nums[0]]).append(strDigit[nums[1]]).append(strDigit[nums[2]])
				.append(strDigit[nums[3]]).append(strDigit[nums[4]]).append(strDigit[nums[5]]).toString();
	}
	
	/**
	 * 生成n位的随机验证码（数字）
	 * @return
	 */
	public static String getRandomDigit(int n){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<n; i++){
			int nums = random.nextInt(10); //生成0到9的随机数
			sb.append(nums);
		}
		return sb.toString();
	}
	
	/**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
	
}
