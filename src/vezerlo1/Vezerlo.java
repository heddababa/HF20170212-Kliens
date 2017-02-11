
package vezerlo1;

import hrremote.HrRemote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import hrkliens.Bejelentkezes;

public class Vezerlo {
  
  public static void main(String[] args) {
         String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            HrRemote remote = (HrRemote) registry.lookup("hrRemote"); //new AdatBazisKezeles()
            System.out.println(remote.getReszlegList());
            new Bejelentkezes(remote).setVisible(true);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    
    //new FoAblak(new AdatBazisKezeles(), user.getJogkor());
  }
  
}
