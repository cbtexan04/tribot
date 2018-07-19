package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "" }, category = "Minigames", name = "Warriors Guild")

public class WarriorsGuild extends Script implements Painting {//411, 
        private boolean isRunning = true;
        private final int[] SCIMITAR_IDS = {1321, 1323, 1325, 1327, 1329, 1331, 1333, 4587};
        private final int[] WARHAMMER_IDS = {1335, 1337, 1339, 1341, 1343, 1345, 1347};
        private final RSTile CATAPAULT_LOCATION = new RSTile(2840, 3552);
        private final RSTile TARGET_TILE = new RSTile(2842, 3545);
        private final RSTile CENTER_DUMMY_TILE = new RSTile(2857, 3551);
        private final int SHIELD_ID = 8856;
        private int successes = 0;
        private int failures = 0;
        private RSObject dummyObject = null;
        private int currentAttackStyle = -1;
        private String status = "Starting up..";
        private CatapaultType currentCatapault = null;
        private DummyType currentDummy = null;
        private Task currentTask = null;
        public enum Task {
                CATAPAULT,
                DUMMIES;
                private Task() {

                }
        }
        public enum CatapaultType {
                STAB(15617, 1),
                BLUNT(15619, 2),
                SLASH(15620, 3),
                MAGIC(15618, 4);
                private int objectID, interfaceID;
                private CatapaultType(int objectID, int interfaceID) {
                        this.objectID = objectID;
                        this.interfaceID = interfaceID;
                }
                private int getObjectID() {
                        return this.objectID;
                }
                private int getInterfaceID() {
                        return this.interfaceID;
                }
                public static CatapaultType forId(int ID) {
                        for (CatapaultType type : CatapaultType.values()) {
                                if (type.getObjectID() == ID) {
                                        return type;
                                }
                        }
                        return null;
                }
        }

        public enum DummyType {
                SLASH(15625, 2, 3, 5), 
                CRUSH(15628, 2, 3, 4, 5),
                AGGRESSIVE(15626, 3), 
                STAB(15629, 4),
                ACCURATE(15624, 2), 
                CONTROLLED(15627, 4), 
                DEFENSIVE(15630, 5); 
                private int objectID;
                private int[] interfaceID;
                private DummyType(int objectID, int... interfaceID) {
                        this.objectID = objectID;
                        this.interfaceID = interfaceID;
                }
                private int getObjectID() {
                        return this.objectID;
                }
                private int getInterfaceID() {
                        return this.interfaceID[0];
                }
                private int[] getInterfaceIDs() {
                        return this.interfaceID;
                }
                public boolean isCompatible(int interfaceID) {
                        for(int i = 0; i < getInterfaceIDs().length; i++) {
                                if(this.interfaceID[i] == interfaceID)
                                        return true;
                        }
                        return false;
                }
                public static DummyType forId(int ID) {
                        for (DummyType type : DummyType.values()) {
                                if (type.getObjectID() == ID) {
                                        return type;
                                }
                        }
                        return null;
                }
        }
        public RSObject getDummyObject() {
                RSObject[] objects = Objects.sortByDistance(Player.getPosition(), Objects.getAll(10));
                for(RSObject obj : objects) {
                        if(obj.getID() >= 15624 && obj.getID() <= 15630) {
                                return obj;
                        }
                }
                return null;
        }

