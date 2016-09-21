package li.vin.net;

import retrofit2.http.GET;
import rx.Observable;

/*package*/ interface Users {

  @GET("users/_current")
  Observable<Wrapped<User>> currentUser();

}
