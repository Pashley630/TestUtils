package com.lgt.qa.testng.listener;

import cucumber.api.testng.PickleEventWrapper;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义测试监听器，负责记录测试日志和失败截图
 *
 */
public class TestListener extends TestListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(TestListener.class); 
	@Override
	public void onTestStart(ITestResult result) {
		String methodName = getMethodName(result);
		logger.info("测试方法"+methodName+"执行开始");
	}
	
	@Override
	public void onTestFailure(ITestResult tr) {
		super.onTestFailure(tr);
		String methodName = getMethodName(tr);
		logger.error("测试方法"+methodName+"执行失败");
		takeScreenShot(tr,methodName);
	}
	
	/**
	 * 截图方法，将在target目录下创建screenShot目录存放截屏图片，文件格式为"方法名.时间.png"
	 * @param tr 测试结果
	 * @param methodName 测试方法名
	 */
	private void takeScreenShot(ITestResult tr, String methodName) {
		Object obj = tr.getInstance();
		try {
			Field field = tr.getTestClass().getRealClass().getField("driver");
			field.setAccessible(true);
			Object o = field.get(obj);
			if(o instanceof WebDriver) {
				TakesScreenshot takeScreenShot = (TakesScreenshot)o;
				File file = takeScreenShot.getScreenshotAs(OutputType.FILE);
				File path = new File("target/screenShots");
				if(!path.exists()) {
					path.mkdirs();
				}
				String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
				String filename = methodName+"."+date+".png";
				File screenShot = new File(path,filename);
				
				if(file.renameTo(screenShot)) {
					logger.info("截图成功，保存在"+screenShot.getCanonicalPath());
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException |
				IllegalAccessException | IOException e) {
			logger.warn("未能进行截图，详细原因请查看StackTrace");
			e.printStackTrace();
		} 
	}
	
	/**
	 * 获取测试方法名，由于cucumber自定义了runScenario测试方法，所以无法直接获取真实的方法名，需要进行必要的反射才能获取
	 * @param result 测试结果
	 * @return
	 */
	private String getMethodName(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		if("runScenario".equals(methodName)) {
			Object[] params = result.getParameters();
			if(params.length == 2) {
				Object obj = params[0];
				if(obj instanceof PickleEventWrapper) {
					PickleEventWrapper pickleEventWrapper = (PickleEventWrapper)obj;
					methodName = pickleEventWrapper.toString();
				}
			}
		}
		return methodName;
	}
}
