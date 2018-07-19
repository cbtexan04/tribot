package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;

import org.tribot.api.EGW;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.InventoryItem;
import org.tribot.api.types.colour.ColourPoint;
import org.tribot.api.types.colour.DTM;
import org.tribot.api.types.colour.DTMPoint;
import org.tribot.api.types.colour.DTMSubPoint;
import org.tribot.api.types.colour.Tolerance;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Constants;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

//import scripts.SuperYewsChopper.HostileEntityCheck;

@ScriptManifest(authors = { "Zainy", "Texan" }, category = "Woodcutting", name = "Ultimate Magic Chopper", version = 1)
public class UltimateMagicChopperPremium extends Script implements Painting {
        private static String version = "1.0";  //TODO
        private String script = "UMC";
        private AuthTimer trialTime = new AuthTimer(0); // 10 MIN
        private AuthTimer checkSession = new AuthTimer(900000); // 15-30MIN
        private boolean authenticated = false;
        private String rs_username = Player.getRSPlayer().getName();
        private String username = "";
        private String password = "";
        private String key;
        private String script_version;
        private int errorConnecting = 0;

        final String magictree = "Magic";
        final int ent = (777);
        final int banker = (56);
        final RSTile middle = new RSTile(2702, 3397);
        final RSTile banktile = new RSTile(2725, 3490);
        final RSTile[] bankpath = {new RSTile(2701, 3397), new RSTile(2714, 3399), new RSTile(2718, 3415),
                new RSTile(2718, 3431), new RSTile(2720, 3442), new RSTile(2724, 3456), new RSTile(2724, 3472), new RSTile(2724, 3480), 
                new RSTile(2725, 3490)};
        final RSTile[] treepath = { new RSTile(2726, 3479), new RSTile(2721, 3468), new RSTile(2721, 3456),
                new RSTile(2721, 3444), new RSTile(2719, 3433), new RSTile(2718, 3423), new RSTile(2715, 3411), 
                new RSTile(2714, 3399), new RSTile(2703, 3397)};
        final RSTile runaway = new RSTile(2700, 3386);
        final int axe = (1359);
        final int logs = (1513);
        final int booth = (25808);
        Point best;
        final Point offsets = new Point(10, -20);
        final Point random = new Point(General.random(-5, 5), General.random(-5, 5));
        final int nest[] = {5073, 5074};
        private long startTime;
        private final Integer startXp = Integer.valueOf(TotalWCXP() > 0 ? TotalWCXP() : 0);
        int escapes;

        @Override
        public void run() {

                script_version = checkVersion(script);
                if (!version.equalsIgnoreCase(script_version)) {
                        println("You have version " + version + " and version "
                                        + script_version + " is available!");
                        println("Please download the latest update!");
                        stopScript();
                }
                println("Version " + version + " is up to date!");
                username = JOptionPane.showInputDialog("Enter authorization login: ");
                password = JOptionPane
                        .showInputDialog("Enter authorization password: ");
                key = generateRandom(5);
                checkAuthentication();
                assignSession(script, username, password, key);
                if (authenticated == true) {
                        println("" + username + " is authorized to run " + script + " !");
                        println("Unique Session Key: " + key);
                } else {
                        println("" + username + " is not authorized to run " + script);
                        return;
                }

                HostileEntityCheck hec = new HostileEntityCheck();
                Mouse.setSpeed(General.random(130, 160));
                startTime = System.currentTimeMillis();
                boolean infinite = true;
                while(infinite){

                        if (!checkSession.isRunning()) {
                                if (!checkSession(script, username, password)) {
                                        println("Stopping script!");
                                        println("Detected script is being run on more than one account, you must purchase additional licenses!");
                                        stopScript();
                                } else {
                                        key = generateRandom(5);
                                        println("Assigning unique session key: " + key);
                                        assignSession(script, username, password, key);
                                        checkSession.reset();
                                }
                        }

                        if (trialTime.isRunning() || authenticated) {

                                hec.start();
                                if(hec.isTrue){
                                        EscapeFromCB();
                                }
                                sleep(100, 200);
                                while(!Ent()){
                                        sleep(150, 250);
                                        if(Tree()){
                                                if(hec.isTrue){
                                                        EscapeFromCB();
                                                }
                                                sleep(100, 200);
                                                while(!Inventory.isFull()){
                                                        if(hec.isTrue){
                                                                EscapeFromCB();
                                                        }
                                                        sleep(100, 250);
                                                        Chop();
                                                        sleep(500, 800);
                                                        if(Player.getRSPlayer().isInCombat()){
                                                                EscapeFromCB();
                                                        }
                                                }
                                        }
                                        while(Inventory.isFull() && !BankIsOnScreen()){
                                                WalkToBank();
                                        }
                                        while(LogsInvent() && BankIsOnScreen()){
                                                sleep(100, 200);
                                                Bank();
                                        }
                                        while(!LogsInvent() && !Tree()){
                                                WalkToTree();
                                                sleep(100, 200);
                                        }
                                }
                                if(Ent()){
                                        EscapeFromEnt();
                                        sleep(100, 200);
                                }
                                if(Spirit()){
                                        EscapeFromCB();
                                }
                                if(hec.isTrue){
                                        EscapeFromCB();
                                }
                                sleep(100, 200);
                        }
                        hec.interrupt();
                }
        }

