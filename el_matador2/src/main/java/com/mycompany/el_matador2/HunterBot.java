package com.mycompany.el_matador2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;


import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004DistanceStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Stop;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;

/**
 * Example of Simple Pogamut bot, that randomly walks around the map searching
 * for preys shooting at everything that is in its way.
 *
 * @author Rudolf Kadlec aka ik
 * @author Jimmy
 */
@AgentScoped
public class HunterBot extends UT2004BotModuleController<UT2004Bot>{

    /**
     * boolean switch to activate engage behavior
     */
    @JProp
    public boolean shouldEngage = true;
    /**
     * boolean switch to activate pursue behavior
     */
    @JProp
    public boolean shouldPursue = true;
    /**
     * boolean switch to activate rearm behavior
     */
    @JProp
    public boolean shouldRearm = true;
    /**
     * boolean switch to activate collect health behavior
     */
    @JProp
    public boolean shouldCollectHealth = true;
    /**
     * how low the health level should be to start collecting health items
     */
    @JProp
    public int healthLevel = 75;
    /**
     * how many bot the hunter killed other bots (i.e., bot has fragged them /
     * got point for killing somebody)
     */
    @JProp
    public int frags = 0;
    /**
     * how many times the hunter died
     */
    @JProp
    public int deaths = 0;
    
    private UT2004PathAutoFixer autoFixer;
    
    private static int instanceCount = 0;
          
     /**
     * Hunter States Definition
     */
    StateMachine stateMachine;
    protected boolean runningToPlayer = false;
    protected int pursueCount = 0;
    protected List<Item> itemsToRunAround = null;
    
    

    /**
     * {@link PlayerKilled} listener that provides "frag" counting + is switches
     * the state of the hunter.
     *
     * @param event
     */
    @EventListener(eventClass = PlayerKilled.class)
    public void playerKilled(PlayerKilled event) {
        if (event.getKiller().equals(info.getId())) {
            ++frags;
        }
        if (enemy == null) {
            return;
        }
        if (enemy.getId().equals(event.getId())) {
            enemy = null;
        }
    }
    /**
     * Used internally to maintain the information about the bot we're currently
     * hunting, i.e., should be firing at.
     */
    protected Player enemy = null;
    /**
     * Item we're running for. 
     */
    protected Item item = null;
    /**
     * Taboo list of items that are forbidden for some time.
     */
    protected TabooSet<Item> tabooItems = null;

    CSVWriter csvWriter;
    FileWriter mFileWriter;
    /**
     * Bot's preparation - called before the bot is connected to GB2004 and
     * launched into UT2004.
     */
    @Override
    public void prepareBot(UT2004Bot bot) {
        tabooItems = new TabooSet<Item>(bot);

        autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder); // auto-removes wrong navigation links between navpoints

        // listeners        
        navigation.getState().addListener(new FlagListener<NavigationState>() {

            @Override
            public void flagChanged(NavigationState changedValue) {
                switch (changedValue) {
                    case PATH_COMPUTATION_FAILED:
                    case STUCK:
                        if (item != null) {
                            tabooItems.add(item, 10);
                        }
                        reset();
                        break;

                    case TARGET_REACHED:
                        reset();
                        break;
                }
            }
        });

        // DEFINE WEAPON PREFERENCES
        weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);                
        weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
        weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);        
        weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
        //weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
