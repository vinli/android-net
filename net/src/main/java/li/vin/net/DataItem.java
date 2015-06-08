package li.vin.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*package*/ abstract class DataItem implements VinliItem {

  @Nullable /*package*/ abstract Map<String, String> data();

  public boolean hasValue(@NonNull String name) {
    final Map<String, String> data = data();
    if (data == null) {
      return false;
    }

    return data.containsKey(name);
  }

  @Nullable public String value(@NonNull String name) {
    final Map<String, String> data = data();
    if (data == null) {
      return null;
    }

    return data.get(name);
  }

  public Set<String> dataKeys() {
    final Map<String, String> data = data();
    if (data == null) {
      return Collections.emptySet();
    }

    return Collections.unmodifiableSet(data.keySet());
  }
}