        private void Chop(){
                ab();
                if(!isAnimating()){
                        RSObject[] MagicTree = Objects.find(20, magictree); 
                        if(!Ent() && MagicTree.length > 0){
                                if(!MagicTree[0].getModel().click("Chop down", random, offsets)){
                                        sleep(700, 900);
                                        ab();
                                }
                                sleep(800, 1400);
                        }else if(Ent()){
                                Walking.walkTo(middle);
                                sleep( 500, 750);
                                if(Player.getPosition().equals(middle)){
                                        sleep(500, 800);
                                }
                        }
                        Nests();
                        if(Player.getRSPlayer().isInCombat()){
                                EscapeFromCB();
                                ab();
                                sleep(100, 200);
                        }
                }
                sleep(300, 400);
                if(Ent()){
                        EscapeFromEnt();
                        sleep(100, 200);
                        ab();
                }
                Nests();
                if(Spirit()){
                        EscapeFromCB();
                        sleep(100, 200);
                }

                EscapeFromCB();
                sleep(500, 800);


        }
        private void Nests(){
                RSGroundItem[] Nests = GroundItems.find(nest);
                if(Nests.length > 0){
                        Nests[0].click("Take");
                        sleep( 450, 700);
                        ab();
                }
        }
        //  private Point nextTree(int dist) {
        //  //  RSObject closest = Objects.findNearest(magictree)[dist];
        //  //  if(dist >= closest.length )return null;
        //      /*
        //       * Normal trees are setup like this 
        //       * with the tree at the center of the big square
        //       * _____________
        //       * |     |     |
        //       * |_____x_____|
        //       * |  .  |     |
        //       * |_____|_____|
        //       * 
        //       * and the dot is the ID point
        //       * We set the x to be far point, 
        //       * and compare distances to it to
        //       * find the center point of the
        //       * big square
        //       */
        //      /*
        //       * larger trees are setup like this 
        //       * with the tree at the center of the big square
        //       * ___________________
        //       * |     |     |     |
        //       * |_____|_____|_____|
        //       * |     |  x  |     |
        //       * |_____|_____|_____|
        //       * |  .  |     |     |
        //       * |_____|_____|_____|
        //       * and the dot is the ID point
        //       * so we just ove RS tiles so we
        //       * are in the middle square
        //       * 
        //       * Certain willows look like this:
        //       * _____________
        //       * |     |     |
        //       * |_____|__x__|
        //       * |  .  |     |
        //       * |_____|_____|
        //       * 
        //       */
        //      Point best = new Point();
        //      //RSObject closest = Objects.findNearest(magictree)[dist];
        //
        //  //  if(logType == 1 || logType == 4){
        //          best = Projection.tileToScreen(new RSTile(closest.getPosition().getX()+1, closest.getPosition().getY()+1), 0);
        //  //  }
        //          if(closest.getID() == 1306){
        //          Point[] bounds = Projection.getTileBounds(closest.getPosition(), 0);
        //          Point farPoint = Projection.tileToScreen(new RSTile(closest.getPosition().getX()+1, closest.getPosition().getY()+1), 0);
        //
        //          best = bounds[0];
        //          
        //          for(int i = 1; i < bounds.length; i++){
        //              if(farPoint.distance(bounds[i]) <= farPoint.distance(best))best = bounds[i];
        //          }
        //      }else{
        //          Point otherPoint0 = Projection.tileToScreen(new RSTile(closest.getPosition().getX()+1, closest.getPosition().getY()+1), 0);
        //          Point otherPoint1 = Projection.tileToScreen(new RSTile(closest.getPosition().getX()+1, closest.getPosition().getY()), 0);
        //          best = new Point((otherPoint0.x + otherPoint1.x)/2,(otherPoint0.y + otherPoint1.y)/2); 
        //      }
        //      return best;
        //  }
        private boolean Spirit(){
                if(Player.getRSPlayer().isInCombat()){
                        return true;
                }
                return false;
        }
        private boolean Ent(){
                RSObject[] Ent = Objects.find(4, ent);
                if(Ent.length > 0){
                        return true;
                }
                return false;
        }

