package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.QuestionAdapter;
import com.dst.abacustrainner.Adapter.ViewListInstructorAdapter;
import com.dst.abacustrainner.Model.ViewInstructorListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InstructorPracticeViewQuestionsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private String studentId;
    private String paperId;
    LinearLayout layoutBack;
    ProgressDialog progressDialog;
    TextView txtName,txtTopicName, txtNodata;
    Button btnDownloadAll;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_practice_view_questions);

        recyclerView = findViewById(R.id.recycler_papers);
        Bundle bundle=getIntent().getExtras();

        paperId=bundle.getString("PaperId");
        studentId=bundle.getString("StudentId");
        txtNodata = findViewById(R.id.txt_empty);
        layoutBack = findViewById(R.id.btn_back_to_home);

        btnDownloadAll  = findViewById(R.id.btnDownloadAll);

        btnDownloadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfFromRecyclerView();
            }
        });

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 2 Columns Grid Layout Set చేయడం
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        VerifyMethod(studentId,paperId);

    }

    private void generatePdfFromRecyclerView() {
        View view = findViewById(R.id.rootLayout);

        if (view == null) {
            Toast.makeText(this, "Root layout not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. PDF జనరేట్ అయ్యే ముందు 'Download Questions' బటన్‌ను హైడ్ చేయడం
        if (btnDownloadAll != null) {
            btnDownloadAll.setVisibility(View.GONE);
        }

        view.post(() -> {
            try {
                int totalWidth = view.getWidth();
                int totalHeight = view.getHeight();

                if (recyclerView != null && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
                    view.measure(
                            View.MeasureSpec.makeMeasureSpec(totalWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    );
                    totalHeight = Math.max(totalHeight, view.getMeasuredHeight());
                    view.layout(0, 0, totalWidth, totalHeight);
                }

                if (totalWidth <= 0 || totalHeight <= 0) {
                    // ఒకవేళ ఎర్రర్ వస్తే మళ్ళీ బటన్‌ను చూపించడం
                    if (btnDownloadAll != null) {
                        btnDownloadAll.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(this, "Please wait for layout to load completely", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. బిట్‌మ్యాప్ క్రియేట్ చేసి కాన్వాస్‌తో డ్రా చేయడం (ఇందులో బటన్ ఉండదు)
                Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);

                // 3. PDF డాక్యుమెంట్ క్రియేట్ చేయడం
                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(totalWidth, totalHeight, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);

                Canvas pdfCanvas = page.getCanvas();
                pdfCanvas.drawBitmap(bitmap, 0, 0, null);
                document.finishPage(page);

                // 4. డౌన్‌లోడ్ ఫోల్డర్‌లో ఫైల్ సేవ్ చేయడం
                File pdfFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!pdfFolder.exists()) {
                    pdfFolder.mkdirs();
                }

                File file = new File(pdfFolder, "QuestionPaper_" + System.currentTimeMillis() + ".pdf");

                FileOutputStream fos = new FileOutputStream(file);
                document.writeTo(fos);
                document.close();
                fos.close();

                // PDF సేవ్ అయ్యాక మళ్ళీ బటన్‌ను నార్మల్‌గా చూపించడం
                if (btnDownloadAll != null) {
                    btnDownloadAll.setVisibility(View.VISIBLE);
                }

                Toast.makeText(InstructorPracticeViewQuestionsActivity.this, "PDF Downloaded Successfully!", Toast.LENGTH_LONG).show();

                // ఆటోమేటిక్‌గా PDF ఓపెన్ అవ్వడానికి
                openPDF(file);

            } catch (Exception e) {
                e.printStackTrace();

                // ఏదైనా ఎర్రర్ వస్తే మళ్ళీ బటన్‌ను చూపించడం
                if (btnDownloadAll != null) {
                    btnDownloadAll.setVisibility(View.VISIBLE);
                }

                Toast.makeText(InstructorPracticeViewQuestionsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openPDF(File file) {
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName()+".provider",
                file
        );


        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(uri,"application/pdf");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            startActivity(intent);
        }
        catch(Exception e){

            Toast.makeText(
                    this,
                    "No PDF Viewer Installed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void VerifyMethod(String studentid, String paperId) {
        progressDialog = new ProgressDialog(InstructorPracticeViewQuestionsActivity.this);
        progressDialog.setMessage("Loading Please wait ......");
        progressDialog.setCancelable(false);
        progressDialog.show();
       /* HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);*/
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentid);
        RequestBody paperPart = RequestBody.create(MediaType.parse("text/plain"), paperId);

        Call<ViewInstructorListResponse> call=apiClient.viewInstructorPracticeList(idPart,paperPart);
        call.enqueue(new Callback<ViewInstructorListResponse>() {
            @Override
            public void onResponse(Call<ViewInstructorListResponse> call, Response<ViewInstructorListResponse> response) {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ViewInstructorListResponse viewTopicListResponse = response.body();

                if (viewTopicListResponse.getErrorCode().equals("202")) {
                    Toast.makeText(InstructorPracticeViewQuestionsActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                    recyclerView.setVisibility(View.GONE); // Hide RecyclerView
                } else if (viewTopicListResponse.getErrorCode().equals("200")) {
                    List<ViewInstructorListResponse.Result.QuestionModel> questionList = viewTopicListResponse.getResult().get(0).getQuestions();
                    if (questionList.isEmpty()) {
                        txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                        recyclerView.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        // 👈 Latest data first
                        txtNodata.setVisibility(View.GONE); // Hide No Data Found TextView
                        recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView

                        adapter = new QuestionAdapter(InstructorPracticeViewQuestionsActivity.this, questionList);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(InstructorPracticeViewQuestionsActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                    txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                    recyclerView.setVisibility(View.GONE); // Hide RecyclerView
                }
            }

            @Override
            public void onFailure(Call<ViewInstructorListResponse> call, Throwable t) {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

    }
}