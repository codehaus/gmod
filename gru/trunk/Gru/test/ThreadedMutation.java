

import java.dyn.InvokeDynamic;
import java.dyn.Linkage;

import org.codehaus.groovy2.lang.MOPLinker;
import org.codehaus.groovy2.lang.RT;

import groovy2.lang.MetaClass;
import groovy2.lang.MetaClassMutator;

// this terst case works but doesn't compile in Eclipse

// java -ea -XX:+UnlockExperimentalVMOptions -XX:+EnableInvokeDynamic -cp .:../classes ThreadedMutation
public class ThreadedMutation {
  /*
  public static int add(int i1, int i2) {
    return i1 - i2;
  }
  
  public static void main(String[] args) throws Throwable {
    final MetaClass mixMetaClass = RT.getMetaClass(ThreadedMutation.class);
    final MetaClass intMetaClass = RT.getMetaClass(int.class);
    
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          throw (AssertionError)new AssertionError().initCause(e);
        }

        System.out.println("mutation");
        MetaClassMutator mutator = intMetaClass.mutator();
        try {
          mutator.addMixin(mixMetaClass);
        } finally {
          mutator.close();
        }
      }
    });
    thread.start();
    
    for(;;) {
      Object o = 2;
      if ((int)InvokeDynamic.invoke$add(o, 2) == 0) {
        break;
      }
    }
    
    System.out.println("OK !");
  }
  
  static {
    Linkage.registerBootstrapMethod(MOPLinker.class, "bootstrap");
  }
  */
}
