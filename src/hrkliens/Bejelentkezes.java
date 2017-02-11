package hrkliens;

import hrremote.AdatbazisKapcsolat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import JelszoGeneralas.JelszoGeneralo;
import vezerlo1.Vezerlo;
import hrremote.HrRemote;
import hrremote.User;
import java.io.File;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bejelentkezes extends JFrame /*implements AdatbazisKapcsolat*/ {

  File xmlFájl=new File("../hrremote1/src/hrremote/userek.xml");
  private JTextField tfUsername=new JTextField(10);
  private JPasswordField pfPassword=new JPasswordField(10);
  private JButton btLogin = new JButton("Belépés");
  private JLabel lbMessage=new JLabel(); 
  //private final String[] MSGS = {"Űrlapkitöltés"};
  //
  private JPanel pnUsername=new JPanel(new BorderLayout());
  private JPanel pnPassword=new JPanel(new BorderLayout());
  private JPanel pnLogin=new JPanel(new BorderLayout());
  private JPanel pnStatus=new JPanel();
  //
  private JPanel pnFields=new JPanel();
  //
  private User user=null;
  private HrRemote remote;
  
  public Bejelentkezes(HrRemote remote) { //#todo: paraméterként megkaphatná a _hívó_-t
    this.remote = remote;
    this.setDefaultCloseOperation(EXIT_ON_CLOSE); //#todo 
    this.setSize(400, 300);
    this.setResizable(false);
    this.setLocationRelativeTo(this); //#todo
    this.setTitle("Login");
    this.setLayout(new GridLayout(4,1));
    //
    JPanel pnTitle=new JPanel(new BorderLayout());
    JLabel lbLogin=new JLabel("Bejelentkezés");
    lbLogin.setFont(new Font("Arial", Font.BOLD, 40));
    lbLogin.setForeground(Color.WHITE);
    pnTitle.setBackground(Color.ORANGE); //#nigel727
    pnTitle.add(lbLogin, BorderLayout.SOUTH);
    this.add(pnTitle);
    //
    
    pnFields.setLayout(new GridLayout(2,2));
    JPanel pnlbUsername=new JPanel(new BorderLayout());
    JPanel pntfUsername=new JPanel(new BorderLayout());
    JPanel pnlbPassword=new JPanel(new BorderLayout());
    JPanel pnpfPassword=new JPanel(new BorderLayout());
    
    JPanel pnLbUsername=new JPanel();
    pnLbUsername.add(new JLabel("Felhasználónév"));
    pnlbUsername.add(pnLbUsername, BorderLayout.EAST);
    
    JPanel pnTfUsername=new JPanel();
    pnTfUsername.add(this.tfUsername);
    pntfUsername.add(pnTfUsername, BorderLayout.WEST);
    
    JPanel pnLbPassword=new JPanel();
    pnLbPassword.add(new JLabel("Jelszó"));
    pnlbPassword.add(pnLbPassword, BorderLayout.EAST);
    
    JPanel pnPfPassword=new JPanel();
    pnPfPassword.add(this.pfPassword);
    pnpfPassword.add(pnPfPassword, BorderLayout.WEST);
    
    pnFields.add(pnlbUsername);
    pnFields.add(pntfUsername);
    pnFields.add(pnlbPassword);
    pnFields.add(pnpfPassword);
   
    this.add(this.pnFields);
    //
    JPanel pnGomb = new JPanel();
    pnGomb.add(btLogin);
    
    this.pnLogin.add(pnGomb, BorderLayout.CENTER);
    this.btLogin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          ellenőrzés();
        } catch (RemoteException ex) {
          Logger.getLogger(Bejelentkezes.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    });
    this.add(this.pnLogin);
    //
    this.lbMessage.setFont(new Font("Verdana", Font.BOLD, 12));
    lbMessage.setForeground(Color.BLACK);
    lbMessage.setText("Kérem a felhasználónevet és jelszót!");   //#todo Ez így ronda
    this.pnStatus.add(this.lbMessage);
    this.add(this.pnStatus);
  }
  
  private void inicializalas() {
    tfUsername.setText("");
    pfPassword.setText("");
  }
  
  private int jelszóEllenőrzés(String name, String password) { //@hedda
    int hibakod=-1; //"inicializalas
    if (name.isEmpty())
      return 1; //felhasznalonev hianyzik
    if (password.isEmpty())
      return 2; //jelszo hianyzik
    Document d=null;
		try {
			d=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFájl); //eloforditas?
		}
    catch(ParserConfigurationException | SAXException | IOException e) {
      hibakod=3; //xml fajl parszolas nem sikerult
			e.printStackTrace();
		}
	  NodeList userLista=d.getDocumentElement().getElementsByTagName("User");
    int i=0;
    boolean vanIlyenLogin=false;
    while (i<userLista.getLength() && !vanIlyenLogin) {
      String loginName=((Element)userLista.item(i)).getElementsByTagName("Name").item(0).getFirstChild().getNodeValue();
      vanIlyenLogin=loginName.equals(name);
      if (vanIlyenLogin) {
        String loginPassword=((Element)userLista.item(i)).getElementsByTagName("Password").item(0).getFirstChild().getNodeValue();
        String loginJogkor=((Element)userLista.item(i)).getElementsByTagName("Role").item(0).getFirstChild().getNodeValue();
        user=new User(loginName, loginPassword, loginJogkor);        
      } 
      else   
        i++;
    }    
    if (vanIlyenLogin) {
      hibakod=(user.getJelszo().equals(JelszoGeneralo.kodolas(password))? 0 : 5 ); //5-megegyezik a jelszo; 4-nem egyezik meg a jelszo. 
    }
    else
      hibakod=4; //nincs ilyen felhasznalo
    return hibakod;
  }
  
  private void ellenőrzés() throws RemoteException {
    int returnVal = jelszóEllenőrzés(tfUsername.getText(), new String(pfPassword.getPassword()));
    String msg="";
    lbMessage.setForeground(Color.RED);
    switch (returnVal) { 
      case 0: new FoAblak(remote, user.getJogkor(), this);
              inicializalas();
              lbMessage.setForeground(Color.BLACK);
              msg="Kérem a felhasználónevet és jelszót!";
              this.setVisible(false);
              break;
      case 1: msg="Hiányzik a felhasználónév!"; break;
      case 2: msg="Hiányzik a jelszó!"; break;
      case 3: msg="Az userek.xml hiányzik vagy nem olvasható!"; break;
      case 4: msg="Nincs ilyen felhasználó!"; break;
      case 5: msg="Hibas jelszó!"; break;
    }
    lbMessage.setText(msg);
  }  
}
//Ez így szerintem csúnya és kidolgozatlan majd csak akkor lesz idegem 
//szépítgetésre 
//ha már van rutionom h a logikát egyedül összerakjam.