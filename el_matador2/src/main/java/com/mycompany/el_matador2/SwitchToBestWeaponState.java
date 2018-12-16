/*
 * Copyright (C) 2018 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycompany.el_matador2;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import java.util.Random;


/**
 *
 * @author h7moreir
 */
public class SwitchToBestWeaponState extends UT2004BotModuleController<UT2004Bot> implements HunterState{
    Transition[] transitions;
    double distance = Double.MAX_VALUE;
    
    public SwitchToBestWeaponState(){
        
        transitions = new Transition[1];
        
        transitions[0] = new TransitionToAttack();
    }
    
    @Override
    public void execute(HunterBot Bot) {
       /* Bot.enemy = Bot.getPlayers().getNearestVisiblePlayer(Bot.getPlayers().getVisibleEnemies().values());
        if(Bot.enemy != null){
             distance = Bot.getInfo().getLocation().getDistance(Bot.enemy.getLocation());
            System.out.println("Distance " + distance);
        }
        if(Bot.enemy != null && distance >= 1000){
            if(Bot.getWeaponry().getRangedWeapons() != null){
                Random r = new Random();  
            System.out.println("Current weapon " + Bot.getWeaponry().getCurrentWeapon());
            Bot.getWeaponry().changeWeapon(Bot.getWeaponry().getRangedWeapons().get(r.nextInt(Bot.getWeaponry().getRangedWeapons().size())));
            System.out.println("Switch Range weapon " + Bot.getWeaponry().getCurrentWeapon());
            }
        }else if(Bot.enemy != null && distance <= 500)
        {
            if(Bot.getWeaponry().getMeleeWeapons() != null){
                Random r = new Random();  
                
            System.out.println("Current weapon " + Bot.getWeaponry().getCurrentWeapon());
            Bot.getWeaponry().changeWeapon(Bot.getWeaponry().getMeleeWeapons().get(r.nextInt(Bot.getWeaponry().getMeleeWeapons().size())));
            System.out.println("Switch Meele weapon " + Bot.getWeaponry().getCurrentWeapon());
            }
        }
              */
    }
    
    public Transition[] getTransitions(){
        return this.transitions;
    }


}
