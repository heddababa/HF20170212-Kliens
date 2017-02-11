package hrkliens;

import hrremote.Dolgozo;
import hrremote.HrRemote;
import hrremote.Munkakor;
import hrremote.Reszleg;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
//import modell.AdatBazisKezeles;
//import modell.Dolgozo;
//import modell.Munkakor;
//import modell.Reszleg;

class DolgozoFelvetel extends JDialog implements KeyListener, ActionListener {    
  JLabel lbVezeteknev = new JLabel("* Vezetéknév:    ", SwingConstants.RIGHT);
  JLabel lbKeresztnev = new JLabel("Keresztnév:    ", SwingConstants.RIGHT);
  JLabel lbEmail = new JLabel("* Nick név email címhez:    ", SwingConstants.RIGHT);
  JLabel lbTelefonszam=new JLabel("Telefonszám:    ", SwingConstants.RIGHT);
  JLabel lbReszlegnev = new JLabel("* Részleg kiválasztása:    ", SwingConstants.RIGHT);
  JLabel lbMunkakor = new JLabel("* Munkakör kiválasztása:    ", SwingConstants.RIGHT);
  JLabel lbFonok = new JLabel("* Főnök kiválasztása:    ", SwingConstants.RIGHT);
  JLabel lbFizetes=new JLabel("* Fizetés:    ", SwingConstants.RIGHT);
  private JTextField tfVezeteknev = new JTextField("", 25);
  private JTextField tfKeresztnev = new JTextField("", 20);
  private JTextField tfEmail = new JTextField("", 25);
  private JTextField tfTelefonszam = new JTextField("", 20);
  private JTextField tfFizetes = new JTextField("");
  //private JSpinner spFizetes=new JSpinner;//(new SpinnerNumberModel(aktFizetes, adhatoMin, adhatoMax, 50));
  private JComboBox cbReszlegLista;
  private JComboBox cbMunkakorLista;
  private JComboBox cbFonokLista;
  //private AdatBazisKezeles modell;
  private HrRemote remote;
  JButton btAdd = new JButton("Mentés");    
  long adhatoMinFizetes=0;
  long adhatoMaxFizetes=0;
    
