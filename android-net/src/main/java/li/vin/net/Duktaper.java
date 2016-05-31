package li.vin.net;

import android.support.annotation.NonNull;
import java.lang.reflect.Method;
import rx.exceptions.Exceptions;

/**
 * Wrap around Duktape JS interpreter using dynamic class loading and reflection to avoid potential
 * issues with the the native libs not loading properly on some devices. Also, this way, if devs
 * choose to they can manually exclude the somewhat bloated Duktape deps from gradle and our SDK
 * won't blow up - it will gracefully fail, as long as exceptions around creation are caught.
 */
final class Duktaper {

  @NonNull
  public static Duktaper create() {
    try {
      Class<?> duktapeCls = Class.forName("com.squareup.duktape.Duktape");
      if (duktapeCls == null) throw new NullPointerException();
      Object duktapeInst = duktapeCls.getDeclaredMethod("create").invoke(null);
      if (duktapeInst == null) throw new NullPointerException();
      return new Duktaper(duktapeCls, duktapeInst);
    } catch (UnsatisfiedLinkError ule) {
      throw Exceptions.propagate(new RuntimeException("cannot link Duktape lib naturally."));
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }

  @NonNull private final Class<?> duktapeCls;
  @NonNull private final Object duktapeInst;

  // cache reflective method lookups that might be called frequently
  private volatile Method evaluateStrStr;
  private volatile Method evaluateStr;

  private Duktaper(@NonNull Class<?> duktapeCls, @NonNull Object duktapeInst) {
    this.duktapeCls = duktapeCls;
    this.duktapeInst = duktapeInst;
  }

  public String evaluate(String script, String fileName) {
    try {
      return (String) evaluateStrStr().invoke(duktapeInst, script, fileName);
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }

  public String evaluate(String script) {
    try {
      return (String) evaluateStr().invoke(duktapeInst, script);
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }

  public void close() {
    try {
      duktapeCls.getDeclaredMethod("close").invoke(duktapeInst);
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }

  // lazy init with DCL:

  private Method evaluateStrStr() throws NoSuchMethodException {
    Method result = evaluateStrStr;
    if (result == null) {
      synchronized (this) {
        result = evaluateStrStr;
        if (result == null) {
          evaluateStrStr = result = //
              duktapeCls.getDeclaredMethod("evaluate", String.class, String.class);
        }
      }
    }
    return result;
  }

  private Method evaluateStr() throws NoSuchMethodException {
    Method result = evaluateStr;
    if (result == null) {
      synchronized (this) {
        result = evaluateStr;
        if (result == null) {
          evaluateStr = result = //
              duktapeCls.getDeclaredMethod("evaluate", String.class);
        }
      }
    }
    return result;
  }
}