/*
        // First range class is defined from 0 to 80 ut units (1 ut unit ~ 1 cm)
        weaponPrefs.newPrefsRange(80)
                .add(UT2004ItemType.SHIELD_GUN, true);
        // Only one weapon is added to this close combat range and it is SHIELD GUN

        // Second range class is from 80 to 1000 ut units (its always from the previous class to the maximum
        // distance of actual class
        weaponPrefs.newPrefsRange(1000)
                .add(UT2004ItemType.FLAK_CANNON, true)
                .add(UT2004ItemType.MINIGUN, true)
                .add(UT2004ItemType.LINK_GUN, false);
        //.add(UT2004ItemType.ASSAULT_RIFLE, true);
        // More weapons are in this class with FLAK CANNON having the top priority

        // Third range class is from 1000 to 4000 ut units - that's quite far actually
        weaponPrefs.newPrefsRange(4000)
                .add(UT2004ItemType.SHOCK_RIFLE, true)
                .add(UT2004ItemType.MINIGUN, false);
        // Two weapons here with SHOCK RIFLE being the top

        // The last range class is from 4000 to 100000 ut units. In practise 100000 is
        // the same as infinity as there is no map in UT that big
        weaponPrefs.newPrefsRange(100000)
                .add(UT2004ItemType.LIGHTNING_GUN, true)
                .add(UT2004ItemType.SHOCK_RIFLE, true);
        // Only two weapons here, both good at sniping

        */
    }

    /**
     * Here we can modify initializing command for our bot.
     *
     * @return
     */
    @Override
    public Initialize getInitializeCommand() {
        // just set the name of the bot and his skill level, 1 is the lowest, 7 is the highest
    	// skill level affects how well will the bot aim
        return new Initialize().setName("Hunter-" + (++instanceCount)).setDesiredSkill(7);
    }

    /**
     * Resets the state of the Hunter.
     */
    protected void reset() {
    	item = null;
        enemy = null;
        navigation.stopNavigation();
        itemsToRunAround = null;
        this.stateMachine.currentState = new SearchItemsState();
    }
    int x=1;
    @EventListener(eventClass=PlayerDamaged.class)
    public void playerDamaged(PlayerDamaged event) throws IOException {
    	log.info("I have just hurt other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");

    	if(this.enemy != null){
        String distance = Double.toString(this.getInfo().getLocation().getDistance(this.enemy.getLocation()));
        String damage = Integer.toString(event.getDamage());
        String weaponName =  this.getWeaponry().getCurrentWeapon().getType().getName();
        String rotation = Double.toString(this.getInfo().getRotation().getYaw()+ this.enemy.getRotation().getYaw());
        String speed = Double.toString(this.enemy.getVelocity().getX());
            String str = weaponName;
            if ("XWeapons.AssaultRiflePickup".equals(str)) {
                mFileWriter = new FileWriter("AssaultRiflePickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.FlakCannonPickup".equals(str)) {
                mFileWriter = new FileWriter("FlakCannonPickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.LinkGunPickup".equals(str)) {
                mFileWriter = new FileWriter("LinkGunPickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.RocketLauncherPickup".equals(str)) {
                mFileWriter = new FileWriter("RocketLauncherPickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.ShockRiflePickup".equals(str)) {
                mFileWriter = new FileWriter("ShockRiflePickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.RedeemerPickup".equals(str)) {
                mFileWriter = new FileWriter("RedeemerPickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.SniperRiflePickup".equals(str)) {
                mFileWriter = new FileWriter("SniperRiflePickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.BioRiflePickup".equals(str)) {
                mFileWriter = new FileWriter("BioRiflePickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else if ("XWeapons.MinigunPickup".equals(str)) {
                mFileWriter = new FileWriter("MinigunPickup.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }
            else {
                mFileWriter = new FileWriter("UnknowGun.csv", true);
                csvWriter = new CSVWriter(mFileWriter);
                csvWriter.writeNext(new String[]{distance, rotation, speed, damage,weaponName});
                csvWriter.close();
            }


        }
    }
    
    @EventListener(eventClass=BotDamaged.class)
    public void botDamaged(BotDamaged event) {
    	log.info("I have just been hurt by other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");
    }
    
        public HunterBot() {
         stateMachine= new StateMachine();


    }

    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini
     * file in UT2004/System folder.
     *
     * @throws cz.cuni.amis.pogamut.base.exceptions.PogamutException
     */
    @Override
    public void logic() {

        stateMachine.currentState.execute(this);
        stateMachine.execute(this);
        // 2) are you shooting? 	-> stop shooting, you've lost your target
        if (info.isShooting() || info.isSecondaryShooting()) {
            getAct().act(new StopShooting());
        }

    }

    ////////////////
    // BOT KILLED //
    ////////////////
    @Override
    public void botKilled(BotKilled event) {
    	reset();
    }

    ///////////////////////////////////
    public static void main(String args[]) throws PogamutException {
        // starts 3 Hunters at once
        // note that this is the most easy way to get a bunch of (the same) bots running at the same time        
    	new UT2004BotRunner(HunterBot.class, "Hunter").setMain(true).setLogLevel(Level.INFO).startAgents(5);
    }  
}