  public DolgozoFelvetel(JFrame parent, HrRemote remote) throws RemoteException {
    super(parent, "Új dolgozó hozzáadása", true);
    this.remote=remote;
    setSize(400, 400);
    setLocationRelativeTo(this);
    
    cbReszlegLista = reszlegListaBetoltes();
    cbReszlegLista.setSelectedIndex(0);
    cbMunkakorLista = munkakorListaBetoltes();
    cbFonokLista = fonokListaBetoltes(((Reszleg)cbReszlegLista.getSelectedItem()).getReszlegId());
    
    JPanel pnVezeteknev=new JPanel(new GridLayout(1, 2));
    pnVezeteknev.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnVezeteknev.add(lbVezeteknev);
    pnVezeteknev.add(tfVezeteknev);
    tfVezeteknev.setText(""); 

    JPanel pnKeresztnev=new JPanel(new GridLayout(1, 2));
    pnKeresztnev.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnKeresztnev.add(lbKeresztnev);
    pnKeresztnev.add(tfKeresztnev);
    tfKeresztnev.setText("");
    pnKeresztnev.add(tfKeresztnev);
    
    JPanel pnEmail=new JPanel(new GridLayout(1, 2));
    pnEmail.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnEmail.add(lbEmail);
    pnEmail.add(tfEmail);
    tfEmail.setText(""); 
    pnEmail.add(tfEmail);
    
    JPanel pnTelefonszam=new JPanel(new GridLayout(1, 2));
    pnTelefonszam.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnTelefonszam.add(lbTelefonszam);
    pnTelefonszam.add(tfTelefonszam);
    tfTelefonszam.setText("");
    pnTelefonszam.add(tfTelefonszam);
    
    JPanel pnReszleg=new JPanel (new GridLayout(1, 2));
    pnReszleg.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnReszleg.add(lbReszlegnev);
    pnReszleg.add(cbReszlegLista);
    cbReszlegLista.addActionListener(this);
    
    JPanel pnMunkakor=new JPanel (new GridLayout(1, 2));
    pnMunkakor.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnMunkakor.add(lbMunkakor);
    pnMunkakor.add(cbMunkakorLista);
    cbMunkakorLista.addActionListener(this);   
    
    JPanel pnFonok=new JPanel (new GridLayout(1, 2));
    pnFonok.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnFonok.add(lbFonok);
    pnFonok.add(cbFonokLista);
    
    JPanel pnFizetes=new JPanel(new GridLayout(1, 2));
    pnFizetes.setBorder(new EmptyBorder(10, 10, 10, 10));
    pnFizetes.add(lbFizetes);
    pnFizetes.add(tfFizetes);
    tfFizetes.setText("");  
    tfFizetes.addKeyListener(this);
    pnFizetes.add(tfFizetes);
    setLbFizetes();
    
    JPanel pnAdatbevitel=new JPanel(new GridLayout(8,2));
    pnAdatbevitel.add(pnVezeteknev);
    pnAdatbevitel.add(pnKeresztnev);
    pnAdatbevitel.add(pnEmail);
    pnAdatbevitel.add(pnTelefonszam);
    pnAdatbevitel.add(pnReszleg);
    pnAdatbevitel.add(pnMunkakor);
    pnAdatbevitel.add(pnFonok);
    pnAdatbevitel.add(pnFizetes);
    
    JPanel pnGomb=new JPanel(new GridLayout(1,1));
    pnGomb.setBorder(new EmptyBorder(10, 90, 10, 90));
    pnGomb.add(btAdd, BorderLayout.CENTER);
    
    add(pnAdatbevitel, BorderLayout.CENTER);
    add(pnGomb, BorderLayout.SOUTH);
    
    setResizable(false);    
    btAdd.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        try {
          boolean mehetAMentes=Ellenorzes();
          if (mehetAMentes) {
            try {
              //int managerId=AdatBazisKezeles.lekerdezReszlegFonoke(((Reszleg)cbReszlegLista.getSelectedItem()).getReszlegId());
              boolean siker=remote.setDolgozo(tfKeresztnev.getText(), 
                                                tfVezeteknev.getText(), 
                                                tfEmail.getText(), 
                                                tfTelefonszam.getText(), 
                                                ((Munkakor)cbMunkakorLista.getSelectedItem()).getMunkakorId(), 
                                                Integer.parseInt(tfFizetes.getText()), 
                                                -1, 
                                                //managerId,
                                                ((Dolgozo)cbFonokLista.getSelectedItem()).getEmpID(),
                                                ((Reszleg)cbReszlegLista.getSelectedItem()).getReszlegId());
              if (siker) { 
                JOptionPane.showMessageDialog((Component) e.getSource(), 
                  "A mentés sikeres volt.", 
                  "Információ", 
                  JOptionPane.INFORMATION_MESSAGE);
                dispose();
              }   
              else
                JOptionPane.showMessageDialog((Component) e.getSource(), 
                  "A mentés nem sikerült.", 
                  "Hibaüzenet", 
                  JOptionPane.ERROR_MESSAGE);
            }
            catch (SQLException ex) {
              System.out.println("Nem sikerult a mentes SQLException!");
            } catch (RemoteException ex) {
              Logger.getLogger(DolgozoFelvetel.class.getName()).log(Level.SEVERE, null, ex);
            }            
          }
        }
        catch (IllegalArgumentException hiba) {
          JOptionPane.showMessageDialog((Component) e.getSource(), 
                  hiba.getMessage(), 
                  "Hibaüzenet", 
                  JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException ex) {
          Logger.getLogger(DolgozoFelvetel.class.getName()).log(Level.SEVERE, null, ex);
        } 
      }        
    });
    setVisible(true);
  }    
  
  private void setLbFizetes() throws RemoteException {
    Reszleg reszleg = (Reszleg)cbReszlegLista.getSelectedItem();
    int[] osszFizetesosszLetszam=remote.getOsszFizetesReszlegben(reszleg.getReszlegId());//modell.lekerdezesOsszFizLetszReszlegenBelul(reszleg.getReszlegId());
    int osszFiz=osszFizetesosszLetszam[0];
    int osszLetszam=osszFizetesosszLetszam[1];
    /*long*/ adhatoMinFizetes=Math.max(Math.round( osszFiz*(-0.05) + (osszFiz*0.95/osszLetszam)), 
            ((Munkakor)cbMunkakorLista.getSelectedItem()).getMinFizetes() );
    /*long*/ adhatoMaxFizetes=Math.min( Math.round( osszFiz*0.05 + (osszFiz*1.05/osszLetszam)),
            ((Munkakor)cbMunkakorLista.getSelectedItem()).getMaxFizetes());
    lbFizetes.setText("* Fizetés ("+adhatoMinFizetes+" - "+adhatoMaxFizetes+"):    ");
  }
  
  private boolean Ellenorzes() throws IllegalArgumentException, RemoteException {
    boolean kotelezoAdatokMegadva=false;
//    Ellenorzesek.hosszEllenorzes("A keresztnév túl hosszú", tfKeresztnev.getText(), 20, false);
//    kotelezoAdatokMegadva=Ellenorzesek.hosszEllenorzes("A vezetéknév túl hosszú", tfVezeteknev.getText(), 25, true);
//    kotelezoAdatokMegadva=kotelezoAdatokMegadva && Ellenorzesek.hosszEllenorzes("Az email túl hosszú", tfEmail.getText(), 25, true);
//    Ellenorzesek.hosszEllenorzes("A telefonszám túl hosszú", tfTelefonszam.getText(), 20, false);
//    kotelezoAdatokMegadva=Ellenorzesek.hosszEllenorzes("Fizetés megadása kötelező", tfFizetes.getText(), 8, true);
    kotelezoAdatokMegadva=(tfVezeteknev.getText().length()>0 &&
                           tfEmail.getText().length()>0 &&
                           tfFizetes.getText().length()>0);
    if (!kotelezoAdatokMegadva)
      throw new IllegalArgumentException("A kötelező adatok nincsenek megadva!");
    if (!emailEllenorzes(tfEmail.getText()))
      throw new IllegalArgumentException("A megadott email nick már létezik!");
    try {
      int ujFizetes=ujFizetes=Integer.parseInt(tfFizetes.getText());
      if ( ujFizetes>adhatoMaxFizetes || ujFizetes < adhatoMinFizetes)
        throw new IllegalArgumentException("A fizetés "+adhatoMinFizetes+" és "+adhatoMaxFizetes+" között lehet!"); 
    } 
    catch (NumberFormatException e) {
      throw new IllegalArgumentException("A fizetés szám kell, hogy legyen!");
    }    
    return kotelezoAdatokMegadva;    
  }
  
