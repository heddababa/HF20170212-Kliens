package hrkliens;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
//import modell.*;
import hrremote.*;
import java.rmi.RemoteException;

public class DolgozoFizetesModositas extends JDialog {
  private JButton btOK = new JButton("Mehet");
  
  public DolgozoFizetesModositas(JFrame tulajdonos, Dolgozo dolgozo, HrRemote remote) throws RemoteException {
    super(tulajdonos, "Adat bekérés", true);
    setLayout(new BorderLayout());
    JPanel pnButton=new JPanel();
    pnButton.add(btOK);
    add(pnButton, BorderLayout.PAGE_END);    
    add(new JPanel(), BorderLayout.PAGE_START);
    add(new JPanel(), BorderLayout.LINE_START);
    add(new JPanel(), BorderLayout.LINE_END);    
    //setUndecorated(true);
    //setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setSize(400, 200);
    setLocationRelativeTo(tulajdonos);
    //addKeyListener(this);
    
    //int[] minmaxFizetes=modell.lekerdezMinMaxFizetes(dolgozo.getMunkakor());
    int minFizetes =remote.getMinfizetes(dolgozo); // /*minmaxFizetes[0];*/modell.lekerdezMinFizetes(dolgozo.getMunkakor());
    int maxFizetes =remote.getMaxFizetss(dolgozo); //*minmaxFizetes[1];*/ modell.lekerdezMaxFizetés(dolgozo.getMunkakor());
    int aktFizetes = dolgozo.getFizetes();
    int emeles5szazalek = Math.round(aktFizetes*1.05F);
    int csokkentes5szazalek =  Math.round(aktFizetes*0.95F);
    int adhatoMax = maxFizetes>emeles5szazalek?emeles5szazalek:maxFizetes;
    int adhatoMin = minFizetes<csokkentes5szazalek?csokkentes5szazalek:minFizetes;
    
    JLabel lbdolgozNev = new JLabel(dolgozo.getNev());
    JLabel lbFizetes = new JLabel(""+dolgozo.getFizetes());
    JLabel lbMaxFizetes = new JLabel(""+adhatoMax);
    JLabel lbMinFizetes = new JLabel(""+adhatoMin);     
    JPanel pnCimkek=new JPanel(new GridLayout(4,2));
    pnCimkek.add(new JLabel("Dolgozó neve:"));             pnCimkek.add(lbdolgozNev);
    pnCimkek.add(new JLabel("Dolgozó fizetése:"));         pnCimkek.add(lbFizetes);
    pnCimkek.add(new JLabel("Adható maximális fizetés:")); pnCimkek.add(lbMaxFizetes);
    pnCimkek.add(new JLabel("Adható minimális fizetés:")); pnCimkek.add(lbMinFizetes);
           
    JSpinner sp=new JSpinner(new SpinnerNumberModel(aktFizetes, adhatoMin, adhatoMax, 50));
    sp.addKeyListener(new KeyAdapter() {
      @Override      
      public void keyTyped(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE) 
          dispose();
      }      
    });
    JPanel pnSpinner=new JPanel(new BorderLayout());
    pnSpinner.add(sp, BorderLayout.PAGE_START);
    
    JPanel pn=new JPanel (new BorderLayout());
    pn.add(pnCimkek, BorderLayout.CENTER);
    pn.add(pnSpinner, BorderLayout.PAGE_END);
    //sp.addKeyListener(this);
    add(pn, BorderLayout.CENTER);
    //pack(); 
    btOK.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e)  {
        //Ha nem valtozott a fizu osszeg, akkor NE mentsuk!
        try {
        if ((int)sp.getModel().getValue()!=aktFizetes) {
          boolean siker = remote.setFizetes(dolgozo, (int)sp.getModel().getValue());
                  //modell.modositFizetés(dolgozo.getEmpID(), (int)sp.getModel().getValue());
          if (siker) {
            dolgozo.setFizetes((int)sp.getModel().getValue());
            dispose();
          }  
          else
            JOptionPane.showMessageDialog((Component) e.getSource(), 
                    "Nem sikerült a módosítás!", 
                    "Hibaüzenet", 
                    JOptionPane.ERROR_MESSAGE);
        }
        else {
          JOptionPane.showMessageDialog((Component) e.getSource(), 
                    "Ugyanarra nem lehet módosítani a fizetést, ami\n korábban volt!", 
                    "Figyelmeztetés", 
                    JOptionPane.WARNING_MESSAGE);          
        }
                } catch (Exception f) {
        }
       // dispose();
      }
    });
    
    setVisible(true);
  }
}  
