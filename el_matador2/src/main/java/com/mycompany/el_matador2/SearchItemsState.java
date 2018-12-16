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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
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
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.utils.collections.MyCollections;

/**
 *
 * @author h7moreir
 */
class SearchItemsState extends UT2004BotModuleController<UT2004Bot> implements HunterState {
    
    Transition[] transitions;
    
    public SearchItemsState() {
        transitions = new Transition[5];
        
        transitions[0] = new TransitionToHurt();
        transitions[2] = new TransitionToAttack();
        transitions[3] = new TransitionToHurted();
        transitions[1] = new TransitionToBestWeapon();
        transitions[4] = new TransitionToSearch();

    }

    @Override
    public void execute(HunterBot Bot) {
        
        
        
        //log.info("Decision is: ITEMS");
        Bot.getConfig().setName("Hunter [ITEMS]");
        if (Bot.getNavigation().isNavigatingToItem()) return;
        
        List<Item> interesting = new ArrayList<Item>();
        
        // ADD WEAPONS
        for (ItemType itemType : ItemType.Category.WEAPON.getTypes()) {
        	if (!Bot.getWeaponry().hasLoadedWeapon(itemType)) interesting.addAll(Bot.getItems().getSpawnedItems(itemType).values());
        }
        // ADD ARMORS
        for (ItemType itemType : ItemType.Category.ARMOR.getTypes()) {
        	interesting.addAll(Bot.getItems().getSpawnedItems(itemType).values());
        }
        // ADD QUADS
        interesting.addAll(Bot.getItems().getSpawnedItems(UT2004ItemType.U_DAMAGE_PACK).values());
        // ADD HEALTHS
        if (Bot.getInfo().getHealth() < 100) {
            System.out.println ("\n ### Looking for life");
            interesting.addAll(Bot.getItems().getSpawnedItems(UT2004ItemType.HEALTH_PACK).values());
        }
        
        Item item = MyCollections.getRandom(Bot.tabooItems.filter(interesting));
        if (item == null) {
        	Bot.getLog().warning("NO ITEM TO RUN FOR!");
        	if (Bot.getNavigation().isNavigating()) return;
                Bot.getBot().getBotName().setInfo("RANDOM NAV");
        	Bot.getNavigation().navigate(Bot.getNavPoints().getRandomNavPoint());
        } else {
        	Bot.item = item;
        	Bot.getLog().info("RUNNING FOR: " + item.getType().getName());
                Bot.getBot().getBotName().setInfo("ITEM: " + item.getType().getName() + "");
        	Bot.getNavigation().navigate(item); 	
        }  
    }
    
    public Transition[] getTransitions(){
    return this.transitions;
    }
}
