package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.swing.JOptionPane;
import org.tribot.api2007.Player;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.colour.Tolerance;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MousePainting;
import org.tribot.script.interfaces.MouseSplinePainting;
import org.tribot.script.interfaces.Painting;

import scripts.TexanSupport.TexanEnum;

@ScriptManifest(authors = { "Texan + Zainy" }, category = "Combat", name = "Ultimate Pest Control")
public class TexanPestControl extends Script implements Painting, MessageListening07, MouseSplinePainting, MousePainting {
	private static String version = "3.45";	//TODO
	private String script = "UPC";
	private AuthTimer trialTime = new AuthTimer(600000); // 10 MIN
	private AuthTimer checkSession = new AuthTimer(900000); // 15-30MIN
	private boolean authenticated = false;
	private String rs_username = Player.getRSPlayer().getName();
	private String username = "";
	private String password = "";
	private String key;
	private String script_version;
	private String combatStyle = "attack";
	private static RSTile squirePos = null;
	private static RSTile knightPos = null;
	private static RSTile westPortalPos = null;
	private static RSTile eastPortalPos = null;
	private static RSTile southWestPortalPos = null;
	private static RSTile southEastPortalPos = null;
	private static RSTile southWestCornerPos = null;
	private static RSTile southEastCornerPos = null;
	private static RSTile currentPortal = null;
	private static RSTile landerStart = new RSTile(2657, 2639, 0);
	private static RSTile boatCenter = new RSTile(2662, 2640, 0);
	static RSTile eastPortalTile;
	static RSTile westPortalTile;
	static RSTile southEastPortalTile;
	static RSTile southWestPortalTile;
	private RSTile southGatePos = null;
	private RSTile targetPortal = null;
	private RSTile targetGate = null;
	
	private final Color color1 = new Color(255, 255, 255);
	private final Font font1 = new Font("Arial", 0, 14);
    private final Image img1 = getImage("http://s7.postimg.org/xkgonxrjf/SDD_zpsa5f5802c.png");
    
	private Random rand = new Random();
	private Timer timer;
	private TexanEnum SCRIPT_STATE;
	
	private String debugInfo = "";
	private static String ccToJoin = "iemz";
	private static String buyPointsXP = "hitpoints";

//	private static ArrayList<Integer> worldNumber = new ArrayList<Integer>();
//	private static ArrayList<Integer> worldOccurance = new ArrayList<Integer>();
	
	private static boolean allowSpecialAttack = false;
	private static boolean portalsSet = false;
	private static boolean buttonClicked = false;
	private static boolean userChoseDefendKnight = false;
	private static boolean userChoseAttackPortals = false;
	private static boolean userChoseRandomGame = true;
	private static boolean allowClanChat = false;
	private boolean login = false;
	private boolean inCombat = false;
	private boolean prayerActivated = false;
	private boolean timerRunning = false;
	private static boolean freeVersion = false;
	private boolean attackingPortal = false;
	private boolean runScript = true;
    
	private static int attackStyle = 3;	//3 ranged, 1 melee, 2 mage
	private static final long START_TIME = System.currentTimeMillis();
	private final int startingRangedXP = Skills.getXP("Ranged");
	private final int startingHitpointsXP = Skills.getXP("Hitpoints");
	private final int startingAttackXP = Skills.getXP("Attack");
	private final int startingStrengthXP = Skills.getXP("Strength");
	private final int startingDefenseXP = Skills.getXP("Defence");
	private final int startingMagicXP = Skills.getXP("Magic");
	private int randomPortal = rand.nextInt(3 - 0 + 1);
	private int loginWorld = 0;
	private static int timeNotInCombat = 0;
	private static int currentWorld = 0;
	private int startPoints = -999;
	private static int currentPoints = 0;
	private int previousPoint;
	private int pointSpent = 0;
	
	final Point randomness = new Point(General.random(-7, 7), General.random(-5, 5));
	final Point offset = new Point(0, 0);
	int skill;
	
