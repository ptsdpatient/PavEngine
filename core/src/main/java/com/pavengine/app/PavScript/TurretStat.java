package com.pavengine.app.PavScript;

import com.pavengine.app.PavEngine;

public class TurretStat {
    public float value;
    public float baseValue;
    public float increment;
    public float cost;
    public boolean inverse;     // if true, higher level means lower value

    public TurretStat(float baseValue, float increment, float cost) {
        this(baseValue, increment, cost, false);
    }

    public TurretStat(float baseValue, float increment, float cost, boolean inverse) {
        this.baseValue = baseValue;
        this.value = baseValue;
        this.increment = increment;

        this.cost = cost;
        this.inverse = inverse;
    }

    public void upgrade(int upgradeIndex) {

        value = baseValue + increment * upgradeIndex;

        if (value < 0) value = 0;
        PavEngine.credits -= cost;
    }
}

