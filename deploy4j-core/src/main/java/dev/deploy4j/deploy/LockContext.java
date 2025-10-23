package dev.deploy4j.deploy;

import java.util.List;

public interface LockContext {

  boolean holdingLock();

  void holdingLock(boolean lock);

  String primaryHost();

  List<String> hosts();

  String version();

}
