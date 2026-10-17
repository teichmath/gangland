public class Wait {
  // Dramatic pauses are skipped under GANGLAND_AUTOMATION=1 so harnesses run at full speed,
  // unless GANGLAND_KEEP_PAUSES=1 (a person playing through tools/play.py).
  static final boolean skip = "1".equals(System.getenv("GANGLAND_AUTOMATION"))
                              && !"1".equals(System.getenv("GANGLAND_KEEP_PAUSES"));

  public static void Blip() {
  	if (skip) return;
  	try {
       Thread.currentThread().sleep(50);
    }
    catch (InterruptedException e) {
       e.printStackTrace();
    }
  }  
  public static void longBlip(long s) {
  	if (skip) return;
  	try {
  		Thread.currentThread().sleep(100*s);
  	}
  	catch (InterruptedException e) {
       e.printStackTrace();
    }
  }
  public static void manySec(long s) {
  	if (skip) return;
  	try {
       Thread.currentThread().sleep(1000*s);
    }
    catch (InterruptedException e) {
       e.printStackTrace();
    }
  }
}
