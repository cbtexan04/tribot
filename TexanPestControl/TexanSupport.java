package scripts;

import java.awt.Color;
import java.awt.Point;

import org.tribot.api.Screen;
import org.tribot.api.types.colour.Tolerance;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

public class TexanSupport {
	public enum TexanEnum {
		goingInBoat, atBeginning, atMiddle, inBoat, goBackToMiddle, atWestGate, atWestPortal, atEastGate, atEastPortal, atSouthGate, atSouthEastPortal, atSouthWestPortal, Lost, spendingPoints, goToSpendPoints, eastOpen, westOpen, inGame, southWestOpen, southEastOpen
		
	}

	public static boolean goingInBoat() {
		if(locatedInBoat()) {
			return false;
		}
		RSTile landerStart = TexanPestControl.getLanderTile();
		RSTile landerStart2 = new RSTile(landerStart.getX(), landerStart.getY()+1, 0);
		RSTile landerStart3 = new RSTile(landerStart.getX(), landerStart.getY()-1, 0);
		if ((org.tribot.api2007.Player.getPosition().distanceTo(landerStart) == 0)) {
			TexanPestControl.setSquire(null);
			return true;
		} else if ((org.tribot.api2007.Player.getPosition().distanceTo(landerStart2) == 0)) {
			TexanPestControl.setSquire(null);
			return true;
		} else if (org.tribot.api2007.Player.getPosition().distanceTo(landerStart3) == 0) {
			TexanPestControl.setSquire(null);
			return true;
		}
		return false;
	}
	
