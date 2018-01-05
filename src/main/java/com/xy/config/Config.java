package com.xy.config;

import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.xy.controller.MainController;
import com.xy.model.Question;

/**
 * @author xy
 */
public class Config extends JFinalConfig {
    @Override
    public void configConstant(Constants constants) {
        constants.setDevMode(true);
        constants.setEncoding("utf-8");
    }

    @Override
    public void configRoute(Routes routes) {
        routes.add("/test", MainController.class);
    }

    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
        Prop p=PropKit.use("db.properties");
        DruidPlugin druidPlugin=new DruidPlugin(p.get("dbUrl"),p.get("username"),p.get("password"));
        plugins.add(druidPlugin);
        ActiveRecordPlugin activeRecordPlugin=new ActiveRecordPlugin(druidPlugin).setDialect(new MysqlDialect());
        plugins.add(activeRecordPlugin);
        activeRecordPlugin.addMapping("t_question", Question.class);
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {

    }

    @Override
    public void configHandler(Handlers handlers) {

    }
}