        private void ab(){
                int random = General.random(0, 120);
                switch(random){
                        case 0:
                                if (!GameTab.getOpen().equals(TABS.FRIENDS)) {
                                        GameTab.open(TABS.FRIENDS);
                                        sleep(2000, 6000);
                                        GameTab.open(TABS.INVENTORY);
                                }
                                break;
                        case 1:
                                Camera.setCameraRotation(Camera.getCameraRotation()+General.random(-45, 45));
                                break;
                        case 2:
                                Camera.setCameraAngle(General.random(50, 100));
                                break;
                        case 3:
                                int rot = Camera.getCameraRotation();
                                Camera.setCameraRotation(Camera.getCameraRotation()+General.random(-45, 45));
                                sleep(500, 12000);
                                Camera.setCameraRotation(rot+ General.random(-20, 20));
                                break;
                        case 4:
                                if (!GameTab.getOpen().equals(TABS.STATS)) {
                                        GameTab.open(TABS.STATS);
                                        sleep(2000, 4000);
                                        GameTab.open(TABS.INVENTORY);
                                }
                                break;  
                        default:
                                return;
                }
        }

        private void WalkToBank(){
                setRun(true);
                sleep(300, 1200);
                if(Tree()){
                        Walking.walkTo(new RSTile(2717 + General.random(-2, 2), 3397 + General.random(-3,  3)));
                        ab();
                        sleep(3000, 5000);
                }
                if(Player.isMoving()){
                        ab();
                        sleep(3000, 5500);
                }
                Walking.walkPath(Walking.generateStraightPath(banktile));
                sleep(300, 500);
                while(Player.isMoving()){
                        sleep(200, 500);
                }
                ab();
                if(Spirit()){
                        escapes++;
                }
        }
        private void reversePath(RSTile[] bankpath) {
                for (int left = 0, right = bankpath.length - 1; left < right; left++, right--) {
                        RSTile temp = bankpath[left];
                        bankpath[left] = bankpath[right];
                        bankpath[right] = temp;
                }
        }
        private void WalkToTree(){
                Walking.walkPath(Walking.generateStraightPath(new RSTile(2717 + General.random(-2, 2), 3397 + General.random(-3,  3))));
                sleep(500);
                ab();
                while(Player.isMoving() && Player.getPosition().distanceTo(new RSTile(2717 + General.random(-2, 2), 3397 + General.random(-3,  3)) ) > General.random(4, 8)){
                        sleep(500, 800);
                }
                Walking.walkPath(Walking.generateStraightPath(middle));
                ab();
                sleep(1200, 1500);
                ab();
                while(Player.isMoving()){
                        sleep(700, 1000);
                }
        }
        private boolean BankIsOnScreen(){
                RSNPC[] Banker = NPCs.find(banker);
                if(Banker.length > 0){
                        sleep(100, 200);
                        return true;
                }
                return false;
        }
        private boolean LogsInvent(){
                RSItem[] Logs = Inventory.find(logs);
                if(Logs.length > 0){
                        return true;
                }
                return false;
        }
        private boolean Tree(){
                RSObject[] Tree = Objects.find(10, magictree);
                if(Tree.length > 0){
                        return true;
                }
                return false;
        }
        private void EscapeFromEnt(){
                if(Ent()){
                        Walking.walkTo(middle);
                        sleep(500, 750);
                        while(Ent()){
                                sleep(200, 550);
                        }
                }
        }
        private void EscapeFromCB(){
                if(Spirit()){
                        escapes++;
                        Keyboard.pressKey((char) KeyEvent.VK_CONTROL);
                        Walking.walkTo(runaway);
                        Keyboard.releaseKey((char) KeyEvent.VK_CONTROL);
                        sleep(500, 800);
                        while(Player.isMoving()){
                                sleep(200, 550);
                        }
                        sleep(4000, 6000);
                        Walking.walkTo(middle);
                        sleep(400, 600);
                        while(Player.isMoving()){
                                sleep(200, 400);
                        }
                }
        }

