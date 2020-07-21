package com.tanghs.elasticsearchlab.model;

/**
 * @description:
 * @author: tanghs
 * @date: 2020/5/29 09:02
 * @version:
 */
public class Child extends Parent {
    private String child = "default";

    public Child() {
        super();
        System.out.println("Child构造方法 child = " + child);
    }

    @Override
    public void initGlobalVariables() {
        child = "child";
        System.out.println("initGlobalVariables child = " + child);
    }
}