        public void doDummies() {
                Mouse.setSpeed(160);
                if(attackedByNPC()) {
                        setStatus("Running from aggressive random!");
                        RSObject doors[] = Objects.getAt(new RSTile(2853, 3554, 0));
                        if(doors.length > 0) { /*Walk out the door, then hide behind a table and logout :D*/
                                if(doors[0].getPosition().distanceTo(Player.getPosition()) > 2) {
                                        Walking.walkTo(new RSTile(2855, 3554, 0));
                                        sleep(500);
                                } else {
                                        doors[0].getModel().click("Open");
                                        sleep(600, 900);
                                }
                        } else {
                                Walking.walkTo(new RSTile(2849, 3553, 0));
                                sleep(15000, 20000);
                                Login.logout();
                        }
                        return;
                }
                if(Player.getPosition().getX() < 2854 && Player.getPosition().getY() > 3550 && Player.getPosition().getX() > 2848) { //If we're in that little room
                        RSObject doors[] = Objects.getAt(new RSTile(2853, 3554, 0));
                        if(doors.length > 0) {
                                if(doors[0].getPosition().distanceTo(Player.getPosition()) > 2) {
                                        Walking.walkTo(new RSTile(2852, 3554, 0));
                                        sleep(500);
                                } else {
                                        doors[0].getModel().click("Open");
                                        sleep(600, 900);
                                }
                        } else {
                                Walking.walkTo(new RSTile(2857, 3552, 0));
                                sleep(600, 900);
                        }
                        return;
                }
                if(!PathFinding.canReach(CENTER_DUMMY_TILE, false)) { /*We got lost but let's try to get back*/
                        setStatus("We seem to be stuck..");
                        if(Player.getPosition().distanceTo(new RSTile(2851, 3550)) < 2) {
                                RSObject doors[] = Objects.getAt(new RSTile(2851, 3551, 0));
                                if(doors.length > 1) { //There's more than 1 object here o.O
                                        doors[1].getModel().click("Open");
                                        sleep(600, 900);
                                } else {
                                        Walking.walkTo(new RSTile(2852, 3553, 0));	
                                        sleep(600, 900);
                                }
                        } else if(PathFinding.canReach(new RSTile(2851, 3550), false)) {
                                PathFinding.aStarWalk(new RSTile(2851, 3550));
                                sleep(600, 900);
                        }
                        return;
                }
                if(Player.getPosition().distanceTo(CENTER_DUMMY_TILE) > 5) { /*We're not in the room but we can reach the tile*/
                        PathFinding.aStarWalk(CENTER_DUMMY_TILE);
                        sleep(600, 900);
                }
                if(!isRunOn() && General.random(0, 2000) == 1)
                        setRun();
                dummyObject = getDummyObject();
                if(dummyObject != null) {
                        if(DummyType.forId(dummyObject.getID()).equals(currentDummy)) {
                                setStatus("Waiting..");
                                return;
                        }
                        currentDummy = DummyType.forId(dummyObject.getID());
                        if(currentDummy.equals(DummyType.CRUSH) && !wearingItem(WARHAMMER_IDS)) {
                                equipItem(WARHAMMER_IDS);
                        } else if(!wearingItem(SCIMITAR_IDS)) {
                                equipItem(SCIMITAR_IDS);
                        }
                        if(!currentDummy.isCompatible(currentAttackStyle)) {
                                setStatus("Switching attack styles");
                                if(!GameTab.getOpen().equals(TABS.COMBAT))
                                        Keyboard.pressFunctionKey(1);
                                if(!currentDummy.equals(DummyType.CRUSH)) {
                                        while(Interfaces.get(81) == null && !objectHasChanged(dummyObject))
                                                sleep(50);
                                        Interfaces.get(81, currentDummy.getInterfaceID()).click(null);
                                }
                                currentAttackStyle = currentDummy.getInterfaceID();
                        }
                        setStatus("HULK SMAAAASH");
                        while(!dummyObject.getModel().click("Hit") && !objectHasChanged(dummyObject));
                }
        }
        public boolean objectHasChanged(RSObject o) {
                RSObject[] object = Objects.getAt(o.getPosition());
                if(object.length > 0) {
                        if(object[0] != null) {
                                if(object[0].getID() == o.getID())
                                        return false;
                        }
                }
                return true;
        }
        public boolean equipItem(int... IDs) {
                setStatus("Equipping new item");
                if(!GameTab.getOpen().equals(TABS.INVENTORY))
                        Keyboard.pressFunctionKey(0);
                RSItem[] warhammers = Inventory.find(IDs);
                if(warhammers.length > 0) {
                        if(warhammers[0] == null)
                                return false;
                        while(!warhammers[0].click("Wield"))
                                return true;
                }
                return false;
        }
        public boolean wearingItem(int... IDS) {
                RSItem[] equipped = getEquipment();
                for(int j = 0; j < IDS.length; j++) {
                        for(RSItem i : equipped) {
                                if(i != null)
                                        if(i.getID() == IDS[j])
                                                return true;
                        }
                }
                return false;
        }

        public boolean isRunOn() {
                return (Game.getSettingsArray()[173] == 1);
        }

        public void setRun() {
                if(!isRunOn()) {
                        if(GameTab.getOpen() != GameTab.TABS.OPTIONS) 
                                GameTab.open(TABS.OPTIONS);
                        Mouse.clickBox(626,415,657,445,1);
                }
        }