        public void setRun(boolean option) {

                int[] settingsArray = Game.getSettingsArray();
                if(settingsArray != null) {
                        if(settingsArray.length >= 173) { 
                                if((settingsArray[173] == 0) == option) { 
                                        openTabIfClosed(GameTab.TABS.OPTIONS);
                                        sleep(400, 500);
                                        Mouse.clickBox(621, 410, 662, 450, 1);
                                }
                                else {
                                }
                        }
                }
        }

        public void openTabIfClosed(GameTab.TABS tab) {
                if(!GameTab.getOpen().equals(tab)) {
                        GameTab.open(tab);
                }
        }

        private void Bank(){
                if((GameTab.getOpen().INVENTORY != null)){
                        GameTab.open(TABS.INVENTORY);
                }
                if(!BankIsOnScreen()){
                        WalkToBank();
                        sleep(1000, 1400);
                }
                ab();
                if(!Banking.isBankScreenOpen()){
                        Banking.openBankBooth();
                        sleep(1200, 1700);
                        Banking.depositAllExcept(Constants.IDs.Items.hatchets);
                        sleep(200, 450);
                } else{
                        Banking.depositAllExcept(Constants.IDs.Items.hatchets);
                        sleep(200, 500);
                }
                if(General.random(1, 2) == 1){
                        Banking.close();
                        sleep(20, 500);
                }
                ab();
        }
        private boolean isAnimating(){
                if(Player.getAnimation() != -1){
                        return true;
                }
                return false;
        }
        private final int TotalWCXP() {
                return Skills.getXP("WOODCUTTING");
        }

        public class Timer
        {
                private long end;
                private final long start;
                private final long period;

                public Timer(long period)
                {
                        this.period = period;
                        this.start = System.currentTimeMillis();
                        this.end = (this.start + period);
                }

                public Timer(long period, long addition) {
                        this.period = period;
                        this.start = (System.currentTimeMillis() + addition);
                        this.end = (this.start + period);
                }

                public long getElapsed() {
                        return System.currentTimeMillis() - this.start;
                }

                public long getRemaining() {
                        if (isRunning()) {
                                return this.end - System.currentTimeMillis();
                        }
                        return 0L;
                }

                public boolean isRunning() {
                        return System.currentTimeMillis() < this.end;
                }

                public void reset() {
                        this.end = (System.currentTimeMillis() + this.period);
                }

