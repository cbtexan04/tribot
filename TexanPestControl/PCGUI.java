package scripts;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
 
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.JCheckBox;
 
@SuppressWarnings("serial")
public class PCGUI extends JFrame implements ActionListener
{
  //this string is a path to the URL that holds the image

  private static final String IMAGE_PATH = "http://s4.postimg.org/518gf52t9/image.jpg";
  JButton encode, decode;
  JLabel Limage;
  // Icon iplay, isave, istop, iopen;
  JFileChooser filechooser;
  int r;
  
  // the variable "img" refers to the image
  private BufferedImage img = null;
  private JPanel mainPanel = new JPanel()
  {
    @Override
    protected void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      if (img != null)
      {
        int width = mainPanel.getWidth();
        int height = mainPanel.getHeight();
        // and do the drawing here:
        g.drawImage(img, 0, 0, width, height, mainPanel);
      }
    }
  };
  private JTextField worldTextField;
  private JTextField ccTextField;
  
  public PCGUI()
  {
    super("Ultimate Pest Control");
    try
    {
      // download the image off of the internet
      img = ImageIO.read(new URL(IMAGE_PATH));
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    mainPanel.setOpaque(true);
    // set the JFrame's contentPane to our image drawing JPanel:
    setContentPane(mainPanel);
    mainPanel.setLayout(null);
    
    final JRadioButton rdbtnRandom = new JRadioButton("Random");
    rdbtnRandom.setForeground(Color.WHITE);
    rdbtnRandom.setBounds(8, 95, 75, 23);
    rdbtnRandom.setOpaque(false);
    mainPanel.add(rdbtnRandom);
    
    final JRadioButton rdbtnAttack = new JRadioButton("Attack Portals");
    rdbtnAttack.setForeground(Color.WHITE);
    rdbtnAttack.setBounds(84, 95, 108, 23);
    rdbtnAttack.setOpaque(false);
    mainPanel.add(rdbtnAttack);
    
    final JRadioButton rdbtnDefend = new JRadioButton("Defend Knight");
    rdbtnDefend.setForeground(Color.WHITE);
    rdbtnDefend.setBounds(196, 95, 110, 23);
    rdbtnDefend.setOpaque(false);
    mainPanel.add(rdbtnDefend);
    
    ButtonGroup group = new ButtonGroup();
    group.add(rdbtnRandom);
    group.add(rdbtnAttack);
    group.add(rdbtnDefend);
    
    JLabel lblWhatAttackStyle = new JLabel("Which play style would you like to use?");
    lblWhatAttackStyle.setForeground(Color.WHITE);
    lblWhatAttackStyle.setHorizontalAlignment(SwingConstants.CENTER);
    lblWhatAttackStyle.setBounds(41, 78, 227, 14);
    mainPanel.add(lblWhatAttackStyle);
    
    JLabel lblUseSpecialAttack = new JLabel("Use Special Attack?");
    lblUseSpecialAttack.setHorizontalAlignment(SwingConstants.CENTER);
    lblUseSpecialAttack.setForeground(Color.WHITE);
    lblUseSpecialAttack.setBounds(77, 127, 163, 14);
    mainPanel.add(lblUseSpecialAttack);
    
    final JRadioButton rdbtnYes = new JRadioButton("Yes");
    rdbtnYes.setForeground(Color.WHITE);
    rdbtnYes.setBounds(102, 141, 52, 23);
    rdbtnYes.setOpaque(false);
    mainPanel.add(rdbtnYes);
    
    final JRadioButton rdbtnNo = new JRadioButton("No");
    rdbtnNo.setForeground(Color.WHITE);
    rdbtnNo.setBounds(169, 141, 52, 23);
    rdbtnNo.setOpaque(false);
    mainPanel.add(rdbtnNo);
    
    ButtonGroup group2 = new ButtonGroup();
    group2.add(rdbtnNo);
    group2.add(rdbtnYes);
        
    JLabel lblUltimateMetalDragon = new JLabel("<html><u>Ultimate Pest Control</u></html>");
    lblUltimateMetalDragon.setForeground(Color.WHITE);
    lblUltimateMetalDragon.setFont(new Font("Old English Text MT", Font.PLAIN, 30));
    lblUltimateMetalDragon.setHorizontalAlignment(SwingConstants.CENTER);
    lblUltimateMetalDragon.setBounds(24, 11, 550, 46);
    mainPanel.add(lblUltimateMetalDragon);
    
    final JSpinner spinner_3 = new JSpinner();
    spinner_3.setModel(new SpinnerListModel(new String[] {"None", "Attack", "Strength", "HP", "Prayer", "Defence", "Magic", "Range"}));
    spinner_3.setBounds(472, 395, 77, 20);
    mainPanel.add(spinner_3);
    
    JLabel lblDragonBones = new JLabel("Which XP to buy?");
    lblDragonBones.setHorizontalAlignment(SwingConstants.CENTER);
    lblDragonBones.setForeground(Color.WHITE);
    lblDragonBones.setBounds(340, 397, 120, 14);
    mainPanel.add(lblDragonBones);
        
    JLabel lblV = new JLabel(TexanPestControl.getVerion());
    lblV.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    lblV.setHorizontalAlignment(SwingConstants.CENTER);
    lblV.setForeground(Color.WHITE);
    lblV.setBounds(555, 0, 46, 14);
    mainPanel.add(lblV);
    
    final JRadioButton rdbtnRange = new JRadioButton("Range");
    rdbtnRange.setOpaque(false);
    rdbtnRange.setForeground(Color.WHITE);
    rdbtnRange.setBounds(192, 191, 68, 23);
    mainPanel.add(rdbtnRange);
    
    JLabel lblWhatMethodOf = new JLabel("What method of combat are you using?");
    lblWhatMethodOf.setHorizontalAlignment(SwingConstants.CENTER);
    lblWhatMethodOf.setForeground(Color.WHITE);
    lblWhatMethodOf.setBounds(41, 174, 227, 14);
    mainPanel.add(lblWhatMethodOf);
    
    final JRadioButton rdbtnMage = new JRadioButton("Mage");
    rdbtnMage.setOpaque(false);
    rdbtnMage.setForeground(Color.WHITE);
    rdbtnMage.setBounds(122, 191, 62, 23);
    mainPanel.add(rdbtnMage);
    
    final JRadioButton rdbtnMelee = new JRadioButton("Melee");
    rdbtnMelee.setOpaque(false);
    rdbtnMelee.setForeground(Color.WHITE);
    rdbtnMelee.setBounds(51, 191, 62, 23);
    mainPanel.add(rdbtnMelee);
    
    ButtonGroup bg = new ButtonGroup();
    bg.add(rdbtnMelee);
    bg.add(rdbtnMage);
    bg.add(rdbtnRange);
    
    JLabel lblClanChat = new JLabel("Clan Chat:");
    lblClanChat.setHorizontalAlignment(SwingConstants.CENTER);
    lblClanChat.setForeground(Color.WHITE);
    lblClanChat.setBounds(144, 272, 80, 14);
    mainPanel.add(lblClanChat);
    
    JLabel lblCurrentWorld = new JLabel("Current World:");
    lblCurrentWorld.setHorizontalAlignment(SwingConstants.CENTER);
    lblCurrentWorld.setForeground(Color.WHITE);
    lblCurrentWorld.setBounds(13, 272, 91, 14);
    mainPanel.add(lblCurrentWorld);
    
    worldTextField = new JTextField();
    worldTextField.setBounds(106, 272, 37, 18);
    mainPanel.add(worldTextField);
    worldTextField.setColumns(10);
    
    ccTextField = new JTextField();
    ccTextField.setColumns(10);
    ccTextField.setBounds(224, 272, 75, 18);
    mainPanel.add(ccTextField);
    
    final JCheckBox chckbxVoidTop = new JCheckBox("Void Top");
    chckbxVoidTop.setForeground(Color.WHITE);
    chckbxVoidTop.setBounds(349, 340, 75, 25);
    chckbxVoidTop.setOpaque(false);
    mainPanel.add(chckbxVoidTop);
    
    final JCheckBox chckbxVoidBottom = new JCheckBox("Void Bottom");
    chckbxVoidBottom.setForeground(Color.WHITE);
    chckbxVoidBottom.setBounds(469, 340, 100, 25);
    chckbxVoidBottom.setOpaque(false);
    mainPanel.add(chckbxVoidBottom);
    
    final JCheckBox chckbxVoidGloves = new JCheckBox("Void Gloves");
    chckbxVoidGloves.setOpaque(false);
    chckbxVoidGloves.setForeground(Color.WHITE);
    chckbxVoidGloves.setBounds(469, 289, 101, 25);
    mainPanel.add(chckbxVoidGloves);
    
    final JCheckBox chckbxVoidRangeHelm = new JCheckBox("Void Range Helm");
    chckbxVoidRangeHelm.setOpaque(false);
    chckbxVoidRangeHelm.setForeground(Color.WHITE);
    chckbxVoidRangeHelm.setBounds(469, 315, 130, 25);
    mainPanel.add(chckbxVoidRangeHelm);
    
    final JCheckBox chckbxVoidMeleeHelm = new JCheckBox("Void Melee Helm");
    chckbxVoidMeleeHelm.setOpaque(false);
    chckbxVoidMeleeHelm.setForeground(Color.WHITE);
    chckbxVoidMeleeHelm.setBounds(349, 289, 130, 25);
    mainPanel.add(chckbxVoidMeleeHelm);
    
    final JCheckBox chckbxVoidMageHelm = new JCheckBox("Void Mage Helm");
    chckbxVoidMageHelm.setOpaque(false);
    chckbxVoidMageHelm.setForeground(Color.WHITE);
    chckbxVoidMageHelm.setBounds(349, 315, 130, 25);
    mainPanel.add(chckbxVoidMageHelm);
    
    final JCheckBox chckbxVoidMace = new JCheckBox("Void Mace");
    chckbxVoidMace.setOpaque(false);
    chckbxVoidMace.setForeground(Color.WHITE);
    chckbxVoidMace.setBounds(350, 366, 100, 25);
    mainPanel.add(chckbxVoidMace);
    
    final JCheckBox chckbxVoidSeal = new JCheckBox("Void Seal");
    chckbxVoidSeal.setOpaque(false);
    chckbxVoidSeal.setForeground(Color.WHITE);
    chckbxVoidSeal.setBounds(469, 366, 100, 25);
    mainPanel.add(chckbxVoidSeal);
    
    JLabel lblSpendingPoints = new JLabel("---------Spending Points---------");
    lblSpendingPoints.setHorizontalAlignment(SwingConstants.CENTER);
    lblSpendingPoints.setForeground(Color.WHITE);
    lblSpendingPoints.setBounds(365, 266, 191, 14);
    mainPanel.add(lblSpendingPoints);
    
    JLabel lblwhichPlayStyle = new JLabel("<html><center><p>Thank you for choosing Ultimate Pest Control! We're here to help-- if you ever have any questions regarding our product, feel free to add us on Skype!</p></center></html>");
    lblwhichPlayStyle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    lblwhichPlayStyle.setHorizontalAlignment(SwingConstants.CENTER);
    lblwhichPlayStyle.setForeground(new Color(102, 255, 255));
    lblwhichPlayStyle.setBounds(341, 80, 233, 141);
    mainPanel.add(lblwhichPlayStyle);
    
    JLabel lblTexansSkypeWilliamtaylor = new JLabel("Texan's Skype: william.taylor.09");
    lblTexansSkypeWilliamtaylor.setHorizontalAlignment(SwingConstants.CENTER);
    lblTexansSkypeWilliamtaylor.setForeground(new Color(102, 255, 204));
    lblTexansSkypeWilliamtaylor.setBounds(340, 210, 227, 14);
    mainPanel.add(lblTexansSkypeWilliamtaylor);
    
    JLabel lblZainysSkypeZainykentz = new JLabel("Zainy's Skype: Zainy_Kentz");
    lblZainysSkypeZainykentz.setHorizontalAlignment(SwingConstants.CENTER);
    lblZainysSkypeZainykentz.setForeground(new Color(102, 255, 255));
    lblZainysSkypeZainykentz.setBounds(340, 227, 227, 14);
    mainPanel.add(lblZainysSkypeZainykentz);
    
    JLabel lblnews = new JLabel("<html><center><p>No new news </p></center></html>");
    lblnews.setHorizontalAlignment(SwingConstants.CENTER);
    lblnews.setForeground(new Color(102, 255, 255));
    lblnews.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    lblnews.setBounds(42, 296, 233, 93);
    mainPanel.add(lblnews);
    
    JLabel lblWhichBoatTo = new JLabel("Which boat to join?");
    lblWhichBoatTo.setHorizontalAlignment(SwingConstants.CENTER);
    lblWhichBoatTo.setForeground(Color.WHITE);
    lblWhichBoatTo.setBounds(72, 224, 163, 14);
    mainPanel.add(lblWhichBoatTo);
    
    final JRadioButton rdbtnNovice = new JRadioButton("Novice");
    rdbtnNovice.setOpaque(false);
    rdbtnNovice.setForeground(Color.WHITE);
    rdbtnNovice.setBounds(78, 238, 67, 23);
    mainPanel.add(rdbtnNovice);
    
    final JRadioButton rdbtnMedium = new JRadioButton("Medium");
    rdbtnMedium.setOpaque(false);
    rdbtnMedium.setForeground(Color.WHITE);
    rdbtnMedium.setBounds(150, 238, 72, 23);
    mainPanel.add(rdbtnMedium);
    
    ButtonGroup group4 = new ButtonGroup();
    group4.add(rdbtnMedium);
    group4.add(rdbtnNovice);
    
    JButton btnGetThatVoid = new JButton("Get That Void!");
    btnGetThatVoid.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
        	if(rdbtnMelee.isSelected()) { TexanPestControl.setAttackStyle(1);}
        	if(rdbtnMage.isSelected()) { TexanPestControl.setAttackStyle(2);}
        	if(rdbtnRange.isSelected()) { TexanPestControl.setAttackStyle(3);}
        	if(rdbtnYes.isSelected()) { TexanPestControl.setSpecialAttack(true);}
        	if(rdbtnNo.isSelected()) { TexanPestControl.setSpecialAttack(false);}
        	if(rdbtnAttack.isSelected()) { TexanPestControl.setPlayStyle("attack");}
        	if(rdbtnDefend.isSelected()) { TexanPestControl.setPlayStyle("defend");}
        	if(rdbtnRandom.isSelected()) { TexanPestControl.setPlayStyle("random");}
        	if(chckbxVoidBottom.isSelected()) { TexanPestControl.addToBuyItem("bottom"); }
        	if(chckbxVoidGloves.isSelected()) { TexanPestControl.addToBuyItem("gloves"); }
        	if(chckbxVoidMageHelm.isSelected()) { TexanPestControl.addToBuyItem("mageHelm"); }
        	if(chckbxVoidMeleeHelm.isSelected()) { TexanPestControl.addToBuyItem("meleeHelm"); }
        	if(chckbxVoidRangeHelm.isSelected()) { TexanPestControl.addToBuyItem("rangerHelm"); }
        	if(chckbxVoidTop.isSelected()) { TexanPestControl.addToBuyItem("top"); }
        	if(chckbxVoidMace.isSelected()) { TexanPestControl.addToBuyItem("mace"); }
        	if(chckbxVoidSeal.isSelected()) { TexanPestControl.addToBuyItem("seal"); }
        	if(rdbtnNovice.isSelected()) { TexanPestControl.setDifficulty("novice"); }
        	if(rdbtnMedium.isSelected()) { TexanPestControl.setDifficulty("medium"); }
        	TexanPestControl.setClanChat(ccTextField.getText().toString());
        	TexanPestControl.setCurrentWorld(worldTextField.getText().toString());
        	TexanPestControl.setXP(spinner_3.getValue().toString());
        	dispose();
        	TexanPestControl.setButtonClicked();
    	}
    });
    btnGetThatVoid.setBounds(101, 398, 120, 23);
    mainPanel.add(btnGetThatVoid);
    
    filechooser = new JFileChooser();
    filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
    this.setSize(617, 470);
    this.setVisible(true);
  }
  
  public void actionPerformed(ActionEvent e)
  {  }
 
  public static void main(String args[])
  {
    PCGUI frame = new PCGUI();
    frame.setSize(617, 470);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
  }
}