        public void doCatapault() {
                if(Player.isMoving()) {
                        sleep(500);
                        return;
                }
                if(attackedByNPC()) {
                        setStatus("Running from aggressive random!");
                        RSObject[] door = Objects.getAt(new RSTile(2842, 3542));
                        if(door.length > 0) {
                                if(door[0] != null)
                                        if(Player.getPosition().distanceTo(door[0].getPosition()) > 5) {
                                                Walking.walkPath(Walking.generateStraightPath(door[0].getPosition()));
                                                sleep(500, 800);
                                        } else if(PathFinding.canReach(door[0].getPosition(), true)) {
                                                if(door[0].getModel().click("Open")) {
                                                        sleep(15000, 20000);
                                                        Login.logout();
                                                }
                                        }
                        }
                }
                if(!Player.getPosition().equals(TARGET_TILE) && Inventory.getCount(SHIELD_ID) >  0) { //We need to walk to the "target" spot
                        if(PathFinding.canReach(TARGET_TILE, false)) {
                                setStatus("Walking to target tile");
                                if(Player.getPosition().distanceTo(TARGET_TILE) < 5)
                                        Walking.walkScreenPath(Walking.generateStraightScreenPath(TARGET_TILE));
                                else
                                        PathFinding.aStarWalk(TARGET_TILE);
                                return;
                        }
                }
                if(!wearingItem(SHIELD_ID)) {
                        RSItem[] shield = Inventory.find(SHIELD_ID);
                        if(shield.length > 0) { //If we have the shield and we're in the proper spot weild it
                                if(Player.getPosition().equals(TARGET_TILE)) {
                                        if(shield[0] == null)
                                                return;
                                        shield[0].click("Wield");
                                        sleep(700, 900);
                                }
                        } else { //We don't have a shield, let's get one
                                if(Player.getPosition().getY() > 3541) {
                                        RSObject[] door = Objects.getAt(new RSTile(2842, 3542));
                                        if(door.length > 0) {
                                                if(door[0] != null)
                                                        if(Player.getPosition().distanceTo(door[0].getPosition()) > 5) {
                                                                Walking.walkPath(Walking.generateStraightPath(door[0].getPosition()));
                                                                sleep(500, 800);
                                                        } else if(PathFinding.canReach(door[0].getPosition(), true)) {
                                                                door[0].getModel().click("Open");
                                                                sleep(600, 900);
                                                        }
                                        }
                                        return;
                                }
                                if(NPCChat.clickContinue(true)) //Godfred's talking to us
                                        return;
                                RSNPC[] gamfred = NPCs.find("Gamfred");
                                if(gamfred.length > 0) {
                                        if(gamfred[0] == null)
                                                return;
                                        if(PathFinding.canReach(gamfred[0].getPosition(), false)) {
                                                gamfred[0].getModel().click("Claim-Shield");
                                                sleep(600, 900);
                                                return;
                                        }
                                }
                        }
                }
                if(Player.getPosition().getY() < 3542) { // We are not in the catapault room, so let's go there.
                        setStatus("Traversing doors");
                        RSObject[] doors = Objects.sortByDistance(Player.getPosition(), Objects.find(20, 15647, 1530));
                        if(doors.length > 0) {
                                if(doors[0] == null)
                                        return;
                                if(doors[0].getPosition().distanceTo(Player.getPosition()) > 5) {
                                        if(PathFinding.canReach(doors[0].getPosition(), false))
                                                Walking.walkPath(Walking.generateStraightPath(doors[0].getPosition()));
                                } else {
                                        doors[0].getModel().click("Open");
                                }
                        }
                        return;
                }
                if(CatapaultType.forId(Objects.getAt(CATAPAULT_LOCATION)[0].getID()) != null) {
                        if(CatapaultType.forId(Objects.getAt(CATAPAULT_LOCATION)[0].getID()).equals(currentCatapault))
                                return;
                        currentCatapault = CatapaultType.forId(Objects.getAt(CATAPAULT_LOCATION)[0].getID());
                        setStatus("Defending from: "+currentCatapault.name());
                        sleep(1500, 3000);
                        Interfaces.get(411, currentCatapault.getInterfaceID()).click(null);
                }
        }

