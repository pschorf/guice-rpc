package com.pschorf.rpc.server.rpc;

import java.io.IOException;

interface ArgumentDeserializer {
  Object deserializeArgument(byte[] data) throws IOException;
}