                public long setEndIn(long ms) {
                        this.end = (System.currentTimeMillis() + ms);
                        return this.end;
                }

                public String toElapsedString() {
                        return format(getElapsed());
                }

                public String toRemainingString() {
                        return format(getRemaining());
                }

                public String format(long time) {
                        StringBuilder t = new StringBuilder();
                        long total_secs = time / 1000L;
                        long total_mins = total_secs / 60L;
                        long total_hrs = total_mins / 60L;
                        int secs = (int)total_secs % 60;
                        int mins = (int)total_mins % 60;
                        int hrs = (int)total_hrs % 60;
                        if (hrs < 10) {
                                t.append("0");
                        }
                        t.append(hrs);
                        t.append(":");
                        if (mins < 10) {
                                t.append("0");
                        }
                        t.append(mins);
                        t.append(":");
                        if (secs < 10) {
                                t.append("0");
                        }
                        t.append(secs);
                        return t.toString();
                }
        }

        @Override
        public void onPaint(Graphics g){
                int xTextModifier = 362;
                int yEndModifier = 470;
                long Time = System.currentTimeMillis()-startTime;

                g.setColor(Color.BLUE);

                g.fillRect(360, 345, 153, 130);

                g.setColor(new Color(0, 0, 0, 255));
                g.fillRect(7, 460, 125, 14);

                g.setColor(Color.WHITE);
                g.drawString("Magic Logs chopped = " + new DecimalFormat("###,###").format(((TotalWCXP() - startXp.intValue())/250)), 362, yEndModifier - 45);
                g.drawString("XP/H = " + new DecimalFormat("###,###").format((int)avarageXpAHour(System.currentTimeMillis()-startTime, TotalWCXP(), this.startXp.intValue())), 362, yEndModifier - 105);
                g.drawString("V1", 487, yEndModifier - 105);
                g.drawString("XP Gained = " + new DecimalFormat("###,###").format(TotalWCXP() - this.startXp.intValue()), 362, yEndModifier - 90);
                g.drawString("Runtime:" + Timing.msToString(Time),  362, yEndModifier);
                g.drawString("Escapes from Combats:" + escapes, 362, yEndModifier - 25);

        }

        public class HostileEntityCheck extends Thread {
                public boolean isTrue = false;
                private int[] npcs = { 438, 439, 440, 441, 442, 443, 2463, 2464, 2465, 2466, 2467, 2468 };

                @Override
                public void run(){
                        while(!isInterrupted()){
                                RSNPC[] list = NPCs.find(npcs);
                                if(list.length > 0){
                                        if(Player.getRSPlayer().isInCombat())isTrue = true;
                                }else{ isTrue= false;
                                        try {
                                                Thread.sleep(700);
                                        } catch (InterruptedException e) {
                                                break;
                                        }
                                }   
                        }
                }
        }

        public class AuthTimer {

                private long end;
                private final long start;
                private final long period;

                public AuthTimer(final long period) {
                        this.period = period;
                        start = System.currentTimeMillis();
                        end = start + period;
                }

                public AuthTimer(final long period, long addition) {
                        this.period = period;
                        start = System.currentTimeMillis() + addition;
                        end = start + period;
                }

                public long getElapsed() {
                        return System.currentTimeMillis() - start;
                }

                public long getRemaining() {
                        if (isRunning()) {
                                return end - System.currentTimeMillis();
                        }
                        return 0;
                }

                public boolean isRunning() {
                        return System.currentTimeMillis() < end;
                }

                public void reset() {
                        end = System.currentTimeMillis() + period;
                }

                public long setEndIn(final long ms) {
                        end = System.currentTimeMillis() + ms;
                        return end;
                }

                public String toElapsedString() {
                        return format(getElapsed());
                }

                public String toRemainingString() {
                        return format(getRemaining());
                }