//  private boolean hosszEllenorzes(String hibauzenet, String szoveg, int hossz, boolean kotelezo)
//    throws IllegalArgumentException {
//    if (szoveg.length()>hossz)
//      throw new IllegalArgumentException(hibauzenet);    //Ez a kivetel miert nem gordul tovabb a hivo metodushoz? 
//    return ( kotelezo? szoveg.length()>0 : true );
//  }
  
  private boolean emailEllenorzes(String email) throws IllegalArgumentException, RemoteException {
    boolean megfeleloEmail=false;
    ArrayList<String> emailLista=remote.getEmailList(); //modell.lekerdezEmail();
    return !emailLista.contains(email);
  }
    
  private JComboBox reszlegListaBetoltes() throws RemoteException {
    JComboBox cbReszlegLista = new JComboBox();
    ArrayList<Reszleg> reszlegek=remote.getReszlegList(); // modell.lekerdezReszleg();
    for (Reszleg reszleg : reszlegek)
      cbReszlegLista.addItem(reszleg);
    return cbReszlegLista;
  }

  private JComboBox munkakorListaBetoltes() throws RemoteException {
    JComboBox cbMunkakorLista = new JComboBox();
    ArrayList<Munkakor> munkakorok=remote.getMunkakorok(); //modell.lekerdezMunkakorok();
    for (Munkakor munkakor : munkakorok)
      cbMunkakorLista.addItem(munkakor);
    return cbMunkakorLista;
  }
  
  private JComboBox fonokListaBetoltes(int reszlegId) throws RemoteException {
    JComboBox cbFonokLista = new JComboBox();
    ArrayList<Dolgozo> fonokok=remote.getFonokList(reszlegId); //modell.lekerdezFonokokListaja(reszlegId);
    for (Dolgozo fonok : fonokok)
      cbFonokLista.addItem(fonok);
    return cbFonokLista;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    ;   
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getSource()==tfFizetes) {
      if ((e.getKeyChar()<'0' || e.getKeyChar()>'9') 
          && (e.getKeyChar()!=KeyEvent.VK_BACK_SPACE)
          && (e.getKeyChar()!=KeyEvent.VK_DELETE))
        tfFizetes.setEditable(false);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    tfFizetes.setEditable(true);
  }  

  @Override
  public void actionPerformed(ActionEvent e) {
    try{
    if (e.getSource() == cbReszlegLista) {
      cbFonokLista.removeAllItems();
      ArrayList<Dolgozo> fonokok = remote.getFonokList(((Reszleg) cbReszlegLista.getSelectedItem()).getReszlegId());
              //modell.lekerdezFonokokListaja(((Reszleg) cbReszlegLista.getSelectedItem()).getReszlegId());
      for (Dolgozo fonok : fonokok) {
        cbFonokLista.addItem(fonok);
      }
      setLbFizetes();
    }
    if (e.getSource() == cbMunkakorLista)
      setLbFizetes();
    }
    catch(Exception f){
      ;
    }
  }
}
