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
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

/**
 *
 * @author h7moreir
 */
public class HurtedState extends UT2004BotModuleController<UT2004Bot> implements HunterState {
    
    Transition[] transitions;
    
    public HurtedState(){
        transitions = new Transition[4];
        
        transitions[3] = new TransitionToSearch();
        transitions[1] = new TransitionToAttack();
        transitions[0] = new TransitionToHurt();
        transitions[2] = new TransitionToHurted();
    }
    
    @Override
    public void execute(HunterBot Bot) {
        Bot.getConfig().setName("Hunter [HURTED]");
        
        Item item = Bot.getItems().getPathNearestSpawnedItem(ItemType.Category.HEALTH);
        if (item == null) {
        	Bot.getLog().warning("NO HEALTH ITEM TO RUN TO => ITEMS");
        	if (Bot.getNavigation().isNavigating()) return;
                Bot.getBot().getBotName().setInfo("RANDOM NAV");
        	Bot.getNavigation().navigate(Bot.getNavPoints().getRandomNavPoint());
        } else {
                Bot.getBot().getBotName().setInfo("MEDKIT");
        	Bot.getNavigation().navigate(item);
        	Bot.item = item;
        }
    }

    @Override
    public Transition[] getTransitions() {
        return this.transitions;
    }
    
}
