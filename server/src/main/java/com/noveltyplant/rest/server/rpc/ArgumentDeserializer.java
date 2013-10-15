package com.noveltyplant.rest.server.rpc;

import java.io.IOException;

interface ArgumentDeserializer {
  Object deserializeArgument(byte[] data) throws IOException;
}
