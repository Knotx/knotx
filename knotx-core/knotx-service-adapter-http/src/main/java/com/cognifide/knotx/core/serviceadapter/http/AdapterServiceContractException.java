package com.cognifide.knotx.core.serviceadapter.http;

/**
 * Thrown to indicate that adapter service contract was violated.
 */
class AdapterServiceContractException extends RuntimeException {
  AdapterServiceContractException(String message) {
    super(message);
  }
}
