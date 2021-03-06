package in.restroin.partnerrestroin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.restroin.partnerrestroin.BookingActivity;
import in.restroin.partnerrestroin.CompleteDining;
import in.restroin.partnerrestroin.R;
import in.restroin.partnerrestroin.adapters.NotificationsAdapter;
import in.restroin.partnerrestroin.interfaces.PartnerRestroINClient;
import in.restroin.partnerrestroin.models.BookingModel;
import in.restroin.partnerrestroin.models.NotificationsModel;
import in.restroin.partnerrestroin.utils.SavedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActiveFragment extends Fragment {
    private final String API_BASE_URL = "https://www.restroin.in/developers/api/v2/";
    private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(loggingInterceptor);
    private Retrofit.Builder builder = new Retrofit.Builder().client(httpClient.build()).baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
    private Retrofit retrofit = builder.build();
    public ActiveFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_bookings, container, false);
        RecyclerView NotificationRecycler = (RecyclerView) view.findViewById(R.id.bookings_recyclerView);
        NotificationRecycler.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        TextView textView = (TextView) view.findViewById(R.id.error_text_view);
        textView.setVisibility(View.GONE);
        addBookingData(NotificationRecycler, new SavedPreferences().getApiKey(view.getContext()), new SavedPreferences().getPartnerID(view.getContext()), new SavedPreferences().getRestaurantID(view.getContext()), view);
        return view;
    }

    private void addBookingData(final RecyclerView recyclerView, String access_key, String partner_id, String restaurant, final View view){
        final List<NotificationsModel> pendingBookings = new ArrayList<>();
        PartnerRestroINClient bookingClient = retrofit.create(PartnerRestroINClient.class);
        Call<List<BookingModel>> call = bookingClient.getBookingData(access_key, partner_id, restaurant, "Confirm");
        call.enqueue(new Callback<List<BookingModel>>() {
            @Override
            public void onResponse(Call<List<BookingModel>> call, final Response<List<BookingModel>> response) {
                for (int i=0; i < response.body().size(); i++){
                    String body = "<b>" + response.body().get(i).getGuest_name() + "</b> 's Dining is Active. Click here to complete the Dining.";
                    pendingBookings.add(new NotificationsModel(response.body().get(i).getBooking_id(), Html.fromHtml(body).toString(), "A"));
                }
                NotificationsAdapter adapter = new NotificationsAdapter(pendingBookings);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                RecyclerView NotificationRecycler = (RecyclerView) view.findViewById(R.id.bookings_recyclerView);
                NotificationRecycler.setVisibility(View.VISIBLE);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                TextView textView = (TextView) view.findViewById(R.id.error_text_view);
                textView.setVisibility(View.GONE);
                final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent motionEvent) {
                        return false;
                    }

                    @Override
                    public void onShowPress(MotionEvent motionEvent) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent motionEvent) {
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent motionEvent) {

                    }

                    @Override
                    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                        return false;
                    }
                });

                recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                    @Override
                    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                        View childView = rv.findChildViewUnder(e.getX(), e.getY());
                        if(childView != null && gestureDetector.onTouchEvent(e)) {
                                Intent go_to_confirm = new Intent(getActivity(), CompleteDining.class);
                                go_to_confirm.putExtra("booking_id", response.body().get(rv.getChildAdapterPosition(childView)).getBooking_id());
                                go_to_confirm.putExtra("booking_step", "Completed");
                                startActivity(go_to_confirm);
                        }
                        return false;
                    }

                    @Override
                    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                    }

                    @Override
                    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<BookingModel>> call, Throwable t) {
                RecyclerView NotificationRecycler = (RecyclerView) view.findViewById(R.id.bookings_recyclerView);
                NotificationRecycler.setVisibility(View.GONE);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                TextView textView = (TextView) view.findViewById(R.id.error_text_view);
                textView.setVisibility(View.VISIBLE);
            }
        });
    }
}
