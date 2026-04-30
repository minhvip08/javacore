package com.simi.marvel.hero.impl;

import com.simi.marvel.hero.SuperHero;

public class CaptainAmerica implements SuperHero {


    @Override
    public String usePower() {
        return "CaptainAmerica using his power";
    }

    /**
     * If Y received kill the villain
     * If N received stop the villain
     *
     * @param c indicates Y or N
     * @return - Returns status
     */
    @Override
    public String stopVillain(char c) {
        if(c=='Y') {
            return "CaptainAmerica killed the Villain";
        } else {
            return "CaptainAmerica stopped the Villain";
        }
    }
}