	private static ArrayList<String> buyVoidItems = new ArrayList<String>();

	
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
			println("" + username + " is not authorized to run " + script
					+ ". Enjoy your 10 minute trial!");
		}
		
		try {
			if(Player.getRSPlayer().getName().equals("EliteOpz")){
				println("You have been banned from using this script.");
				return;
			}
		} catch (Exception e) { }
		
		Mouse.setSpeed(General.random(165, 175));
        Walking.walking_timeout = 5000;
        
		int choice = Integer.parseInt(JOptionPane.showInputDialog("Use old GUI (for MAC users having issues)? 1 for yes, 2 for no"));

		if(choice == 2) {
			PCGUI popup = new PCGUI();		
		} else if (choice == 1) {
			OriginalGUI popup = new OriginalGUI();
			popup.initComponents();
		} else {
			println("Invalid choice. Exiting.");
		}
		
		while(!buttonClicked) {
			sleep(2000);
		}
		
		setAutoRetal(true);
		setAttackStyle();

		if(freeVersion) {
			setPlayStyle("defend");
		}
		
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
			playGame();

		} else {
			if (!authenticate(script, username, password)) {
				println("" + rs_username
						+ " is not authorized to run this script!");
				println("Your 10 second trial is complete, please purchase the script!");
				stopScript();
			}
			
		}
		println("Autologout enabled. Logging out...");
		println("Last status was: " + debugInfo);
		while(!Login.logout()) {
			Login.logout();
			sleep(500,600);
		}
	}
	
	static String getVerion() {
		return version;
	}
	
	static RSTile getLanderTile() {
		return landerStart;
	}
	
	static RSTile getBoatCenter(){
		return boatCenter;
	}
	
	static void setDifficulty(String x) {
		if(x.equals("novice")) {
			landerStart = new RSTile(2657, 2639, 0);
			boatCenter = new RSTile(2662, 2640, 0);
		} else if (x.equals("medium")){
			landerStart = new RSTile(2644, 2644, 0);
			boatCenter = new RSTile(2639, 2644, 0);
		}
	}

	private boolean clickContinue() {
		try {
	        boolean clicked = false;
	        int timesClicked = 0;
	        while (runScript) {
	        	setPoints();
	            RSInterfaceMaster[] all = Interfaces.getAll();
	            RSInterfaceMaster checkSecond = Interfaces.get(228);
	            for (RSInterfaceMaster master : all) {
	                for (RSInterfaceChild child : master.getChildren()) {
	                    if (child.getText().equals("Click here to continue")) {
	                        child.click("Continue");
	                        clicked = true;
	                        timesClicked++;
	                        continue;
	                    }
	                    if(checkSecond != null) {
	                    	RSInterfaceChild[] childrenCheck = checkSecond.getChildren();
	                    	if(childrenCheck.length > 0) {
	        	                for(int i = 0; i < childrenCheck.length; i++) {
	        	                	if(childrenCheck[i].getText().contains("Would you like to go on the mission")) {
	        	                		Mouse.clickBox(100, 406	, 419, 400, 1);
	        	    					sleep(400,500);
	        	    					return false;
	        	                	}
	        	                }
	                    	}
	                    }
	                }
	            }
	            if (!clicked) {
	                break;
	            }
	            clicked = false;
	            sleep(700, 800);
	        }
	        return clicked;
		} catch (Exception e) {
			return false;
		}
    }
	
	private void playGame() {
		if(userChoseDefendKnight) {
			playDefendKnight();
		} else if (userChoseAttackPortals) {
			playAttackPortals();
		} else if (userChoseRandomGame) {
			playAttackPortals();
			//playRandomGame();
			//println("Random is Unavailable");
		}
	}
	
	private void playRandomGame() {
		while (runScript) {
			if (trialTime.isRunning() || authenticated) {
			} else {
			if (!authenticate(script, username, password)) {
				println("" + rs_username
						+ " is not authorized to run this script!");
				println("Your 10 second trial is complete, please purchase the script!");
				stopScript();
				return;
			}
			}
				
			if(new TexanSupport().goingInBoat()) {
				debugInfo = "On Loader";
				resetGame();
				int tempWorld = getWorlds();
				if(tempWorld != currentWorld) { login = true; loginWorld = tempWorld; }
				if(login) { doLoginStuff(); }
				checkClanChat();
				goInBoat();
				continue;
			}
		setPoints();
        if(!timerRunning) { beginTimer(); }
        switch (randomPortal) {
		case 0:
			if(!westDestroyed()){
	        attackWestPortal();
			}else if(!southWestDestroyed()){
				if(Player.getPosition().distanceTo(southWestPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(southWestPortalTile));
				}
				attackSouthWestPortal();
			}else{
				if(Player.getPosition().distanceTo(southEastPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(southEastPortalTile));
				}
				attackSouthEastPortal();
			}
			break;
		case 1:
			if(!southEastDestroyed()){
			attackSouthEastPortal();
			}else if(!southWestDestroyed()){
				if(Player.getPosition().distanceTo(southWestPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(southWestPortalTile));
				}
				attackSouthWestPortal();
			}else{
				if(Player.getPosition().distanceTo(eastPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(eastPortalTile));
					while(isMoving()){
						sleep(50 ,150);
					}
				}
				attackEastPortal();
			}
			break;
		case 2:
			if(!eastDestroyed()){
			attackEastPortal();
			}else if(!southEastDestroyed()){
				if(Player.getPosition().distanceTo(southEastPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(southEastPortalTile));
				}
				attackSouthEastPortal();
			}else{
				if(Player.getPosition().distanceTo(southWestPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(southWestPortalTile));
				}
				attackSouthWestPortal();
			}
			break;
		case 3:
			if(!southWestDestroyed()){
			attackSouthWestPortal();
			}else if(!southEastDestroyed()){
				if(Player.getPosition().distanceTo(southEastPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(southEastPortalTile));
				}
				attackSouthEastPortal();
			}else{
				if(Player.getPosition().distanceTo(westPortalPos) > 6 && !noPortal()){
					Walking.walkPath(Walking.generateStraightPath(westPortalTile));
				}
				attackWestPortal();
			}
			break;
		default:
	        attackWestPortal();
			break;
		}
		}
	}

	private void playAttackPortals() {	//TODO: here
		while (runScript) {
			if (trialTime.isRunning() || authenticated) {
				//do nothing
			} else {
				if (!authenticate(script, username, password)) {
					println("" + rs_username
							+ " is not authorized to run this script!");
					println("Your 10 second trial is complete, please purchase the script!");
					stopScript();
					return;
				}
			}

		setPoints();
        if(!timerRunning) { beginTimer(); }
        SCRIPT_STATE = getAttackingState();
        
        if(inGame() && knightPos == null) {
        	if(nearSquire()) {
        		setKnight(new RSTile (squirePos.getX() +1, squirePos.getY() -13)); 
        	} else {
        		TexanSupport.knightCloseby();
        	}
        }
        
        switch (SCRIPT_STATE) {
        case goToSpendPoints:
        	debugInfo = "Going to Spend";
        	goTowardsPCBank();
        	break;
        case spendingPoints:
        	debugInfo = "Spending";
        	spendPoints();
        	goInBoat();
        	setPoints();
        	sleep(500,1000);
        	break;
		case goingInBoat:
			debugInfo = "On Loader";
			resetGame();
			int tempWorld = getWorlds();
			if(tempWorld != currentWorld) { login = true; loginWorld = tempWorld; }
			if(login) { doLoginStuff(); }
			checkClanChat();
			goInBoat();
			clickContinue();
			break;
		case inBoat:
			debugInfo = "Waiting in boat";
			if(login) { doLoginStuff(); }
			resetGame();
			break;
		case atBeginning:
			debugInfo = "Spawning";
			resetGame();
			setRun(true);
			if(nearSquire()) {	//just set the squire
				setKnight(new RSTile (squirePos.getX() +1, squirePos.getY() -13));
				setExactPortals();
				tryToSetPortals();
				Walking.walking_timeout = 2000;
				Walking.walkPath(Walking.generateStraightPath(new RSTile(squirePos.getX() + 2, squirePos.getY() - (General.random(12, 13)))));
				Walking.walking_timeout = 5000;
				//				if(!walkPath(Walking.generateStraightPath(new RSTile(squirePos.getX() + 2, squirePos.getY() - (General.random(12, 13)))))){
//					break;
//				}
				while(isMoving()) {
					if(TexanSupport.goingInBoat()) {
						break;
					}
					sleep(50,100);
				}
			}
			break;
		case inGame:
			Walking.walking_timeout = 5000;
			debugInfo = "Waiting on Portal";
			if(!inGame()) {	//weird, shouldn't be here, but adding a failsafe
				break;
			}
			setAutoRetal(true);
			if(portalTilesOnScreen() || portalInMinimap()) { //at a portal tile, but it's down. don't run to middle for a new one
				if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
					attackingPortal = false; //not attacking the portal  splatters: 3727, 3728, 3729
					//RSNPC[] attackRsnpc = NPCs.findNearest(3749, 3748, 3747, 3733, 3735, 3732, 3734, 3752, 3753, 3754, 3755, 3743, 3744, 3762, 3763, 3764, 3765, 3774, 3773);
					RSNPC[] attackRsnpc = NPCs.findNearest("Spinner", "Brawler", "Defiler", "Torcher", "Shifter", "Ravager");
					try {
						if(userChoseDefendKnight) {
							if (attackRsnpc != null) {
								if(npcSouthOfGate(attackRsnpc[0].getPosition())) {
									return;
								}
								if (attackRsnpc[0].isOnScreen()) {
									attackRsnpc[0].click("Attack");
								}
							}
						} else if (userChoseAttackPortals || userChoseRandomGame) {
							if (attackRsnpc != null) {
								if (attackRsnpc[0].isOnScreen()) {
									attackRsnpc[0].click("Attack");
								}
							}
						}
					} catch (Exception e) {
						
					}
				}				
				break;
			}
			if(squirePos == null) {
				TexanSupport.atBeginning(); 	//will set the squire
				if(squirePos == null) {	//if still null, try again
					break;
				}
				Walking.walkPath(Walking.generateStraightPath(new RSTile(squirePos.getX() + 2, squirePos.getY() - (General.random(12, 13)))));
//				if(!walkPath(Walking.generateStraightPath(new RSTile(squirePos.getX() + 2, squirePos.getY() - (General.random(12, 13)))))){
//					break;
//				}
			} else {							//squire already set
					if(!inGame()) { break; }
					if(TexanSupport.knightCloseby()) {
						attack();
						break;
					}
					Walking.walkPath(Walking.generateStraightPath(new RSTile(squirePos.getX() + 2, squirePos.getY() - (General.random(12, 13)))));
//					if(!walkPath(Walking.generateStraightPath(new RSTile(squirePos.getX() + 2, squirePos.getY() - (General.random(12, 13)))))) {
//						break;
//					}
				}
				try {
					if(!inGame()) { break; }
					if(knightPos == null) {
						RSNPC[] voidKnight = NPCs.find(1000, 3782, 3783, 3784, 3785);
						TexanPestControl.setKnight(voidKnight[0].getPosition());
					}
					if(southEastPortalTile == null){
						setExactPortals();
					}
					if(!inGame()) { break; }
					tryToSetPortals();
					setExactPortals();
				} catch (Exception e) { }
				
				tryToSetPortals();
				setExactPortals();
			break;
		case eastOpen:
			Walking.walking_timeout = 5000;
			if(!inGame()) { break; }
			if(knightPos == null) { break; }
				if(!inCombatWithPortals()) {
					debugInfo = "Attacking East";
			        if(eastDropped() && !eastDestroyed()){	
			        	if(Player.getPosition().distanceTo(eastPortalTile) > 4 && !noPortal()){
			        		goToSouthEastCorner();
			        		Walking.walkPath(Walking.generateStraightPath(new RSTile(eastPortalPos.getX(), eastPortalPos.getY())));
//				        	if(!walkPath(Walking.generateStraightPath(new RSTile(eastPortalPos.getX(), eastPortalPos.getY())))) {
//				        		break;
//				        	}
				        	if(!isMoving()){
			        			attackBrawlers();
			        		}
				        	break;
			        	}
			        	attack();
			        }
			     }
			break;
		case westOpen:
			Walking.walking_timeout = 5000;
			if(!inGame()) { break; }
			if(knightPos == null) { break; }
			if(!inCombatWithPortals()){
				debugInfo = "Attacking West";
		        if(westDropped() && !westDestroyed()) {	
		        	if(Player.getPosition().distanceTo(westPortalTile) > 4 && !noPortal()){
		        		goToSouthWestCorner();
		        		Walking.walkPath(Walking.generateStraightPath(new RSTile(westPortalPos.getX(), westPortalPos.getY())));
//			        	if(!walkPath(Walking.generateStraightPath(new RSTile(westPortalPos.getX(), westPortalPos.getY())))){
//			        		break;
//			        	}
			        	if(!isMoving()){
		        			attackBrawlers();
		        		}
			        	break;
		        	}
		        	attack();
		        }  	        
			}
		break;
		case southEastOpen:
			Walking.walking_timeout = 5000;
			if(!inGame()) {  break; }
			if(knightPos == null) { break; }
			if(!inCombatWithPortals()) {
				debugInfo = "Attacking S.E.";
		        if(southEastDropped() && !southEastDestroyed()){
		        	if(Player.getPosition().distanceTo(southEastPortalTile) > 4 && !noPortal()){
		        		goToSouthGateFromMiddle();
		        		Walking.walkPath(Walking.generateStraightPath(new RSTile(southEastPortalPos.getX(), southEastPortalPos.getY())));
//		        		if(!walkPath(Walking.generateStraightPath(new RSTile(southEastPortalPos.getX(), southEastPortalPos.getY())))){
//		        			break;
//		        		}
		        		if(!isMoving()){
		        			attackBrawlers();
		        		}
		        		break;
		        	}
		        	attack();
		        }  	        
			}
		break;
		case southWestOpen:
			Walking.walking_timeout = 5000;
			if(!inGame()) { break; }
			if(knightPos == null) { break; }
			if(!inCombatWithPortals()) {
				debugInfo = "Attacking S.W.";
				if(southWestDropped() && !southWestDestroyed()){	//wouldn't it have stopped here when it got destroyed? thats whyy!!!! cos its dropped even though its destoyed. get it? no,but you seem to.. dam keyboard lol
					if(Player.getPosition().distanceTo(southWestPortalTile) > 4 && !noPortal()){//wait wont work, cos it looks at the tile 	//not close to portal that might work
		        		goToSouthGateFromMiddle();
		        		Walking.walkPath(Walking.generateStraightPath(new RSTile(southWestPortalPos.getX(), southWestPortalPos.getY())));
//		        		if(!walkPath(Walking.generateStraightPath(new RSTile(southWestPortalPos.getX(), southWestPortalPos.getY())))) {
//		        			break;
//		        		}
		        		if(!isMoving()){
		        			attackBrawlers();
		        		}
		        		break;
		        	}
		        	attack();
		        } 
			}
		break;
		case Lost:
			Walking.walking_timeout = 5000;
			debugInfo = "Lost";
			getBackOnTrack();
			break;
		} //end of switch statement
		} //end of while(true)
	} //end of playAttack portals


	private boolean nearSquire() {
		RSNPC[] beginningSquire = NPCs.findNearest(3781);
		if(beginningSquire.length > 0) {
			if(beginningSquire[0].getPosition().getY() <= Player.getPosition().getY()) {
				TexanPestControl.setSquire(beginningSquire[0].getPosition());
				timeNotInCombat = 0;
				return true;
			}
		}
		return false;
	}
	
	private boolean isMoving(){
        int current = Screen.getPixelShift(142, 144, 373, 276, 250);
        sleep(100, 200);
        if(current > 18000){
        	return true;
        }
        return false;
	}

	private void playDefendKnight() {
		while (runScript) {
			if (trialTime.isRunning() || authenticated) {
				//do nothing
			} else {
			if (!authenticate(script, username, password)) {
				println("" + rs_username
						+ " is not authorized to run this script!");
				println("Your 10 minute trial is complete, please purchase the script!");
				stopScript();
				return;
			}
			}
			
		tryToSetPortals();
		setPoints();
        if(!timerRunning) { beginTimer(); }
        SCRIPT_STATE = getDefendingState();
        switch (SCRIPT_STATE) {
        case goToSpendPoints:
        	goTowardsPCBank();
        	break;
        case spendingPoints:
        	spendPoints();
        	break;
		case goingInBoat:
			debugInfo = "On Loader";
			int tempWorld = getWorlds();
			if(tempWorld != currentWorld) { login = true; loginWorld = tempWorld; }
			if(login) { doLoginStuff(); }
			checkClanChat();
			resetGame();
			goInBoat();
			break;
		case inBoat:
			debugInfo = "Waiting in boat";
			if(login) { doLoginStuff(); }
			resetGame();
			break;
		case atBeginning:
			debugInfo = "Start PC boat";
			goToMiddleFromBoat();
			break;
		case atMiddle:
			debugInfo = "At void knight";
			if(!prayerActivated) {
				activatePrayer();
				prayerActivated = true;
			}
			needToGoBack();
			attack();
			break;
		case goBackToMiddle:
			debugInfo = "going back to void knight";
			goToKnight();
			break;
        }
		}
	}
	
	public boolean walkPath(RSTile[] path) { //TODO
	   	for(int i = 0; i < path.length; i++) {
	   		if(inGame()) {
	   			Walking.walkTo(path[i]);
		   		if(runOn()) { sleep(1000); } else { sleep(2000); }
	   		} else {
	   			return false;
	   		}
	   	}
	   	while(Player.isMoving()) {
	   		sleep(50);
	   	}
	   	return true;
	 }
	

	private boolean runOn() {
        int[] settingsArray = Game.getSettingsArray();
        if(settingsArray != null) {
            if(settingsArray.length >= 173) { 
                if((settingsArray[173] == 0) == true) { 
                	return false;
                }
            }
        }
		return true;
	}

	private void attackEastPortal() {
		SCRIPT_STATE = getAttackingStateEast();
        switch (SCRIPT_STATE) {
        case goToSpendPoints:
        	debugInfo = "Going to spend";
        	goTowardsPCBank();
        	break;
        case spendingPoints:
        	debugInfo = "Spending Points";
        	spendPoints();
        	break;
        case goingInBoat:
			debugInfo = "On Loader";
			int tempWorld = getWorlds();
			if(tempWorld != currentWorld) { login = true; loginWorld = tempWorld; }
			if(login) { doLoginStuff(); }
			checkClanChat();
			goInBoat();
			resetGame();
			break;
		case inBoat:
			debugInfo = "Waiting in boat";
			if(login) { doLoginStuff(); }
			resetGame();
			break;
		case atBeginning:
			debugInfo = "Spawned in PC boat";
			targetPortal = eastPortalPos;
			goToEastGateFromBoat();
			break;
		case atEastGate:
			debugInfo = "At East Gate";
			goToEastPortalFromGate();
			break;
		case atEastPortal:
			debugInfo = "At East Portal";
			needToGoBackToPortal();
			currentPortal = eastPortalPos;
			if(!prayerActivated) {
				activatePrayer();
				prayerActivated = true;
			}
			attack();
			break;
		case Lost:
			debugInfo = "Lost :(";
			getBackOnTrack();
			break;
        }		
	}

	private void attackWestPortal() {
		SCRIPT_STATE = getAttackingStateWest();
        switch (SCRIPT_STATE) {
        case goToSpendPoints:
        	debugInfo = "Going to spend";
        	goTowardsPCBank();
        	break;
        case spendingPoints:
        	debugInfo = "Spending Points";
        	spendPoints();
        	break;
		case goingInBoat:
			debugInfo = "On Loader";
			int tempWorld = getWorlds();
			if(tempWorld != currentWorld) { login = true; loginWorld = tempWorld; }
			if(login) { doLoginStuff(); }
			checkClanChat();
			goInBoat();
			resetGame();
			break;
		case inBoat:
			debugInfo = "Waiting in boat";
			if(login) { doLoginStuff(); }
			resetGame();
			break;
		case atBeginning:
			debugInfo = "Spawned in PC boat";
			targetPortal = westPortalPos;
			goToWestGateFromBoat();
			break;
		case atWestGate:
			debugInfo = "At West Gate";
			goToWestPortalFromGate();
			break;
		case atWestPortal:
			debugInfo = "At West Portal";
			needToGoBackToPortal();
			currentPortal = westPortalPos;
			if(!prayerActivated) {
				activatePrayer();
				prayerActivated = true;
			}
			attack();
			break;
		case Lost:
			debugInfo = "Lost :(";
			getBackOnTrack();
			break;
        }
	}

	private void spendPoints() {
		if(buyPointsXP.equals("unused") && buyVoidItems.size() == 0) {
			println("No more items left to buy!");
			while(!Login.logout()) {
				Login.logout();
				sleep(500,600);
			}
			return;
		}
		RSTile landerStart = getLanderTile();
		Color tradingColor = Screen.getColourAt(400, 25);
		Color barColor = Screen.getColourAt(480, 80);
		int r = tradingColor.getRed();
		int g = tradingColor.getGreen();
		int b = tradingColor.getBlue();
		if((r == 73) && (g == 64) && (b == 52)) { //trading!
			if (buyVoidItems.size() > 0) {	//void items left to buy
				if ((barColor.getRed() == 77) && (barColor.getGreen() == 66) && (barColor.getBlue() == 51)) { //viewing XP
					Mouse.clickBox(479, 242	, 486, 238, 1);
				}
				String itemToBuy = buyVoidItems.get(0);
				if(itemToBuy.equals("mace")) {
					Mouse.clickBox(82, 123, 136, 131, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				if(itemToBuy.equals("seal")) {
					Mouse.clickBox(300, 240, 344, 247, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				if(itemToBuy.equals("top")) {
					Mouse.clickBox(300, 129, 340, 124, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				if(itemToBuy.equals("bottom")) {
					Mouse.clickBox(83, 168, 119, 163, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				if(itemToBuy.equals("gloves")) {
					Mouse.clickBox(300, 168, 340, 163, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				if(itemToBuy.equals("mageHelm")) {
					Mouse.clickBox(83, 206, 123, 201, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				if(itemToBuy.equals("rangerHelm")) {
					Mouse.clickBox(300, 206, 340, 201, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				if(itemToBuy.equals("meleeHelm")) {
					Mouse.clickBox(83, 245, 123, 239, 1);
					sleep(500, 1000);
					Mouse.clickBox(228, 295, 269, 285, 1);
					buyVoidItems.remove(0);
					sleep(500, 1000);
					Walking.walkPath(Walking.generateStraightPath(landerStart));
					sleep(1500, 2000);
					while(Player.isMoving()) { sleep(50,100); }
					return;
				}
				
			} else if(!buyPointsXP.equals("None")) {	//get XP
				if ((barColor.getRed() == 77) && (barColor.getGreen() == 66) && (barColor.getBlue() == 51)) { //viewing XP
					if(buyPointsXP.equals("Prayer")) {
						Mouse.clickBox(188, 186	, 224, 180, 1);
						sleep(500, 1000);
						Mouse.clickBox(228, 295, 269, 285, 1);
						sleep(500, 1000);
						Walking.walkPath(Walking.generateStraightPath(landerStart));
						sleep(1500, 2000);
						while(Player.isMoving()) { sleep(50,100); }
						return;
					}
					if(buyPointsXP.equals("Strength")) {
						Mouse.clickBox(405, 74, 443, 66, 1);
						sleep(500, 1000);
						Mouse.clickBox(228, 295, 269, 285, 1);
						sleep(500, 1000);
						Walking.walkPath(Walking.generateStraightPath(landerStart));
						sleep(1500, 2000);
						while(Player.isMoving()) { sleep(50,100); }
						return;
					}
					if(buyPointsXP.equals("Attack")) {
						Mouse.clickBox(186, 74	, 223, 66, 1);
						sleep(500, 1000);
						Mouse.clickBox(228, 295, 269, 285, 1);
						sleep(500, 1000);
						Walking.walkPath(Walking.generateStraightPath(landerStart));
						sleep(1500, 2000);
						while(Player.isMoving()) { sleep(50,100); }
						return;
					}
					if(buyPointsXP.equals("Range")) {
						Mouse.clickBox(405, 110	, 444, 104, 1);
						sleep(500, 1000);
						Mouse.clickBox(228, 295, 269, 285, 1);
						sleep(500, 1000);
						Walking.walkPath(Walking.generateStraightPath(landerStart));
						sleep(1500, 2000);
						while(Player.isMoving()) { sleep(50,100); }
						return;
					}
					if(buyPointsXP.equals("Defence")) {
						Mouse.clickBox(186, 110	, 224, 103, 1);
						sleep(500, 1000);
						Mouse.clickBox(228, 295, 269, 285, 1);
						sleep(500, 1000);
						Walking.walkPath(Walking.generateStraightPath(landerStart));
						sleep(1500, 2000);
						while(Player.isMoving()) { sleep(50,100); }
						return;
					}
					if(buyPointsXP.equals("Magic")) {
						Mouse.clickBox(186, 147	, 223, 143, 1);
						sleep(500, 1000);
						Mouse.clickBox(228, 295, 269, 285, 1);
						sleep(500, 1000);
						Walking.walkPath(Walking.generateStraightPath(landerStart));
						sleep(1500, 2000);
						while(Player.isMoving()) { sleep(50,100); }
						return;
					}
					if(buyPointsXP.equals("HP")) {
						Mouse.clickBox(408, 148	, 444, 143, 1);
						sleep(500, 1000);
						Mouse.clickBox(228, 295, 269, 285, 1);
						sleep(500, 1000);
						Walking.walkPath(Walking.generateStraightPath(landerStart));
						sleep(1500, 2000);
						while(Player.isMoving()) { sleep(50,100); }
						return;
					}
				} else {
					return;
				}
			}
		} else {
			RSNPC[] voidKnightPoints = NPCs.findNearest(3788, 3789);	//TODO
			if(voidKnightPoints.length > 0) {
				voidKnightPoints[0].click("Exchange");
			}
		}
	}

	private void goTowardsPCBank() {
		Walking.walkTo(new RSTile(2663, 2650, 0));
		sleep(1200, 1500);
	}

	private void attackSouthEastPortal() {
		SCRIPT_STATE = getAttackingStateSouthEast();
        switch (SCRIPT_STATE) {
        case goToSpendPoints:
        	debugInfo = "Going to spend";
        	goTowardsPCBank();
        	break;
        case spendingPoints:
        	debugInfo = "Spending Points";
        	spendPoints();
        	break;
		case goingInBoat:
			debugInfo = "On Loader";
			int tempWorld = getWorlds();
			if(tempWorld != currentWorld) { login = true; loginWorld = tempWorld; }
			if(login) { doLoginStuff(); }
			checkClanChat();
			goInBoat();
			resetGame();
			break;
		case inBoat:
			debugInfo = "Waiting in boat";
			if(login){ doLoginStuff();}
			resetGame();
			break;
		case atBeginning:
			debugInfo = "Spawned in PC boat";
			targetPortal = southEastPortalPos;
			goToMiddleFromBoat();
			break;
		case atMiddle:
			debugInfo = "At void knight";
			goToSouthGateFromMiddle();
			break;
		case atSouthGate:
			debugInfo = "At South Gate";
			needToOpenGate("south");
			goToSouthEastPortalFromGate();
			break;
		case atSouthEastPortal:
			debugInfo = "At South Portal";
			needToGoBackToPortal();
			currentPortal = southEastPortalPos;
			if(!prayerActivated) {
				activatePrayer();
				prayerActivated = true;
			}
			attack();
			break;
		case Lost:
			debugInfo = "Lost :(";
			getBackOnTrack();
			break;
        }		
	}

	private void attackSouthWestPortal() {
		SCRIPT_STATE = getAttackingStateSouthWest();
        switch (SCRIPT_STATE) {
        case goToSpendPoints:
        	debugInfo = "Going to spend";
        	goTowardsPCBank();
        	break;
        case spendingPoints:
        	debugInfo = "Spending Points";
        	spendPoints();
        	break;
		case goingInBoat:
			debugInfo = "On Loader";
			int tempWorld = getWorlds();
			if(tempWorld != currentWorld) { login = true; loginWorld = tempWorld; }
			if(login) { doLoginStuff(); }
			checkClanChat();
			goInBoat();
			resetGame();
			break;
		case inBoat:
			debugInfo = "Waiting in boat";
			if(login){doLoginStuff();}
			resetGame();
			break;
		case atBeginning:
			debugInfo = "Spawned in PC boat";
			targetPortal = southWestPortalPos;
			goToMiddleFromBoat();
			break;
		case atMiddle:
			debugInfo = "At void knight";
			goToSouthGateFromMiddle();
			break;
		case atSouthGate:
			debugInfo = "At South Gate";
			needToOpenGate("south");
			goToSouthWestPortalFromGate();
			break;
		case atSouthWestPortal:
			debugInfo = "At South Portal";
			needToGoBackToPortal();
			currentPortal = southWestPortalPos;
			if(!prayerActivated) {
				activatePrayer();
				prayerActivated = true;
			}
			attack();
			break;
		case Lost:
			debugInfo = "Lost :(";
			getBackOnTrack();
			break;
        }		
	}
	
	public TexanEnum getAttackingStateEast() {
		TexanSupport support = new TexanSupport();
		if (support.needToSpendPoints()) {
			return TexanEnum.goToSpendPoints;
		} else if (support.nearPCBank()) {
			return TexanEnum.spendingPoints;
		} else if (support.goingInBoat()) {
			if(clickContinue()) {
				setPoints();
				return SCRIPT_STATE;
			}
			return TexanEnum.goingInBoat;
		} else if (support.locatedInBoat()) {
			return TexanEnum.inBoat;
		} else if (support.atBeginning()) {
			return TexanEnum.atBeginning;
		} else if (support.knightCloseby()) {
			return TexanEnum.atMiddle;
		} else if (support.atEastGate()) {
			return TexanEnum.atEastGate;
		} else if (support.atEastPortal()) {
			return TexanEnum.atEastPortal;
		} else if (isMoving()) {
			return SCRIPT_STATE;
		}
		return TexanEnum.Lost;
	}
	
	public TexanEnum getAttackingStateWest() {
		TexanSupport support = new TexanSupport();
		if (support.needToSpendPoints()) {
			return TexanEnum.goToSpendPoints;
		} else if (support.nearPCBank()) {
			return TexanEnum.spendingPoints;
		} else if (support.goingInBoat()) {
			if(clickContinue()) {
				setPoints();
				return SCRIPT_STATE;
			}
			return TexanEnum.goingInBoat;
		} else if (support.locatedInBoat()) {
			return TexanEnum.inBoat;
		} else if (support.atBeginning()) {
			return TexanEnum.atBeginning;
		} else if (support.knightCloseby()) {
			return TexanEnum.atMiddle;
		} else if (support.atWestGate()) {
			return TexanEnum.atWestGate;
		} else if (support.atWestPortal()) {
			return TexanEnum.atWestPortal;
		} else if (isMoving()) {
			return SCRIPT_STATE;
		}
		return TexanEnum.Lost;
	}
	
	public TexanEnum getAttackingStateSouthEast() {
		TexanSupport support = new TexanSupport();
		if (support.needToSpendPoints()) {
			return TexanEnum.goToSpendPoints;
		} else if (support.nearPCBank()) {
			return TexanEnum.spendingPoints;
		} else if (support.goingInBoat()) {
			if(clickContinue()) {
				setPoints();
				return SCRIPT_STATE;
			}
			return TexanEnum.goingInBoat;
		} else if (support.locatedInBoat()) {
			return TexanEnum.inBoat;
		} else if (support.atBeginning()) {
			return TexanEnum.atBeginning;
		} else if (support.knightCloseby()) {
			return TexanEnum.atMiddle;
		} else if (support.atSouthGate()) {
			return TexanEnum.atSouthGate;
		} else if (support.atSouthEastPortal()) {
			return TexanEnum.atSouthEastPortal;
		} else if (isMoving()) {
			return SCRIPT_STATE;
		}else return TexanEnum.Lost;	//TODO: changed
	}

	public TexanEnum getAttackingStateSouthWest() {
		TexanSupport support = new TexanSupport();
		if (support.needToSpendPoints()) {
			return TexanEnum.goToSpendPoints;
		} else if (support.nearPCBank()) {
			return TexanEnum.spendingPoints;
		} else if (support.goingInBoat()) {
			if(clickContinue()) {
				setPoints();
				return SCRIPT_STATE;
			}
			return TexanEnum.goingInBoat;
		} else if (support.locatedInBoat()) {
			return TexanEnum.inBoat;
		} else if (support.atBeginning()) {
			return TexanEnum.atBeginning;
		} else if (support.knightCloseby()) {
			return TexanEnum.atMiddle;
		} else if (support.atSouthGate()) {
			return TexanEnum.atSouthGate;
		} else if (support.atSouthWestPortal()) {
			return TexanEnum.atSouthWestPortal;
		} else if (isMoving()) {
			return SCRIPT_STATE;
		}
		return TexanEnum.Lost;
	}

	public TexanEnum getDefendingState() {
		TexanSupport support = new TexanSupport();
		if(support.needToSpendPoints()){
			return TexanEnum.goToSpendPoints;
		}
		else if(support.nearPCBank()){
			return TexanEnum.spendingPoints;
		}
		else if (TexanSupport.goingInBoat()) {
			if(clickContinue()) {
				setPoints();
				return SCRIPT_STATE;
			}
			return TexanEnum.goingInBoat;
		} else if (support.locatedInBoat()) {
			return TexanEnum.inBoat;
		} else if (TexanSupport.atBeginning()) {
			return TexanEnum.atBeginning;
		} else if (TexanSupport.knightCloseby()) {
			return TexanEnum.atMiddle;
		} else if (support.needToMoveBack()) {
			return TexanEnum.goBackToMiddle;
		} else if (isMoving()) {
			return SCRIPT_STATE;
		}
		return TexanEnum.atBeginning;
	}
	
	public TexanEnum getAttackingState() {
		TexanSupport support = new TexanSupport();
		sleep(300);
		if(TexanSupport.onIsland()) {
			resetGame();
		}
		if(support.nearPCBank()) {
			setPoints();
			return TexanEnum.spendingPoints;
		} else if(support.needToSpendPoints()) {
			setPoints();
			return TexanEnum.goToSpendPoints;
		} else if(TexanSupport.goingInBoat()){
			setPoints();
			return TexanEnum.goingInBoat;
		} else if(support.locatedInBoat()){
			return TexanEnum.inBoat;
		} else if(TexanSupport.atBeginning()) {
			return TexanEnum.atBeginning;
		} else if(support.westPortalOpen()) {
			return TexanEnum.westOpen;
		} else if(support.eastPortalOpen()) {
			return TexanEnum.eastOpen;
		} else if(support.southEastPortalOpen()) {
			return TexanEnum.southEastOpen;
		} else if(support.southWestPortalOpen()) {
			return TexanEnum.southWestOpen;
		} else if(support.inGame()){
			return TexanEnum.inGame;
		} else {
			return TexanEnum.Lost;
		}
	}
	
	static boolean inGameBoolean(){
		return inGame();
	}
	
	private boolean portalInMinimap() {
		Point click = Projection.tileToMinimap(westPortalTile);
		Point click2 = Projection.tileToMinimap(eastPortalTile);
		Point click3 = Projection.tileToMinimap(southEastPortalTile);
		Point click4 = Projection.tileToMinimap(southWestPortalTile);
		if(Projection.isInMinimap(click) || Projection.isInMinimap(click2) || Projection.isInMinimap(click3) || Projection.isInMinimap(click4)) {
			return true;
		}
		return false;
	}
	
	private void getBackOnTrack() {
		if(isMoving()) {
			return;
		}
		RSTile bank = new RSTile(2663, 2650, 0);
		RSTile landerStart = getLanderTile();
		Point click = Projection.tileToMinimap(landerStart);
		if(TexanSupport.onIsland()) {
			if(currentPoints == 250) {
				Walking.walkPath(Walking.generateStraightPath(bank));
				return;
			}
			if(Projection.isInMinimap(click)) {
				Walking.walkPath(Walking.generateStraightPath(landerStart));	//back at the lander
				if(Player.getPosition().distanceTo(landerStart) < 3) {
					return;
				}
			}
			if(Player.getPosition().distanceTo(bank) < 10 ) {
				Walking.walkPath(Walking.generateStraightPath(bank));
				if(getVoidPoints() >= 250) {
					return;			//need to spend, and now at bank
				}
			}
		}
		if(inGame()) {
			if(currentPortal != null) {
				needToGoBackToPortal();
				sleep(500,1000);
				return;
			} else if (targetGate != null) {
				Walking.walkPath(Walking.generateStraightPath(targetGate));
				sleep(500,1000);
				return;
			} else if (knightPos != null) {
				Walking.walkPath(Walking.generateStraightPath(knightPos));
				sleep(500,1000);
				return;
			}
		} else if (TexanSupport.onIsland()) {
			Walking.walkPath(Walking.generateStraightPath(landerStart));
			return;
		}
	}
	
	private void needToGoBackToPortal() {
		if(currentPortal != null) {
			if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
				if(Player.getPosition().distanceTo(currentPortal) > 4) {
					timeNotInCombat = 0;
					Walking.walkTo(currentPortal);
				}
			}
		}
	}

	private void goToEastPortalFromGate() {
		timeNotInCombat = 0;
		if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
			RSTile eastPortal = new RSTile(squirePos.getX() + 22, squirePos.getY() - 17);
			Walking.walkTo(eastPortal);
			sleep(900,1200);
			while(isMoving()){
				sleep(300, 450);
			}
		}
	}
	private void goToEastGate(){
		timeNotInCombat = 0;
		if(squirePos.getY() <= Player.getPosition().getY()) {
			RSTile inBetweenBoatAndGate = new RSTile(squirePos.getX() + 9, squirePos.getY()-7);
			walkPath(Walking.generateStraightPath(inBetweenBoatAndGate));
			sleep(1200, 1400);
			while(isMoving()){
				sleep(200, 400);
			}
		}
	}
	
	private void goToWestGateFromWestPortal(){
		timeNotInCombat = 0;
		if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
			RSTile westGate = new RSTile(squirePos.getX() -12, squirePos.getY()-14);
			walkPath(Walking.generateStraightPath(westGate));
			sleep(800, 1400);
			while(isMoving()){
				sleep(200, 500);
			}
		}
	}
	private void goFromEastToWest2(){
		timeNotInCombat = 0;
		if(getAttackingState() == TexanEnum.atEastPortal){
			goToEastGate();
			needToOpenGate("east");
		}
			goToWestGateFromWestPortal();
			needToOpenGate("west");
			goToWestPortalFromGate();
	}
	
	private void goFromWestToEas2t(){
		timeNotInCombat = 0;
		if(getAttackingState() == TexanEnum.atWestPortal){
			goToWestGateFromWestPortal();
			needToOpenGate("west");
		}
			goToEastGate();
			needToOpenGate("east");
			goToEastPortalFromGate();
	}

	private void goToEastGateFromBoat() {
		timeNotInCombat = 0;
		if(squirePos.getY() <= Player.getPosition().getY()) {
			RSTile inBetweenBoatAndGate = new RSTile(squirePos.getX() + 9, squirePos.getY()-7);
			Walking.walkTo(inBetweenBoatAndGate);
			sleep(1200, 1400);
			return;
		}
		RSTile eastGate = new RSTile(squirePos.getX() + 14, squirePos.getY() - 14);
		targetGate = eastGate;
		Walking.walkTo(eastGate);
		sleep(1200, 1400);
	}
	
	private void goToEastGateFromMiddle() {
		timeNotInCombat = 0;
		if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
			RSTile eastGate = new RSTile(squirePos.getX() + 12, squirePos.getY()-14);
			targetGate = eastGate;
			Walking.walkTo(eastGate);
			sleep(800, 1400);
		}
	}
	
	private void goToSouthWestPortalFromGate() {
		timeNotInCombat = 0;
		if(Player.getPosition().distanceTo(knightPos) < 10) {
			Walking.walkTo(new RSTile(Player.getPosition().getX() - 10 + General.random(-2, 2), Player.getPosition().getY() - 10 + General.random(-2, 2)));
			sleep(1200, 1500);
			return;
		}
		while(isMoving()) {
			sleep(25, 50);
		}
		Walking.walkTo(new RSTile(southEastPortalPos.getX() + General.random(-2, 2), southEastPortalPos.getY() + General.random(-2, 2)));
		sleep(1500, 2200);
	}
	
	private void goToSouthEastPortalFromGate() {
		timeNotInCombat = 0;
		if(Player.getPosition().distanceTo(knightPos) < 10) {
			Walking.walkTo(new RSTile(Player.getPosition().getX() + 10 + General.random(-2, 2), Player.getPosition().getY() - 10 + General.random(-2, 2)));
			sleep(1200, 1500);
			return;
		}
		while(isMoving()) {
			sleep(25, 50);
		}
		Walking.walkTo(new RSTile(southEastPortalPos.getX() + General.random(-2, 2), southEastPortalPos.getY() + General.random(-2, 2)));
		sleep(1500, 2200);
	}
	
	private void goToSouthGateFromMiddle2() {
		timeNotInCombat = 0;
		RSTile southGate = new RSTile(knightPos.getX(), knightPos.getY()-6);
		southGatePos = southGate;
		targetGate = southGate;
		Walking.walkTo(southGate);
		sleep(800, 1400);
	}
	
	private void goToWestGateFromBoat() {
		timeNotInCombat = 0;
		if(squirePos.getY() <= Player.getPosition().getY()) {
			RSTile inBetweenBoatAndGate = new RSTile(squirePos.getX() -4, squirePos.getY()-7);
			Walking.walkTo(inBetweenBoatAndGate);
			sleep(300, 500);
			return;
		}
		RSTile westGate = new RSTile(squirePos.getX() -12, squirePos.getY()-14);
		targetGate = westGate;
		Walking.walkTo(westGate);
		sleep(600, 800);
	}
	
	private void goToWestGateFromMiddle() {
		timeNotInCombat = 0;
		if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
			RSTile westGate = new RSTile(squirePos.getX() -12, squirePos.getY()-14);
			Walking.walkTo(westGate);
			sleep(800, 1400);
		}
	}
	
	private void goToWestPortalFromGate() {
		timeNotInCombat = 0;
		if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
			RSTile westPortal = new RSTile(squirePos.getX() -24, squirePos.getY()-15);
			Walking.walkTo(westPortal);
			sleep(200,300);
		}
	}
	
	private void goToSouthGateFromMiddle() {
		timeNotInCombat = 0;
		if(TexanSupport.knightCloseby() && !isMoving()){
		RSTile southGate = new RSTile(knightPos.getX(), knightPos.getY()-4);
		southGatePos = southGate;
		targetGate = southGate;
		Walking.walkTo(southGate);
		sleep(800, 1400);
		}
	}
	private void westGateFromMiddle(){
		timeNotInCombat = 0;
		if(TexanSupport.knightCloseby() && !isMoving()){
		RSTile innerWestGate = new RSTile(knightPos.getX()- 10, knightPos.getY());
		walkPath(Walking.generateStraightPath(innerWestGate));
		while(isMoving()){
			sleep(50, 120);
		}
		}
	}
	
	private void eastGateFromMiddle(){
		timeNotInCombat = 0;
		if(TexanSupport.knightCloseby() && !isMoving()){
		RSTile innerEastGate = new RSTile(knightPos.getX()+10, knightPos.getY());
		walkPath(Walking.generateStraightPath(innerEastGate));
		while(isMoving()){sleep(12, 64);}
		}
	}
	
	private void goToSouthWestCorner() {
		if(southWestPortalPos == null) {
			return;
		}
		if(TexanSupport.knightCloseby()) {
			return;
		}
		if(Player.getPosition().distanceTo(southWestPortalPos) < 7) {	//already really close to corner
			return;
		}
		if(Player.getPosition().distanceTo(eastPortalPos) > 15) {	//not close to the portal
			return;
		}
        Walking.walking_timeout = 10000;
		Point click = Projection.tileToMinimap(southWestPortalTile);
		if(Projection.isInMinimap(click)) {
	 		Mouse.click(click, 1);
	 		sleep(4000,4500);
		} else {
			walkPath(Walking.generateStraightPath(southWestPortalTile));
	 		sleep(3000,3500);
		}
	}
	
	private void goToSouthEastCorner() {
		if(southEastPortalPos == null) {
			return;
		}
		if(TexanSupport.knightCloseby()) {
			return;
		}
		if(Player.getPosition().distanceTo(southEastPortalTile) < 7) {	//already really close to corner
			return;
		}
		if(Player.getPosition().distanceTo(westPortalPos) > 15) {	//not close to the portal
			return;
		}
        Walking.walking_timeout = 10000;
		Point click = Projection.tileToMinimap(southEastPortalTile);
		if(Projection.isInMinimap(click)) {
	 		Mouse.click(click, 1);
	 		sleep(4000,4500);
		} else {
			walkPath(Walking.generateStraightPath(southEastPortalTile));
	 		sleep(3000,3500);
		}
	}
	
//	private void goFromEastToWest(){
//		timeNotInCombat = 0;
//		if(getAttackingState() == TexanEnum.atEastPortal){
//			goToEastGate();
//			needToOpenGate("east");
//		}
//			westGateFromMiddle();
//			needToOpenGate("west");
//		//	goToWestPortalFromGate();
//	}

	
//	private void goFromWestToEast(){
//		timeNotInCombat = 0;
//		if(getAttackingState() == TexanEnum.atWestPortal){
//			goToWestGateFromWestPortal();
//			needToOpenGate("west");
//		}
//			eastGateFromMiddle();
//			needToOpenGate("east");
//		//	goToEastPortalFromGate();
//	}

	

	
	private void resetGame() {
		knightPos = null;
		squirePos = null;
		portalsSet = false;
		currentPortal = null;
		southEastPortalPos = null;
		southWestPortalPos = null;
		westPortalPos = null;
		eastPortalPos = null;
		southGatePos = null;
		targetGate = null;
		targetPortal = null;
		eastPortalTile = null;
		westPortalTile = null;
		southEastPortalTile = null;
		southWestPortalTile = null;
		randomPortal = rand.nextInt(3 - 0 + 1);
		attackingPortal = false;
		sleep(200, 300);
	}
	
	public static int getVoidPoints() {
		return currentPoints;
	}

	public static void tryToSetPortals() {
		if(knightPos == null) {
			return;
		} else {
			if(westPortalPos == null) {	//none of portals set yet
				westPortalPos = new RSTile(knightPos.getX() - 25, knightPos.getY());
				eastPortalPos = new RSTile(knightPos.getX() + 23, knightPos.getY() - 3);
				southWestPortalPos = new RSTile(knightPos.getX() - 10, knightPos.getY() - 19);
				southEastPortalPos = new RSTile(knightPos.getX() + 14, knightPos.getY() - 20);
				southWestCornerPos = new RSTile(knightPos.getX() - 7, knightPos.getY() - 15);
				southEastCornerPos = new RSTile(knightPos.getX() + 11, knightPos.getY() - 16);
				portalsSet = true;
				timeNotInCombat = 0;
			}
		}
	}
	
	public static void setExactPortals(){
		if(knightPos == null) {
			return;
		} else{
			westPortalTile = new RSTile(knightPos.getX() - 27, knightPos.getY());
			eastPortalTile = new RSTile(knightPos.getX() + 25, knightPos.getY() - 3);
			southWestPortalTile = new RSTile(knightPos.getX() - 10, knightPos.getY() - 22);
      		southEastPortalTile = new RSTile(knightPos.getX() + 14, knightPos.getY() - 21);
			southWestCornerPos = new RSTile(knightPos.getX() - 7, knightPos.getY() - 15);
			southEastCornerPos = new RSTile(knightPos.getX() + 11, knightPos.getY() - 16);
			timeNotInCombat = 0;
      	}
	}

	private void needToOpenGate(String string) {
		timeNotInCombat = 0;
		RSObject[] gateClosed = Objects.findNearest(15, 14235);
		if(string.equals("south")) {
			if((knightPos.getY() - Player.getPosition().getY()) < 7) {
				RSObject[] southGateClosed = Objects.findNearest(15, 14235);
				if(southGateClosed.length > 0) {
					if(southGateClosed[0].getPosition().distanceTo(Player.getPosition()) < 8) {
						southGateClosed[0].click("Open");
						sleep(1000, 1750);
						if(isMoving()){
							sleep(2000, 3500);
						}
						return;
					}
				}
			} else {
				return;
			}
		}
		if(gateClosed.length > 0) {
			if(gateClosed[0].getPosition().isOnScreen()) {
				gateClosed[0].click("Open");
				sleep(1000, 1750);
			}
		}		
	}

	private void goToKnight() {
		timeNotInCombat = 0;
		try {	
			int voidKnightY = knightPos.getY();
			int playerY = Player.getPosition().getY();
			if((voidKnightY - playerY) > 7) {
				RSObject[] southGateClosed = Objects.findNearest(15, 14235);
				if(southGateClosed.length > 0) {
					if(southGateClosed[0].getPosition().distanceTo(Player.getPosition()) < 8) {
						southGateClosed[0].click("Open");
						sleep(1000, 1750);
					}
				}
			}
			Walking.walkTo(knightPos);
		} catch (Exception e) {
		}
	}

	private void attack() {
		if(!inGame()) { return; }
		if(TexanSupport.goingInBoat() || TexanSupport.atBeginning()){
			return;
		}
		timeNotInCombat = 0;
		try {
		while (isMoving()) {
			if(!inGame()) { return; }
			attackingPortal = false; //moving, not attacking
		}
		if(allowSpecialAttack) {
			if ((Game.getSettingsArray()[300] / 10) > 75) {
				if (!GameTab.getOpen().equals(TABS.COMBAT)) {
					GameTab.open(TABS.COMBAT);
				}
				if (!(Game.getSettingsArray()[301] == 1)) {
					while(!Game.isUptext("Use Special Attack")) {
						if(TexanSupport.goingInBoat() || TexanSupport.atBeginning()){
							return;
						}
						if (!GameTab.getOpen().equals(TABS.COMBAT)) {
							GameTab.open(TABS.COMBAT);
						}
						Mouse.moveBox(575, 415, 710, 428);
						sleep(50, 100);
					}
		            Mouse.clickBox(575, 415, 710, 428, 0);
		            sleep(200,300);
		        }
			}
			checkChangedCombatStyle();
		}
		if(!freeVersion) {
			attackPortal();
		}
		if (Player.getRSPlayer().getInteractingCharacter() == null) {	//no one interacting with me
			attackingPortal = false; //not attacking the portal  splatters: 3727, 3728, 3729
			RSNPC[] attackRsnpc = NPCs.findNearest("Spinner", "Brawler", "Defiler", "Torcher", "Shifter", "Ravager");
			if(userChoseDefendKnight) {
				if (attackRsnpc != null) {
					if(npcSouthOfGate(attackRsnpc[0].getPosition())) {
						return;
					}
					if (attackRsnpc[0].isOnScreen()) {
						DynamicClicking.clickRSTile(attackRsnpc[0].getAnimablePosition(), "Attack");
					}
				}
			} else if (userChoseAttackPortals || userChoseRandomGame) {
				if(currentPortal.distanceTo(attackRsnpc[0].getPosition()) < 7) {
					if (attackRsnpc[0].isOnScreen()) {
						DynamicClicking.clickRSTile(attackRsnpc[0].getAnimablePosition(), "Attack");
					}
				}
			}
		}
		} catch (Exception e) {
		}
	}
	
	private void checkChangedCombatStyle() {
		if (!GameTab.getOpen().equals(TABS.COMBAT)) {
			GameTab.open(TABS.COMBAT);
		}
	
		if(combatStyle.equals("att")) {
			Color attack1 = Screen.getColourAt(626, 275);
			if(attack1.getRed() < 100) {
				Mouse.clickBox(574, 286, 630, 255, 1);
				sleep(300,500);
			}
		} else if(combatStyle.equals("str1")) {
			Color strength1 = Screen.getColourAt(713, 278);
			if(strength1.getRed() < 100) {
				Mouse.clickBox(659, 287, 711, 257, 1);
				sleep(300,500);
			}

		} else if(combatStyle.equals("str2")) {
			Color strength2 = Screen.getColourAt(625, 325);
			if(strength2.getRed() < 100) {
				Mouse.clickBox(575, 340, 626, 310, 1);
				sleep(300,500);
			}

		} else if(combatStyle.equals("def")) {
			Color defence = Screen.getColourAt(659, 315);
			if(defence.getRed() < 100) {
				Mouse.clickBox(658, 341, 710, 308, 1);
				sleep(300,500);
			}
		}
	}

	private void attackPortal(){
		timeNotInCombat = 0;
		attackBrawlers();
		attackSpinner();
		if(!spinnersPresent()){
			RSNPC[] Portals = NPCs.find("Portal");
			if(Portals.length > 0) {
				if(portalsOnScreen()){
					if(!prayerActivated) {
						activatePrayer();
						prayerActivated = true;
					}
					if(!attackingPortal) {

						if(Portals[0].isOnScreen()) {
							Portals[0].click("Attack Portal", randomness, offset);
							attackingPortal = true;
						} else {
							Camera.setCameraRotation(Camera.getTileAngle(Portals[0].getPosition()) + General.random(-25, 25));
							sleep(100, 200);
							Portals[0].click("Attack Portal", randomness, offset);
							attackingPortal = true;
						}
					}
				}
			}
		}
}
	
	private void attackBrawlers(){
		timeNotInCombat = 0;
		RSNPC[] Brawler = NPCs.find("Brawler");
		if(Brawler.length > 0){
			if(Brawler[0].isOnScreen() && Player.getPosition().distanceTo(Brawler[0].getPosition()) < 3){
				Brawler[0].getModel().click("Attack Brawler");
				sleep(100);
				while(Player.getRSPlayer().isInCombat() && Brawler[0].isOnScreen()){
					sleep(50, 150);
				}
			}
		}
	}
	private boolean inCombatWithPortals(){
		timeNotInCombat = 0;
		if(noPortal()) {
			return false;
		}
		RSNPC[] Portal = NPCs.find("Portal");
		if(Portal.length > 0)
		if(inCombat() && portalsOnScreen()){
			return true;
		}
		return false;
	}
	private boolean inCombat(){
		if(Player.getAnimation() > 0){
			return true;
		}
		return false;
	}
	
	private boolean noPortal(){
		RSNPC[] Portal = NPCs.find("Portal");
		if(Portal.length > 0 && Portal[0].isOnScreen()){
			return true;
		}
		return false;
	}
	private boolean portalsOnScreen(){
		timeNotInCombat = 0;
		if(southEastPortalOnScreen() || southWestPortalOnScreen() || eastPortalOnScreen() || westPortalOnScreen()){
			return true;
		}
		return false;
	}
		
	private boolean spinnersPresent(){
		timeNotInCombat = 0;
		RSNPC[] Spinner = NPCs.find("Spinner");
		if(Spinner.length > 0 && Spinner[0].isOnScreen()){
			return true;
		}
		return false;
	}
		
	
	private boolean eastPortalOnScreen(){
		timeNotInCombat = 0;
		if(Projection.isInViewport(Projection.tileToScreen(eastPortalTile, 1)) && eastDropped() && !eastDestroyed()){
			return true;
		}
		return false;
	}
	
	private boolean portalTilesOnScreen(){ 
		if(Projection.isInViewport(Projection.tileToScreen(southEastPortalTile, 1))) {
			return true;
		}
		if(Projection.isInViewport(Projection.tileToScreen(southWestPortalTile, 1))) {
			return true;
		}
		if(Projection.isInViewport(Projection.tileToScreen(eastPortalTile, 1))) {
			return true;
		}
		if(Projection.isInViewport(Projection.tileToScreen(westPortalTile, 1))) {
			return true;
		}
		return false;
	}
	
	private boolean westPortalOnScreen(){
		timeNotInCombat = 0;
		if(Projection.isInViewport(Projection.tileToScreen(westPortalTile, 1)) && westDropped() && !westDestroyed()){
			return true;
		}
		return false;
	}
	
	private boolean southEastPortalOnScreen(){
		timeNotInCombat = 0;
		if(Projection.isInViewport(Projection.tileToScreen(southEastPortalTile, 1)) && southEastDropped() && !southEastDestroyed()){
			return true;
		}
		return false;
	}
	private boolean southWestPortalOnScreen(){
		timeNotInCombat = 0;
		if(Projection.isInViewport(Projection.tileToScreen(southWestPortalTile, 1)) && southWestDropped() && !southWestDestroyed()){
			return true;
		}
		return false;
	}
	
	
	private void attackSpinner(){
		timeNotInCombat = 0;
		RSNPC[] Spinner = NPCs.find("Spinner");
		if(Spinner.length > 0){
			if(Spinner[0].isOnScreen()){
				DynamicClicking.clickRSNPC(Spinner[0], "Attack Spinner");
				sleep(200, 5500);
				while(Spinner[0].isInteractingWithMe()){
					sleep(200, 250);
				}
			}
		}
	}
	
	private boolean eastDropped(){
		if(!Screen.coloursMatch(new Color(69,62, 62),Screen.getColourAt(406, 58), new Tolerance(10))){
			return true;
		}
		return false;
	}
	
	private boolean westDropped(){
		if(!Screen.coloursMatch(new Color(69, 62, 62), Screen.getColourAt(370, 56), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southWestDropped(){
		if(!Screen.coloursMatch(new Color(80, 72, 72), Screen.getColourAt(483, 53), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southEastDropped(){  
		if(!Screen.coloursMatch(new Color(80, 72, 72), Screen.getColourAt(443, 55), new Tolerance(10))){
			return true;
		}
		return false;
	}
	
	private boolean westDestroyed(){
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(370, 44), new Tolerance(10))){
			return true;
		}
		return false;
	}
	
	private boolean eastDestroyed(){
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(408, 45), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southWestDestroyed(){
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(484, 45), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southEastDestroyed(){
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(444, 45), new Tolerance(10))){
			return true;
		}
		return false;
	}

	private boolean npcSouthOfGate(RSTile npc) {
		timeNotInCombat = 0;
		int voidKnightY = knightPos.getY();
		int npcY = npc.getY();
		if((voidKnightY - npcY) > 7) {
			return true;
		}
		return false;
	}
	
	private void activatePrayer() {
		timeNotInCombat = 0;
		if(Skills.getCurrentLevel("Prayer") < 7) {
			return ;
		}
		int prayer = Skills.getActualLevel("Prayer");
		if(GameTab.getOpen() != TABS.PRAYERS) {
			GameTab.open(TABS.PRAYERS);
		}
		sleep(100, 300);
		
		if(attackStyle == 3) {
			if(prayer > 7  && prayer < 26 ) {
				Mouse.clickBox(668, 220, 690, 240, 1);
				return;
			}
			if(prayer > 25 && prayer < 44) {
				Mouse.clickBox(593, 292, 616, 315, 1);
				return;
			}
			if(prayer >= 44) {
				Mouse.clickBox(705, 327, 727, 351, 1);
				return;
			}
		}
		
		if(attackStyle == 1) {
			if(prayer > 3  && prayer < 13 ) {
				Mouse.clickBox(597, 223, 612, 240, 1);
				return;
			}
			if(prayer > 12 && prayer < 31) {
				Mouse.clickBox(597, 261, 611, 277, 1);
				return;
			}
			if(prayer >= 31) {
				Mouse.clickBox(709, 297, 727, 313, 1);
			}
			if(prayer >= 34) {
				Mouse.clickBox(558, 332, 582, 351, 1);
				return;
			}
		}
		
		if(attackStyle == 2) {
			if(prayer > 8  && prayer < 27 ) {
				Mouse.clickBox(703, 218, 730, 243, 1);
				return;
			}
			if(prayer > 26 && prayer < 45) {
				Mouse.clickBox(628, 292, 655, 315, 1);
				return;
			}
			if(prayer >= 45) {
				Mouse.clickBox(553, 336, 580, 392, 1);
			}
		}
	}
	
	public void needToGoBack() {
		if(knightPos == null) {
			return;
		}
		if(Player.getRSPlayer().getInteractingCharacter() == null) {
			if (Player.getPosition().distanceTo(knightPos) > 4) {
				goToKnight();
				sleep(500,700);
			}
		}
	}
	
	private void goToMiddleFromBoat1(){
		try {
			RSNPC[] Knight = NPCs.find("Void Knight");
			if(Knight.length > 0){
				Walking.walkPath(Walking.generateStraightPath(Knight[0].getPosition()));
				sleep(500);
				while(isMoving()){
					sleep( 50, 150);
				}
			}
		}
		catch(Exception e){
			
		}
	}
	
	private void goToMiddleFromBoat() {
		if(TexanSupport.onIsland()) {	//not in the game!
			return;
		}
		try {
			goToMiddle();
			RSNPC[] voidKnight = NPCs.find(1000, 3782, 3783, 3784, 3785);
			if(voidKnight.length == 0) {
				if(squirePos.isOnScreen() && squirePos != null) {
					int xpos = squirePos.getX();
					int ypos = squirePos.getY();
					RSTile newPosition = new RSTile(xpos, ypos - 10);
					Walking.walkTo(newPosition);
					sleep(2500, 3000);	
				} else {
					int xpos = Player.getPosition().getX();
					int ypos = Player.getPosition().getY();
					RSTile newPosition = new RSTile(xpos, ypos - General.random(4, 7));
					Walking.walkTo(newPosition);
					sleep(500, 800);
				}
			} else {
				if(voidKnight.length > 0) {
					Walking.walkTo(voidKnight[0].getPosition());
					sleep(1800, 2200);
				}
			}
		} catch (Exception e) {
			println("Caught error: " + e);
		}
	}
	
	private void goToMiddle(){
		timeNotInCombat = 0;
		try {
			if(knightPos != null){
			Walking.walkPath(Walking.generateStraightPath(knightPos));
			sleep(500);
			while(isMoving()){
				sleep( 50, 150);
			}
			}

		}catch (Exception e){
			
		}
	}
	private void goInBoat() {
		prayerActivated = false;
		if(TexanSupport.goingInBoat()){
		RSObject[] lander = Objects.findNearest(15, 14315, 25631);
		if (lander.length != 0) {
		//	Camera.setCameraAngle(General.random(1, 100));
		//	Camera.setCameraRotation(General.random(1, 359));
			int numb = General.random(1, 8);
			if(numb == 1 || numb == 4){
			lander[0].click("Cross");
			sleep(200, 300);
			Mouse.click(1);
			Mouse.click(1);
			Mouse.click(1);
			Mouse.click(1);
			}else
			if(numb == 2){
				lander[0].click("Cross");
				Mouse.click(1);
				sleep(50, 200);
				Mouse.click(1);
				sleep(100, 150);
				Mouse.click(1);
				Mouse.click(1);
				sleep(50, 200);
				Mouse.click(1);
				sleep(100, 150);
			}else if(numb == 3){
				lander[0].click("Cross");
				sleep(200, 300);
				Mouse.click(1);
				Mouse.click(1);
				Mouse.click(1);
				Mouse.click(1);
			}else if(numb == 5){
				lander[0].click("Cross");
				sleep(200, 300);
				Mouse.click(1);
				Mouse.click(1);
				Mouse.click(1);
				sleep(200, 300);
			}else if(numb > 5){
				lander[0].click("Cross");
				sleep(200, 300);
		//		lander[0].click("Cross");
				if(Game.isUptext("Cross")){
					Mouse.click(1);
					Mouse.click(1);
					Mouse.click(1);
					Mouse.click(1);

				}
			}
		}
		sleep(200, 300);
		}
	}

	private void doLoginStuff() {
		if(loginWorld == currentWorld) {
			login = false;
			return;
		}
		if(inCombat) {
			sleep(10000, 11000);
		}
		debugInfo = "World Hop";
		while(!Login.logout()) {
			Login.logout();
		}
		worldHop(loginWorld);
		sleep(3000, 4000);
		while(!Login.login()) {
			Login.login();
		}
		inCombat = false;
		login = false;
		currentWorld = loginWorld;
//		worldNumber.clear();
//		worldOccurance.clear();
		if(allowClanChat) {
	        RSInterfaceChild clanChat = Interfaces.get(548, 30);
	        clanChat.click();
	        sleep(100, 300);
	        RSInterfaceChild joinClanChat = Interfaces.get(589, 8);
	        if(joinClanChat.getText().equals("Join Chat")) {
	            joinClanChat.click();
	            sleep(50, 450);
	            Keyboard.typeSend(ccToJoin);
	        }
		}
        sleep(100, 300);
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

	private void beginTimer() {
//		worldNumber.clear();
//		worldOccurance.clear();
	    timerRunning = true;
		timer = new Timer();
		int numOfSeconds = 40 + (int)(Math.random() * ((80 - 40) + 1));	//40 second min, 80 second max
		//debugInfo = Integer.toString(numOfSeconds);
	    timer.schedule(new RemindTask(), numOfSeconds*1000);
	    timer.schedule(new RandomTask(), (numOfSeconds*1000)/General.random(2, 3));	//wanna figure out how to have one task at a time. not 2 timers @ same time
	}		//skype
	

	private void setPoints() {
		if(startPoints == -999) {
			try {
				startPoints = Integer.parseInt(Interfaces.get(407, 14).getText().replaceAll("\\D+", ""));
			} catch (Exception e) { }
		}
		try {
			currentPoints = Integer.parseInt(Interfaces.get(407, 14).getText().replaceAll("\\D+", ""));
		} catch (Exception e) { }		
	}
	
	private void previousPoint(){
		previousPoint = currentPoints;
	//	println("PS"+ pointsSpent);
	//	println("CP" + currentPoints);
	}
	
	private void pointsSpent(){
		if(previousPoint > currentPoints){
			if(pointSpent < 1){
				pointSpent= previousPoint - currentPoints;
			}else{
				pointSpent = pointSpent + (previousPoint - currentPoints);
			}
		}
	}
	
	static boolean inGame(){
		if(TexanSupport.onIsland()) {
			return false;
		}
		RSObject[] lander = Objects.findNearest(100, 14315, 25631);
		if(lander.length > 0) {
			return false;	//on docks
		}
		if(knightPos != null) {
			timeNotInCombat = 0;
			return true;
		}
		if(squirePos != null) {
			timeNotInCombat = 0;
			return true;
		}
		if(Screen.coloursMatch(new Color(98, 95, 90), Screen.getColourAt(21, 65), new Tolerance(22)) &&
				Screen.coloursMatch(new Color(43, 40, 40), Screen.getColourAt(19, 50), new Tolerance(17)))
	//	if(Screen.coloursMatch(new Color(83, 83, 111), Screen.getColourAt(401, 45), new Tolerance(5)))
		{
			timeNotInCombat = 0;
			return true;
		}
		return false;
	}

	private void setClanChat() {
		if(allowClanChat) {
			if (GameTab.getOpen() != GameTab.TABS.CLAN) {
				GameTab.open(GameTab.TABS.CLAN);
			}
	        sleep(100, 300);
	        RSInterfaceChild joinClanChat = Interfaces.get(589, 8);
	        if(joinClanChat.getText().equals("Join Chat")) {
	            joinClanChat.click();
	            sleep(50, 450);
	            Keyboard.typeSend(ccToJoin);
	        }
		}
	}

	
	public void setAutoRetal(boolean option) {
		try {
			int[] settingsArray = Game.getSettingsArray();
			if (settingsArray != null) {
				if (settingsArray.length >= 172) {
					if ((settingsArray[172] == 1) == option) {
						if (GameTab.getOpen() != GameTab.TABS.COMBAT) {
							GameTab.open(GameTab.TABS.COMBAT);
						}
						Mouse.clickBox(604, 359, 716, 401, 1);
						sleep(200,300);
						checkChangedCombatStyle();
					}
				}
			}

		} catch (Exception e) {
		}
	}
	
	private void checkClanChat() {
    	if(allowClanChat) {
	        RSInterfaceChild joinClanChat = Interfaces.get(589, 8);
			if (!GameTab.getOpen().equals(TABS.CLAN)) {
				GameTab.open(TABS.CLAN);
			}
			sleep(300,400);
	    	if(joinClanChat.getText().equals("Join Chat")) {
	            joinClanChat.click();
	            sleep(100, 450);
	    		Color tradingColor = Screen.getColourAt(502, 395);
	            while(tradingColor.getRed() < 40) {
		            joinClanChat.click();
		            sleep(100, 450);
	            	tradingColor = Screen.getColourAt(502, 395);
	            }
	            Keyboard.typeSend(ccToJoin);
	        }
    	}
	}
	
	private boolean worldHop(int world){
		final int[] WORLD_COLUMN_1 = {301,302,303,304,305,306,308,309,310,311,312,313,314,316,317,318},
				    WORLD_COLUMN_2 = {319,320,321,322,325,326,327,328,329,330,333,334,335,336,337,338},
				    WORLD_COLUMN_3 = {341,342,343,344,345,346,349,350,351,352,353,354,357,358,359,360},
				    WORLD_COLUMN_4 = {361,362,365,366,367,368,369,370,373,374,375,376,377,378};
		final int[][]   WORLD_LIST = {WORLD_COLUMN_1, WORLD_COLUMN_2, WORLD_COLUMN_3, WORLD_COLUMN_4};	
		
		final int topX = 220,
				  topY =  80,
				  X_MULTIPLIER = 100,
				  Y_MULTIPLIER =  24;
		
		int worldPos = 0,
			    wNum = 0,
		  	    xPos = 0,
			    yPos = 0;
		
		super.setLoginBotState(false);
		sleep(5000, 7500);
		
		if(Login.getLoginState() == STATE.LOGINSCREEN || Login.getLoginState() == STATE.WELCOMESCREEN){
			Mouse.clickBox(10, 471, 88, 488, 1);
			sleep(5400,7500);
		
			for(int x = 0; x < WORLD_LIST.length; x++){
				for(int y = 0; y < WORLD_LIST[x].length; y++){
					wNum = WORLD_LIST[x][y];
					if(wNum == world){
						worldPos = x;
						xPos = worldPos * X_MULTIPLIER + topX;
						yPos = y * Y_MULTIPLIER + topY;

						Mouse.click(xPos, yPos, 1);
						super.setLoginBotState(true);
						return true;
					}
				}
			}
		}
		return false;
	}

	class RandomTask extends TimerTask {
	    public void run() {
	    	if(timerRunning) {
	    		return;
	    	}
	    	int randomTab = General.random(1,96);	//friends tab, stat tab
	    	switch (randomTab) {
	    	case 1:	//stat tab
		    	if(attackStyle == 1) {	//melee
		    		skill = General.random(1, 3);
		    		switch(skill){
		    		case 1:
		    			Mouse.moveBox(558, 213, 601, 232); //attack
		    		    break;
		    		case 2:
		    			Mouse.moveBox(558, 246, 601, 262); //strength
		    			break;
		    		case 3:
		    			Mouse.moveBox(558, 277, 601, 293); //def
		    			break;
		    		}//position of the tabs
		    		sleep(1200,2000);
		    		return;
		    	} else {	//look @ hp
		    		Mouse.moveBox(620, 215, 664, 229);	//position of the tabs
		    		sleep(1200,2000);
		    	}
	    		break;
	    	case 2:	//friend's tab-- done
				if (!GameTab.getOpen().equals(TABS.FRIENDS)) {
					GameTab.open(TABS.FRIENDS);
				}
	    		break;
	    	case 3:
		    	if(attackStyle == 2) {	//mage
		    		Mouse.moveBox(558, 370, 603, 388);
		    		sleep(1200,2000);
		    		return;
		    	} else { //look @ hp
		    		Mouse.moveBox(620, 215, 664, 229);	//position of the tabs
		    		sleep(1200,2000);
		    	}
	    		break;
	    	case 4:
		    	if(attackStyle == 3) {	//ranged
		    		Mouse.moveBox(558, 308, 602, 304);
		    		sleep(1200,2000);
		    		return;
		    	} else {
		    		Mouse.moveBox(620, 215, 664, 229);	//position of the tabs
		    		sleep(1200,2000);
		    	}
	    		break;
	    	default:
	    		return;
	    	}

	    }
	}
	
	private void setAttackStyle() {
		if (!GameTab.getOpen().equals(TABS.COMBAT)) {
			GameTab.open(TABS.COMBAT);
			sleep(300,500);
		}
		Color attack1 = Screen.getColourAt(626, 275);
		Color strength1 = Screen.getColourAt(713, 278);
		Color strength2 = Screen.getColourAt(625, 325);
		Color defence = Screen.getColourAt(659, 315);
		if(attack1.getRed() > 100) {
			combatStyle = "att";
		} else if (strength1.getRed() > 100) {
			combatStyle = "str1";
		} else if (strength2.getRed() > 100) {
			combatStyle = "str2";
		} else if (defence.getRed() > 100) {
			combatStyle = "def";
		}
	}
	
	class RemindTask extends TimerTask {
	    public void run() {
	    	RSTile lostSpot = new RSTile(2669, 2663, 0);
			RSTile bank = new RSTile(2663, 2650, 0);
	    	if(Player.getPosition().distanceTo(lostSpot) < 10) {
				Point click = Projection.tileToMinimap(bank);
				if(Projection.isInMinimap(click)) {
	   	 			Mouse.click(click, 1);
					sleep(3000,4000);
				} else {
					Walking.walkPath(Walking.generateStraightPath(bank));
				}
	    	}
	    	int numToRotate = (int) (Math.random() * 360);
	    	Camera.setCameraAngle(90);
	        Camera.setCameraRotation(numToRotate);
	        timerRunning = false;
//	        try {
//	        	selectionSort();
//	        	for(int i = 0; i < worldOccurance.size(); i++) {
//		        	if (worldOccurance.get(i) > 6 ) {
//		        		loginWorld = worldNumber.get(i);		        		
//		        		login = true;
//		        		worldNumber.clear();
//		        		worldOccurance.clear();
//		        		timer.cancel();
//		        		return;
//		        	}
//		        }
//			} catch (Exception e) {
//        		worldNumber.clear();
//        		worldOccurance.clear();
//		        timer.cancel();
//        		return;
//			}
//    		worldNumber.clear();
//    		worldOccurance.clear();
	        timer.cancel();
	    }
	}

//	public static void selectionSort() {
//	    for (int i=0; i<worldOccurance.size()-1; i++) {
//	        for (int j=i+1; j<worldOccurance.size(); j++) {
//	            if (worldOccurance.get(i).compareTo(worldOccurance.get(j)) > 0) {
//	                //... Exchange elements in first array
//	                int temp = worldOccurance.get(i);
//	                worldOccurance.set(i, worldOccurance.get(j));
//	                worldOccurance.set(j, temp);
//
//	                //... Exchange elements in second array
//	                temp = worldNumber.get(i);
//	                worldNumber.set(i, worldNumber.get(j));
//	                worldNumber.set(j, temp);
//	            }
//	        }
//	    }
//	    Collections.reverse(worldNumber);
//	    Collections.reverse(worldOccurance);
//	}
	
	@Override
	public void clanMessageRecieved(String arg0, String arg1) {
//		String world = "";
//		Matcher numberMatcher = Pattern.compile("[0-9]{3}").matcher(arg1) ;
//		Matcher numberMatcher2 = Pattern.compile("[0-9]{2}").matcher(arg1) ;
//	    if( numberMatcher.find() ) {
//			if(arg1.contains("bot") || arg1.contains("bots") || arg1.contains("fake") || arg1.contains("don't") || arg1.contains("Don't")) {
//				worldNumber.clear();
//				worldOccurance.clear();
//				login = false;
//				return;
//			}
//            world = numberMatcher.group(0);
//    		int worldAsInt = Integer.parseInt(world);
//    		if(worldAsInt > 302 && worldAsInt < 379) {
//        		if (!worldNumber.contains(worldAsInt)){
//        			worldNumber.add(worldAsInt);
//					worldOccurance.add(1);
//        		} else {
//        			for(int i = 0; i < worldNumber.size(); i++) {
//        				if (worldNumber.get(i) == worldAsInt) {
//        					worldOccurance.set(i, worldOccurance.get(i) + 1 );
//        				}
//        			}
//        		}
//    		}
//    		return;
//	    }
//	    else if( numberMatcher2.find() ) {
//			if(arg1.contains("bot") || arg1.contains("bots") || arg1.contains("fake") || arg1.contains("don't") || arg1.contains("Don't")) {
//				worldNumber.clear();
//				worldOccurance.clear();
//				login = false;
//				return;
//			}
//            world = numberMatcher2.group(0);
//    		int worldAsInt = Integer.parseInt(world);
//    		if(worldAsInt > 2 && worldAsInt < 79) {
//    			worldAsInt = 300 + Integer.parseInt(world);	//add 300 to it
//        		if (!worldNumber.contains(worldAsInt)){
//        			worldNumber.add(worldAsInt);
//					worldOccurance.add(1);
//        		} else {
//        			for(int i = 0; i < worldNumber.size(); i++) {
//        				if (worldNumber.get(i) == worldAsInt) {
//        					worldOccurance.set(i, worldOccurance.get(i) + 1 );
//        				}
//        			}
//        		}
//    		}
//	    }
//	    return;
	}

	@Override
	public void playerMessageRecieved(String arg0, String arg1) {
	}

	@Override
	public void serverMessageRecieved(String aMessage) {
		if(aMessage.contains("10 seconds after combat")) {
			inCombat = true; //this is the only thing that sets combat
		}
	}

	public static void setSquire(RSTile position) {
		squirePos = position;
	}

	public static void setKnight(RSTile position) {
		knightPos = position;
	}
	
	public static RSTile getKnight() {
		return knightPos;
	}
	@Override
	public void onPaint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		timeNotInCombat++;
		if(timeNotInCombat/10 > 1200) {	//10 minutes of no combat
			runScript = false;
		}
		pointsSpent(); //HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		previousPoint();   //HEREEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		String timeran = Timing.msToString(System.currentTimeMillis() - START_TIME);
		int pointDifference = (currentPoints - startPoints);
		g.setFont(font1);
		g.setColor(color1);
        g.drawImage(img1, -29, 182, null);
        g.drawString("" + debugInfo, 71, 296);
        g.drawString("" + timeran, 84, 316);
        if(pointDifference == 0 || startPoints == -999) {
            g.drawString("0.0", 272, 295);
        } else {
            g.drawString("" +  new DecimalFormat("###,###").format(avaragePointAHour(System.currentTimeMillis() - START_TIME, currentPoints + pointSpent, startPoints )), 272, 295);

        }
		if(startPoints == -999) {
			g.drawString("Updating...", 297, 314);
		} else {
	        g.drawString("" + (pointDifference + pointSpent) , 297, 314);
	       
		}
	}
	  private final float avaragePointAHour(long timePassed, int currentXp, int startXp) {
	        return 1000.0F / ((float)(timePassed / 60L / 60L) / (currentXp - startXp));
	  }

	@SuppressWarnings("unused")
	private int getChangeInCmb() {
		int rangedXPGained = Skills.getXP("Ranged") - startingRangedXP;
		int mageXPGained = Skills.getXP("Magic") - startingMagicXP;
		int hitpointsXPGained = Skills.getXP("Hitpoints") - startingHitpointsXP;
		int attackXPGained = Skills.getXP("Attack") - startingAttackXP;
		int strengthXPGained = Skills.getXP("Strength") - startingStrengthXP;
		int defenseXPGained = Skills.getXP("Defence") - startingDefenseXP;
		return (rangedXPGained + mageXPGained + hitpointsXPGained + attackXPGained + strengthXPGained + defenseXPGained);
	}

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch(IOException e) {
            return null;
        }
    }

    public static RSTile getSouthEastPortal() {
    	return southEastPortalPos;
    }

	public static RSTile getSouthWestPortal() {
		return southWestPortalPos;
	}
	
	public static RSTile getSquire() {
		return squirePos;
	}

	public static void setSpecialAttack(boolean b) {
		allowSpecialAttack = b;
	}

	public static void setButtonClicked() {
		buttonClicked = true;
	}

	public static void setAttackStyle(int i) {
		attackStyle = i;
	}


	public static void setPlayStyle(String string) {
		if(string.equals("attack")) {
			userChoseAttackPortals = true;
			userChoseDefendKnight = false;
			userChoseRandomGame = false;
			return;
		}
		if(string.equals("defend")) {
			userChoseDefendKnight = true;
			userChoseAttackPortals = false;
			userChoseRandomGame = false;
			return;
		}
		if(string.equals("random")) {
			userChoseDefendKnight = false;
			userChoseAttackPortals = false;
			userChoseRandomGame = true;
			return;
		}
	}

	public static boolean isDefendingKnight() {
		return userChoseDefendKnight;
	}

	public static boolean isAttackingPortals() {
		return userChoseAttackPortals;
	}

	public static RSTile getEastPortal() {
		return eastPortalPos;
	}

	public static RSTile getWestPortal() {
		return westPortalPos;
	}

	public static RSTile getCurrentPortal() {
		return currentPortal;
	}

	public static boolean isRandomGame() {
		return userChoseRandomGame;
	}

	public static void addToBuyItem(String string) {
		buyVoidItems.add(string);
	}

	public static void setClanChat(String text) {
		if(text.length() > 0) {
			ccToJoin = text;
			allowClanChat = true;
		} else {
			allowClanChat = false;
		}
	}

	public static void setCurrentWorld(String worldTextField) {
		if(worldTextField.length() > 0) {
			try {
				currentWorld = Integer.parseInt(worldTextField);
			} catch (Exception e) {
				currentWorld = 344;
			}
		} else {
			currentWorld = 344;
		}
	}
	
	public static boolean getVersion() {
		return freeVersion;
	}
	
	public static void setXP(String string) {
		if(string.equals("Attack")) { buyPointsXP = string; }
		if(string.equals("Defence")) { buyPointsXP = string; }
		if(string.equals("Strength")) { buyPointsXP = string; }
		if(string.equals("HP")) { buyPointsXP = string; }
		if(string.equals("Range")) { buyPointsXP = string; }
		if(string.equals("Magic")) { buyPointsXP = string; }
		if(string.equals("Prayer")) { buyPointsXP = string; }
		if(string.equals("None")) { buyPointsXP = string; }
	}
	
	private String generateRandom(int length) {
		Random random = new Random();
		String key = "";
		// char[] digits = new char[length];
		key = key + (random.nextInt(9) + '1');
		for (int i = 1; i < length; i++) {
			key = key + (random.nextInt(10) + '0');
		}
		return key;
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
			println("Error connecting to server");
			e.printStackTrace();
		}
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
			println("Error connecting to server");
			e.printStackTrace();
		}
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

	private BufferedReader getReader(String link) throws IOException {
		String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
		URL url = new URL(link);
		URLConnection uc = url.openConnection();
		uc.setRequestProperty("User-Agent", USER_AGENT);
		InputStreamReader inr = new InputStreamReader(uc.getInputStream());
		return new BufferedReader(inr);
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
	
	public int getWorlds() {
		try {
			if (!GameTab.getOpen().equals(TABS.CLAN)) {
				GameTab.open(TABS.CLAN);
				sleep(500);
			}
		    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();	//world, count
			RSInterfaceComponent[] components = Interfaces.get(589, 5).getChildren();
			if(components == null) {
				return currentWorld;
			}
			int x = 1;
			while (x <= components.length) {
				int world = Integer.parseInt(components[x].getText().split(" ")[1].replaceFirst(".*?(\\d+).*", "$1"));
				if(!map.containsKey(world))
		        {
		            map.put(world, 1);
		        }
		        map.put(world, map.get(world)+1);
				x += 3;
			}
			
			if(x == 1) {
				return currentWorld;
			}
			
			int countTemp = 0;
			int worldTemp = currentWorld;
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				int world = entry.getKey();
				int count = entry.getValue();
				if(count > countTemp) {
					countTemp = count;
					worldTemp = world;
				}
			}
			return worldTemp;
		} catch (Exception e) {
			return currentWorld;
		}
	}

	@Override
	public void paintMouse(Graphics arg0, Point arg1, Point arg2) {
		
	}

	@Override
	public void paintMouseSpline(Graphics arg0, ArrayList<Point> arg1) {
		
	}
}
