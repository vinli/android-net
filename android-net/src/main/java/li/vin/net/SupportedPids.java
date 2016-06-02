package li.vin.net;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class SupportedPids {

  private static final Set<String> KNOWN_UNSUPPORTED_PIDS;

  static {
    KNOWN_UNSUPPORTED_PIDS = new HashSet<>();

    // markers for supported pids
    KNOWN_UNSUPPORTED_PIDS.add("01-00");
    KNOWN_UNSUPPORTED_PIDS.add("01-20");
    KNOWN_UNSUPPORTED_PIDS.add("01-40");
    KNOWN_UNSUPPORTED_PIDS.add("01-60");
    KNOWN_UNSUPPORTED_PIDS.add("01-80");

    // dtcs
    KNOWN_UNSUPPORTED_PIDS.add("01-01");

    // accelerometer
    KNOWN_UNSUPPORTED_PIDS.add("01-0c");

    // >2 byte pids from https://en.wikipedia.org/wiki/OBD-II_PIDs#Standard_PIDs
    KNOWN_UNSUPPORTED_PIDS.add("01-24");
    KNOWN_UNSUPPORTED_PIDS.add("01-25");
    KNOWN_UNSUPPORTED_PIDS.add("01-26");
    KNOWN_UNSUPPORTED_PIDS.add("01-27");
    KNOWN_UNSUPPORTED_PIDS.add("01-28");
    KNOWN_UNSUPPORTED_PIDS.add("01-29");
    KNOWN_UNSUPPORTED_PIDS.add("01-2a");
    KNOWN_UNSUPPORTED_PIDS.add("01-2b");

    KNOWN_UNSUPPORTED_PIDS.add("01-34");
    KNOWN_UNSUPPORTED_PIDS.add("01-35");
    KNOWN_UNSUPPORTED_PIDS.add("01-36");
    KNOWN_UNSUPPORTED_PIDS.add("01-37");
    KNOWN_UNSUPPORTED_PIDS.add("01-38");
    KNOWN_UNSUPPORTED_PIDS.add("01-39");
    KNOWN_UNSUPPORTED_PIDS.add("01-3a");
    KNOWN_UNSUPPORTED_PIDS.add("01-3b");

    KNOWN_UNSUPPORTED_PIDS.add("01-41");
    KNOWN_UNSUPPORTED_PIDS.add("01-4f");
    KNOWN_UNSUPPORTED_PIDS.add("01-50");

    KNOWN_UNSUPPORTED_PIDS.add("01-64");
    //KNOWN_UNSUPPORTED_PIDS.add("01-66");
    KNOWN_UNSUPPORTED_PIDS.add("01-67");
    KNOWN_UNSUPPORTED_PIDS.add("01-68");
    KNOWN_UNSUPPORTED_PIDS.add("01-69");
    KNOWN_UNSUPPORTED_PIDS.add("01-6a");
    KNOWN_UNSUPPORTED_PIDS.add("01-6b");
    KNOWN_UNSUPPORTED_PIDS.add("01-6c");
    KNOWN_UNSUPPORTED_PIDS.add("01-6d");
    KNOWN_UNSUPPORTED_PIDS.add("01-6e");
    KNOWN_UNSUPPORTED_PIDS.add("01-6f");

    KNOWN_UNSUPPORTED_PIDS.add("01-70");
    KNOWN_UNSUPPORTED_PIDS.add("01-71");
    KNOWN_UNSUPPORTED_PIDS.add("01-72");
    KNOWN_UNSUPPORTED_PIDS.add("01-73");
    KNOWN_UNSUPPORTED_PIDS.add("01-74");
    KNOWN_UNSUPPORTED_PIDS.add("01-75");
    KNOWN_UNSUPPORTED_PIDS.add("01-76");
    KNOWN_UNSUPPORTED_PIDS.add("01-77");
    KNOWN_UNSUPPORTED_PIDS.add("01-78");
    KNOWN_UNSUPPORTED_PIDS.add("01-79");
    KNOWN_UNSUPPORTED_PIDS.add("01-7a");
    KNOWN_UNSUPPORTED_PIDS.add("01-7b");
    KNOWN_UNSUPPORTED_PIDS.add("01-7c");
    KNOWN_UNSUPPORTED_PIDS.add("01-7f");

    KNOWN_UNSUPPORTED_PIDS.add("01-81");
    KNOWN_UNSUPPORTED_PIDS.add("01-82");
    KNOWN_UNSUPPORTED_PIDS.add("01-83");

    KNOWN_UNSUPPORTED_PIDS.add("01-a0");
    KNOWN_UNSUPPORTED_PIDS.add("01-c0");
  }

  private final String raw;
  private HashMap<String, Boolean> supportMap;

  /*package*/ SupportedPids(@NonNull String raw) {
    this.raw = raw;
  }

  public String getRaw() {
    return raw;
  }

  public boolean supports(@NonNull String code) {
    if (!(code = code.toLowerCase(Locale.US)).startsWith("01-")) return false;
    if (KNOWN_UNSUPPORTED_PIDS.contains(code)) return false;
    if (supportMap == null) buildSupportMap();
    Boolean result = supportMap.get(code);
    return result == null
        ? false
        : result;
  }

  @NonNull
  public String[] getSupport() {
    if (supportMap == null) buildSupportMap();
    Set<String> support = new HashSet<>();
    for (Map.Entry<String, Boolean> e : supportMap.entrySet()) {
      if (e.getValue() != null && e.getValue()) {
        support.add(e.getKey().toUpperCase(Locale.US));
      }
    }
    return support.toArray(new String[support.size()]);
  }

  @Override
  public String toString() {
    if (supportMap == null) buildSupportMap();
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Boolean> entry : supportMap.entrySet()) {
      String key = entry.getKey();
      Boolean val = entry.getValue();
      if (sb.length() != 0) sb.append("::");
      sb.append("key='").append(key).append("',val='").append(val).append("'");
    }
    return sb.toString();
  }

  private void buildSupportMap() {
    supportMap = new HashMap<>();
    ArrayList<String> groups = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < raw.length(); i++) {
      if (sb.length() == 8) {
        groups.add(sb.toString());
        sb.delete(0, 8);
      }
      sb.append(raw.charAt(i));
    }
    if (sb.length() == 8) {
      groups.add(sb.toString());
    }
    for (int i = 0; i < groups.size(); i++) {
      parseBitflags(i, groups.get(i));
    }
  }

  private void parseBitflags(int group, String bitflags) {
    int groupStart = group * 32 + 1;
    for (int i = 0; i < 8; i++) {
      int j = i * 4;
      int hexInt = Integer.parseInt(bitflags.substring(i, i + 1), 16);
      String bin = Integer.toBinaryString(hexInt);
      while (bin.length() < 4) bin = '0' + bin;
      putIntoMap(groupStart + j, bin.charAt(0) == '1');
      putIntoMap(groupStart + j + 1, bin.charAt(1) == '1');
      putIntoMap(groupStart + j + 2, bin.charAt(2) == '1');
      putIntoMap(groupStart + j + 3, bin.charAt(3) == '1');
    }
  }

  private void putIntoMap(int index, boolean flag) {
    String hex = Integer.toHexString(index).toLowerCase(Locale.US);
    while (hex.length() < 2) hex = '0' + hex;
    supportMap.put("01-" + hex, flag);
  }
}
