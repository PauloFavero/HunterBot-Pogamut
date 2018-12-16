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

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import static java.lang.Math.log;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;

/**
 *
 * @author h7moreir
 */
class AttackState extends UT2004BotModuleController<UT2004Bot> implements HunterState {

    Transition[] transitions;
    
    public AttackState() {
        transitions = new Transition[4];
        transitions[3] = new TransitionToSearch();
        transitions[0] = new TransitionToPursue();
        transitions[1] = new TransitionToHurted();
        transitions[2] = new TransitionToAttack();
    }

    @Override
    public void execute(HunterBot bot) {

        boolean shooting = false;
        double distance = Double.MAX_VALUE;
        //log.info("Decision is: ENGAGE");
        bot.getConfig().setName("Hunter [ATTACK]");
        bot.pursueCount = 0;
        
        // 1) pick new bot.enemy if the old one. has been lost
        if (bot.enemy == null || !bot.enemy.isVisible()) {
            // pick new bot.enemy
            System.out.println ("\n ### bot  is " + bot);
            System.out.println ("\n ### bot enemy " + bot.enemy);
            //System.out.println ("playerrrrr" + players.getNearestVisiblePlayer(players.getVisibleEnemies().values()));
            bot.enemy = bot.getPlayers().getNearestVisiblePlayer(bot.getPlayers().getVisibleEnemies().values());
            if (bot.enemy == null) {
                //log.info("Can't see any enemies... ???");
                return;
            }
        }

        System.out.println ("\n ### visivel "+bot.enemy.isVisible() );
        // 2) stop shooting if bot.enemy is not visible
        
        if (!bot.enemy.isVisible()) {
            if (bot.getInfo().isShooting() || bot.getInfo().isSecondaryShooting()) {
                // stop shooting
                getAct().act(new StopShooting());
            }
            System.out.println ("\n ### bot enemy is NOT visible");
            bot.runningToPlayer = false;
        } else {
            // 2) or shoot on bot.enemy if it is visible
            System.out.println ("distancia1 " + bot.getInfo());
            distance = bot.getInfo().getLocation().getDistance(bot.enemy.getLocation());
            System.out.println ("distancia " + distance);
            if (bot.getShoot().shoot(bot.getWeaponPrefs(), bot.enemy) != null) {
               // log.info("Shooting at bot.enemy!!!");
                shooting = true;
            }
        }
        // 3) if bot.enemy is far or not visible - run to him
        int decentDistance = Math.round(random.nextFloat() * 800) + 200;
      //  log.info("decentDistance " + decentDistance.toString());
        if (!bot.enemy.isVisible() || !shooting || decentDistance < distance) {
            if (!bot.runningToPlayer) {
                System.out.println ("\n ### Not runing to player !!!");
                System.out.println ("\n ### navigation " + bot.getNavigation());
                bot.getNavigation().navigate(bot.enemy);
                bot.runningToPlayer = true;
            }
        } else {
            bot.runningToPlayer = false;
            bot.getNavigation().stopNavigation();
        }

        bot.item = null;
    }
     public Transition[] getTransitions(){
    return this.transitions;
    }
}
