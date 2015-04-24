package li.vin.net;

/**
 * Created by kyle on 7/9/14.
 */
public final class VinliError extends RuntimeException {

  /*package*/ static VinliError serverError(ServerError err) {
    return new VinliError(err.message, err);
  }

  private final ServerError mServerError;

  /*package*/ VinliError(String message, ServerError err) {
    super(message);
    mServerError = err;
  }

  public int getStatusCode() {
    return mServerError == null ? -1 : mServerError.statusCode;
  }

  /*package*/ final class ServerError {
    private final int statusCode;
    private final String error, message;

    /*package*/ ServerError(int statusCode, String message) {
      this.statusCode = statusCode;
      this.message = message;
      this.error = null; // set by GSON
    }
  }

}
