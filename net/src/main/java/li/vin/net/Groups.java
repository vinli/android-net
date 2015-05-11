package li.vin.net;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/*package*/ interface Groups {

  @GET("/groups") Observable<Page<Group>> getGroups();

  @GET("/groups")
  Observable<Page<Group>> getGroups(@Query("limit") Integer limit, @Query("offset") Integer offset);

  @GET("/groups/{id}") Observable<Group> getGroup(@Path("id") String groupId);

}