                public String format(final long time) {
                        final StringBuilder t = new StringBuilder();
                        final long total_secs = time / 1000;
                        final long total_mins = total_secs / 60;
                        final long total_hrs = total_mins / 60;
                        final int secs = (int) total_secs % 60;
                        final int mins = (int) total_mins % 60;
                        final int hrs = (int) total_hrs % 60;
                        if (hrs < 10) {
                                t.append("0");
                        }
                        t.append(hrs);
                        t.append(":");
                        if (mins < 10) {
                                t.append("0");
                        }
                        t.append(mins);
                        t.append(":");
                        if (secs < 10) {
                                t.append("0");
                        }
                        t.append(secs);
                        return t.toString();
                }
        }

        private void checkAuthentication() {
                if (authenticate(script, username, password) == true) {
                        authenticated = true;
                } else {
                        authenticated = false;
                }
        }

        private void assignSession(String s, String u, String pw, String key) {
                final String LINK = "http://tribot-scripts.org/panel.php?action=assign";
                String script = "&script=" + s;
                String user = "&user=" + u;
                String pass = "&pass=" + pw;
                String session = "&session=" + key;
                String phpCall = (LINK + script + user + pass + session);
                try {
                        String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
                        URL url = new URL(phpCall);
                        URLConnection uc = url.openConnection();
                        uc.setRequestProperty("User-Agent", USER_AGENT);
                        InputStreamReader inr = new InputStreamReader(uc.getInputStream());
                } catch (IOException e) {
                        if(errorConnecting > 5) {
                                println("Error connecting to server");
                                return;
                        }
                        sleep(300);
                        errorConnecting++;
                        assignSession(s, u, pw, key);
                }
                errorConnecting = 0;
        }

        private boolean checkSession(String s, String u, String pw) {
                final String LINK = "http://tribot-scripts.org/panel.php?action=retrieve";
                String script = "&script=" + s;
                String user = "&user=" + u;
                String pass = "&pass=" + pw;
                String phpCall = (LINK + script + user + pass);
                String line = "";
                try {
                        BufferedReader br = getReader(phpCall);
                        line = br.readLine();
                } catch (IOException e) {
                        if(errorConnecting > 5) {
                                println("Error connecting to server");
                                return false;
                        }
                        sleep(300);
                        errorConnecting++;
                        checkSession(s, u, pw);
                }
                errorConnecting = 0;
                if (line.equalsIgnoreCase(key)) {
                        return true;
                } else {
                        return false;
                }
        }

        public boolean authenticate(String s, String u, String pw) {
                final String LINK = "http://tribot-scripts.org/panel.php?action=auth";
                String script = "&script=" + s;
                String user = "&user=" + u;
                String pass = "&pass=" + pw;
                String phpCall = (LINK + script + user + pass);
                String line = "";
                try {
                        BufferedReader br = getReader(phpCall);
                        line = br.readLine();
                } catch (IOException e) {
                        println("Error connecting to server");
                        e.printStackTrace();
                }
                if (line.equalsIgnoreCase("True")) {
                        return true;
                } else {
                        return false;
                }
        }

        public String checkVersion(String s) {
                final String LINK = "http://tribot-scripts.org/panel.php?action=get_version";
                String script = "&script=" + s;
                String phpCall = (LINK + script);
                String line = "";
                try {
                        BufferedReader br = getReader(phpCall);
                        line = br.readLine();
                } catch (IOException e) {
                        println("Error connecting to server");
                        e.printStackTrace();
                }
                return line;
        }

        private String generateRandom(int length) {
                Random random = new Random();
                String key = "";
                key = key + (random.nextInt(9) + '1');
                for (int i = 1; i < length; i++) {
                        key = key + (random.nextInt(10) + '0');
                }
                return key;
        }

        private BufferedReader getReader(String link) throws IOException {
                String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
                URL url = new URL(link);
                URLConnection uc = url.openConnection();
                uc.setRequestProperty("User-Agent", USER_AGENT);
                InputStreamReader inr = new InputStreamReader(uc.getInputStream());
                return new BufferedReader(inr);
        }
}
