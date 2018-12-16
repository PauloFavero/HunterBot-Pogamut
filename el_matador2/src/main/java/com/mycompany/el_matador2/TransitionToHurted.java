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

/**
 *
 * @author h7moreir
 */
public class TransitionToHurted extends UT2004BotModuleController<UT2004Bot> implements Transition {

    @Override
    public HunterState transition(HunterBot Bot) {

        if(Bot.shouldCollectHealth && Bot.getInfo().getHealth() < 100){
            return new HurtedState();
        }
        else return null;
    }
    
}