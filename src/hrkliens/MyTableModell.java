
package hrkliens;

import hrremote.Dolgozo;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
//import modell.Dolgozo;


public class MyTableModell extends AbstractTableModel{
  private ArrayList<Dolgozo> dolgozoLista = new ArrayList<>();
  private String[] oszlopNevek = new String[]{"Név","Részleg","Munkakör","Belépési dátum","Fizetés"};
  
  public MyTableModell(ArrayList<Dolgozo> dolgozoLista) {
    this.dolgozoLista = dolgozoLista;
  }

  public void setDolgozoLista(ArrayList<Dolgozo> dolgozoLista) {
    this.dolgozoLista = dolgozoLista;
  }
  
  ArrayList<Dolgozo> getDolgozLista() {
    return dolgozoLista;
  }
  
  
  public Dolgozo getDolgozo (int index){
    return dolgozoLista.get(index);
  }
  
  public void steDolgozo (Dolgozo dolgozo, int index){
    dolgozoLista.set(index, dolgozo);
  }
  
  @Override
  public int getRowCount() {
    return dolgozoLista.size();
  }

  @Override
  public String getColumnName(int column) {
    return oszlopNevek[column];
            
  }

  @Override
  public int getColumnCount() {
    return oszlopNevek.length;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch(columnIndex){
      case  0: return dolgozoLista.get(0).getNev().getClass() ;
      case  1: return dolgozoLista.get(0).getReszlegNev().getClass();
      case  2: return dolgozoLista.get(0).getMunkakor().getClass();
      case  3: return dolgozoLista.get(0).getFelveteliDatum().getClass();
      case  4: return ((Integer)dolgozoLista.get(0).getFizetes()).getClass();
      default: return dolgozoLista.get(0).getNev().getClass() ;
    }
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    switch(columnIndex){
      case  0: return dolgozoLista.get(rowIndex).getNev() ;
      case  1: return dolgozoLista.get(rowIndex).getReszlegNev();
      case  2: return dolgozoLista.get(rowIndex).getMunkakor();
      case  3: return dolgozoLista.get(rowIndex).getFelveteliDatum();
      case  4: return dolgozoLista.get(rowIndex).getFizetes();
      default: return dolgozoLista.get(rowIndex).getNev() ;
    }
  }

}
