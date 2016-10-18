package com.cognifide.knotx.core.serviceadapter.http;

/**
 * Thrown to indicate that adapter service contract was violated.
 */
class UnsupportedServiceException extends RuntimeException {
  UnsupportedServiceException(String message) {
    super(message);
  }
}