	public boolean atWestGate() {
		try {
			RSTile westGate = new RSTile(TexanPestControl.getSquire().getX() -12, TexanPestControl.getSquire().getY()-14, 0);
			if(westGate.isOnScreen()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}

	}
	
	public boolean atWestPortal() {
		try {
			RSTile westPortal = new RSTile(TexanPestControl.getSquire().getX() -24, TexanPestControl.getSquire().getY()-15, 0);
			if(westPortal.isOnScreen()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean inGame() {
		return TexanPestControl.inGame();
	}
	
	public boolean eastPortalOpen(){
		if(onIsland()) {
			return false;
		}
		if(eastDropped() && !eastDestroyed() && inGame()){
			return true;
		}
		return false;
	}
	private boolean eastDropped(){
		if(onIsland()) {
			return false;
		}
		if(!Screen.coloursMatch(new Color(69,62, 62),Screen.getColourAt(406, 58), new Tolerance(10))){
			return true;
		}
		return false;
	}
	
	public boolean westPortalOpen(){
		if(onIsland()) {
			return false;
		}
		if(westDropped() && !westDestroyed() && inGame()){
			return true;
		}
		return false;
	}
	
	public static boolean onIsland() {
		RSTile landerStart = TexanPestControl.getLanderTile();
		RSTile jointSpot = new RSTile(2608, 2584, 0);
		RSTile fencedSpot = new RSTile(2657, 2660, 0);
		Point click = Projection.tileToMinimap(jointSpot);
		Point click2 = Projection.tileToMinimap(fencedSpot);
		if(Player.getPosition().distanceTo(landerStart) < 20) {
			return true;
		}
		if(Projection.isInMinimap(click)) {
			return true;
		}
		if(Projection.isInMinimap(click2)) {
			return true;
		}
		return false;
	}
	
	public boolean southEastPortalOpen() {
		if(onIsland()) {
			return false;
		}
		if(southEastDropped() && !southEastDestroyed() && inGame()){
			return true;
		}
		return false;
	}
	
	public boolean southWestPortalOpen(){
		if(onIsland()) {
			return false;
		}
		if(southWestDropped() && !southWestDestroyed() && inGame()){
			return true;
		}
		return false;
	}
	
	
	
	private boolean westDropped(){
		if(onIsland()) {
			return false;
		}
		if(!Screen.coloursMatch(new Color(69, 62, 62), Screen.getColourAt(370, 56), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southWestDropped(){
		if(onIsland()) {
			return false;
		}
		if(!Screen.coloursMatch(new Color(80, 72, 72), Screen.getColourAt(483, 53), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southEastDropped(){ 
		if(onIsland()) {
			return false;
		}
		if(!Screen.coloursMatch(new Color(80, 72, 72), Screen.getColourAt(443, 55), new Tolerance(10))){
			return true;
		}
		return false;
	}
	
	private boolean westDestroyed(){
		if(onIsland()) {
			return false;
		}
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(370, 44), new Tolerance(10))){
			return true;
		}
		return false;
	}
	
	private boolean eastDestroyed(){
		if(onIsland()) {
			return false;
		}
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(408, 45), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southWestDestroyed(){
		if(onIsland()) {
			return false;
		}
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(484, 45), new Tolerance(10))){
			return true;
		}
		return false;
	}
	private boolean southEastDestroyed(){
		if(onIsland()) {
			return false;
		}
		if(Screen.coloursMatch(new Color(178, 22, 11), Screen.getColourAt(444, 45), new Tolerance(10))){
			return true;
		}
		return false;
	}

	public static boolean locatedInBoat () {
		RSTile boatCenter = TexanPestControl.getBoatCenter();
		if(boatCenter.distanceTo(Player.getPosition()) < 5) {
			return true;
		}
		return false;
	}

	public static boolean atBeginning() {
		if(TexanPestControl.getKnight() != null) {
			TexanPestControl.tryToSetPortals();
		}
		RSNPC[] beginningSquire = NPCs.findNearest(3781);
		if(beginningSquire.length > 0) {
			if(beginningSquire[0].getPosition().getY() <= Player.getPosition().getY()) {
				TexanPestControl.setSquire(beginningSquire[0].getPosition());
				TexanPestControl.setKnight(new RSTile(beginningSquire[0].getPosition().getX()+1, beginningSquire[0].getPosition().getY()-13));
				return true;
			}
		}
		return false;
	}

	public static boolean knightCloseby() {
		RSNPC[] voidKnight = NPCs.find(1000, 3782, 3783, 3784, 3785);
		try {
		if(voidKnight.length > 0) {
			TexanPestControl.setKnight(voidKnight[0].getPosition());
			TexanPestControl.tryToSetPortals();
			TexanPestControl.setExactPortals();
			if(TexanPestControl.isDefendingKnight()) {
				if (voidKnight[0].getPosition().distanceTo(Player.getPosition()) < 8) {
					return true;
				} else if (voidKnight[0].isOnScreen() && voidKnight[0].getPosition().distanceTo(Player.getPosition()) < 5) {
					return true;
				}
			} else if (TexanPestControl.isAttackingPortals() || TexanPestControl.isRandomGame()) {
				if (voidKnight[0].isOnScreen() && voidKnight[0].getPosition().distanceTo(Player.getPosition()) < 5) {
					return true;
				}
			}
		}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean needToMoveBack() {
		if((!knightCloseby()) && (TexanPestControl.getKnight() != null) && TexanPestControl.isDefendingKnight()) {
			return true;
		}
		return false;
	}

	public boolean atEastGate() {
		try {
			RSTile eastGate = new RSTile(TexanPestControl.getSquire().getX() + 14, TexanPestControl.getSquire().getY() - 14, 0);
			if(eastGate.isOnScreen() && eastGate.distanceTo(Player.getPosition()) < 8) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}

	}
	
	public boolean atEastPortal() {
		try {
			RSTile eastPortal = new RSTile(TexanPestControl.getSquire().getX() + 23, TexanPestControl.getSquire().getY() - 13, 0);
			if(eastPortal.isOnScreen() && eastPortal.distanceTo(Player.getPosition()) < 10) {
				return true;
			}
			if(eastPortal.distanceTo(Player.getPosition()) < 5) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean atSouthWestPortal() {
		try {
			RSTile southWestPortal = TexanPestControl.getSouthWestPortal();
			if(southWestPortal == null) {
				return false;
			}
			if(TexanPestControl.getCurrentPortal() == southWestPortal){
				return true;
			}
			if((southWestPortal.isOnScreen() && southWestPortal.distanceTo(Player.getPosition()) < 6) || (southWestPortal.distanceTo(Player.getPosition()) < 8)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	
	}

	
	public boolean needToSpendPoints() {
		if(TexanPestControl.inGameBoolean()) {	//in game
			return false;
		}
		try {
			int points = Integer.parseInt(Interfaces.get(407, 14).getText().replaceAll("\\D+", ""));
			if(points >= 250) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean atSouthEastPortal() {
		try {
			RSTile southEastPortal = TexanPestControl.getSouthEastPortal();
			if(southEastPortal == null) {
				return false;
			}
			if(TexanPestControl.getCurrentPortal() == southEastPortal){
				return true;
			}
			if((southEastPortal.isOnScreen() && southEastPortal.distanceTo(Player.getPosition()) < 6) || (southEastPortal.distanceTo(Player.getPosition()) < 8)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean atSouthGate() {
		try {
			RSTile southGate = new RSTile(TexanPestControl.getKnight().getX(), TexanPestControl.getKnight().getY() - 7, 0);
			if(southGate.isOnScreen() && southGate.distanceTo(Player.getPosition()) < 10) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean nearPCBank() {
		if(TexanPestControl.getVoidPoints() < 250) {
			return false;	//might be near bank, but shouldn't be spending
		}
		while(Player.isMoving()) {
			//do nothing
		}
		if(Player.getPosition().getY() > 2644) {
			if(Player.getPosition().distanceTo(new RSTile(2663, 2650)) < 8) {
				return true;
			}
		}
		return false;
	}
	

}
