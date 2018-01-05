package com.xy.spider;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xy.model.Question;
import org.apache.commons.httpclient.Header;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;


public class Spider {
    private static String username=PropKit.use("config.properties").get("username");
    private static String password = PropKit.use("config.properties").get("password");




    public static List<Header> getHeaders() {
        List<Header> list = new ArrayList<Header>();
        list.add(new Header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));
        list.add(new Header("accept-encoding", "gzip, deflate, br"));
        list.add(new Header("accept-language", "zh-CN,zh;q=0.8,en;q=0.6"));
        list.add(new Header("cache - control", "max-age=0"));
        list.add(new Header("upgrade-insecure-requests", "1"));
        list.add(new Header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36"));
        return list;
    }


    public static void gethrefInfo(String examName,int year) throws IOException {
        String classpath = PathKit.getWebRootPath()+"/WEB-INF/classes/";
        System.setProperty("webdriver.chrome.driver", classpath + "chromedriver.exe");
        String loginUrl = "http://www.rkpass.cn/login.jsp?formlogin_back_url=index.html";

        WebDriver diver = new ChromeDriver();

        diver.manage().window().maximize();

        diver.get(loginUrl);

        WebDriverWait wait = new WebDriverWait(diver, 40);


        wait.until((ExpectedCondition<WebElement>) d -> d.findElement(By.id("username_1"))).sendKeys(username);
        wait.until((ExpectedCondition<WebElement>) d -> d.findElement(By.id("password"))).sendKeys(password);
        wait.until((ExpectedCondition<WebElement>) d -> d.findElement(By.xpath(".//*/input[@src='image/denglu.jpg']"))).click();

        for(int i=1;i<76;i++){
            diver.get("http://www.rkpass.cn/tk_timu/6_485_"+i+"_xuanze.html");
            wait.until((ExpectedCondition<WebElement>) d -> d.findElement(By.id("answer_option_user_a"))).click();
            wait.until((ExpectedCondition<WebElement>) d -> d.findElement(By.xpath(".//*/img[@src='http://www.rkpass.cn/image/tk_tjchakanjx.jpg']"))).click();
            Question question = new Question();
            question.set("exam_id",2);
            question.set("number",i);
            StringBuffer imgPath=new StringBuffer("");
            diver.findElements(By.className("shisi_text")).forEach(webElement -> {
                String text = webElement.getText();
                if(!StringUtils.isEmpty(text)&&text.length()>0){
                    char first = text.charAt(0);
                    switch (first) {
                        case 'A':
                            question.set("a", text);
                            break;
                        case 'B':
                            question.set("b", text);
                            break;
                        case 'C':
                            question.set("c", text);
                            break;
                        case 'D':
                            question.set("d", text);
                            break;
                        default:
                            question.set("title", text);
                            break;
                    }
                }

                try {

                    webElement.findElements(By.tagName("img")).forEach(e->{
                        String url=e.getAttribute("src")+";";
                        imgPath.append(url);
                    });

                }catch (Exception e){
                    System.out.println("没有图片!");
                }
            });
            System.out.println("图片地址:"+imgPath.toString());
            question.set("img",imgPath.toString());
            try {
                String answer=diver.findElement(By.xpath("//span[contains(text(),'答案：')]/..")).getText();
                System.out.println(answer);
                if(answer.contains("A")){
                    question.set("answer",1);
                }else  if(answer.contains("B")){
                    question.set("answer",2);
                }else  if(answer.contains("C")){
                    question.set("answer",3);
                }else  if(answer.contains("D")){
                    question.set("answer",4);
                }else{
                    System.out.println("没有匹配到答案:"+answer);
                }
            }catch (Exception e){
                System.out.println("读取答案异常"+e);
                return;
            }


            String passRate=diver.findElement(By.xpath(".//*/span[contains(@class,'lv')]")).getText();
            question.set("pass_rate",Integer.valueOf(passRate.substring(0,passRate.length()-1)));
            Db.save("t_question",question.toRecord());
        }

        diver.quit();
//        diver.close();
    }



}
