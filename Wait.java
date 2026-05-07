public class Wait {
  public static void Blip() {
  	try {
       Thread.currentThread().sleep(50);
    }
    catch (InterruptedException e) {
       e.printStackTrace();
    }
  }  
  public static void longBlip(long s) {
  	try {
  		Thread.currentThread().sleep(100*s);
  	}
  	catch (InterruptedException e) {
       e.printStackTrace();
    }
  }
  public static void manySec(long s) {
  	try {
       Thread.currentThread().sleep(1000*s);
    }
    catch (InterruptedException e) {
       e.printStackTrace();
    }
  }
}
