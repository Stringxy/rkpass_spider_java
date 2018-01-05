package com.xy.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.xy.spider.Spider;

import java.io.IOException;

/**
 * @author xy
 */
public class MainController extends Controller {
    @Before(Tx.class)
    public void index(){
        try {
            Spider.gethrefInfo("test",1);
            renderJson("{result:'成功'}");
        } catch (IOException e) {
            e.printStackTrace();
            renderJson("{result:'失败'}");
        }
    }
}
