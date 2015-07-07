package li.vin.net;

import retrofit.http.GET;
import rx.Observable;

/*package*/ interface Users {

  @GET("/users/_current")
  Observable<Wrapped<User>> currentUser();

}
