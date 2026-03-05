package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.OrderInfoAdapter;
import com.dst.abacustrainner.Model.OrderInfoResponse;
import com.dst.abacustrainner.Model.StudentOrdersResponse;
import com.dst.abacustrainner.Model.WorksheetOrder;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    TextView txtOrderedOn, txtStatus, txtPaymentThrough,
            txtPaymentMethod, txtCurrency, txtAmount;

    String studentId,OrderId;

    Button butDownloadReceipt;

    OrderInfoAdapter orderInfoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerView = findViewById(R.id.recyclerCourses);
        butDownloadReceipt = findViewById(R.id.btnDownloadReceipt);
        txtOrderedOn = findViewById(R.id.txt_orderOn);
        txtStatus = findViewById(R.id.txt_orderStatus);
        txtPaymentThrough = findViewById(R.id.txt_paymentthrough);
        txtPaymentMethod = findViewById(R.id.txt_paymentmethod);
        txtCurrency = findViewById(R.id.txt_currency);
        txtAmount = findViewById(R.id.txt_amount);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        studentId = getIntent().getStringExtra("studentId");
        OrderId = getIntent().getStringExtra("orderId");
        butDownloadReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePDF();
            }
        });

        loadOrderInfo(studentId,OrderId);


    }

    private void generatePDF() {
        View view = findViewById(R.id.rootLayout);

        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        1
                ).create();

        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas pdfCanvas = page.getCanvas();
        pdfCanvas.drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);

        File file = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS),
                "OrderReceipt.pdf"
        );

        try {
            document.writeTo(new FileOutputStream(file));
            document.close();
            Toast.makeText(this, "PDF Saved in Downloads", Toast.LENGTH_LONG).show();
            openPDF(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void openPDF(File file) {
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }


    private void loadOrderInfo(String studentId, String orderId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody StudentLevelPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody OrderLevelPart = RequestBody.create(MediaType.parse("text/plain"), orderId);

        Call<OrderInfoResponse> call = apiClient.getOrderInfo(StudentLevelPart,OrderLevelPart);
        call.enqueue(new Callback<OrderInfoResponse>() {
            @Override
            public void onResponse(Call<OrderInfoResponse> call, Response<OrderInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderInfoResponse.WorksheetOrderInfo order =
                            response.body().getResult()
                                    .getWorksheetOrderInfo().get(0);
                    txtOrderedOn.setText(convertTimestamp(order.getOrderedOn()));
                    txtStatus.setText(order.getState());
                    txtPaymentThrough.setText(order.getPaymentThrough());
                    txtPaymentMethod.setText(order.getPaymentMethod());
                    txtCurrency.setText(order.getCurrency());
                    txtAmount.setText(order.getAmount());

                    orderInfoAdapter = new OrderInfoAdapter(
                            OrderDetailsActivity.this,
                            order.getSubscriptions()
                    );

                    recyclerView.setAdapter(orderInfoAdapter);

                }
            }

            @Override
            public void onFailure(Call<OrderInfoResponse> call, Throwable t) {
                //showEmptyMessage("Server error. Please try again.");

            }
        });

    }

    public static String convertTimestamp(String timestamp){

        long time = Long.parseLong(timestamp) * 1000;

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());

        return sdf.format(new Date(time));
    }

}