        public void loop() {
                sleep(100);
                if(!Login.getLoginState().equals(Login.STATE.INGAME)) {
                        setStatus("Not logged in...");
                        return;
                }
                if(Player.isMoving())
                        setStatus("Walking");
                if(Player.getPosition().distanceTo(CENTER_DUMMY_TILE) > 50) {
                        setStatus("We're lost..");
                        return;
                }
                if(currentTask.equals(Task.CATAPAULT))
                        doCatapault();
                if(currentTask.equals(Task.DUMMIES))
                        doDummies();
        }

        @Override
        public void run() {
                try {
                        Mouse.setSpeed(150);
                        setRun();
                        if(Player.getRSPlayer().getPosition().getPlane() == 1 && Player.getPosition().distanceTo(TARGET_TILE) < 10)
                                currentTask = Task.CATAPAULT;
                        if(Player.getRSPlayer().getPosition().getPlane() == 0 && Player.getPosition().distanceTo(CENTER_DUMMY_TILE) < 10)
                                currentTask = Task.DUMMIES;
                        while(isRunning) {
                                try {
                                        loop();
                                }catch(Exception e) {

                                }
                        }
                }catch(Exception e) {
                        println("Something bad has happened.");
                }
        }

        private RSItem[] getEquipment(){
                RSInterfaceChild equip = Interfaces.get(387, 28);
                RSItem[] items = null;
                if(equip != null){
                        items = equip.getItems();
                }
                return items;
        }

        public boolean attackedByNPC() {
                RSNPC[] npcs = NPCs.getAll();
                for(RSNPC npc: npcs) {
                        if(npc == null)
                                continue;
                        if(npc.isInteractingWithMe() && Player.getRSPlayer().isInCombat()) {
                                return true;
                        }
                }
                return false;
        }

        public void setStatus(Object o) {
                status = o.toString();
        }

        public static RSTile getNearest(RSTile[] t){
                RSTile nearest = null;
                double distance = 9999999;
                for (RSTile object : t){
                        if (object.distanceTo(Player.getPosition()) < distance){
                                nearest = object;
                                distance = object.distanceTo(Player.getPosition());
                        }
                }
                return nearest;
        }
        @Override
        public void onPaint(Graphics g1) {
                Graphics2D g = (Graphics2D)g1;
                g.setColor(Color.LIGHT_GRAY);
                g.drawString("Moose's Warrior's guild - BETA", 10, 255);
                g.drawString("Current activity: " +currentTask.name(), 10, 270);
                if(status != null)
                        g.drawString("Status: " +status.toString(), 10, 285);
                g.drawString("Runtime: " +Timing.msToString(this.getRunningTime()), 10, 300);

                if(currentTask.equals(Task.CATAPAULT)) {
                        if(Interfaces.get(411) != null) {
                                Rectangle r = Interfaces.get(411, currentCatapault.getInterfaceID()).getAbsoluteBounds();
                                g.setColor(Color.CYAN);
                                g.drawRect(r.x, r.y, r.width, r.height);
                        }
                        g.setColor(Color.RED);
                        g.drawPolygon(Projection.getTileBoundsPoly(TARGET_TILE, 0));
                }
                if(dummyObject != null) {
                        Point p = Projection.tileToScreen(dummyObject.getPosition(), 0);
                        g1.setColor(DummyType.forId(dummyObject.getID()).equals(currentDummy) && Player.getPosition().distanceTo(dummyObject.getPosition()) == 1 ? Color.GREEN : Color.RED);
                        g1.drawRect((int)p.getX() - 30, (int)p.getY() - 25, 60, 50);
                        g1.setColor(Color.YELLOW);
                        g1.setFont(new Font("Arial", Font.PLAIN, 10));
                        if(currentDummy != null) {
                                g1.drawString(currentDummy.name(), (int)p.getX() - 30, (int)p.getY() - 15);
                                if(Interfaces.get(81) != null)
                                        if(GameTab.getOpen().equals(TABS.COMBAT))
                                                for(int i = 0; i < currentDummy.getInterfaceIDs().length; i++) {
                                                        Rectangle r = Interfaces.get(81, currentDummy.getInterfaceIDs()[i]).getAbsoluteBounds();
                                                        g1.drawRect(r.x, r.y, r.width, r.height);
                                                }
                        }
                }
        }

}
