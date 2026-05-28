package cmn.util;

import java.util.Date;

/**
 * 时间操作工具类
 * @author chenxb
 *
 */
public class DateUtil extends cn.hutool.core.date.DateUtil{

	public static String formatMs(Long millisecond) {
		if(millisecond < 1000) {
			return format(new Date(millisecond), "S毫秒");
		}else if(millisecond < 60 * 1000L) {
			return format(new Date(millisecond), "s.SSS秒");
		}else if(millisecond < 60 * 60 * 1000L) {
			return format(new Date(millisecond), "m分s.SSS秒");
		}else if(millisecond < 24 * 60 * 60 * 1000L) {
			long hours = millisecond / (60 * 60 * 1000L); 
			long millisecond2 = millisecond - hours * (60 * 60 * 1000L);
			return hours+"时"+format(new Date(millisecond2), "m分s.SSS秒");
		}else if(millisecond < 30 * 24 * 60 * 60 * 1000L) {
			long days = millisecond / (24 * 60 * 60 * 1000L); 
			long millisecond2 = millisecond - days * (24 * 60 * 60 * 1000L);
			long hours = millisecond2 / (60 * 60 * 1000L); 
			long millisecond3 = millisecond2 - hours * (60 * 60 * 1000L);
			if(hours > 0) {
				return days + "天"+hours+"时"+format(new Date(millisecond3), "m分s.SSS秒");
			}else {
				return days + "天"+format(new Date(millisecond3), "m分s.SSS秒");
			}
		}else if(millisecond < 365 * 24 * 60 * 60 * 1000L) {
			long days = millisecond / (24 * 60 * 60 * 1000L); 
			long millisecond2 = millisecond - days * (24 * 60 * 60 * 1000L);
			long hours = millisecond2 / (60 * 60 * 1000L); 
			long millisecond3 = millisecond2 - hours * (60 * 60 * 1000L);
			if(hours > 0) {
				return days + "天"+hours+"时"+format(new Date(millisecond3), "m分s.SSS秒");
			}else {
				return days + "天"+format(new Date(millisecond3), "m分s.SSS秒");
			}
		}else{
			return format(new Date(millisecond), "yyyy-MM-dd hh:mm:ss.SSS");
		}
	}
	
	public static void main(String[] args) {
		System.out.println(formatMs(31L));
		System.out.println(formatMs(310L));
		System.out.println(formatMs(6310L));
		System.out.println(formatMs(346031L));
		System.out.println(formatMs(346310L+60 * 60 * 1000L));
		System.out.println(formatMs(346310L+24*60 * 60 * 1000L));
		System.out.println(formatMs(346310L+30*24*60 * 60 * 1000L));
		System.out.println(formatMs(346310L+12*30*24*60 * 60 * 1000L));
		System.out.println(formatMs(System.currentTimeMillis()));
	}
}
