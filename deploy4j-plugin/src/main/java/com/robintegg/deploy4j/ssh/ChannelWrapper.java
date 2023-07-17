package com.robintegg.deploy4j.ssh;

import com.jcraft.jsch.ChannelExec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
class ChannelWrapper implements AutoCloseable {

  private final ChannelExec channel;

  public ChannelWrapper(ChannelExec channel) {
    log.info("channel opened");
    this.channel = channel;
  }

  @Override
  public void close() throws Exception {
    log.info("closing channel");
    this.channel.disconnect();
  }

}
