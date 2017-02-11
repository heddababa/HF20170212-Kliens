package hrkliens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import hrremote.*;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoAblak extends JFrame
        implements ActionListener {

  private JComboBox cbReszlegLista;
  JLabel lbTalalat = new JLabel("Nincs találat");

  private JTextField tfDolgozoKeres = new JTextField("Keresendő dolgozó", 12);

  private JButton btUjDolgozo = new JButton("Új dolgozó felvétele");
  private JLabel lbReszleg = new JLabel("Részleg:   ", SwingConstants.RIGHT);
  private JLabel lbKereses = new JLabel("Dolgozó keresése:   ", SwingConstants.RIGHT);
 // private AdatBazisKezeles modell;
  private HrRemote remote;
  private JTable tDolgozoTable = new JTable(new DefaultTableModel());
  private JScrollPane spDolgozoLista = new JScrollPane(tDolgozoTable);
  private ArrayList<Dolgozo> dolgozok;

  public FoAblak(HrRemote remote , String jogkor, Bejelentkezes hivo) throws RemoteException {
    this.remote = remote;
    //System.out.println(this.remote == null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) { 
        hivo.setVisible(true);
      }      
    });
    setTitle("HR Fizetésemelés");
    setSize(700, 500);
    setLocationRelativeTo(this);
    setIconImage(Toolkit.getDefaultToolkit().getImage("image1.jpg"));

    cbReszlegLista = reszlegListaBetoltes();

    JPanel pnReszlegek = new JPanel(new GridLayout(1, 7));
    pnReszlegek.add(lbReszleg);
    pnReszlegek.add(cbReszlegLista);
    pnReszlegek.add(lbKereses);
    tfDolgozoKeres.setText("");
    tfDolgozoKeres.getDocument().addDocumentListener(new MyDocumentListener());
    tfDolgozoKeres.getDocument().putProperty("name", "Text Area");
    pnReszlegek.add(tfDolgozoKeres);
    lbTalalat.setVisible(false);
    lbTalalat.setForeground(Color.blue);
    lbTalalat.setHorizontalAlignment(SwingConstants.CENTER);
    pnReszlegek.add(lbTalalat);

    JPanel pn = new JPanel(new GridLayout(1, 2));
    pn.add(panelKeszit(pnReszlegek));
    add(pn, BorderLayout.NORTH);

    add(spDolgozoLista);

    JPanel pnDolgozoGomb = new JPanel(new GridLayout(1, 1));
    pnDolgozoGomb.add(btUjDolgozo, BorderLayout.CENTER);
    pnDolgozoGomb.setBorder(new EmptyBorder(10, 250, 10, 250));
    
    add(pnDolgozoGomb, BorderLayout.AFTER_LAST_LINE);
    
    if (jogkor.equals("HR2"))
      pnDolgozoGomb.setVisible(true);
    else pnDolgozoGomb.setVisible(false);

    setVisible(true);

    cbReszlegLista.addActionListener(this);
    btUjDolgozo.addActionListener(this);

    tDolgozoTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getSource() == tDolgozoTable) {
          int index = tDolgozoTable.getSelectedRow();
          MyTableModell tableModell = ((MyTableModell) tDolgozoTable.getModel());
          Dolgozo dolgozo = tableModell.getDolgozo(index);
          if (dolgozo != null) {
            DolgozoFizetesModositas dg;
            JFrame tulaj = (JFrame) SwingUtilities.getRoot((Component) e.getSource());
            try {
            dg = new DolgozoFizetesModositas(tulaj, dolgozo, remote);
            } catch (Exception ex) {
              Logger.getLogger(FoAblak.class.getName()).log(Level.SEVERE, null, ex);
            }
            tableModell.steDolgozo(dolgozo, index);
            tableModell.fireTableDataChanged();
            tDolgozoTable.addRowSelectionInterval(index, index);
          }
        }
      }
    });

    tDolgozoTable.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getSource() == tDolgozoTable) {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            int index = tDolgozoTable.getSelectedRow();
            MyTableModell tableModel = ((MyTableModell) tDolgozoTable.getModel());
            Dolgozo dolgozo = tableModel.getDolgozo(index);
            if (dolgozo != null) {
              try {
                new DolgozoFizetesModositas((JFrame) SwingUtilities.getRoot((Component) e.getSource()), dolgozo, remote);
              } catch (RemoteException ex) {
                Logger.getLogger(FoAblak.class.getName()).log(Level.SEVERE, null, ex);
              }
              tableModel.steDolgozo(dolgozo, index);
              tableModel.fireTableDataChanged();
              tDolgozoTable.addRowSelectionInterval(index, index);
              tDolgozoTable.setRowSelectionInterval(index, index);
            }
          }
        }
      }
    });

    reszlegListaBetoltes();

    tDolgozoTable.setModel(dolgozoListaBetoltes(-1));
    tDolgozoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tDolgozoTable.requestFocus();
    tDolgozoTable.addRowSelectionInterval(0, 0);
  }

  private JPanel panelKeszit(JPanel pnReszlegek) {
    JPanel ujPn = new JPanel();
    ujPn.add(pnReszlegek);
    return ujPn;
  }

  private JComboBox reszlegListaBetoltes() throws RemoteException {
    JComboBox cbReszlegLista = new JComboBox();
    //System.out.println("Most fog kérni valamit");
    
    ArrayList<Reszleg> reszlegek = remote.getReszlegList();
    cbReszlegLista.addItem(new Reszleg(" -- Összes dolgozó", -1));
    for (Reszleg reszleg : reszlegek) {
      cbReszlegLista.addItem(reszleg);
    }
    return cbReszlegLista;
  }

  private MyTableModell dolgozoListaBetoltes(int reszlegID) throws RemoteException {
    dolgozok = remote.getDolgozokListajaAdottReszleghez(reszlegID);//lekerdezDolgozokListajaAdottReszleghez(reszlegID);
    MyTableModell mytm = new MyTableModell(dolgozok);
    return mytm;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == cbReszlegLista) {
      Reszleg reszleg = (Reszleg) cbReszlegLista.getSelectedItem();
      try {
        tDolgozoTable.setModel(dolgozoListaBetoltes(reszleg.getReszlegId()));
      } catch (RemoteException ex) {
        Logger.getLogger(FoAblak.class.getName()).log(Level.SEVERE, null, ex);
      }
      tfDolgozoKeres.setText("");
      lbTalalat.setVisible(false);
      tDolgozoTable.requestFocus();
      tDolgozoTable.addRowSelectionInterval(0, 0);
    }
    if (e.getSource()==btUjDolgozo){
      try {
        new DolgozoFelvetel(this, remote);
      } catch (RemoteException ex) {
        Logger.getLogger(FoAblak.class.getName()).log(Level.SEVERE, null, ex);
      }
      try {
        // ez nem is tudom h működik e így :O
        tDolgozoTable.setModel(dolgozoListaBetoltes(-1)); // eredeti a try catch a plusz
      } catch (RemoteException ex) {
        Logger.getLogger(FoAblak.class.getName()).log(Level.SEVERE, null, ex);
      }
      cbReszlegLista.setSelectedIndex(0);
    }
      
  }

  class MyDocumentListener implements DocumentListener {

    final String newline = "\n";

    public void insertUpdate(DocumentEvent e) {
      //System.out.println("Ez most beszúrás");
      updateLog(e);
    }

    public void removeUpdate(DocumentEvent e) {
      //System.out.println("Ez most törlés");
      updateLog(e);
    }

    public void changedUpdate(DocumentEvent e) {
      //Plain text components don't fire these events.
      //System.out.println("Ez most velmi változtatás");
      updateLog(e);
    }

    public void updateLog(DocumentEvent e) {
      if (tfDolgozoKeres.getText().length() > 0) {
        String keres = tfDolgozoKeres.getText().toLowerCase();
        ArrayList<Dolgozo> dolgozokListaSzükitett = new ArrayList<>();
        //ArrayList<Dolgozo> dolgozoListaAktualis = ((MyTableModell) tDolgozoTable.getModel()).getDolgozLista();
        for (int i = 0; i < dolgozok.size(); i++) {
          if (dolgozok.get(i).getNev().toLowerCase().contains(keres)) {
            dolgozokListaSzükitett.add(dolgozok.get(i));
          }
        }
        if (dolgozokListaSzükitett.size() == 0) {
          lbTalalat.setVisible(true);
          dolgozokListaSzükitett.clear();
        } else {
          lbTalalat.setVisible(false);
        }
        ((MyTableModell) tDolgozoTable.getModel()).setDolgozoLista(dolgozokListaSzükitett);
        ((MyTableModell) tDolgozoTable.getModel()).fireTableDataChanged();
      } else {
        lbTalalat.setVisible(false);
        ((MyTableModell) tDolgozoTable.getModel()).setDolgozoLista(dolgozok);
        ((MyTableModell) tDolgozoTable.getModel()).fireTableDataChanged();
        
//        Reszleg reszleg = (Reszleg) cbReszlegLista.getSelectedItem();
//        tDolgozoTable.setModel(dolgozoListaBetoltes(reszleg.getReszlegId()));
        tfDolgozoKeres.setText("");
        lbTalalat.setVisible(false);
        //tDolgozoTable.requestFocus();
        tDolgozoTable.addRowSelectionInterval(0, 0);
      }
    }
  }
}
