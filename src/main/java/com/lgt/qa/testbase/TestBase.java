package com.lgt.qa.testbase;


import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.Listeners;

/**
 * TestBase类,用于被继承，默认报告保存位置为target/cucumber-report，
 * html报告在html子目录下，json报告在json目录下，便于lazyall报告调用
 *
 */
@CucumberOptions(
//		features= {"classpath:test1.feature"},
		monochrome=true,
		plugin= {"pretty","html:target/cucumber-report/html","json:target/cucumber-report/json/cucumber.json"}
//		glue= {"com.ssic.qa.webtest"}
		)
@Listeners({com.lgt.qa.testng.listener.TestListener.class})
public class TestBase extends AbstractTestNGCucumberTests {
}
