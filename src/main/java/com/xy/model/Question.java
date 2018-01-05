package com.xy.model;

import com.jfinal.plugin.activerecord.Model;

import java.math.BigDecimal;

/**
 * @author xy
 */
public class Question extends Model<Question>{
    public static final Question dao=new Question().dao();
}
