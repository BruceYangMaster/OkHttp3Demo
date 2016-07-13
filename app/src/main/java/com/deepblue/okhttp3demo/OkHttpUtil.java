//package com.deepblue.okhttp3demo;
//
//import android.os.Handler;
//import android.os.Looper;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.Headers;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
///**
// * Created by user on 2016/6/12.
// */
//public class OkHttpUtil {
//    private static OkHttpClient client;
//    private static Handler handler;
//    private static long current = 0;
//
//    static {
//        client = new OkHttpClient();
//        handler = new Handler(Looper.getMainLooper());
//    }
//
//    /**
//     * get请求
//     *
//     * @param url
//     * @param tag
//     * @param callBack
//     */
//    public static void get(String url, Object tag, final ResultCallBack callBack) {
//        Request request = new Request.Builder().url(url).tag(tag).build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                final String erroMsg = e.getLocalizedMessage();
//                postErrorRunInMain(erroMsg, callBack);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String str = response.body().string();
//                postSuccessRunInMain(str, callBack);
//            }
//        });
//    }
//
//
//    /**
//     * post请求
//     *
//     * @param url
//     * @param params
//     * @param callBack
//     */
//    public static void post(String url, Object tag, Map<String, String> params, final ResultCallBack callBack) {
//        FormEncodingBuilder builder = new FormEncodingBuilder();
//        Set<String> keySet = params.keySet();
//        Iterator<String> it = keySet.iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            builder.add(key, params.get(key));
//        }
//
//        Request reuest = new Request.Builder().url(url).tag(tag).post(builder.build()).build();
//        client.newCall(reuest).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                final String erroMsg = e.getLocalizedMessage();
//                postErrorRunInMain(erroMsg, callBack);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                final String str = response.body().string();
//                postSuccessRunInMain(str, callBack);
//            }
//        });
//    }
//
//    /**
//     * 文件上传
//     *
//     * @param url
//     * @param file
//     * @param callBack
//     */
//    public static void upload(String url, Object tag, File file, Map<String, String> params, final ResultCallBack callBack) {
//        //文件body
//        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//        //携参数上传文件body
//        RequestBody body = new MultipartBuilder()
//                .type(MultipartBuilder.FORM)
//                .addFormDataPart("username", "wangsfine")
//                .addPart(Headers.of("Content-Disposition", "form-data; name=\"mFile\";filename=\\\"wjd.mp4\\\"\""), fileBody)
//                .build();
//        //使用装饰者模式自定义RequestBody为上传文件添加进度监听
//        ProgressRequestBody progressRequestBody = new ProgressRequestBody(body, new ProgressRequestBody.ProgressRequestListener() {
//            @Override
//            public void onRequestProgress(final long bytesWritten, final long contentLength, final boolean done) {
//                postUploadRunInMain(bytesWritten, contentLength, done, callBack);
//            }
//        });
//        //得到request
//        final Request request = new Request.Builder()
//                .url(url)
//                .tag(tag)
//                .post(progressRequestBody)
//                .build();
//
//        //request访问
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                final String erroMsg = e.getLocalizedMessage();
//                postErrorRunInMain(erroMsg, callBack);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                final String msg = response.body().string();
//                postSuccessRunInMain(msg, callBack);
//            }
//        });
//    }
//
//
//    /**
//     * 下载文件
//     *
//     * @param url
//     * @param file
//     * @param callBack
//     */
//    public static void download(final String url, Object tag, final File file, final ResultCallBack callBack) {
//        Request request = new Request.Builder().url(url).tag(tag).build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                final String errorMsg = e.getLocalizedMessage();
//                postErrorRunInMain(errorMsg, callBack);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                InputStream is = null;
//                final long contentLenth = response.body().contentLength();
//                byte[] buf = new byte[2048];
//                int len = 0;
//                FileOutputStream fos = null;
//                try {
//                    is = response.body().byteStream();
//                    fos = new FileOutputStream(file);
//                    while ((len = is.read(buf)) != -1) {
//                        fos.write(buf, 0, len);
//                        current += len;
//                        postDownloadRunInMain(current, contentLenth, callBack);
//                    }
//                    fos.flush();
//                    //如果下载文件成功，第一个参数为文件的绝对路径
//                    postSuccessRunInMain(file.getAbsolutePath(), callBack);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    final String errorMsg = e.getLocalizedMessage();
//                    postErrorRunInMain(errorMsg, callBack);
//                } finally {
//                    try {
//                        if (is != null) is.close();
//                    } catch (IOException e) {
//                        final String erroMsg = e.getLocalizedMessage();
//                        postErrorRunInMain(erroMsg, callBack);
//                    }
//                    try {
//                        if (fos != null) fos.close();
//                    } catch (IOException e) {
//                        final String erroMsg = e.getLocalizedMessage();
//                        postErrorRunInMain(erroMsg, callBack);
//                    }
//                }
//            }
//        });
//
//    }
//
//
//    /**
//     * 取消请求
//     *
//     * @param tag
//     */
//    public static void cancle(Object tag) {
//        client.cancel(tag);
//    }
//
//
//    /**
//     * 主线程回调接口
//     */
//    public interface ResultCallBack {
//        void onSuccess(String str);
//
//        void onError(String erroMsg);
//
//        void download(long current, long contentLength);
//
//        void upload(long current, long contentLength, boolean done);
//    }
//
//    //********************将请求结果或进度post到主线程**********************
//    private static void postSuccessRunInMain(final String successMsg, final ResultCallBack callBack) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                callBack.onSuccess(successMsg);
//            }
//        });
//    }
//
//    private static void postErrorRunInMain(final String errorMsg, final ResultCallBack callBack) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                callBack.onError(errorMsg);
//            }
//        });
//    }
//
//    private static void postDownloadRunInMain(final long current, final long contentLength, final ResultCallBack callBack) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                callBack.download(current, contentLength);
//            }
//        });
//    }
//
//    private static void postUploadRunInMain(final long current, final long contentLength, final boolean done, final ResultCallBack callBack) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                callBack.upload(current, contentLength, done);
//            }
//        });
//    }
//}
