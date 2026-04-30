package com.simi.marvel.hero.app;

import com.simi.marvel.hero.SuperHero;
import com.simi.marvel.hero.impl.CaptainAmerica;
import com.simi.marvel.hero.impl.Developer;
import com.simi.marvel.hero.impl.IronMan;
import com.simi.marvel.hero.impl.SpiderMan;

public class MarvelHeroDemo {

    public static void main(String[] args) {

        System.out.println(SuperHero.UNIVERSE_NAME);

        SuperHero ironMan = new IronMan();
        invokeSuperHero(ironMan);

        SuperHero spiderMan = new SpiderMan();
        invokeSuperHero(spiderMan);

        SuperHero captainAmerica = new CaptainAmerica();
        invokeSuperHero(captainAmerica);

        Developer developer = new Developer();
        developer.walk();
    }

    private static void invokeSuperHero(SuperHero superHero) {
        System.out.println(superHero.usePower());
        System.out.println(superHero.stopVillain('N'));
    }


}
